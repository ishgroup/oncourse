//
// TimestampUtilities.java
// ISHWebObjects
//
// Copyright (c) 2005 ISH Group Pty Ltd. All rights reserved.
//
package ish.oncourse.utils;

import org.apache.commons.lang3.time.DateUtils;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * This class implements functions used to convert a date and time strings into
 * a Date.
 * 
 * @author Marek Wawrzyczny
 * @author ldeck
 * @author thomas
 * @version 1.0
 */

public class TimestampUtilities {

	/**
	 * The days of week names. e.g., Mon, Tue, Wed and so on.
	 */
	public static List<String> DaysOfWeekAbbreviatedNames = Arrays
			.asList("Mon", "Tue", "Wed", "Thu", "Fri", "Sat",
					"Sun");

	public static List<String> DaysOfWeekendNames = Arrays.asList("Saturday", "Sunday");

	public static List<String> DaysOfWeekendNamesLowerCase = Arrays
			.asList("saturday", "sunday");

	/**
	 * The days of week names. e.g., Monday, Tuesday, and so on.
	 */
	public static List<String> DaysOfWeekNames = Arrays.asList("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday",
			"Sunday");


	public static List<String> DaysOfWorkingWeekNames = Arrays
			.asList("Monday", "Tuesday", "Wednesday",
					"Thursday", "Friday");

	public static List<String> DaysOfWorkingWeekNamesLowerCase = Arrays
			.asList("monday", "tuesday", "wednesday",
					"thursday", "friday");

	/**
	 * @param timestamp
	 *            - the timestamp
	 * @param longName
	 *            - whether to return the long or abbreviated name of the day
	 * @param zone
	 *            - the timezone for the day or null for default
	 * @return the string name of the day of week for the given timestamp in the
	 *         given timezone
	 */
	public static String dayOfWeek(Date timestamp, boolean longName,
			TimeZone zone) {
		Calendar gc = Calendar.getInstance(tz(zone));
		gc.setTime(timestamp);
		SimpleDateFormat fm = new SimpleDateFormat(longName ? "EEEE" : "EEE");
		fm.setTimeZone(tz(zone));
		return fm.format(gc.getTime());
	}

	/**
	 * @param timestamp
	 *            - the timestamp to normalise
	 * @return the timestamp at midnight in the default timezone
	 */
	public static Date normalisedDate(Date timestamp) {
		return DateUtils.truncate(timestamp, Calendar.DAY_OF_MONTH);
	}

	/**
	 * @param timestamp
	 *            - the timestamp to normalise
	 * @param zone
	 *            - the timezone to normalise in
	 * @return a timestamp at midnight in the given timezone
	 */
	public static Date normalisedDate(Date timestamp, TimeZone zone) {
		Calendar result = Calendar.getInstance(tz(zone));
		result.setTimeInMillis(timestamp.getTime());
		return DateUtils.truncate(result, Calendar.DAY_OF_MONTH).getTime();
	}

	private static TimeZone tz(TimeZone zone) {
		return zone == null ? TimeZone.getDefault() : zone;
	}

	/**
	 * @param days
	 *            - the days to order
	 * @return the set of days in the default order
	 */
	public static Set<String> uniqueDaysInOrder(List<String> days) {
		Set<String> daysInOrder = new LinkedHashSet<>(DaysOfWeekNames);

		Set<String> excludedDays = new LinkedHashSet<>(DaysOfWeekNames);
		excludedDays.removeAll(days);

		daysInOrder.removeAll(excludedDays);

		return daysInOrder;
	}
}
