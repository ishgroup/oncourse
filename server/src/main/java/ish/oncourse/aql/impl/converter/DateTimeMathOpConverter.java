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
import ish.oncourse.aql.impl.TypeClassifier;
import org.apache.cayenne.exp.parser.SimpleNode;

import java.util.EnumMap;
import java.util.Map;

public class DateTimeMathOpConverter implements Converter<AqlParser.DateMathOpContext> {

    private final Map<TypeClassifier, Converter<AqlParser.DateMathOpContext>> converters
            = new EnumMap<>(TypeClassifier.class);

    DateTimeMathOpConverter() {
        // Supported converters per data type
        converters.put(TypeClassifier.DATE, new TimeMathOpConverter());
    }

    @Override
    public SimpleNode apply(AqlParser.DateMathOpContext mop, CompilationContext ctx) {
        var classifier = TypeClassifier.of(ctx.getCurrentPathJavaType());
        var converter = converters.get(classifier);
        if(converter == null) {
            ctx.reportError(mop.start.getLine(), mop.start.getCharPositionInLine(),
                    "Unsupported operator for " + classifier.getReadableName() + " type");
            return null;
        }
        return converter.apply(mop, ctx);
    }
}
