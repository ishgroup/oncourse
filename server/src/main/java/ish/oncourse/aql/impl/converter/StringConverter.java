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
import ish.oncourse.aql.impl.Op;
import org.apache.cayenne.exp.parser.ASTScalar;
import org.apache.cayenne.exp.parser.SimpleNode;

/**
 * Converts string literals. Supports single and double quoted literals.
 *
 *

 */
class StringConverter implements Converter<AqlParser.StringContext> {

    @Override
    public SimpleNode apply(AqlParser.StringContext s, CompilationContext ctx) {
        var value = s.SingleQuotedStringLiteral() == null
                ? s.DoubleQuotedStringLiteral().getText()   // TODO: process escape sequences?
                : s.SingleQuotedStringLiteral().getText();
        value = value.substring(1, value.length() - 1); // trim quotes

        if (Long.class.equals(ctx.getCurrentPathJavaType())) {
            return new ASTScalar(Integer.valueOf(value));
        }
        return new LazyStringScalar(value, findParentOperator(s));
    }

    static Op findParentOperator(AqlParser.ValueContext s) {
        var parent = s.getParent();
        while(parent != null) {
            if(parent instanceof AqlParser.OperatorPredicateContext) {
                return Op.from(((AqlParser.OperatorPredicateContext) parent).operator());
            }
            parent = parent.getParent();
        }
        return Op.LIKE;
    }
}
