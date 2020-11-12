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
import ish.oncourse.aql.impl.MathOp;
import org.apache.cayenne.exp.parser.*;

/**
 * Converts math operators for numeric types.
 *

 */
class NumericMathOpConverter implements Converter<AqlParser.MathOperatorContext> {

    @Override
    public SimpleNode apply(AqlParser.MathOperatorContext op, CompilationContext ctx) {
        var mathOp = MathOp.from(op);
        switch (mathOp) {
            case PLUS:
                return new ASTAdd();
            case MINUS:
                return new ASTSubtract();
            case MUL:
                return new ASTMultiply();
            case DIV:
                return new ASTDivide();
            case MOD: {
                // no default constructor, construct by hand
                var nodes = ctx.popLevel();
                ctx.pushLevel();
                return new ASTMod(nodes.get(0), nodes.get(1));
            }
        }

        throw new IllegalArgumentException("Unsupported math operation " + op);
    }
}
