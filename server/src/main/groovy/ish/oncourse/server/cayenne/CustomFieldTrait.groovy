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

package ish.oncourse.server.cayenne

import ish.common.types.DataType
import static ish.common.types.DataType.BOOLEAN
import static ish.common.types.DataType.DATE
import static ish.common.types.DataType.DATE_TIME
import static ish.common.types.DataType.EMAIL
import static ish.common.types.DataType.ENTITY
import static ish.common.types.DataType.FILE
import static ish.common.types.DataType.LIST
import static ish.common.types.DataType.LONG_TEXT
import static ish.common.types.DataType.MAP
import static ish.common.types.DataType.MESSAGE_TEMPLATE
import static ish.common.types.DataType.MONEY
import static ish.common.types.DataType.PATTERN_TEXT
import static ish.common.types.DataType.TEXT


import ish.math.Money
import ish.oncourse.API
import ish.util.LocalDateUtils
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

import java.time.format.DateTimeParseException

@API
trait CustomFieldTrait {
    private static final Logger logger = LogManager.getLogger();


    abstract CustomFieldType getCustomFieldType()
    abstract String getValue()

    Object getObjectValue() {
        try {
            switch (customFieldType.dataType) {
                case LIST:
                case MAP:
                case EMAIL:
                case DataType.URL:
                case TEXT:
                case LONG_TEXT:
                case PATTERN_TEXT:
                    return value
                case DATE:
                    return LocalDateUtils.stringToValue(value)
                case DATE_TIME:
                    return LocalDateUtils.stringToTimeValue(value)
                case BOOLEAN:
                    return Boolean.valueOf(value)
                case MONEY:
                    return new Money(value)
                case ENTITY:
                case FILE:
                case MESSAGE_TEMPLATE:
                    throw new IllegalArgumentException("$ENTITY.displayName type is not supported for automation options")

            }
        } catch (DateTimeParseException e) {
            logger.error("Can not parse value $value to $customFieldType.dataType.displayName data type")
            logger.catching(e)
            return null
        }
    }

}
