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

package ish.util

import groovy.transform.CompileStatic
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

@CompileStatic
class DateFormatterTest {

    @Test
    void testFormatDate() {
        Calendar gc = Calendar.getInstance()
        gc.add(Calendar.DATE, -1)
        Date d = gc.getTime()
        Assertions.assertEquals("yesterday", DateFormatter.formatDate(d), "yesterday")

        gc.set(Calendar.YEAR, 2004)
        gc.set(Calendar.MONTH, Calendar.OCTOBER)
        gc.set(Calendar.DAY_OF_MONTH, 1)
        d = gc.getTime()
        Assertions.assertEquals("Fri 1 Oct 2004", DateFormatter.formatDate(d), "Fri 1 Oct 04")

        gc.set(Calendar.YEAR, 1996)
        gc.set(Calendar.MONTH, Calendar.DECEMBER)
        gc.set(Calendar.DAY_OF_MONTH, 5)
        d = gc.getTime()
        Assertions.assertEquals("Thu 5 Dec 1996", DateFormatter.formatDate(d), "5/12/96")
    }

    @Test
    void parseDate() {
        Calendar gc = DateFormatter.today(TimeZone.getDefault())
        gc.set(Calendar.DAY_OF_WEEK, Calendar.WEDNESDAY)
        Assertions.assertEquals(gc.getTime(), DateFormatter.parseDate("Wednesday", TimeZone.getDefault()), "Wed")

        gc = DateFormatter.today(TimeZone.getDefault())
        gc.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY)
        Assertions.assertEquals(gc.getTime(), DateFormatter.parseDate("Fri", TimeZone.getDefault()), "Fri")

        gc = DateFormatter.today(TimeZone.getDefault())
        gc.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        Assertions.assertEquals(gc.getTime(), DateFormatter.parseDate("Monday", TimeZone.getDefault()), "Monday")

        gc = DateFormatter.today(TimeZone.getDefault())
        gc.set(Calendar.MONTH, Calendar.JANUARY)
        Assertions.assertEquals(gc.getTime(), DateFormatter.parseDate("JAN", TimeZone.getDefault()), "January")

        gc = DateFormatter.today(TimeZone.getDefault())
        gc.set(Calendar.MONTH, Calendar.FEBRUARY)
        Assertions.assertEquals(gc.getTime(), DateFormatter.parseDate("FebRuary", TimeZone.getDefault()), "February")

        gc = DateFormatter.today(TimeZone.getDefault())
        gc.set(Calendar.YEAR, 1995)
        Assertions.assertEquals(gc.getTime(), DateFormatter.parseDate("1995", TimeZone.getDefault()), "January")
    }

    @Test
    void testParseElement() {
        Calendar aDate = new GregorianCalendar(1914, Calendar.JUNE, 12)
        DateFormatter.parseElement(aDate, "jul", "", TimeZone.getDefault())
        Assertions.assertEquals(aDate, new GregorianCalendar(1914, Calendar.JULY, 12))
    }

    @Test
    void testDaylightSavingsDate() {
        String dateTimeInput = "6/4/2008"
        Calendar cal = Calendar.getInstance()
        cal.setTime(DateFormatter.parseDate(dateTimeInput, TimeZone.getDefault()))
        Assertions.assertEquals(6, cal.get(Calendar.DAY_OF_MONTH), dateTimeInput)

        dateTimeInput = "7/4/2008"
        cal.setTime(DateFormatter.parseDate(dateTimeInput, TimeZone.getDefault()))
        Assertions.assertEquals(7, cal.get(Calendar.DAY_OF_MONTH), dateTimeInput)
    }
}