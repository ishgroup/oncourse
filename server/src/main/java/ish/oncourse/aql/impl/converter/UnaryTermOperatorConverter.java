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

public class UnaryTermOperatorConverter implements Converter<AqlParser.UnaryTermContext> {

    private final Map<TypeClassifier, Converter<AqlParser.UnaryTermContext>> converters
            = new EnumMap<>(TypeClassifier.class);

    UnaryTermOperatorConverter() {
        converters.put(TypeClassifier.DATE, new DateUnaryTermConverter());
        converters.put(TypeClassifier.CUSTOM_FIELD_DATE, new DateUnaryTermConverter());
        converters.put(TypeClassifier.CUSTOM_FIELD_DATE_TIME, new DateUnaryTermConverter());
    }

    @Override
    public SimpleNode apply(AqlParser.UnaryTermContext unaryOperator, CompilationContext ctx) {

        var classifier = TypeClassifier.of(ctx.getCurrentPathJavaType());
        var converter = converters.get(classifier);
        SimpleNode result = null;
        if(converter != null) {
            result = converter.apply(unaryOperator, ctx);
        }

        if(result == null) {
            ctx.reportError(unaryOperator.start.getLine(), unaryOperator.start.getCharPositionInLine(),
                    "Can't use operator '" + unaryOperator.getText()
                            + "' for " + classifier.getReadableName() + " type");
        }
        return result;
    }
}
