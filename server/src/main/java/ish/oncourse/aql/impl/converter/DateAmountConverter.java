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
import org.apache.cayenne.exp.parser.ASTScalar;
import org.apache.cayenne.exp.parser.SimpleNode;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.BiFunction;

public class DateAmountConverter implements Converter<AqlParser.DateAmountTermContext> {

    private final Map<TypeClassifier, DateAmountConverter.UnitConverter> unitConverters
            = new EnumMap<>(TypeClassifier.class);

    DateAmountConverter() {
        // create all available specific converters
        unitConverters.put(TypeClassifier.DATE, new DateTimeUnitConverter());
//        unitConverters.put(TypeClassifier.NUMERIC, new NumericUnitConverter());
    }

    @Override
    public SimpleNode apply(AqlParser.DateAmountTermContext amountContext, CompilationContext ctx) {
        var amount = amountContext.amount();
        long value = Long.parseLong(amount.IntegerLiteral().getText());
        var unit = amount.unit().getText();
        var classifier = TypeClassifier.of(ctx.getCurrentPathJavaType());

        var converter = unitConverters.get(classifier);
        if(converter != null) {
            var unitValue = converter.apply(value, unit);
            if(unitValue != null) {
                return new ASTScalar(unitValue);
            }
        }

        ctx.reportError(amount.unit().start.getLine(), amount.unit().start.getCharPositionInLine(),
                "Unknown value: " + value + " " + unit);
        return null;
    }

    interface UnitConverter extends BiFunction<Long, String, Object> {
    }
}
