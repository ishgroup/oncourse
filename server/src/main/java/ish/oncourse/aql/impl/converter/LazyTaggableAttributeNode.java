/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.aql.impl.converter;

import ish.oncourse.aql.impl.CompilationContext;
import ish.oncourse.aql.impl.ExpressionUtil;
import ish.oncourse.aql.model.attribute.tagging.TaggableAttribute;
import ish.oncourse.cayenne.TaggableClasses;
import ish.util.EntityPathUtils;
import ish.util.TaggableUtil;
import org.apache.cayenne.exp.parser.*;

import java.util.List;

import static ish.oncourse.aql.NodeUtils.inverseNodeByIds;

/**
 * Node, that redefines parent node of tags predicates and adds entityIdentifier, nodetype and parent tag if required checking to request
 * <p>
 *                 _op__(not eq)          ->                  _____________________and______________________________
 *                  /      \                                 /                   \                                  \
 *               tags        literal                       _op__           ________eq__                              ________eq_____________________
 *                                                                        /            \                             \                              \
 *                                          taggingRelations.entityIdentifier      TaggableClasses.databaseValue      taggingRelations.tag.nodeType  nodeType
 */
public class LazyTaggableAttributeNode extends LazyExprNodeWithBasePathResolver {

    private final TaggableAttribute attribute;

    public LazyTaggableAttributeNode(TaggableAttribute currentAttribute) {
        this.attribute = currentAttribute;
    }

    @Override
    public SimpleNode resolveParent(SimpleNode parent, List<SimpleNode> args, CompilationContext ctx) {
        var astObjPath = (ASTObjPath) args.get(1);
        if (parent instanceof LazyRelationshipComparisonNode) {
            parent = ((LazyRelationshipComparisonNode) parent).resolveSelf(astObjPath, ctx);
        }
        String basePath = astObjPath.getPath();
        int lastPointIndex = 0;

        if(attribute.getCurrentAlias() != null)
            lastPointIndex = basePath.indexOf(attribute.getCurrentAlias());

        String entityPath = basePath.substring(0, lastPointIndex);

        var taggedEntity = EntityPathUtils.resolvePathToTaggable(entityPath, ctx);
        if (taggedEntity == null) {
            return null;
        }

        TaggableClasses taggableClasses = TaggableUtil.resolveTaggableClass(taggedEntity);

        ASTAnd notEmptyExpr = buildTagChecksExpr(entityPath, taggableClasses);

        if (basePath.endsWith(TAGS) || basePath.endsWith(CHECKED_TASKS) || basePath.endsWith(COMPLETED_CHECKLISTS)) {
            // tags is empty || tags is null expressions
            if (parent instanceof ASTEqual) {
                return inverseNodeByIds(notEmptyExpr, taggedEntity, ctx);
            } else if (parent instanceof ASTNotEqual) {
                return notEmptyExpr;
            }
        }

        parent = processAsAlias(parent, args);
        notEmptyExpr.jjtAddChild(parent, notEmptyExpr.jjtGetNumChildren());

        if(NOT_COMPLETED_CHECKLISTS.equals(attribute.getCurrentAlias()) || UNCHECKED_TASKS.equals(attribute.getCurrentAlias())){
            return inverseNodeByIds(notEmptyExpr, taggedEntity, ctx);
        }
        return notEmptyExpr;
    }

    @Override
    public SimpleNode resolveSelf(CompilationContext ctx) {
        return this;
    }

    private SimpleNode processAsAlias(SimpleNode parent, List<SimpleNode> args) {
        var other = args.subList(1, args.size());
        var parentIdx = 0;
        for (var child : other) {
            //replace first alias (tags/checklists) with cayenne path; replace join with left join to avoid interceptions
            if (child instanceof ASTObjPath) {
                child = updatePath(child);
            }
            ExpressionUtil.addChild(parent, child, parentIdx++);
        }
        return parent;
    }

    private ASTAnd buildTagChecksExpr(String entityPath, TaggableClasses taggableClasses) {
        var idx = 0;

        var and = new ASTAnd();

        // add check if tag is not null
        var notEmpty = new ASTNotEqual();
        String basePathWithJoin = entityPath + TAGGING_RELATIONS + "+." + "tag";
        notEmpty.jjtAddChild(new ASTObjPath(basePathWithJoin), 0);
        notEmpty.jjtAddChild(new ASTScalar(null), 1);
        and.jjtAddChild(notEmpty, idx++);

        // add check if tag is for this entity type
        String identPath = entityPath + TAGGING_RELATIONS + "+." + "entityIdentifier";
        ASTEqual identifierEqNode = new ASTEqual(new ASTObjPath(identPath), taggableClasses.getDatabaseValue());
        ExpressionUtil.addChild(and, identifierEqNode, idx++);

        // add check if it is tag or checklist
        String nodeTypePath = basePathWithJoin + ".nodeType";
        ASTEqual nodeTypeEqNode = new ASTEqual(new ASTObjPath(nodeTypePath), attribute.getNodeType().getDatabaseValue());
        ExpressionUtil.addChild(and, nodeTypeEqNode, idx++);

        // add check for parentTag for completed checklists
        if(TaggableAttribute.COMPLETED_CHECKLISTS.equals(attribute) || TaggableAttribute.UNCOMPLETED_CHECKLISTS.equals(attribute)) {
            String completedChecklistPath = basePathWithJoin + ".parentTag";
            ASTEqual completedChecklistNode = new ASTEqual(new ASTObjPath(completedChecklistPath), null);
            ExpressionUtil.addChild(and, completedChecklistNode, idx++);
        }

        // add check for parentTag for completed checklists
        if(TaggableAttribute.UNCHECKED_TASKS.equals(attribute)) {
            String completedChecklistPath = basePathWithJoin + ".parentTag";
            ASTNotEqual completedChecklistNode = new ASTNotEqual(new ASTObjPath(completedChecklistPath), null);
            ExpressionUtil.addChild(and, completedChecklistNode, idx);
        }

        return and;
    }

    private ASTObjPath updatePath(SimpleNode child) {
        String path = ((ASTObjPath) child).getPath();
        String newPath = path;

        if(path.contains(attribute.getCurrentAlias() + ".")){
            newPath = path.replaceFirst(attribute.getCurrentAlias() + ".", "taggingRelations+.tag+.");
        } else if(path.contains(attribute.getCurrentAlias())){
            newPath = path.replaceFirst(attribute.getCurrentAlias(), "taggingRelations+.tag");
        }

        var newObjPath = new ASTObjPath(newPath);
        newObjPath.setPathAliases(attribute.getPathAliases());
        return newObjPath;
    }
}
