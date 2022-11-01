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
import ish.oncourse.aql.impl.LazyExpressionNode;
import ish.oncourse.aql.model.Entity;
import ish.util.EntityPathUtils;
import org.apache.cayenne.exp.parser.*;

import java.util.List;
import java.util.Map;

/**
 * Node, that redefines parent node of empty literal and adds comparision with empty string in expression
 * <p>
 *                      _op__(eq / not eq)          ->         ____or___             ||              ____and_
 *                      /               \                    /          \                           /        \
 *                  field               empty           _op__(eq)      __eq_               _op_(not eq)      _not eq_
 *                                                                    /     \                               /       \
 *                                                                  field    ''                          field       ''
 */
public class LazyEmptyNode extends LazyExpressionNode {

    @Override
    public SimpleNode resolveParent(SimpleNode parent, List<SimpleNode> args, CompilationContext ctx) {
        if (args.size() < 2 || args.get(args.size() - 1) != this) {
            // violation of this invariant means this code is out of sync with parser...
            ctx.reportError(-1, -1, "Invalid arguments in Lazy empty node property resolution.");
            return null;
        }

        SimpleNode pathNode = resolvePathNode(parent, args, ctx);

        if (pathNode == null)
            return parent;

        AggregateConditionNode root;
        ConditionNode astConditionNode;

        if (parent instanceof ASTEqual) {
            root = new ASTOr();
            astConditionNode = new ASTEqual();
        } else if (parent instanceof ASTNotEqual) {
            astConditionNode = new ASTNotEqual();
            root = new ASTAnd();
        } else {
            return parent;
        }

        String basePath = resolvePathFor(pathNode);
        Entity entity = EntityPathUtils.resolvePath(basePath, ctx);

        if (entity != null) {
            parent.jjtAddChild(new ASTScalar(null), parent.jjtGetNumChildren() - 1);
            return parent;
        }

        if (parent.jjtGetNumChildren() == 0)
            ExpressionUtil.addChild(parent, pathNode, 0);

        ExpressionUtil.addChild(parent, new ASTScalar(null), args.size() - 1);
        ExpressionUtil.addChild(root, parent, 0);

        customizeConditionNode(astConditionNode, pathNode);
        ExpressionUtil.addChild(root, astConditionNode, 1);
        return root;
    }

    private SimpleNode resolvePathNode(SimpleNode parent, List<SimpleNode> args, CompilationContext ctx){
        SimpleNode pathNode = null;

        int pathNodeIndex = 0;
        if (!(args.get(0) instanceof ASTObjPath || args.get(0) instanceof LazyCustomFieldNode))
            pathNodeIndex++;

        if (args.get(pathNodeIndex) instanceof LazyCustomFieldNode) {
            return null;
        }

        if (parent instanceof LazyRelationshipComparisonNode) {
            if (parent.jjtGetNumChildren() == 0) {
                parent.jjtAddChild(args.get(pathNodeIndex), 0);
            }
            parent = ((LazyRelationshipComparisonNode) parent).resolveSelf(ctx);
            pathNode = (SimpleNode) parent.jjtGetChild(0);
        }

        if (pathNode == null)
            return args.get(pathNodeIndex);

        return pathNode;
    }

    private String resolvePathFor(SimpleNode node) {
        if (!(node instanceof BasePathProvider || node instanceof ASTPath)) {
            throw new IllegalArgumentException();
        }

        String path;

        if (node instanceof BasePathProvider)
            path = ((BasePathProvider) node).resolveBasePath();
        else
            path = ((ASTPath) node).getPath();

        if(node instanceof ASTPath){
            Map<String, String> pathAliases = node.getPathAliases();
            for(var aliasEntry:pathAliases.entrySet()){
                path = path.replaceAll(aliasEntry.getKey(), aliasEntry.getValue());
            }
        }

        return path;
    }

    private void customizeConditionNode(ConditionNode baseNode, SimpleNode pathNode){
        ASTObjPath emptyExpPathNode = new ASTObjPath(((ASTObjPath) pathNode).getPath());
        emptyExpPathNode.setPathAliases(pathNode.getPathAliases());
        ExpressionUtil.addChild(baseNode, emptyExpPathNode, 0);

        ASTScalar emptyStrNode = new ASTScalar();
        emptyStrNode.setValue("");

        ExpressionUtil.addChild(baseNode, emptyStrNode, 1);
    }

    @Override
    public SimpleNode resolveSelf(CompilationContext ctx) {
        return this;
    }
}
