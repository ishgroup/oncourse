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

package ish.oncourse.aql.impl;

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.parser.SimpleNode;

import java.util.List;

/**
 * Lazy node that is resolved only when attached to the final tree.
 * This allows to deffer actual node creation until root and siblings are fully created.
 * This node should be added to tree directly.
 *

 */
public abstract class LazyExpressionNode extends SimpleNode {

    public LazyExpressionNode() {
        super(0);
    }

    /**
     * Resolve parent. Can return parent as is or modify it.
     *
     * @param parent node that lazy node will be attached to
     * @param args siblings on this node
     * @param ctx compilation context
     * @return resolved node
     */
    public abstract SimpleNode resolveParent(SimpleNode parent, List<SimpleNode> args, CompilationContext ctx);

    /**
     * Resolve self.
     *
     * @param ctx compilation context
     * @return resolved node
     */
    public abstract SimpleNode resolveSelf(CompilationContext ctx);

    @Override
    protected String getExpressionOperator(int index) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected Object evaluateNode(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Expression shallowCopy() {
        throw new UnsupportedOperationException();
    }
}
