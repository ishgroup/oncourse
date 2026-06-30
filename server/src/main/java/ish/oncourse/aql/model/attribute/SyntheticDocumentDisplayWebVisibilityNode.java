/*
 * Copyright ish group pty ltd 2025.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 */

package ish.oncourse.aql.model.attribute;

import ish.common.types.AttachmentInfoVisibility;
import ish.oncourse.aql.impl.CompilationContext;
import ish.oncourse.aql.impl.LazyExpressionNode;
import ish.oncourse.server.api.v1.model.DocumentVisibilityDTO;
import ish.oncourse.server.cayenne.*;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.exp.parser.*;
import org.apache.cayenne.query.ObjectSelect;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Implements a special node for handling Document web visibility filtering in AQL queries.
 * <p>
 * This class extends {@link LazyExpressionNode} to provide custom resolution logic for the synthetic
 * {@link Document#DISPLAY_WEB_VISIBILITY_PROPERTY} attribute, which abstracts the complex visibility rules
 * for documents based on their {@link Document#WEB_VISIBILITY} property and attachment relationships.
 * <p>
 * The node translates high-level visibility concepts (PUBLIC, PRIVATE, etc.) into appropriate Cayenne
 * expressions that consider both DB visibility settings and contextual factors like whether
 * documents are tutor-related.
 *
 * @see DocumentDisplayWebVisibility
 * @see Document#WEB_VISIBILITY
 * @see DocumentVisibilityDTO
 */
public class SyntheticDocumentDisplayWebVisibilityNode extends LazyExpressionNode {

    private static final String WEB_VISIBILITY_PATH = Document.WEB_VISIBILITY.getName();
    private static final String ID_PATH = Document.ID.getName();
    private static final String ATTACHMENT_RELATIONS_PATH = Document.ATTACHMENT_RELATIONS.getName();
    private static final String ENTITY_IDENTIFIER_PATH = AttachmentRelation.ENTITY_IDENTIFIER.getName();

    /**
     * Predicate to check if a document is attached to tutors-related entities.
     */
    private static final Predicate<Document> DOCUMENT_TUTOR_RELATED = doc -> doc.getAttachmentRelations().stream()
            .map(AttachmentRelation::getEntityIdentifier)
            .anyMatch(AttachmentRelation.TUTOR_RELATED_ENTITIES::contains);

    /**
     * Strategy enum for different visibility processing approaches.
     */
    private enum VisibilityStrategy {
        DIRECT_MATCH,           // PUBLIC, LINK
        COURSE_RELATED_ONLY,    // TUTORS_ONLY, TUTORS_AND_ENROLLED_STUDENTS
        COMPLEX_FILTERING       // PRIVATE, STUDENTS_ONLY
    }

    @Override
    public SimpleNode resolveSelf(CompilationContext ctx) {
        return this;
    }

    @Override
    public SimpleNode resolveParent(SimpleNode parent, List<SimpleNode> args, CompilationContext ctx) {
        if (parent.getType() != Expression.EQUAL_TO) {
            throw new IllegalArgumentException("Unsupported operation: " + parent.getClass().getSimpleName());
        }
        if (args.size() < 3) {
            throw new IllegalArgumentException("Not sufficient arguments to resolve " + Document.DISPLAY_WEB_VISIBILITY_PROPERTY);
        }
        if (!(args.get(1) instanceof ASTObjPath)) {
            throw new IllegalArgumentException("Argument 2 must be a path");
        }
        if (!(args.get(2) instanceof ASTScalar)) {
            throw new IllegalArgumentException("Argument 3 must be a scalar");
        }

        ASTObjPath pathNode = (ASTObjPath) args.get(1);
        Object value = ((ASTScalar) args.get(2)).getValue();
        DocumentVisibilityDTO visibility = DocumentVisibilityDTO.valueOf(value.toString());

        String prefix = extractPrefix(pathNode.getPath());

        return createExpressionForVisibility(visibility, prefix, ctx.getContext());
    }

    private String extractPrefix(String path) {
        int index = path.lastIndexOf("." + Document.DISPLAY_WEB_VISIBILITY_PROPERTY);
        return index <= 0 ? "" : path.substring(0, index);
    }

    private VisibilityStrategy getVisibilityStrategy(DocumentVisibilityDTO visibility) {
        switch (visibility) {
            case PUBLIC:
            case LINK:
                return VisibilityStrategy.DIRECT_MATCH;
            case TUTORS_ONLY:
            case TUTORS_AND_ENROLLED_STUDENTS:
                return VisibilityStrategy.COURSE_RELATED_ONLY;
            case PRIVATE:
            case STUDENTS_ONLY:
                return VisibilityStrategy.COMPLEX_FILTERING;
            default:
                throw new IllegalArgumentException("Unsupported document visibility: " + visibility);
        }
    }

    private SimpleNode createExpressionForVisibility(DocumentVisibilityDTO visibility, String prefix, ObjectContext context) {
        switch (getVisibilityStrategy(visibility)) {
            case DIRECT_MATCH:
                return createDirectMatchExpression(visibility, prefix);
            case COURSE_RELATED_ONLY:
                return createCourseRelatedExpression(visibility, prefix);
            case COMPLEX_FILTERING:
                return createComplexFilteringExpression(visibility, prefix, context);
            default:
                throw new IllegalArgumentException("Unsupported document visibility: " + visibility);
        }
    }

    private SimpleNode createDirectMatchExpression(DocumentVisibilityDTO visibility, String prefix) {
        String webVisibilityPath = objPath(prefix, WEB_VISIBILITY_PATH);
        return (SimpleNode) ExpressionFactory.matchExp(webVisibilityPath, visibility);
    }

    private SimpleNode createCourseRelatedExpression(DocumentVisibilityDTO visibility, String prefix) {
        String webVisibilityPath = objPath(prefix, WEB_VISIBILITY_PATH);
        Expression visibilityMatch = ExpressionFactory.matchExp(webVisibilityPath, visibility);

        String relationPath = objPath(prefix, ATTACHMENT_RELATIONS_PATH + "." + ENTITY_IDENTIFIER_PATH);
        Expression courseRelatedMatch = ExpressionFactory.inExp(relationPath, AttachmentRelation.TUTOR_RELATED_ENTITIES);

        return (SimpleNode) ExpressionFactory.and(visibilityMatch, courseRelatedMatch);
    }

    private SimpleNode createComplexFilteringExpression(DocumentVisibilityDTO visibility, String prefix, ObjectContext context) {
        switch (visibility) {
            case PRIVATE:
                return createPrivateVisibilityExpression(prefix, context);
            case STUDENTS_ONLY:
                return createStudentsOnlyVisibilityExpression(prefix, context);
            default:
                throw new IllegalArgumentException("Unsupported complex visibility: " + visibility);
        }
    }

    private SimpleNode createPrivateVisibilityExpression(String prefix, ObjectContext context) {
        String webVisibilityPath = objPath(prefix, WEB_VISIBILITY_PATH);
        Expression privateDocsMatch = ExpressionFactory.matchExp(webVisibilityPath, AttachmentInfoVisibility.PRIVATE);

        Predicate<Document> notTutorRelated = DOCUMENT_TUTOR_RELATED.negate();
        List<Long> effectivelyPrivateDocsIds = findDocumentsByPredicate(context, AttachmentInfoVisibility.TUTORS, notTutorRelated);

        String idPath = objPath(prefix, ID_PATH);
        Expression effectivelyPrivateDocsMatch = ExpressionFactory.inExp(idPath, effectivelyPrivateDocsIds);

        return (SimpleNode) ExpressionFactory.or(privateDocsMatch, effectivelyPrivateDocsMatch);
    }

    private SimpleNode createStudentsOnlyVisibilityExpression(String prefix, ObjectContext context) {
        Predicate<Document> notTutorRelated = DOCUMENT_TUTOR_RELATED.negate();
        List<Long> studentsOnlyDocsIds = findDocumentsByPredicate(context, AttachmentInfoVisibility.STUDENTS, notTutorRelated);

        String idPath = objPath(prefix, ID_PATH);
        return (SimpleNode) ExpressionFactory.inExp(idPath, studentsOnlyDocsIds);
    }

    private String objPath(String prefix, String property) {
        return prefix.isEmpty() ? property : prefix + "." + property;
    }

    private List<Long> findDocumentsByPredicate(ObjectContext context, AttachmentInfoVisibility visibility,
                                                Predicate<Document> predicate) {
        List<Document> documents = ObjectSelect.query(Document.class)
                .where(Document.WEB_VISIBILITY.eq(visibility))
                .prefetch(Document.ATTACHMENT_RELATIONS.disjointById())
                .select(context);

        return documents.stream()
                .filter(predicate)
                .map(Document::getId)
                .collect(Collectors.toList());
    }
}
