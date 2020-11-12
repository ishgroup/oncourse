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

package ish.oncourse.aql.impl;

import java.time.*;

/**

 */
public class DateTimeInterval {

    private static final DayOfWeek WEEK_START = DayOfWeek.MONDAY;

    private final LocalDateTime start;
    private final LocalDateTime end;

    private DateTimeInterval(LocalDateTime start, LocalDateTime end) {
        this.start = start;
        this.end = end;
    }

    public static DateTimeInterval of(LocalDate date) {
        return new DateTimeInterval(LocalDateTime.of(date, LocalTime.MIN), LocalDateTime.of(date, LocalTime.MAX));
    }
    
    public static DateTimeInterval of(String operation) {
        var now = LocalDate.now();
        var dayStart = LocalTime.MIN;
        var dayEnd = LocalTime.MAX;
        var thisWeekStart = now.with(WEEK_START);
        var thisMonthStart = now.withDayOfMonth(1);
        var thisYearStart = thisMonthStart.withMonth(1);

        LocalDateTime start;
        LocalDateTime end;

        switch (operation) {
            case "today":
                start = LocalDateTime.of(now, dayStart);
                end = LocalDateTime.of(now, dayEnd);
                break;

            case "yesterday":
                var yesterday = now.minus(Period.ofDays(1));
                start = LocalDateTime.of(yesterday, dayStart);
                end = LocalDateTime.of(yesterday, dayEnd);
                break;

            case "tomorrow":
                var tomorrow = now.plus(Period.ofDays(1));
                start = LocalDateTime.of(tomorrow, dayStart);
                end = LocalDateTime.of(tomorrow, dayEnd);
                break;

            case "this week":
                start = LocalDateTime.of(thisWeekStart, dayStart);
                end = LocalDateTime.of(thisWeekStart.plus(Period.ofWeeks(1).minusDays(1)), dayEnd);
                break;

            case "next week":
                start = LocalDateTime.of(thisWeekStart.plus(Period.ofWeeks(1)), dayStart);
                end = LocalDateTime.of(thisWeekStart.plus(Period.ofWeeks(2).minusDays(1)), dayEnd);
                break;

            case "last week":
                start = LocalDateTime.of(thisWeekStart.minus(Period.ofWeeks(1)), dayStart);
                end = LocalDateTime.of(thisWeekStart.minus(Period.ofDays(1)), dayEnd);
                break;

            case "this month":
                start = LocalDateTime.of(thisMonthStart, dayStart);
                end = LocalDateTime.of(thisMonthStart.plus(Period.ofMonths(1).minusDays(1)), dayEnd);
                break;

            case "next month":
                start = LocalDateTime.of(thisMonthStart.plus(Period.ofMonths(1)), dayStart);
                end = LocalDateTime.of(thisMonthStart.plus(Period.ofMonths(2).minusDays(1)), dayEnd);
                break;

            case "last month":
                start = LocalDateTime.of(thisMonthStart.minus(Period.ofMonths(1)), dayStart);
                end = LocalDateTime.of(thisMonthStart.minus(Period.ofDays(1)), dayEnd);
                break;

            case "this year":
                start = LocalDateTime.of(thisYearStart, dayStart);
                end = LocalDateTime.of(thisYearStart.plus(Period.ofYears(1).minusDays(1)), dayEnd);
                break;

            case "next year":
                start = LocalDateTime.of(thisYearStart.plus(Period.ofYears(1)), dayStart);
                end = LocalDateTime.of(thisYearStart.plus(Period.ofYears(2).minusDays(1)), dayEnd);
                break;

            case "last year":
                start = LocalDateTime.of(thisYearStart.minus(Period.ofYears(1)), dayStart);
                end = LocalDateTime.of(thisYearStart.minus(Period.ofDays(1)), dayEnd);
                break;

            default:
                return null;
        }

        return new DateTimeInterval(start, end);
    }

    public LocalDateTime getStart() {
        return start;
    }

    public LocalDateTime getEnd() {
        return end;
    }
}
