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
import org.apache.cayenne.exp.parser.*;

import java.util.List;

public class LazyEmptyNode extends LazyExpressionNode {

    @Override
    public SimpleNode resolveParent(SimpleNode parent, List<SimpleNode> args, CompilationContext ctx) {
        if(parent == null || args.size()<2 || args.get(1) != this) {
            // violation of this invariant means this code is out of sync with parser...
            ctx.reportError(-1, -1, "Invalid arguments in Lazy empty node property resolution.");
            return null;
        }

        AggregateConditionNode root;
        ConditionNode astConditionNode;

        if(parent instanceof ASTEqual) {
            root = new ASTOr();
            astConditionNode = new ASTEqual();
        }else if(parent instanceof ASTNotEqual) {
            astConditionNode = new ASTNotEqual();
            root = new ASTAnd();
        }else {
            ctx.reportError(-1, -1, "Invalid parent node Lazy empty node property resolution.");
            return null;
        }

        ExpressionUtil.addChild(parent, args.get(0),0);
        ExpressionUtil.addChild(parent, new ASTScalar(null),1);
        ExpressionUtil.addChild(root, parent, 0);


        ExpressionUtil.addChild(astConditionNode,args.get(0),0);

        ASTScalar emptyStrNode = new ASTScalar();
        emptyStrNode.setValue("");

        ExpressionUtil.addChild(astConditionNode, emptyStrNode,1);

        ExpressionUtil.addChild(root, astConditionNode, 1);

        return root;
    }

    @Override
    public SimpleNode resolveSelf(CompilationContext ctx) {
        return this;
    }
}
