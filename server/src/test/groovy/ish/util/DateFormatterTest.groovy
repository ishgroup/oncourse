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

import org.junit.jupiter.api.Test

import static org.junit.Assert.assertEquals

class DateFormatterTest {

	@Test
    void testFormatDate() {
		Calendar gc = Calendar.getInstance()
        gc.add(Calendar.DATE, -1)
        Date d = gc.getTime()
        assertEquals("yesterday", "yesterday", DateFormatter.formatDate(d))

        gc.set(Calendar.YEAR, 2004)
        gc.set(Calendar.MONTH, Calendar.OCTOBER)
        gc.set(Calendar.DAY_OF_MONTH, 1)
        d = gc.getTime()
        assertEquals("Fri 1 Oct 04", "Fri 1 Oct 2004", DateFormatter.formatDate(d))

        gc.set(Calendar.YEAR, 1996)
        gc.set(Calendar.MONTH, Calendar.DECEMBER)
        gc.set(Calendar.DAY_OF_MONTH, 5)
        d = gc.getTime()
        assertEquals("5/12/96", "Thu 5 Dec 1996", DateFormatter.formatDate(d))
    }

	@Test
    void parseDate() {
		Calendar gc = DateFormatter.today(TimeZone.getDefault())
        gc.set(Calendar.DAY_OF_WEEK, Calendar.WEDNESDAY)
        assertEquals("Wed", gc.getTime(), DateFormatter.parseDate("Wednesday", TimeZone.getDefault()))

        gc = DateFormatter.today(TimeZone.getDefault())
        gc.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY)
        assertEquals("Fri", gc.getTime(), DateFormatter.parseDate("Fri", TimeZone.getDefault()))

        gc = DateFormatter.today(TimeZone.getDefault())
        gc.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        assertEquals("Monday", gc.getTime(), DateFormatter.parseDate("Monday", TimeZone.getDefault()))

        gc = DateFormatter.today(TimeZone.getDefault())
        gc.set(Calendar.MONTH, Calendar.JANUARY)
        assertEquals("January", gc.getTime(), DateFormatter.parseDate("JAN", TimeZone.getDefault()))

        gc = DateFormatter.today(TimeZone.getDefault())
        gc.set(Calendar.MONTH, Calendar.FEBRUARY)
        assertEquals("February", gc.getTime(), DateFormatter.parseDate("FebRuary", TimeZone.getDefault()))

        gc = DateFormatter.today(TimeZone.getDefault())
        gc.set(Calendar.YEAR, 1995)
        assertEquals("January", gc.getTime(), DateFormatter.parseDate("1995", TimeZone.getDefault()))
    }

	@Test
    void testParseElement() {
		Calendar aDate = new GregorianCalendar(1914, Calendar.JUNE, 12)
        DateFormatter.parseElement(aDate, "jul", "", TimeZone.getDefault())
        assertEquals(aDate, new GregorianCalendar(1914, Calendar.JULY, 12))
    }

	@Test
    void testDaylightSavingsDate() {
		String dateTimeInput = "6/4/2008"
        Calendar cal = Calendar.getInstance()
        cal.setTime(DateFormatter.parseDate(dateTimeInput, TimeZone.getDefault()))
        assertEquals(dateTimeInput, 6, cal.get(Calendar.DAY_OF_MONTH))

        dateTimeInput = "7/4/2008"
        cal.setTime(DateFormatter.parseDate(dateTimeInput, TimeZone.getDefault()))
        assertEquals(dateTimeInput, 7, cal.get(Calendar.DAY_OF_MONTH))
    }
}