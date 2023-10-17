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
import org.apache.cayenne.exp.parser.ASTScalar;
import org.apache.cayenne.exp.parser.SimpleNode;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Parses date/time values.
 *

 */
class CustomFieldDateTimeConverter extends DateTimeIdentifierConverter {

    @Override
    protected SimpleNode nodeOfInterval(DateTimeInterval interval) {
        return new LazyCustomDateTimeScalar(interval);
    }
}
