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
import org.antlr.v4.runtime.tree.TerminalNode;
import org.apache.cayenne.exp.parser.*;

/**
 * Converts IN expressions.
 * <p>
 * Depending on arguments this can be translated into IN() or BETWEEN() operator.
 *

 */
class InPredicateConverter implements Converter<AqlParser.InPredicateContext> {

    @Override
    public SimpleNode apply(AqlParser.InPredicateContext in, CompilationContext ctx) {
        var not = in.NOT() != null;
        // complex logic for ranges, as they can be open on one side
        if(in.range() != null) {
            var rangeValuesSize = in.range().dateTermOp().size();
            if(rangeValuesSize == 0) {
                ctx.reportError(in.range().start.getLine(), in.range().start.getCharPositionInLine(),
                        "Fully open range (i.e. *..*) is not supported");
                return null;
            }

            var openLeft = false;
            var openRight = false;
            if(rangeValuesSize == 1) {
                openLeft = in.range().children.get(0) instanceof TerminalNode;
                openRight = !openLeft;
            }

            if(openLeft) {
                return not ? new ASTGreaterOrEqual() : new ASTLessOrEqual();
            }

            if(openRight) {
                return not ? new ASTLessOrEqual() : new ASTGreaterOrEqual();
            }

            return not ? new ASTNotBetween() : new ASTBetween();
        }
        return not ? new ASTNotIn() : new ASTIn();
    }
}
