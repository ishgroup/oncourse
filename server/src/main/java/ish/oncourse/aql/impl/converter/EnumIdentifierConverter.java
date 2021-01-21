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

import ish.common.util.DisplayableExtendedEnumeration;
import ish.oncourse.aql.impl.AqlParser;
import ish.oncourse.aql.impl.CompilationContext;
import org.apache.cayenne.ExtendedEnumeration;
import org.apache.cayenne.exp.parser.ASTScalar;
import org.apache.cayenne.exp.parser.SimpleNode;

/**
 * Converts identifier into enum value, enum type determined by current path.
 *

 */
class EnumIdentifierConverter implements Converter<AqlParser.IdContext> {

    @Override
    public SimpleNode apply(AqlParser.IdContext id, CompilationContext ctx) {
        var identifier = id.Identifier().getText();
        @SuppressWarnings("unchecked")
        var enumClass = (Class<? extends Enum<?>>) ctx.getCurrentPathJavaType();
        SimpleNode result = getValueForEnumClass(enumClass, identifier);
        if(result != null) {
            return result;
        }
        ctx.reportError(id.start.getLine(), id.start.getCharPositionInLine(),
                "Unknown value '" + identifier + "' for enum " + ctx.getCurrentPathJavaType().getName());
        return null;
    }

    static ASTScalar getValueForEnumClass(Class<? extends Enum<?>> enumClass, String identifier) {
        for (Enum<?> value : enumClass.getEnumConstants()) {
            if (value.name().equals(identifier)) {
                return new ASTScalar(value);
            }
            if(value instanceof ExtendedEnumeration) {
                if(((ExtendedEnumeration) value).getDatabaseValue().toString().equals(identifier)) {
                    return new ASTScalar(value);
                }
            }
            if(value instanceof DisplayableExtendedEnumeration) {
                if(((DisplayableExtendedEnumeration<?>) value).getDisplayName().equals(identifier)) {
                    return new ASTScalar(value);
                }
            }
        }

        return null;
    }
}
