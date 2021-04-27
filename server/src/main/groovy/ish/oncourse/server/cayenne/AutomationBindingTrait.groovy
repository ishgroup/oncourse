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
import static ish.common.types.DataType.*
import ish.math.Money
import ish.util.LocalDateUtils
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

import java.time.format.DateTimeParseException


trait AutomationBindingTrait {

    private static final Logger logger = LogManager.logger

    abstract DataType getDataType()
    abstract String getValue()
    abstract String getName()

    abstract void setAutomation(AutomationTrait automation)

   Object getObjectValue() {
       String valueString = value
       if (valueString != null) {
           return parseValue(valueString)
       } else {
           throw new IllegalAccessException("Option binding $name doesn't have storred value. See AutomationBindingTrait.parseValue(String valueString)")
       }
   }

    Object parseValue(String valueString) {
        if (valueString == null) {
            return null
        }
        try {
            switch (dataType) {
                case FILE:
                    return valueString.getBytes()
                case OBJECT:
                case MESSAGE_TEMPLATE:
                case PATTERN_TEXT:
                case TEXT:
                    return valueString
                case DATE:
                    return LocalDateUtils.stringToValue(valueString)
                case DATE_TIME:
                    return LocalDateUtils.stringToTimeValue(valueString)
                case BOOLEAN:
                    return Boolean.valueOf(valueString)
                case MONEY:
                    return new Money(valueString)
                case ENTITY:
                case LIST:
                case MAP:
                case LONG_TEXT:
                    throw new IllegalArgumentException("$ENTITY.displayName type is not supported for aoutomation options")
            }
        } catch (DateTimeParseException e) {
            logger.error("Can not parse value  $value to $dataType.displayName data type")
            logger.catching(e)
            return null
        }
    }
}
