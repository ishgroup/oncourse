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
 *                 _op__(not eq)          ->                  _____________________and______________________________
 *                  /      \                                 /                   \                                  \
 *               tags        literal                       _op__           ________eq__                              ________eq_____________________
 *                                                                        /            \                             \                              \
 *                                          taggingRelations.entityIdentifier      TaggableClasses.databaseValue      taggingRelations.tag.nodeType  nodeType
 */
public class LazyTagsNode extends LazyExprNodeWithBasePathResolver {
    private final NodeType nodeType;


    public LazyTagsNode(NodeType nodeType) {
        this.nodeType = nodeType;
    }

    @Override
    public SimpleNode resolveParent(SimpleNode parent, List<SimpleNode> args, CompilationContext ctx) {
        var astObjPath = (ASTObjPath) args.get(1);
        if (parent instanceof LazyRelationshipComparisonNode) {
            parent = ((LazyRelationshipComparisonNode) parent).resolveSelf(astObjPath, ctx);
        }
        String basePath = astObjPath.getPath();
        var lastPointIndex = basePath.lastIndexOf("tags");
        String relationsPath = basePath.substring(0, lastPointIndex);

        var taggedEntity = EntityPathUtils.resolvePathToTaggable(relationsPath, ctx);
        if (taggedEntity == null) {
            return null;
        }

        TaggableClasses taggableClasses = TaggableUtil.resolveTaggableClass(taggedEntity);

        ASTAnd notEmptyExpr = buildNotEmptyExpr(parent, relationsPath, args, taggableClasses);

        if (parent instanceof ASTEqual && (basePath.endsWith(TAGS) || basePath.endsWith(CHECKLISTS))) {
            List<Long> notEmptyIds = ObjectSelect.columnQuery(taggedEntity.getJavaClass(), Property.create("id", Long.class))
                    .where(notEmptyExpr)
                    .select(ctx.getContext());

            if(notEmptyIds.isEmpty())
                return new ASTTrue();

            return new ASTNotIn(new ASTObjPath("id"), new ASTList(notEmptyIds));
        }

        var other = args.subList(1, args.size());
        var parentIdx = 0;
        for (var child : other) {
            //rewrite this
            if (child instanceof ASTObjPath) {
                child = updatePath(child);
            }
            ExpressionUtil.addChild(parent, child, parentIdx++);
        }

        notEmptyExpr.jjtAddChild(parent, notEmptyExpr.jjtGetNumChildren());
        return notEmptyExpr;
    }

    @Override
    public SimpleNode resolveSelf(CompilationContext ctx) {
        return this;
    }

    private ASTAnd buildNotEmptyExpr(SimpleNode parent, String relationsPath, List<SimpleNode> args, TaggableClasses taggableClasses) {
        var idx = 0;

        var and = new ASTAnd();
       // and.jjtAddChild(parent, idx++);
        var notEmpty = new ASTNotEqual();
        String basePathWithJoin = relationsPath + TAGGING_RELATIONS + "+." + "tag";
        notEmpty.jjtAddChild(new ASTObjPath(basePathWithJoin), 0);
        notEmpty.jjtAddChild(new ASTScalar(null), 1);
        and.jjtAddChild(notEmpty, idx++);



        String identPath = relationsPath + TAGGING_RELATIONS + "+." + "entityIdentifier";
        ASTEqual identifierEqNode = new ASTEqual(new ASTObjPath(identPath), taggableClasses.getDatabaseValue());
        ExpressionUtil.addChild(and, identifierEqNode, idx++);

        String nodeTypePath = basePathWithJoin + ".nodeType";
        ASTEqual nodeTypeEqNode = new ASTEqual(new ASTObjPath(nodeTypePath), nodeType.getDatabaseValue());
        ExpressionUtil.addChild(and, nodeTypeEqNode, idx);

        return and;
    }

    private ASTObjPath updatePath(SimpleNode child) {
        String path = ((ASTObjPath) child).getPath();
        String newPath = path;
        if (nodeType == NodeType.TAG && path.contains(TAGS + ".")) {
            newPath = path.replace(TAGS + ".", "taggingRelations+.tag+.");
        } else if (path.contains(CHECKLISTS + ".")) {
            newPath = path.replace(CHECKLISTS + ".", "taggingRelations+.tag+.");
        }

        return new ASTObjPath(newPath);
    }
}
