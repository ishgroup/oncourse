/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.aql.impl.converter;

import ish.common.types.NodeType;
import ish.oncourse.aql.impl.CompilationContext;
import ish.oncourse.aql.impl.ExpressionUtil;
import ish.oncourse.aql.model.Entity;
import ish.oncourse.cayenne.TaggableClasses;
import ish.util.EntityPathUtils;
import ish.util.TaggableUtil;
import org.apache.cayenne.exp.Property;
import org.apache.cayenne.exp.parser.*;
import org.apache.cayenne.query.ObjectSelect;

import java.util.List;

/**
 * Node, that redefines parent node of taggingRelations predicates (e.g. taggingRelations is not null, taggingRelations is empty,
 * taggingRelations.tag.name is "Writing", taggingRelations.tag.name like "Wri", taggingRelations.tag.createdOn last year)
 * and adds entityIdentifier checking to request
 * <p>
 *                    _op__                    ->                  ________and________
 *                  /      \                                 /                   \
 *   taggingRelations       literal                       _op__           ________eq__
 *                                                                        /            \
 *                                          taggingRelations.entityIdentifier      TaggableClasses.databaseValue
 */
public class LazyTaggingRelationsNode extends LazyExprNodeWithBasePathResolver {

    private static final String TAGGING_RELATIONS = "taggingRelations";

    public LazyTaggingRelationsNode() {
    }

    @Override
    public SimpleNode resolveParent(SimpleNode parent, List<SimpleNode> args, CompilationContext ctx) {
        if (ctx.hasErrors()) {
            return null;
        }
        if(args.get(0) != this || !(args.get(1) instanceof ASTPath)) {
            ctx.reportError(-1, -1, "Invalid arguments in taggingRelations property resolution.");
        }
        String basePath = ((ASTPath) args.get(1)).getPath();
        String entityPath = basePath.substring(0, basePath.indexOf(TAGGING_RELATIONS));
        var taggedEntity = EntityPathUtils.resolvePathToTaggable(entityPath, ctx);
        if (taggedEntity == null) {
            return null;
        }
        TaggableClasses taggableClasses = TaggableUtil.resolveTaggableClass(taggedEntity);

        if (basePath.endsWith(TAGGING_RELATIONS)) {
            taggedEntity = ctx.getQueryRootEntity();
            return buildTaggingRelationsExpression(parent, args, ctx, basePath, taggableClasses, taggedEntity);
        }

        return buildTaggingRelationsExpressionWithExtraFields(parent, args, entityPath, taggableClasses);
    }

    @Override
    public SimpleNode resolveSelf(CompilationContext ctx) {
        return this;
    }

    private SimpleNode buildTaggingRelationsExpression(SimpleNode parent, List<SimpleNode> args, CompilationContext ctx, String basePath, TaggableClasses taggableClasses, Entity taggedEntity) {
        var pathExp = (ASTObjPath) args.get(1);
        if (parent instanceof LazyRelationshipComparisonNode) {
            parent = ((LazyRelationshipComparisonNode) parent).resolveSelf(pathExp, ctx);
        }

        var notEmptyExpr = new ASTAnd();

        var notEmpty = new ASTNotEqual();
        String basePathWithJoin = basePath + "+";
        notEmpty.jjtAddChild(new ASTObjPath(basePathWithJoin), 0);
        notEmpty.jjtAddChild(new ASTScalar(null), 1);
        notEmptyExpr.jjtAddChild(notEmpty, 0);

        String identPath = basePath + "+." + "entityIdentifier";
        ASTEqual identifierEqNode = new ASTEqual(new ASTObjPath(identPath), taggableClasses.getDatabaseValue());
        ExpressionUtil.addChild(notEmptyExpr, identifierEqNode, 1);

        if (parent instanceof ASTEqual) {
            List<Long> notEmptyIds = ObjectSelect.columnQuery(taggedEntity.getJavaClass(), Property.create("id", Long.class))
                    .where(notEmptyExpr)
                    .select(ctx.getContext());

            return new ASTNotIn(new ASTObjPath("id"), new ASTList(notEmptyIds));
        }

        return notEmptyExpr;
    }

    private SimpleNode buildTaggingRelationsExpressionWithExtraFields(SimpleNode parent, List<SimpleNode> args, String entityPath, TaggableClasses taggableClasses) {
        var other = args.subList(1, args.size());
        var idx = 0;
        for (var child : other) {
            //rewrite this
            if (child instanceof ASTObjPath && ((ASTObjPath) child).getPath().contains("taggingRelations.tag.")) {
                String path = ((ASTObjPath) child).getPath();
                child = new ASTObjPath(path.replace(".tag.", "+.tag+."));
            }
            ExpressionUtil.addChild(parent, child, idx++);
        }

        var and = new ASTAnd();
        and.jjtAddChild(parent, 0);

        String identPath = entityPath + TAGGING_RELATIONS + "+." + "entityIdentifier";
        ASTEqual identifierEqNode = new ASTEqual(new ASTObjPath(identPath), taggableClasses.getDatabaseValue());
        ExpressionUtil.addChild(and, identifierEqNode, 1);

        // add check if it is tag or checklist
        String nodeTypePath = entityPath + TAGGING_RELATIONS + "+." + "tag" + ".nodeType";
        ASTEqual nodeTypeEqNode = new ASTEqual(new ASTObjPath(nodeTypePath), NodeType.TAG);
        ExpressionUtil.addChild(and, nodeTypeEqNode, idx);

        return and;
    }
}
