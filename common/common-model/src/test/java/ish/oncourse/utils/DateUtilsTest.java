package ish.oncourse.utils;

import static org.junit.Assert.*;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.junit.Test;

public class DateUtilsTest {
	
	@Test
	public void test() {
		TimeZone timeZone = TimeZone.getTimeZone("GMT");
		Calendar start = Calendar.getInstance(timeZone), start2 = Calendar.getInstance(timeZone);
		Date now = new Date();
		start.setTime(now);
		start2.setTime(now);
		start2.add(Calendar.DAY_OF_MONTH, 1);
		assertTrue("The day should be different but the hours-minutes should be the same", DateUtils.isTheSameTime(start, start2));
		assertFalse("But the global time should be different", org.apache.commons.lang.time.DateUtils.isSameLocalTime(start, start2));
		start2.setTimeZone(TimeZone.getTimeZone("Etc/GMT-0"));
		assertFalse("Should return false for different timezones", DateUtils.isTheSameTime(start, start2));
	}
}
