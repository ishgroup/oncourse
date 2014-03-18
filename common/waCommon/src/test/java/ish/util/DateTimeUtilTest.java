package ish.util;

import org.apache.commons.lang3.time.DateUtils;
import org.junit.Test;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class DateTimeUtilTest {

	// @Test
	public void testAddDaysDaylightSafe() {
		int daysToAdd = 1;

		Calendar cal = createTestCalendar();
		cal.add(Calendar.DATE, daysToAdd - 1);
		assertTrue(cal.getTime().compareTo(DateTimeUtil.addDaysDaylightSafe(createTestCalendar().getTime(), daysToAdd - 1)) == 0);

		cal = createTestCalendar();
		cal.add(Calendar.DATE, daysToAdd);
		cal.add(Calendar.HOUR_OF_DAY, 1);
		assertTrue(cal.getTime().compareTo(DateTimeUtil.addDaysDaylightSafe(createTestCalendar().getTime(), daysToAdd)) == 0);

		cal = createTestCalendar();
		cal.add(Calendar.DATE, daysToAdd + 1);
		cal.add(Calendar.HOUR_OF_DAY, 1);
		assertTrue(cal.getTime().compareTo(DateTimeUtil.addDaysDaylightSafe(createTestCalendar().getTime(), daysToAdd + 1)) == 0);

	}

	// @Test
	public void testGetDaysLeapYearSafe() {
		Calendar c1 = createTestCalendar();
		Calendar c2 = createTestCalendar();
		assertEquals(0, DateTimeUtil.getDaysLeapYearDaylightSafe(c1.getTime(), c2.getTime()));

		c2 = createTestCalendar();
		c2.add(Calendar.DATE, 1); // across daylight savings
		assertEquals(1, DateTimeUtil.getDaysLeapYearDaylightSafe(c1.getTime(), c2.getTime()));

		c2 = createTestCalendar();
		c2.add(Calendar.DAY_OF_YEAR, -11);
		assertEquals(-11, DateTimeUtil.getDaysLeapYearDaylightSafe(c1.getTime(), c2.getTime()));

		// 0-23h is the same day
		c2.set(Calendar.HOUR_OF_DAY, 0);
		for (int i = 1; i < 24; i++) {
			c2.add(Calendar.HOUR_OF_DAY, 1);
			assertEquals(-11, DateTimeUtil.getDaysLeapYearDaylightSafe(c1.getTime(), c2.getTime()));
		}
		// 24h is a different day
		c2.add(Calendar.HOUR_OF_DAY, 1);
		assertEquals(-10, DateTimeUtil.getDaysLeapYearDaylightSafe(c1.getTime(), c2.getTime()));

		c2 = createTestCalendar();
		// 200 * 10 days to check over at least one leap year
		for (int i = 1; i <= 200; i++) {
			c2.add(Calendar.DAY_OF_YEAR, 10);
			assertEquals(10 * i, DateTimeUtil.getDaysLeapYearDaylightSafe(c1.getTime(), c2.getTime()));
		}

	}

	/**
	 * sydney had daylight savings change on 2nd Oct 2011, 2am->3am
	 * 
	 * @return
	 */
	private Calendar createTestCalendar() {

		TimeZone tz = TimeZone.getTimeZone("Australia/Sydney");
		Calendar cal = GregorianCalendar.getInstance(tz);
		cal.set(Calendar.MILLISECOND, 12);
		cal.set(Calendar.SECOND, 23);
		cal.set(Calendar.MINUTE, 34);
		cal.set(Calendar.HOUR_OF_DAY, 2);
		cal.set(Calendar.DATE, 1);
		cal.set(Calendar.MONTH, Calendar.OCTOBER);
		cal.set(Calendar.YEAR, 2011);

		return cal;
	}

	@Test
	public void testGetFirstDayOfLastMonth() {
		Calendar gc = GregorianCalendar.getInstance();

		Date testDate = DateTimeUtil.getFirstDayOfLastMonth();

		gc.add(Calendar.MONTH, -1);
		gc.set(Calendar.DAY_OF_MONTH, 1);
		Date expectedDate = DateUtils.truncate(gc, Calendar.DATE).getTime();
		assertEquals(testDate, expectedDate);
	}

	@Test
	public void testGetFirstDayOfCurrentMonth() {
		Calendar gc = GregorianCalendar.getInstance();

		Date testDate = DateTimeUtil.getFirstDayOfCurrentMonth();

		gc.set(Calendar.DAY_OF_MONTH, 1);
		gc = DateUtils.truncate(gc, Calendar.DATE);

		Date expectedDate = gc.getTime();
		assertEquals(testDate, expectedDate);
	}

	@Test
	public void testTrancateToMidnight() {
		Calendar gc = GregorianCalendar.getInstance();

		Date testDate = DateTimeUtil.trancateToMidnight(gc.getTime());
		Date expectedDate = DateUtils.truncate(gc.getTime(), Calendar.DATE);
		assertEquals(testDate, expectedDate);
	}

	@Test
	public void testGetFinancialYearStart() {
		Calendar gc = GregorianCalendar.getInstance();

		Date testDate = DateTimeUtil.getFinancialYearStart();

		if (gc.get(GregorianCalendar.MONTH) < GregorianCalendar.JULY) {
			gc.add(Calendar.YEAR , -1);
		}
		gc.set(GregorianCalendar.MONTH, GregorianCalendar.JULY);
		gc.set(GregorianCalendar.DAY_OF_MONTH, 1);

		Date expectedDate = DateUtils.truncate(gc, Calendar.DATE).getTime();
		assertEquals(testDate, expectedDate);
	}
}
