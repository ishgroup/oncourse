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
 * Identifier converter.
 * It is current type sensitive. E.g. 'today' will be parsed only in date type context.
 * In string type context it will be used as a string constant.
 *

 */
class IdentifierConverter implements Converter<AqlParser.IdContext> {

    private final Map<TypeClassifier, Converter<AqlParser.IdContext>> classifierMap
            = new EnumMap<>(TypeClassifier.class);

    IdentifierConverter() {
        // create all available specific converters
        classifierMap.put(TypeClassifier.DATE, new DateTimeIdentifierConverter());
        classifierMap.put(TypeClassifier.ENUM, new EnumIdentifierConverter());
        classifierMap.put(TypeClassifier.STRING, new StringIdentifierConverter());
        classifierMap.put(TypeClassifier.CUSTOM_FIELD, new StringIdentifierConverter());
        classifierMap.put(TypeClassifier.CUSTOM_FIELD_DATE, new DateIdentifierConverter());
        classifierMap.put(TypeClassifier.CUSTOM_FIELD_DATE_TIME, new DateTimeIdentifierConverter());
        classifierMap.put(TypeClassifier.CONTACT, new StringIdentifierConverter());
        classifierMap.put(TypeClassifier.SITE, new StringIdentifierConverter());
        classifierMap.put(TypeClassifier.ROOM, new StringIdentifierConverter());
        classifierMap.put(TypeClassifier.QUALIFICATION, new StringIdentifierConverter());
        classifierMap.put(TypeClassifier.MODULE, new StringIdentifierConverter());
        classifierMap.put(TypeClassifier.DOCUMENT, new StringIdentifierConverter());
        classifierMap.put(TypeClassifier.INVOICE, new StringIdentifierConverter());
        classifierMap.put(TypeClassifier.ACCOUNT, new StringIdentifierConverter());
        classifierMap.put(TypeClassifier.ACCOUNT_TRANSACTION, new StringIdentifierConverter());
        classifierMap.put(TypeClassifier.PAYSLIP, new StringIdentifierConverter());
        classifierMap.put(TypeClassifier.BANKING, new DateTimeIdentifierConverter());
        classifierMap.put(TypeClassifier.SYSTEM_USER, new SystemUserIdentifierConverter());
    }

    @Override
    public SimpleNode apply(AqlParser.IdContext id, CompilationContext ctx) {
        var identifier = id.Identifier() == null
                ? id.unaryOperator().getText()
                : id.Identifier().getText();
        var javaType = ctx.getCurrentPathJavaType();
        if (javaType == null) {
            ctx.reportError(id.start.getLine(), id.start.getCharPositionInLine(),
                    "Unexpected identifier: '" + identifier + "'");
            return null;
        }

        var classifier = TypeClassifier.of(javaType);

        var converter = classifierMap.get(classifier);
        if(converter != null) {
            return converter.apply(id, ctx);
        }

        ctx.reportError(id.start.getLine(), id.start.getCharPositionInLine(),
                "Unexpected identifier: '" + identifier + "'");
        return null;
    }

}
