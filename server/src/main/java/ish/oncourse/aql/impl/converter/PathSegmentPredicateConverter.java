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

import ish.oncourse.aql.impl.AqlParser;
import ish.oncourse.aql.impl.CompilationContext;
import ish.oncourse.aql.impl.ExpressionUtil;
import ish.oncourse.aql.impl.LazyExpressionNode;
import org.apache.cayenne.exp.parser.ASTAnd;
import org.apache.cayenne.exp.parser.SimpleNode;

import java.util.List;

/**

 */
class PathSegmentPredicateConverter implements Converter<AqlParser.PathSegmentPredicateContext> {

    @Override
    public SimpleNode apply(AqlParser.PathSegmentPredicateContext pathSegmentPredicate, CompilationContext ctx) {
        return new PathSegmentPredicateLazyNode();
    }

    static class PathSegmentPredicateLazyNode extends LazyExpressionNode {

        @Override
        public SimpleNode resolveParent(SimpleNode parent, List<SimpleNode> args, CompilationContext ctx) {
            return parent;
        }

        @Override
        public SimpleNode resolveSelf(CompilationContext ctx) {
            SimpleNode result = this;
            if (jjtGetNumChildren() > 0) {
                if(jjtGetNumChildren() > 1) {
                    result = new ASTAnd();
                    for(var i = 0; i<jjtGetNumChildren(); i++) {
                        ExpressionUtil.addChild(result, (SimpleNode) jjtGetChild(i), i);
                    }
                } else {
                    result = (SimpleNode) jjtGetChild(0);
                    if (result instanceof LazyExpressionNode) {
                        result = this;
                    }
                }
            }
            return result;
        }
    }
}
