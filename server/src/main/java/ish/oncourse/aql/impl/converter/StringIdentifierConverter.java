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
import org.apache.cayenne.exp.parser.SimpleNode;

/**
 * Converts identifiers in string data type context.
 * Just use given identifier as a string scalar.
 *

 */
class StringIdentifierConverter implements Converter<AqlParser.IdContext> {
    @Override
    public SimpleNode apply(AqlParser.IdContext id, CompilationContext ctx) {
        return new LazyStringScalar(id.Identifier().getText(), StringConverter.findParentOperator(id));
    }
}
