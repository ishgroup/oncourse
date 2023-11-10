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
import org.apache.cayenne.exp.parser.ASTCurrentTimestamp;
import org.apache.cayenne.exp.parser.SimpleNode;

/**
 * Converts identifiers in context of date type.
 *
 * @see DateTimeInterval
 *

 */
class DateTimeIdentifierConverter implements Converter<AqlParser.IdContext> {

    @Override
    public SimpleNode apply(AqlParser.IdContext id, CompilationContext ctx) {
        var identifier = id.Identifier() == null
                ? id.unaryOperator().getText()
                : id.Identifier().getText();

        if ("now".equals(identifier)) {
            return new ASTCurrentTimestamp();
        }
        var interval = DateTimeInterval.of(identifier);
        
        if(interval == null) {
            ctx.reportError(id.start.getLine(), id.start.getCharPositionInLine(),
                    "Unknown date value '" + identifier + "'");
            return null;
        }

        return nodeOfInterval(interval);
    }

    protected SimpleNode nodeOfInterval(DateTimeInterval interval){
        return new LazyDateTimeScalar(interval);
    }


}
