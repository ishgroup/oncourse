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

public class DateTimeLiteralConverter implements Converter<AqlParser.DateTimeLiteralContext> {

    // 30/11/2014
    private static final DateTimeFormatter MAIN_DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/uuuu");
    // 2014-11-30
    private static final DateTimeFormatter ISO_DATE_FORMATTER = DateTimeFormatter.ofPattern("uuuu-MM-dd");
    // 10:03 am
    private static final DateTimeFormatter TIME12_FORMATTER = DateTimeFormatter.ofPattern("hh:mm a");
    // 23:40
    private static final DateTimeFormatter TIME24_FORMATTER = DateTimeFormatter.ofPattern("kk:mm");

    @Override
    public SimpleNode apply(AqlParser.DateTimeLiteralContext dt, CompilationContext ctx) {
        var date = parseDate(dt.dateLiteral());
        var time = parseTime(dt.timeLiteral());

        if(date != null && time != null) {
            return new ASTScalar(LocalDateTime.of(date, time));
        }

        if(date != null) {
            return new LazyDateScalar(DateTimeInterval.of(date));
        }

        if(time != null) {
            return new ASTScalar(time);
        }

        ctx.reportError(dt.start.getLine(), dt.start.getCharPositionInLine(),
                "Unknown format of date/time: " + dt.getText());
        return null;
    }

    private LocalDate parseDate(AqlParser.DateLiteralContext dateLiteral) {
        if(dateLiteral == null) {
            return null;
        }

        if(dateLiteral.MainDateFormat() != null) {
            var dateText = normalizeDateString(dateLiteral.MainDateFormat().getText());
            return LocalDate.parse(dateText, MAIN_DATE_FORMATTER);
        } else if(dateLiteral.IsoDateFormat() != null) {
            return LocalDate.parse(dateLiteral.IsoDateFormat().getText(), ISO_DATE_FORMATTER);
        }

        return null;
    }

    private LocalTime parseTime(AqlParser.TimeLiteralContext timeLiteral) {
        if(timeLiteral == null) {
            return null;
        }

        if(timeLiteral.Time12() != null) {
            var timeString = normalize12HourTimeString(timeLiteral.Time12().getText());
            return LocalTime.parse(timeString, TIME12_FORMATTER);
        } else if(timeLiteral.Time24() != null) {
            return LocalTime.parse(timeLiteral.Time24().getText(), TIME24_FORMATTER);
        }

        return null;
    }

    /**
     * Parser supports more formats of date, so we normalize string a little bit.
     *
     * @param dateString string with date to normalize
     * @return normalized string
     */
    private String normalizeDateString(String dateString) {
        // 3/10/2014 -> 03/10/2014
        // 3/1/2014 -> 03/01/2014
        String[] splitDate = dateString.split("/");
        if (splitDate[0].length() == 2 && splitDate[1].length() == 2 && splitDate[2].length() == 4) {
            return dateString;
        }

        StringBuilder result = new StringBuilder();

        if (splitDate[0].length() < 2) {
            result.append("0");
        }
        result.append(splitDate[0]);
        result.append("/");

        if(splitDate[1].length() < 2) {
            result.append("0");
        }
        result.append(splitDate[1]);
        result.append("/");

        result.append(splitDate[2]);

        return result.toString();
    }

    /**
     * Parser supports several variants for am/pm notation, so we should cleanup it a little bit
     * to create LocalTime from this string.
     *
     * @param timeString string with time to normalize
     * @return normalized time string
     */
    private String normalize12HourTimeString(String timeString) {
        // 1. 3:00a.m.  -> 03:00a.m.
        if(timeString.charAt(1) == ':') {
            timeString = '0' + timeString;
        }
        // 2. 03:00A.M. -> 03:00a.m.
        timeString = timeString.toLowerCase();
        // 3. 03:00a.m. -> 03:00am
        timeString = timeString.replaceAll("\\.", "");
        // 4. 03:00am   -> 03:00 am
        if(!timeString.contains(" ")) {
            timeString = timeString.replace("am", " am");
            timeString = timeString.replace("pm", " pm");
        }
        return timeString;
    }
}
