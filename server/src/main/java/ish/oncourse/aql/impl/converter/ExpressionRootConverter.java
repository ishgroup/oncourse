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
import org.apache.cayenne.exp.parser.SimpleNode;

/**
 * Converter for the expression node.
 * Basically it checks if given expression is part of a path and setups context accordingly.
 *

 */
class ExpressionRootConverter<T extends AqlParser.ExpressionContext> implements Converter<T> {

    @Override
    public SimpleNode apply(T exp, CompilationContext ctx) {
        if (exp.getParent() instanceof AqlParser.PathSegmentContext) {
            return new LazyNestedExpressionNode();
        }
        return null;
    }
}
