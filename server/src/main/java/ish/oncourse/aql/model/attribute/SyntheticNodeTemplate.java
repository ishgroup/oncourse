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

package ish.oncourse.aql.model.attribute;

import ish.oncourse.aql.impl.CompilationContext;
import ish.oncourse.aql.impl.ExpressionUtil;
import ish.oncourse.aql.impl.LazyExpressionNode;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.exp.parser.ASTPath;
import org.apache.cayenne.exp.parser.SimpleNode;

import java.util.List;

/*
 * Type of lazy expression node to swap path(long path or
 * path with outer joins) to UNIQUE synthetic attribute.
 * Supports synthetic attributes comparison with date.
 */
public class SyntheticNodeTemplate extends LazyExpressionNode {

    private Expression expression;
    private String pathToReplace;

    private Expression expressionPath;

    public SyntheticNodeTemplate(Expression expression, String pathToReplace) {
        this.expression = expression;
        this.pathToReplace = pathToReplace;
    }

    @Override
    public SimpleNode resolveParent(SimpleNode parent, List<SimpleNode> args, CompilationContext ctx) {
        var other = args.subList(1, args.size());
        return processPass(parent, other);
    }

    private SimpleNode processPass(SimpleNode parent, List<SimpleNode> other) {
        var index = 0;
        for (var simpleNode : other) {
            if (simpleNode instanceof ASTPath) {
                ExpressionUtil.addChild(parent, processPass((ASTPath) simpleNode), index++);
            } else {
                ExpressionUtil.addChild(parent, simpleNode, index++);
            }
        }

        // this was done to save path for doing shallow copy later in expressions with dates.
        if (parent.getOperandCount() >= 2 &&
                parent.getOperand(0) instanceof ASTPath &&
                !(parent.getOperand(1) instanceof ASTPath)) {
            expressionPath = (Expression) parent.getOperand(0);
        }

        return parent;
    }

    private ASTPath processPass(ASTPath prevPath) {
        var pathString = prevPath.getPath();
        var resPath = (ASTPath) ExpressionFactory
                .pathExp(pathString.replaceAll(pathToReplace, expression.toString()));
        resPath.setPathAliases(expression.getPathAliases());
        return resPath;
    }

    @Override
    public SimpleNode resolveSelf(CompilationContext ctx) {
        return this;
    }

    @Override
    public Expression shallowCopy() {
        if (expressionPath != null) {
            return expressionPath.shallowCopy();
        }
        return super.shallowCopy();
    }
}
