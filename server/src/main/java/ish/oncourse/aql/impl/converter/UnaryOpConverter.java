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

/**
 * Converts unary operators (e.g. 'today', 'me'), depends on current data type.
 *

 */
class UnaryOpConverter implements Converter<AqlParser.UnaryOperatorPredicateContext> {

    private final Map<TypeClassifier, Converter<AqlParser.UnaryOperatorContext>> converters
            = new EnumMap<>(TypeClassifier.class);

    UnaryOpConverter() {
        converters.put(TypeClassifier.DATE, new DateUnaryOpConverter());
        converters.put(TypeClassifier.PERSISTENT, new PersistentUnaryOpConverter());
        converters.put(TypeClassifier.CUSTOM_FIELD_DATE, new CustomFieldDateUnaryOpConverter());
        converters.put(TypeClassifier.CUSTOM_FIELD_DATE_TIME, new DateUnaryOpConverter());
    }

    @Override
    public SimpleNode apply(AqlParser.UnaryOperatorPredicateContext unaryOp, CompilationContext ctx) {

        var classifier = TypeClassifier.of(ctx.getCurrentPathJavaType());
        var converter = converters.get(classifier);
        var unaryOperator = unaryOp.unaryOperator();
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
