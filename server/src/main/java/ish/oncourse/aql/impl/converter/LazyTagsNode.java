/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.aql.impl.converter;

import ish.common.types.NodeType;
import ish.oncourse.aql.impl.CompilationContext;
import ish.oncourse.aql.impl.ExpressionUtil;
import ish.oncourse.cayenne.TaggableClasses;
import ish.util.EntityPathUtils;
import ish.util.TaggableUtil;
import org.apache.cayenne.exp.Property;
import org.apache.cayenne.exp.parser.*;
import org.apache.cayenne.query.ObjectSelect;

import java.util.List;

/**
 * Node, that redefines parent node of tags predicates and adds entityIdentifier checking to request
 * <p>
 *                 _op__(not eq)          ->                  ________and________
 *                  /      \                                 /                   \
 *               tags        literal                       _op__           ________eq__
 *                                                                        /            \
 *                                          taggingRelations.entityIdentifier      TaggableClasses.databaseValue
 */
public class LazyTagsNode extends LazyExprNodeWithBasePathResolver {

    private final ASTObjPath objPath;
    private final NodeType nodeType;


    public LazyTagsNode(ASTObjPath objPath, NodeType nodeType) {
        this.objPath = objPath;
        this.nodeType = nodeType;
    }

    @Override
    public SimpleNode resolveParent(SimpleNode parent, List<SimpleNode> args, CompilationContext ctx) {
        if (parent instanceof LazyRelationshipComparisonNode) {
            parent = ((LazyRelationshipComparisonNode) parent).resolveSelf(objPath, ctx);
        }
        String basePath = resolveBasePath();
        String relationsPath = basePath.substring(0, basePath.lastIndexOf("."));

        var taggedEntity = EntityPathUtils.resolvePathToTaggable(basePath, ctx);
        if (taggedEntity == null) {
            return null;
        }

        TaggableClasses taggableClasses = TaggableUtil.resolveTaggableClass(taggedEntity);

        ASTAnd notEmptyExpr = buildNotEmptyExpr(relationsPath, taggableClasses);

        if (parent instanceof ASTEqual) {
            List<Long> notEmptyIds = ObjectSelect.columnQuery(taggedEntity.getJavaClass(), Property.create("id", Long.class))
                    .where(notEmptyExpr)
                    .select(ctx.getContext());

            return new ASTNotIn(new ASTObjPath("id"), new ASTList(notEmptyIds));
        }

        return notEmptyExpr;
    }

    @Override
    public SimpleNode resolveSelf(CompilationContext ctx) {
        return this;
    }

    private ASTAnd buildNotEmptyExpr(String relationsPath, TaggableClasses taggableClasses) {
        var and = new ASTAnd();

        var notEmpty = new ASTNotEqual();
        String basePathWithJoin = relationsPath + "+." + "tag";
        notEmpty.jjtAddChild(new ASTObjPath(basePathWithJoin), 0);
        notEmpty.jjtAddChild(new ASTScalar(null), 1);
        and.jjtAddChild(notEmpty, 0);

        String identPath = relationsPath + "+." + "entityIdentifier";
        ASTEqual identifierEqNode = new ASTEqual(new ASTObjPath(identPath), taggableClasses.getDatabaseValue());
        ExpressionUtil.addChild(and, identifierEqNode, 1);

        String nodeTypePath = basePathWithJoin + ".nodeType";
        ASTEqual nodeTypeEqNode = new ASTEqual(new ASTObjPath(nodeTypePath),nodeType.getDatabaseValue());
        ExpressionUtil.addChild(and, nodeTypeEqNode, 2);

        return and;
    }
}
