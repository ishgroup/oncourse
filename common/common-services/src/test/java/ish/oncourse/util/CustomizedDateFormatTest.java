package ish.oncourse.util;

import static org.junit.Assert.*;

import java.text.Format;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;

public class CustomizedDateFormatTest {
	
	@Test
	public void test() {
		//check for positive timezone
		TimeZone timeZone = TimeZone.getTimeZone("Australia/West");
		testForTimeZone(timeZone);
		//check for negative timezone
		timeZone = TimeZone.getTimeZone("Pacific/Tahiti");
		testForTimeZone(timeZone);
		//check the GMT
		timeZone = TimeZone.getTimeZone("GMT");
		testForTimeZone(timeZone);
	}
	
	private void testForTimeZone(TimeZone timeZone) {
		Format format = new CustomizedDateFormat(FormatUtils.shortDateFormatString, timeZone);
		Calendar calendar = Calendar.getInstance(timeZone);
		calendar.set(Calendar.MINUTE, 9);
		calendar.set(Calendar.HOUR, 1);
		Date date = calendar.getTime();
		//check short date format
		assertEquals("Result should be the same for the short date format", 
			FormatUtils.getDateFormat(FormatUtils.shortDateFormatString, timeZone).format(date), format.format(date));
		//check short time format
		format = new CustomizedDateFormat(FormatUtils.shortTimeFormatString, timeZone);
		assertEquals("Result should be the same but lowercased for the short time format if the minutes isn't 0 and pm time", 
			FormatUtils.getDateFormat(FormatUtils.shortTimeFormatString, timeZone).format(date).toLowerCase(), format.format(date));
		calendar.set(Calendar.HOUR, 13);
		date = calendar.getTime();
		assertEquals("Result should be the same but lowercased for the short time format if the minutes isn't 0 and am time", 
			FormatUtils.getDateFormat(FormatUtils.shortTimeFormatString, timeZone).format(date).toLowerCase(), format.format(date));
		//check when the time is 1:00
		calendar.set(Calendar.HOUR, 1);
		calendar.set(Calendar.MINUTE, 0);
		date = calendar.getTime();
		assertEquals("Result should not be the same but lowercased for the short time format if the minutes is 0 and pm time", 
			FormatUtils.getDateFormat(FormatUtils.shortTimeFormatString, timeZone).format(date).toLowerCase().replace(":00", StringUtils.EMPTY), 
			format.format(date));
		calendar.set(Calendar.HOUR, 13);
		date = calendar.getTime();
		assertEquals("Result should not be the same but lowercased for the short time format if the minutes is 0 and am time", 
			FormatUtils.getDateFormat(FormatUtils.shortTimeFormatString, timeZone).format(date).toLowerCase().replace(":00", StringUtils.EMPTY), 
			format.format(date));
		//check format with the timezone string
		//check the pm
		calendar = Calendar.getInstance(timeZone);
		calendar.set(Calendar.MINUTE, 9);
		calendar.set(Calendar.HOUR, 1);
		date = calendar.getTime();
		format = new CustomizedDateFormat(FormatUtils.timeFormatWithTimeZoneString, timeZone);
		int totalMinutesOffset = timeZone.getRawOffset()/60000;
		boolean isPositiveOffset = totalMinutesOffset>=0;
		if (!isPositiveOffset) {
			totalMinutesOffset = 0-totalMinutesOffset;
		}
		Integer hoursOffset = totalMinutesOffset/60;
		Integer minutesOffset = totalMinutesOffset%60;
		String hoursOffsetString = hoursOffset < 10 ? "0" + hoursOffset.toString() : hoursOffset.toString(),
			minutesOffsetString = minutesOffset < 10 ? "0" + minutesOffset.toString() : minutesOffset.toString();
		String expectedResult = String.format("1:09%s (UTC%s%s:%s)", FormatUtils.getDateFormat("a", timeZone).format(date).toLowerCase(), 
			(isPositiveOffset? "+": "-"), hoursOffsetString, minutesOffsetString);
		assertEquals("Result should match the formater", expectedResult, format.format(date));
		//check the am
		calendar.set(Calendar.HOUR, 13);
		date = calendar.getTime();
		expectedResult = String.format("1:09%s (UTC%s%s:%s)", FormatUtils.getDateFormat("a", timeZone).format(date).toLowerCase(), (isPositiveOffset? "+": "-"), 
			hoursOffsetString, minutesOffsetString);
		assertEquals("Result should match the formater", expectedResult, format.format(date));
		//check the 0 minutes
		calendar.set(Calendar.HOUR, 1);
		calendar.set(Calendar.MINUTE, 0);
		date = calendar.getTime();
		expectedResult = String.format("1%s (UTC%s%s:%s)", FormatUtils.getDateFormat("a", timeZone).format(date).toLowerCase(), (isPositiveOffset? "+": "-"), 
			hoursOffsetString, minutesOffsetString);
		assertEquals("Result should match the formater", expectedResult, format.format(date));
		calendar.set(Calendar.HOUR, 13);
		date = calendar.getTime();
		expectedResult = String.format("1%s (UTC%s%s:%s)", FormatUtils.getDateFormat("a", timeZone).format(date).toLowerCase(), (isPositiveOffset? "+": "-"), 
			hoursOffsetString, minutesOffsetString);
		assertEquals("Result should match the formater", expectedResult, format.format(date));
		System.out.println(expectedResult);
	}
}
