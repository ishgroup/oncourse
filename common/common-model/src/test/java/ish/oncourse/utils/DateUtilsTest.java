package ish.oncourse.utils;

import org.junit.Assert;
import org.junit.Test;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class DateUtilsTest {
	
	@Test
	public void testIsTheSameTime() {
		TimeZone timeZone = TimeZone.getTimeZone("GMT");
		Calendar start = Calendar.getInstance(timeZone), start2 = Calendar.getInstance(timeZone);
		Date now = new Date();
		start.setTime(now);
		start2.setTime(now);
		start2.add(Calendar.DAY_OF_MONTH, 1);
		assertTrue("The day should be different but the hours-minutes should be the same", DateUtils.isTheSameTime(start, start2));
		assertFalse("But the global time should be different", org.apache.commons.lang3.time.DateUtils.isSameLocalTime(start, start2));
		start2.setTimeZone(TimeZone.getTimeZone("Etc/GMT-0"));
		assertFalse("Should return false for different timezones", DateUtils.isTheSameTime(start, start2));
	}

	@Test
	public void testStartOfDay() {
		Calendar calendarExpected = Calendar.getInstance(TimeZone.getTimeZone("GMT+0"));
		calendarExpected.set(2000, Calendar.JANUARY, 2, 0, 0, 0);
		calendarExpected.set(Calendar.MILLISECOND, 0);

		Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+0"));
		calendar.set(2000, Calendar.JANUARY, 2, 3, 4, 5);
		Date date = calendar.getTime();

		assertEquals(calendarExpected.getTime(), DateUtils.startOfDay(date, "GMT+0"));
	}

	@Test
	public void testEndOfDay() {
		Calendar calendarExpected = Calendar.getInstance(TimeZone.getTimeZone("GMT+0"));
		calendarExpected.set(2000, Calendar.JANUARY, 2, 23, 59, 59);
		calendarExpected.set(Calendar.MILLISECOND, 999);

		Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+0"));
		calendar.set(2000, Calendar.JANUARY, 2, 3, 4, 5);
		Date date = calendar.getTime();

		assertEquals(calendarExpected.getTime(), DateUtils.endOfDay(date, "GMT+0"));
	}

	@Test
	public void testStartOfMonth() {
		Calendar calendarExpected = Calendar.getInstance();
		calendarExpected.set(2000, Calendar.JANUARY, 1, 0, 0, 0);
		calendarExpected.set(Calendar.MILLISECOND, 0);

		Calendar calendar = Calendar.getInstance();
		calendar.set(2000, Calendar.JANUARY, 2, 3, 4, 5);
		Date date = calendar.getTime();

		assertEquals(calendarExpected.getTime(), DateUtils.startOfMonth(date));
	}

	@Test
	public void testEndOfMonth() {
		Calendar calendarExpected = Calendar.getInstance();
		calendarExpected.set(2000, Calendar.JANUARY, 31, 23, 59, 59);
		calendarExpected.set(Calendar.MILLISECOND, 999);

		Calendar calendar = Calendar.getInstance();
		calendar.set(2000, Calendar.JANUARY, 2, 3, 4, 5);
		Date date = calendar.getTime();

		assertEquals(calendarExpected.getTime(), DateUtils.endOfMonth(date));
	}

	@Test
	public void testIsCurrentMonth() {
		assertTrue(DateUtils.isCurrentMonth(new Date()));
		assertFalse(DateUtils.isCurrentMonth(new Date(ZonedDateTime.now().minusMonths(1).toInstant().toEpochMilli())));
	}

	@Test
	public void testNormalisedDate() {
		Date timestamp = new Date();
		Date date = DateUtils.normalisedDate(timestamp);
		Assert.assertEquals(date, org.apache.commons.lang3.time.DateUtils.truncate(timestamp, Calendar.DAY_OF_MONTH));
		Assert.assertEquals(0, org.apache.commons.lang3.time.DateUtils.getFragmentInHours(date, Calendar.HOUR_OF_DAY));
		Assert.assertEquals(0, org.apache.commons.lang3.time.DateUtils.getFragmentInHours(date, Calendar.MINUTE));
		Assert.assertEquals(0, org.apache.commons.lang3.time.DateUtils.getFragmentInHours(date, Calendar.SECOND));
		Assert.assertEquals(0, org.apache.commons.lang3.time.DateUtils.getFragmentInHours(date, Calendar.MILLISECOND));
	}

	@Test
	public void testUniqueDaysInOrder() {
		{
			List<String> days = Arrays.asList("Wednesday", "Monday");
			Set<String> result = DateUtils.uniqueDaysInOrder(days);
			Set<String> expected = new LinkedHashSet<>(Arrays.asList("Monday", "Wednesday"));
			assertEquals(expected, result);
		}

		{
			List<String> days = Arrays.asList("Monday", "Wednesday", "Monday", "Wednesday", "Monday");
			Set<String> result = DateUtils.uniqueDaysInOrder(days);
			Set<String> expected = new LinkedHashSet<>(Arrays.asList("Monday", "Wednesday"));
			assertEquals(expected, result);
		}

		{
			List<String> days = Collections.emptyList();
			Set<String> result = DateUtils.uniqueDaysInOrder(days);
			Set<String> expected = new LinkedHashSet<>(Collections.emptyList());
			assertEquals(expected, result);
		}

		{
			List<String> days = Arrays.asList("Friday", "Wednesday", "Monday", "Tuesday", "Sunday");
			Set<String> result = DateUtils.uniqueDaysInOrder(days);
			Set<String> expected = new LinkedHashSet<>(Arrays.asList("Monday", "Tuesday", "Wednesday", "Friday", "Sunday"));
			assertEquals(expected, result);
		}
	}

}
