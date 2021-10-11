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

        SimpleNode pathNode = null;

        int pathNodeIndex = 0;
        if (!(args.get(0) instanceof ASTObjPath || args.get(0) instanceof LazyCustomFieldNode))
            pathNodeIndex++;

        if (args.get(pathNodeIndex) instanceof LazyCustomFieldNode) {
            return parent;
        }

        if (parent instanceof LazyRelationshipComparisonNode) {
            if (parent.jjtGetNumChildren() == 0) {
                parent.jjtAddChild(args.get(pathNodeIndex), 0);
            }
            parent = ((LazyRelationshipComparisonNode) parent).resolveSelf(ctx);
            pathNode = (SimpleNode) parent.jjtGetChild(0);
        }

        if (pathNode == null)
            pathNode = args.get(pathNodeIndex);

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

        ASTObjPath emptyExpPathNode = new ASTObjPath(((ASTObjPath) pathNode).getPath());
        ExpressionUtil.addChild(astConditionNode, emptyExpPathNode, 0);

        ASTScalar emptyStrNode = new ASTScalar();
        emptyStrNode.setValue("");

        ExpressionUtil.addChild(astConditionNode, emptyStrNode, 1);

        ExpressionUtil.addChild(root, astConditionNode, 1);
        return root;
    }

    private String resolvePathFor(SimpleNode node) {
        if (!(node instanceof BasePathProvider || node instanceof ASTPath)) {
            throw new IllegalArgumentException();
        }

        if (node instanceof BasePathProvider)
            return ((BasePathProvider) node).resolveBasePath();
        else
            return ((ASTPath) node).getPath();
    }

    @Override
    public SimpleNode resolveSelf(CompilationContext ctx) {
        return this;
    }
}
