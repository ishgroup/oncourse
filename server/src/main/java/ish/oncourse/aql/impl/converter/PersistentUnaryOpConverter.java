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
import org.apache.cayenne.exp.parser.ASTEqual;
import org.apache.cayenne.exp.parser.ASTNamedParameter;
import org.apache.cayenne.exp.parser.SimpleNode;

/**
 * Unary operator converter for persistent data type.
 * Only one operator supported for now - 'me'.
 *
 * @since 4.2
 */
class PersistentUnaryOpConverter implements Converter<AqlParser.UnaryOperatorContext> {

    @Override
    public SimpleNode apply(AqlParser.UnaryOperatorContext op, CompilationContext ctx) {
        if("me".equals(op.getText())) {
            var equal = new ASTEqual();
            ExpressionUtil.addChild(equal, new ASTNamedParameter("me"), 1);
            return equal;
        }

        return null;
    }
}
