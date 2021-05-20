/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.util

import groovy.transform.CompileStatic
import org.apache.commons.lang3.time.DateUtils
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat

@CompileStatic
class DateTimeUtilTest {

	// @Test
    void testAddDaysDaylightSafe() {
		int daysToAdd = 1

        Calendar cal = createTestCalendar()
        cal.add(Calendar.DATE, daysToAdd - 1)
        Assertions.assertTrue(cal.getTime().compareTo(DateTimeUtil.addDaysDaylightSafe(createTestCalendar().getTime(), daysToAdd - 1)) == 0)

        cal = createTestCalendar()
        cal.add(Calendar.DATE, daysToAdd)
        cal.add(Calendar.HOUR_OF_DAY, 1)
        Assertions.assertTrue(cal.getTime().compareTo(DateTimeUtil.addDaysDaylightSafe(createTestCalendar().getTime(), daysToAdd)) == 0)

        cal = createTestCalendar()
        cal.add(Calendar.DATE, daysToAdd + 1)
        cal.add(Calendar.HOUR_OF_DAY, 1)
        Assertions.assertTrue(cal.getTime().compareTo(DateTimeUtil.addDaysDaylightSafe(createTestCalendar().getTime(), daysToAdd + 1)) == 0)

    }

	// @Test
    void testGetDaysLeapYearSafe() {
		Calendar c1 = createTestCalendar()
        Calendar c2 = createTestCalendar()
        Assertions.assertEquals(0, DateTimeUtil.getDaysLeapYearDaylightSafe(c1.getTime(), c2.getTime()))

        c2 = createTestCalendar()
        c2.add(Calendar.DATE, 1) // across daylight savings
		Assertions.assertEquals(1, DateTimeUtil.getDaysLeapYearDaylightSafe(c1.getTime(), c2.getTime()))

        c2 = createTestCalendar()
        c2.add(Calendar.DAY_OF_YEAR, -11)
        Assertions.assertEquals(-11, DateTimeUtil.getDaysLeapYearDaylightSafe(c1.getTime(), c2.getTime()))

        // 0-23h is the same day
		c2.set(Calendar.HOUR_OF_DAY, 0)
        for (int i = 1; i < 24; i++) {
			c2.add(Calendar.HOUR_OF_DAY, 1)
            Assertions.assertEquals(-11, DateTimeUtil.getDaysLeapYearDaylightSafe(c1.getTime(), c2.getTime()))
        }
		// 24h is a different day
		c2.add(Calendar.HOUR_OF_DAY, 1)
        Assertions.assertEquals(-10, DateTimeUtil.getDaysLeapYearDaylightSafe(c1.getTime(), c2.getTime()))

        c2 = createTestCalendar()
        // 200 * 10 days to check over at least one leap year
		for (int i = 1; i <= 200; i++) {
			c2.add(Calendar.DAY_OF_YEAR, 10)
            Assertions.assertEquals(10 * i, DateTimeUtil.getDaysLeapYearDaylightSafe(c1.getTime(), c2.getTime()))
        }

	}

	/**
	 * sydney had daylight savings change on 2nd Oct 2011, 2am->3am
	 * 
	 * @return
	 */
	private Calendar createTestCalendar() {

		TimeZone tz = TimeZone.getTimeZone("Australia/Sydney")
        Calendar cal = GregorianCalendar.getInstance(tz)
        cal.set(Calendar.MILLISECOND, 12)
        cal.set(Calendar.SECOND, 23)
        cal.set(Calendar.MINUTE, 34)
        cal.set(Calendar.HOUR_OF_DAY, 2)
        cal.set(Calendar.DATE, 1)
        cal.set(Calendar.MONTH, Calendar.OCTOBER)
        cal.set(Calendar.YEAR, 2011)

        return cal
    }

	@Test
    void testGetFirstDayOfLastMonth() {
		Calendar gc = GregorianCalendar.getInstance()

        Date testDate = DateTimeUtil.getFirstDayOfLastMonth()

        gc.add(Calendar.MONTH, -1)
        gc.set(Calendar.DAY_OF_MONTH, 1)
        Date expectedDate = DateUtils.truncate(gc, Calendar.DATE).getTime()
        Assertions.assertEquals(testDate, expectedDate)
    }

	@Test
    void testGetFirstDayOfCurrentMonth() {
		Calendar gc = GregorianCalendar.getInstance()

        Date testDate = DateTimeUtil.getFirstDayOfCurrentMonth()

        gc.set(Calendar.DAY_OF_MONTH, 1)
        gc = DateUtils.truncate(gc, Calendar.DATE)

        Date expectedDate = gc.getTime()
        Assertions.assertEquals(testDate, expectedDate)
    }

	@Test
    void testTrancateToMidnight() {
		Calendar gc = GregorianCalendar.getInstance()

        Date testDate = DateTimeUtil.trancateToMidnight(gc.getTime())
        Date expectedDate = DateUtils.truncate(gc.getTime(), Calendar.DATE)
        Assertions.assertEquals(testDate, expectedDate)
    }

	@Test
    void testGetFinancialYearStart() {
		Calendar gc = GregorianCalendar.getInstance()

        Date testDate = DateTimeUtil.getFinancialYearStart()

        if (gc.get(GregorianCalendar.MONTH) < GregorianCalendar.JULY) {
			gc.add(Calendar.YEAR , -1)
        }
		gc.set(GregorianCalendar.MONTH, GregorianCalendar.JULY)
        gc.set(GregorianCalendar.DAY_OF_MONTH, 1)

        Date expectedDate = DateUtils.truncate(gc, Calendar.DATE).getTime()
        Assertions.assertEquals(testDate, expectedDate)
    }


	@Test
    void testGetDifferenceInDays() throws ParseException {
		DateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss")
        Assertions.assertEquals(2, DateTimeUtil.getDaysLeapYearDaylightSafe(format.parse("01/01/2015 00:00:01"),format.parse("01/03/2015 23:59:59")))
        Assertions.assertEquals(-2, DateTimeUtil.getDaysLeapYearDaylightSafe( format.parse("01/03/2015 00:00:01"),format.parse("01/01/2015 23:59:59")))
        Assertions.assertEquals(0, DateTimeUtil.getDaysLeapYearDaylightSafe(format.parse("01/01/2015 00:00:01"),format.parse("01/01/2015 23:59:59")))
        Assertions.assertEquals(-4, DateTimeUtil.getDaysLeapYearDaylightSafe(format.parse("10/04/2016 00:00:01"),format.parse("09/30/2016 23:59:59")))

    }
}
