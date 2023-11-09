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

import ish.oncourse.aql.impl.CompilationContext;
import ish.oncourse.aql.impl.DateTimeInterval;
import ish.oncourse.aql.impl.ExpressionUtil;
import ish.oncourse.aql.impl.LazyExpressionNode;
import org.apache.cayenne.exp.parser.*;

import java.time.LocalDateTime;
import java.util.List;

/**

 */
public class LazyDateTimeScalar extends LazyExpressionNode {

    private final DateTimeInterval interval;

    public LazyDateTimeScalar(DateTimeInterval interval) {
        this.interval = interval;
    }

    @Override
    public SimpleNode resolveParent(SimpleNode parent, List<SimpleNode> args, CompilationContext ctx) {
        // if no parent, this node becomes one
        if(parent == null) {
            return null;
        }

        var nodeToResolve = parent;

        if(parent instanceof ASTAnd) {
            nodeToResolve = (SimpleNode) parent.jjtGetChild(1);
        }

        var resolvedNode = resolveNodeFor(nodeToResolve, args, ctx);
        if(resolvedNode != null) {
            return parent instanceof ASTAnd ? parent : resolvedNode;
        }

        var index = args.indexOf(this);
        args.set(index, new ASTScalar(interval.getStart().toLocalDate()));
        for (var i = 0; i < args.size(); i++) {
            ExpressionUtil.addChild(parent, args.get(i), i);
        }

        return parent;
    }

    private SimpleNode resolveNodeFor(SimpleNode parent, List<SimpleNode> args, CompilationContext ctx){
        if(parent instanceof ASTEqual) {
            return resolveDateForEqual(parent, args, ctx);
        } else if (parent instanceof ASTNotEqual) {
            return resolveDateForNotEqual(parent, args, ctx);
        } else if(parent instanceof ASTGreater) {
            return resolveDateForGreater(parent, args, ctx);
        } else if(parent instanceof ASTGreaterOrEqual) {
            return resolveDateForGreaterOrEqual(parent, args, ctx);
        } else if(parent instanceof ASTLess) {
            return resolveDateForLess(parent, args, ctx);
        } else if(parent instanceof ASTLessOrEqual) {
            return resolveDateForLessOrEqual(parent, args, ctx);
        } else if(parent instanceof ASTBetween) {
            return resolveDateForBetween(parent, args, ctx);
        } else if(parent instanceof LazyEntityComparisonNode) {
            return resolveDateForEntityComparision(parent, args, ctx);
        } else if((parent instanceof ConditionNode) || (parent instanceof AggregateConditionNode)) {
            return parent;
        } else {
            return null;
        }
    }

    private SimpleNode resolveDateForBetween(SimpleNode parent, List<SimpleNode> args, CompilationContext ctx) {
        var index = args.indexOf(this);
        var size = args.size();
        ExpressionUtil.addChild(parent, getPathExpression(args, ctx), 0);
        ASTScalar scalar;
        if(index == size - 1) {
            scalar = new ASTScalar(convertedDateOf(interval.getEnd()));
            ExpressionUtil.addChild(parent, scalar, 2);
        } else if(index == size - 2) {
            scalar = new ASTScalar(convertedDateOf(interval.getStart()));
            ExpressionUtil.addChild(parent, scalar, 1);
        } else {
            scalar = new ASTScalar(interval.getStart().toLocalDate());

            args.set(index, scalar);
            for(var i = 0; i<args.size(); i++) {
                ExpressionUtil.addChild(parent, args.get(i), i++);
            }
        }

        return parent;
    }

    private SimpleNode resolveDateForEqual(SimpleNode parent, List<SimpleNode> args, CompilationContext ctx) {
        var result = new ASTAnd();

        var greaterOrEqual = new ASTGreaterOrEqual();
        ExpressionUtil.addChild(greaterOrEqual, getPathExpression(args, ctx), 0);
        ExpressionUtil.addChild(greaterOrEqual, new ASTScalar(convertedDateOf(interval.getStart())), 1);

        var lessOrEqual = new ASTLessOrEqual();
        ExpressionUtil.addChild(lessOrEqual, getPathExpression(args, ctx), 0);
        ExpressionUtil.addChild(lessOrEqual, new ASTScalar(convertedDateOf(interval.getEnd())), 1);

        ExpressionUtil.addChild(result, greaterOrEqual, args.size()-2);
        ExpressionUtil.addChild(result, lessOrEqual, args.size()-1);

        if(parent.jjtGetParent() != null){
            parent.jjtGetParent().jjtAddChild(result, 1);
        }
        return result;
    }

