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
import org.apache.cayenne.exp.parser.ASTAnd;
import org.apache.cayenne.exp.parser.SimpleNode;

import java.util.Collections;
import java.util.List;

/**
 * Node describing expression nested in path: path{exp} or path{exp}.attr
 *

 */
public class LazyNestedExpressionNode extends LazyExpressionNode {

    private SimpleNode parentPath;

    @Override
    public SimpleNode resolveParent(SimpleNode parent, List<SimpleNode> args, CompilationContext ctx) {
        // we can't resolve with only parent, so it's two stage resolve
        if(parentPath == null) {
            // we need to add children to parent, if we inside path segment predicate, or they will be lost
            if(parent instanceof PathSegmentPredicateConverter.PathSegmentPredicateLazyNode) {
                var i = 0;
                for(var node : args) {
                    if(node instanceof LazyNestedExpressionNode) {
                        node = (SimpleNode) node.jjtGetChild(0);
                    }
                    ExpressionUtil.addChild(parent, node, i++);
                }
                return parent;
            }
            // replace parent with this node, to be able to get higher in the tree
            parentPath = parent;
            return this;
        }

        // update parent's content
        for(var i = 0; i<args.size(); i++) {
            var arg = args.get(i);
            if(arg == this) {
                var child = resolveParentPath();
                ExpressionUtil.addChild(parent, child, i);
            } else {
                ExpressionUtil.addChild(parent, arg, i);
            }
        }

        var parentOverride = parent;
        if(parentPath instanceof LazyExpressionNode) {
            var lazyParent = (LazyExpressionNode)parentPath;
            parentOverride = lazyParent.resolveParent(parent, Collections.emptyList(), ctx);
        }

        // generate final tree elements
        if(parentOverride == null) {
            return (SimpleNode) jjtGetChild(0);
        }
        var and = new ASTAnd();
        ExpressionUtil.addChild(and, parentOverride, 0);
        ExpressionUtil.addChild(and, (SimpleNode) jjtGetChild(0), 1);
        return and;
    }

    private SimpleNode resolveParentPath() {
        if(parentPath instanceof LazyNestedExpressionNode) {
            return ((LazyNestedExpressionNode) parentPath).resolveParentPath();
        }
        return parentPath;
    }

    @Override
    public SimpleNode resolveSelf(CompilationContext ctx) {
        return this;
    }
}
