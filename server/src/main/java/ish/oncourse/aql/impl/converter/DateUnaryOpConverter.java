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
import ish.oncourse.aql.impl.DateTimeInterval;
import ish.oncourse.aql.impl.ExpressionUtil;
import org.apache.cayenne.exp.parser.ASTBetween;
import org.apache.cayenne.exp.parser.ASTScalar;
import org.apache.cayenne.exp.parser.SimpleNode;

import java.time.LocalDateTime;

/**
 * Date/time unary operators converter.
 *
 * @see DateTimeInterval
 *

 */
class DateUnaryOpConverter implements Converter<AqlParser.UnaryOperatorContext> {

    @Override
    public SimpleNode apply(AqlParser.UnaryOperatorContext op, CompilationContext ctx) {
        var interval = DateTimeInterval.of(op.getText());
        if(interval == null) {
            ctx.reportError(op.start.getLine(), op.start.getCharPositionInLine(),
                    "Unknown date operation " + op.getText());
            return null;
        }
        var between = new ASTBetween();
        ExpressionUtil.addChild(between, new ASTScalar(scalarOf(interval.getStart())), 1);
        ExpressionUtil.addChild(between, new ASTScalar(scalarOf(interval.getEnd())), 2);
        return between;
    }

    protected Object scalarOf(LocalDateTime localDateTime){
        return localDateTime;
    }
}
