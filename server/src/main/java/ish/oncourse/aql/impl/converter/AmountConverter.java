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
import org.antlr.v4.runtime.RuleContext;
import org.apache.cayenne.exp.parser.ASTScalar;
import org.apache.cayenne.exp.parser.SimpleNode;

import java.time.Duration;
import java.time.Period;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

/**
 * Converts amount rule (e.g. 1 week) into scalar.
 * Has specialization for each supported type.
 *

 */
class AmountConverter implements Converter<AqlParser.AmountContext> {

    private final Map<TypeClassifier, UnitConverter> unitConverters
            = new EnumMap<>(TypeClassifier.class);

    AmountConverter() {
        // create all available specific converters
        unitConverters.put(TypeClassifier.DATE, new DateUnitConverter());
        unitConverters.put(TypeClassifier.NUMERIC, new NumericUnitConverter());
    }

    @Override
    public SimpleNode apply(AqlParser.AmountContext amount, CompilationContext ctx) {

        var values = amount.IntegerLiteral().stream()
                .map(literal -> Long.parseLong(literal.getText()))
                .collect(Collectors.toList());
        var units = amount.unit().stream().map(RuleContext::getText).collect(Collectors.toList());

        SimpleNode value = buildNode(values,units,ctx);

        if (value == null) {
            ctx.reportError(amount.unit().get(0).start.getLine(), amount.unit().get(0).start.getCharPositionInLine(),
                    "Unknown value: " + values + " " + units);
            return null;
        }
        return value;
    }

    private SimpleNode buildNode(List<Long> values, List<String> units, CompilationContext ctx) {
        var classifier = TypeClassifier.of(ctx.getCurrentPathJavaType());
        var converter = unitConverters.get(classifier);

        if(converter != null) {

            Duration durationUnitValue = Duration.ZERO;
            Period periodUnitValue = Period.ZERO;
            for (int i = 0; i < values.size(); i++) {
                try {
                    Object value = converter.apply(values.get(i), units.get(i));

                    if (value == null) {
                        return null;
                    }

                    if (value instanceof Duration) {
                        durationUnitValue = durationUnitValue.plus((Duration) value);
                    } else {
                        periodUnitValue = periodUnitValue.plus((Period) value);
                    }

                } catch (IndexOutOfBoundsException e) {
                    return null;
                }
            }

            if (durationUnitValue != Duration.ZERO ^ periodUnitValue != Period.ZERO) {
                if (durationUnitValue != Duration.ZERO) {
                    return new ASTScalar(durationUnitValue);
                } else {
                    return new ASTScalar(periodUnitValue);
                }
            } else {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < values.size(); i++) {
                    sb.append(values.get(i)).append(" ").append(units.get(i)).append(" ");
                }
                sb.trimToSize();
                throw new IllegalArgumentException("Time period " + sb + " not supported");
            }
        }
        return null;
    }

    interface UnitConverter extends BiFunction<Long, String, Object> {

    }

}
