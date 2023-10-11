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
import ish.oncourse.aql.impl.MathOp;
import org.apache.cayenne.exp.parser.ASTCurrentTimestamp;
import org.apache.cayenne.exp.parser.ASTScalar;
import org.apache.cayenne.exp.parser.SimpleNode;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAmount;

/**
 * Evaluates date math operations. Result is a single scalar.
 *

 */
class DateMathOpConverter implements Converter<AqlParser.MathOperatorContext> {

    @Override
    public SimpleNode apply(AqlParser.MathOperatorContext op, CompilationContext ctx) {
        var value = evaluateAsDate(MathOp.from(op), op, ctx);
        ctx.pushLevel();
        if(value instanceof LocalDate) {
            var localDate = (LocalDate) value;
            return new LazyDateTimeScalar(DateTimeInterval.of(localDate));
        }

        return new ASTScalar(value);
    }

    private Temporal evaluateAsDate(MathOp op, AqlParser.MathOperatorContext mop, CompilationContext ctx) {
        var arguments = evaluateArgs(ctx);
        if(!arguments.isValid()) {
            ctx.reportError(mop.start.getLine(), mop.start.getCharPositionInLine(),
                    "Can't evaluate date expression, wrong arguments");
            return null;
        }

        switch (op) {
            case PLUS:
                return arguments.value.plus(arguments.delta);

            case MINUS:
                return arguments.value.minus(arguments.delta);

            case MOD:
            case MUL:
            case DIV:
                ctx.reportError(mop.start.getLine(), mop.start.getCharPositionInLine(),
                        "Operator not supported for Date type");
                return null;
        }

        return null;
    }

    private DateOpArguments evaluateArgs(CompilationContext ctx) {
        var arguments = ctx.popLevel();
        var result = new DateOpArguments();
        for(var node : arguments) {
            if(node instanceof ASTScalar) {
                var value = ((ASTScalar) node).getValue();
                if(value instanceof Temporal) {
                    result.value = (Temporal)value;
                } else if(value instanceof TemporalAmount) {
                    result.delta = (TemporalAmount)value;
                }
            } else if (node instanceof ASTCurrentTimestamp) {
                result.value = LocalDateTime.now();
            } else if (node instanceof LazyDateTimeScalar) {
                result.value = ((LazyDateTimeScalar)node).getInterval().getStart().toLocalDate();
            }
        }

        return result;
    }

    private static class DateOpArguments {
        Temporal value;
        TemporalAmount delta;
        boolean isValid() {
            return value != null && delta != null;
        }
    }
}
