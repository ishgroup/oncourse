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
import ish.oncourse.aql.impl.ExpressionUtil;
import ish.oncourse.aql.impl.LazyExpressionNode;
import ish.oncourse.aql.impl.Op;
import org.apache.cayenne.exp.parser.*;

import java.util.List;

/**
 * Abstract comparison node for entities, that will use ~ for comparing
 */
abstract class LazyEntityComparisonNode extends LazyExpressionNode {

    private final Op op;

    LazyEntityComparisonNode(Op op) {
        this.op = op;
    }

    @Override
    public SimpleNode resolveParent(SimpleNode parent, List<SimpleNode> args, CompilationContext ctx) {
        // should be two args: path and string scalar
        SimpleNode node = null;
        if(validateArgs()) {
            node = createNode(ctx);
        }

        // if no parent, this node becomes one
        if(parent == null) {
            return node;
        }

        var index = args.indexOf(this);
        if(node == null) {
            args.remove(this);
        } else {
            args.set(index, node);
        }

        var i = 0;
        for(var next : args) {
            if(next instanceof LazyExpressionNode) {
                continue;
            }
            ExpressionUtil.addChild(parent, next, i++);
        }
        return parent;
    }

    @Override
    public SimpleNode resolveSelf(CompilationContext ctx) {
        return this;
    }

    protected SimpleNode createNode(CompilationContext ctx) {
        return createNode();
    }

    protected abstract SimpleNode createNode();

    private boolean validateArgs() {
        if(jjtGetNumChildren() == 1) {
            var node = jjtGetChild(0);
            if(!(node instanceof ASTScalar) && !(node instanceof LazyDateTimeScalar)) {
                return false;
            }
            jjtAddChild(node, 1);
            ExpressionUtil.addChild(this, new ASTObjPath(""), 0);
            return true;
        }

        if(jjtGetNumChildren() != 2) {
            return false;
        }

        if(!(jjtGetChild(0) instanceof ASTObjPath)) {
            return false;
        }

        return jjtGetChild(1) instanceof ASTScalar;

    }

    protected SimpleNode opToNode(Op op) {
        switch (op) {
            case EQ:
                return new ASTEqual();
            case NE:
                return new ASTNotEqual();
            case LIKE:
            case CONTAINS:
                return new ASTLikeIgnoreCase();
            case NOT_LIKE:
            case NOT_CONTAINS:
                return new ASTNotLikeIgnoreCase();
        }

        return null;
    }

    protected SimpleNode createComparisionNode(String path, String value) {
        return createComparisionNode(path, value, op);
    }

    protected SimpleNode createComparisionNode(String path, String value, Op operation) {
        if(value == null) {
            return null;
        }
        var node = opToNode(operation);
        if(node == null) {
            return null;
        }
        ExpressionUtil.addChild(node, new ASTObjPath(path), 0);
        ExpressionUtil.addChild(node, new ASTScalar(value), 1);
        return node;
    }

    public Op getOp() {
        return this.op;
    }

}
