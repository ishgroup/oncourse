/*
 * Copyright ish group pty ltd 2020.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 */

package ish.oncourse.aql.impl.converter;

import ish.oncourse.aql.impl.AqlParser;
import ish.oncourse.aql.impl.CompilationContext;
import ish.oncourse.aql.model.CustomFieldMarker;
import ish.oncourse.aql.model.Entity;
import ish.oncourse.aql.model.SyntheticAttributeDescriptor;
import org.antlr.v4.runtime.ParserRuleContext;
import org.apache.cayenne.exp.parser.ASTObjPath;
import org.apache.cayenne.exp.parser.SimpleNode;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Converts path expression into Cayenne ObjPath.
 * <p>
 * Also this class resolves current data type and stores it into context.
 * {@link Entity} used a model for resolving path segments.
 * Custom fields are translated here too.
 *

 */
class PathConverter implements Converter<AqlParser.PathContext> {

    @Override
    public SimpleNode apply(AqlParser.PathContext pathCtx, CompilationContext ctx) {
        var pathSegmentContexts = pathCtx.pathSegment();
        var pathSegments = pathSegmentContexts.stream()
                .map(s -> s.Identifier().getText())
                .collect(Collectors.toList());

        // We start resolving from last segment in current path we need to evaluate,
        // and go all the way up to root from there recursively.
        var lastSegment = pathSegmentContexts.isEmpty()
                ? null
                : pathSegmentContexts.get(pathSegmentContexts.size() - 1);

        // root will be pointing to Entity we actually should resolve our path against
        var root = resolveParentContext(pathCtx, lastSegment,
                ResolveResult.success(ctx.getQueryRootEntity(), ""));

        if(root.isError()) {
            var start = pathCtx.getStart();
            ctx.reportError(start.getLine(), start.getCharPositionInLine(), root.getError());
            return null;
        }

        var last = pathCtx.Identifier().getText();
        var entity = root.getEntity();
        var attributeType = entity.getAttribute(last);
        var relationshipType = entity.getRelationship(last);
        var syntheticAttribute = entity.getSyntheticAttribute(last);

        if(syntheticAttribute.isPresent()) {
            var synthAttributeType = syntheticAttribute.flatMap(SyntheticAttributeDescriptor::getAttributeType)
                    .orElse(CustomFieldMarker.class);
            @SuppressWarnings("unchecked")
            Class<?> pathType = syntheticAttribute.flatMap(SyntheticAttributeDescriptor::nextEntity)
                    .map(Entity::getJavaClass)
                    .orElse((Class)synthAttributeType);
            ctx.setCurrentPathJavaType(pathType);
        } else if(attributeType.isPresent()) {
            ctx.setCurrentPathJavaType(attributeType.get());
            // custom field, translate path
            if(CustomFieldMarker.class == attributeType.get()) {
                return translateCustomField(pathSegments, last);
            }
        } else if(relationshipType.isPresent()) {
            relationshipType.map(Entity::getJavaClass).ifPresent(ctx::setCurrentPathJavaType);
        } else {
            var symbol = pathCtx.Identifier().getSymbol();
            ctx.reportError(symbol.getLine(), symbol.getCharPositionInLine(),
                    "Can't resolve attribute '" + last + "' "
                            + "for entity '" + entity.getJavaClass().getSimpleName() + "'");
            return null;
        }

        var objPath = new ASTObjPath(root.getPath() + last);
        if(root.getSyntheticAttribute() != null) {
            ctx.addNode(root.getSyntheticAttribute().spawnNode());
            ctx.addNode(objPath);
            return null;
        }

        if(syntheticAttribute.isPresent()) {
            ctx.addNode(syntheticAttribute.get().spawnNode());
            ctx.addNode(objPath);
            if(pathCtx.getText().equals("tags") || last.equals("tags"))
                return new LazyTagsNode(objPath);
            return null;
        }

        return objPath;
    }

    private AqlParser.PathSegmentContext findParentSegment(ParserRuleContext rule) {
        var parent = rule.getParent();
        while(parent != null) {
            if(parent instanceof AqlParser.PathSegmentContext) {
                return (AqlParser.PathSegmentContext)parent;
            }
            parent = parent.getParent();
        }
        return null;
    }

    private ResolveResult resolveParentContext(ParserRuleContext pathCtx,
                                               AqlParser.PathSegmentContext currentSegment,
                                               ResolveResult resolveResult) {
        if(resolveResult.isError()) {
            return resolveResult;
        }

        var parentSegment = findParentSegment(pathCtx);
        if(parentSegment != null) {
            // nested path need additional resolve all the way up
            resolveResult = resolveParentContext(parentSegment.getParent(), parentSegment, resolveResult);
            if(resolveResult.isError()) {
                return resolveResult;
            }
        }

        var allSegments = pathCtx
                .getRuleContexts(AqlParser.PathSegmentContext.class);

        // build and validate cayenne path
        var path = new StringBuilder(resolveResult.getPath());
        Optional<SyntheticAttributeDescriptor> syntheticAttribute = Optional.empty();
        var entity = resolveResult.getEntity();
        for (var segment : allSegments) {
            var identifier = segment.Identifier().getText();
            Optional<Entity> next = Optional.empty();
            if (entity.getSyntheticAttribute(identifier).isPresent()) {
                syntheticAttribute = entity.getSyntheticAttribute(identifier);
                if(syntheticAttribute.isPresent()) {
                    next = syntheticAttribute.get().nextEntity();
                }
            }
            if (next.isEmpty()) {
                next = entity.getRelationship(identifier);
            }
            if (next.isEmpty()) {
                return ResolveResult.error("Can't resolve path segment: " + identifier);
            }
            path.append(identifier);
//            if (entity.getSyntheticAttribute(identifier).isEmpty()) {
//                path.append('+');
//            }
            path.append('.');
            entity = next.get();
            if (segment == currentSegment) {
                break;
            }
        }

        return ResolveResult.success(entity,  path.toString(), syntheticAttribute.orElse(null));
    }

    private SimpleNode translateCustomField(List<String> pathSegments, String last) {
        var path = String.join(".", pathSegments);
        if(!Objects.equals(path, "")) {
            path += '.';
        }
        return new LazyCustomFieldNode(path, last);
    }

    private static class ResolveResult {
        private final String path;
        private final Entity entity;
        private final String error;
        private final SyntheticAttributeDescriptor syntheticAttribute;

        static ResolveResult success(Entity entity, String path) {
            return new ResolveResult(entity, path, null, null);
        }

        static ResolveResult success(Entity entity, String path, SyntheticAttributeDescriptor syntheticAttribute) {
            return new ResolveResult(entity, path, syntheticAttribute, null);
        }

        static ResolveResult error(String error) {
            return new ResolveResult(null, null, null, error);
        }

        private ResolveResult(Entity entity, String path, SyntheticAttributeDescriptor syntheticAttribute, String error) {
            this.entity = entity;
            this.path = path;
            this.error = error;
            this.syntheticAttribute = syntheticAttribute;
        }

        Entity getEntity() {
            return entity;
        }

        String getPath() {
            return path;
        }

        SyntheticAttributeDescriptor getSyntheticAttribute() {
            return syntheticAttribute;
        }

        String getError() {
            return error;
        }

        boolean isError() {
            return error != null;
        }
    }
}
