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

import java.time.Duration;
import java.time.Period;

/**
 * Converter of date/time amounts
 * <p>
 * Supported units:
 * <ul>
 *    <li> seconds
 *    <li> second
 *    <li> minutes
 *    <li> minute
 *    <li> hours
 *    <li> hour
 *    <li> days
 *    <li> day
 *    <li> weeks
 *    <li> week
 *    <li> months
 *    <li> month
 *    <li> years
 *    <li> year
 * </ul>
 *

 */
class DateUnitConverter implements AmountConverter.UnitConverter {
    @Override
    public Object apply(Long value, String unit) {
        switch (unit) {
            case "seconds":
            case "second":
                return Duration.ofSeconds(value);
            case "minutes":
            case "minute":
                return Duration.ofMinutes(value);
            case "hours":
            case "hour":
                return Duration.ofHours(value);
            case "days":
            case "day":
                return Period.ofDays((int)(long)value);
            case "weeks":
            case "week":
                return Period.ofWeeks((int) (long) value);
            case "month":
            case "months":
                return Period.ofMonths((int) (long) value);
            case "year":
            case "years":
                return Period.ofYears((int) (long) value);
        }

        return null;
    }
}
