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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private static final Map<String, String> aliases = new HashMap<>() {{
        put(TAGS, "taggingRelations+.tag");
        put(CHECKLISTS, "taggingRelations+.tag");
    }};

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
        String entityPath = basePath.substring(0, lastPointIndex);

        var taggedEntity = EntityPathUtils.resolvePathToTaggable(entityPath, ctx);
        if (taggedEntity == null) {
            return null;
        }

        TaggableClasses taggableClasses = TaggableUtil.resolveTaggableClass(taggedEntity);

        ASTAnd notEmptyExpr = buildTagChecksExpr(entityPath, taggableClasses);

        if (basePath.endsWith(TAGS) || basePath.endsWith(CHECKLISTS)) {
            // tags is empty || tags is null expressions
            if (parent instanceof ASTEqual) {
                List<Long> notEmptyIds = ObjectSelect.columnQuery(taggedEntity.getJavaClass(), Property.create("id", Long.class))
                        .where(notEmptyExpr)
                        .select(ctx.getContext());

                if (notEmptyIds.isEmpty())
                    return new ASTTrue();

                return new ASTNotIn(new ASTObjPath("id"), new ASTList(notEmptyIds));
            } else if (parent instanceof ASTNotEqual) {
                return notEmptyExpr;
            }
        }

        parent = processAsAlias(parent, args);
        notEmptyExpr.jjtAddChild(parent, notEmptyExpr.jjtGetNumChildren());
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
            //replace first alias (tags/checklists) with cayenne path
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
        ASTEqual nodeTypeEqNode = new ASTEqual(new ASTObjPath(nodeTypePath), nodeType.getDatabaseValue());
        ExpressionUtil.addChild(and, nodeTypeEqNode, idx);

        return and;
    }

    private ASTObjPath updatePath(SimpleNode child) {
        String path = ((ASTObjPath) child).getPath();
        String newPath = path;
        if (nodeType == NodeType.TAG && path.contains(TAGS + ".")) {
            newPath = path.replaceFirst(TAGS + ".", "taggingRelations+.tag+.");
        } else if (path.contains(CHECKLISTS + ".")) {
            newPath = path.replaceFirst(CHECKLISTS + ".", "taggingRelations+.tag+.");
        }

        var newObjPath = new ASTObjPath(newPath);
        newObjPath.setPathAliases(aliases);
        return newObjPath;
    }
}