    private SimpleNode resolveDateForNotEqual(SimpleNode parent, List<SimpleNode> args, CompilationContext ctx) {
        var result = new ASTOr();

        var greater = new ASTGreater();
        ExpressionUtil.addChild(greater, getPathExpression(args, ctx), 0);
        ExpressionUtil.addChild(greater, new ASTScalar(convertedDateOf(interval.getEnd())), 1);

        var less = new ASTLess();
        ExpressionUtil.addChild(less, getPathExpression(args, ctx), 0);
        ExpressionUtil.addChild(less, new ASTScalar(convertedDateOf(interval.getStart())), 1);

        var isNull = new ASTEqual();
        ExpressionUtil.addChild(isNull, getPathExpression(args, ctx), 0);
        ExpressionUtil.addChild(isNull, new ASTScalar(null), 1);

        ExpressionUtil.addChild(result, less, 0);
        ExpressionUtil.addChild(result, greater, 1);
        ExpressionUtil.addChild(result, isNull, 2);

        if(parent.jjtGetParent() != null){
            parent.jjtGetParent().jjtAddChild(result, 1);
        }

        return result;
    }

    private SimpleNode resolveDateForGreater(SimpleNode parent, List<SimpleNode> args, CompilationContext ctx) {
        ExpressionUtil.addChild(parent, getPathExpression(args, ctx), 0);
        ExpressionUtil.addChild(parent, new ASTScalar(convertedDateOf(interval.getEnd())), 1);
        return parent;
    }

    private SimpleNode resolveDateForGreaterOrEqual(SimpleNode parent, List<SimpleNode> args, CompilationContext ctx) {
        ExpressionUtil.addChild(parent, getPathExpression(args, ctx), 0);
        ExpressionUtil.addChild(parent, new ASTScalar(convertedDateOf(interval.getStart())), 1);
        return parent;
    }

    private SimpleNode resolveDateForLess(SimpleNode parent, List<SimpleNode> args, CompilationContext ctx) {
        ExpressionUtil.addChild(parent, getPathExpression(args, ctx), 0);
        ExpressionUtil.addChild(parent, new ASTScalar(convertedDateOf(interval.getStart())), 1);
        return parent;
    }

    private SimpleNode resolveDateForLessOrEqual(SimpleNode parent, List<SimpleNode> args, CompilationContext ctx) {
        ExpressionUtil.addChild(parent, getPathExpression(args, ctx), 0);
        ExpressionUtil.addChild(parent, new ASTScalar(convertedDateOf(interval.getEnd())), 1);
        return parent;
    }

    private SimpleNode resolveDateForEntityComparision(SimpleNode parent, List<SimpleNode> args, CompilationContext ctx) {
        var i = 0;
        for(var arg: args) {
            ExpressionUtil.addChild(parent, arg, i++);
        }
        return parent;
    }

    private SimpleNode getPathExpression(List<SimpleNode> args, CompilationContext ctx) {
        //return already built lazy custom node instead of obj path
        if(args.get(0) instanceof LazyCustomFieldNode)
            return (SimpleNode) args.get(1).jjtGetParent().jjtGetChild(0);
        var exp = args.get(0).deepCopy();
        if(exp instanceof SimpleNode) {
            return (SimpleNode)exp;
        }

        ctx.reportError(-1, -1, "Unable to resolve path expression for date comparision");
        return null;
    }

    @Override
    public SimpleNode resolveSelf(CompilationContext ctx) {
        return this;
    }

    @Override
    public boolean equals(Object object) {
        return this == object;
    }

    DateTimeInterval getInterval() {
        return interval;
    }
    
    protected Object convertedDateOf(LocalDateTime localDateTime){
        return localDateTime;
    }
}
