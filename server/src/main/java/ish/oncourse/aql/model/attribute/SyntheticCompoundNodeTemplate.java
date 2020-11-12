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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SyntheticCompoundNodeTemplate extends LazyExpressionNode {

    private List<Expression> expressions;
    private String attributeName;

    public SyntheticCompoundNodeTemplate(String attributeName,
                                         Expression expression1,
                                         Expression expression2,
                                         Expression... otherExpressions) {
        this.expressions = new ArrayList<>();
        this.expressions.add(expression1);
        this.expressions.add(expression2);
        this.expressions.addAll(Arrays.asList(otherExpressions));
        this.attributeName = attributeName;
    }

    @Override
    public SimpleNode resolveParent(SimpleNode parent, List<SimpleNode> args, CompilationContext ctx) {
        var other = args.subList(1, args.size());
        Expression result = null;

        for(var expression : expressions) {
            var currNode = processExpression(other, parent, expression);
            if(result == null) {
                result = currNode;
            } else {
                result = result.orExp(currNode);
            }
        }
        return (SimpleNode) result;
    }

    private SimpleNode processExpression(List<SimpleNode> other, SimpleNode parent, Expression expToPut) {
        var nodeToProcess = (SimpleNode) parent.deepCopy();
        var index = 0;
        for(var simpleNode : other) {
            if (simpleNode instanceof ASTPath) {
                ExpressionUtil
                        .addChild(nodeToProcess,
                                processPass((ASTPath) simpleNode, expToPut),
                                index++);
            } else {
                ExpressionUtil.addChild(nodeToProcess, simpleNode, index++);
            }
        }
        return nodeToProcess;
    }

    private ASTPath processPass(ASTPath prevPath, Expression expression) {
        var pathString = prevPath.getPath();
        return (ASTPath) ExpressionFactory
                .pathExp(pathString.replaceFirst(attributeName, expression.toString()));
    }

    @Override
    public SimpleNode resolveSelf(CompilationContext ctx) {
        return this;
    }
}
