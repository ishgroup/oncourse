//
// TimestampUtilities.java
// ISHWebObjects
//
// Copyright (c) 2005 ISH Group Pty Ltd. All rights reserved.
//
package ish.oncourse.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;

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

	private static final List<String> _DateFormats = Arrays
			.asList("dMMyy", "dMMyyyy", "d/MM/yy", "d/MM/yyyy",
					"d-MM-yy", "d-MM-yyyy", "d.MM.yy", "d.MM.yyyy");

	private static final List<String> _TimeFormats = Arrays
			.asList("hmmaaa", "hhmmaaa", "h:mmaaa", "hh:mmaaa",
					"H:mm", "HH:mm", "Hmm", "HHmm");

	/**
	 * The days of week names. e.g., Mon, Tue, Wed and so on.
	 */
	public static List<String> DaysOfWeekAbbreviatedNames = Arrays
			.asList("Mon", "Tue", "Wed", "Thu", "Fri", "Sat",
					"Sun");

	public static List<String> DaysOfWeekAbbreviatedNamesLowerCase = Arrays
			.asList("mon", "tue", "wed", "thu", "fri", "sat",
					"sun");

	public static List<String> DaysOfWeekendAbbreviatedNames = Arrays
			.asList("Sat", "Sun");

	public static List<String> DaysOfWeekendAbbreviatedNamesLowerCase = Arrays
			.asList("sat", "sun");

	public static List<String> DaysOfWeekendNames = Arrays.asList("Saturday", "Sunday");

	public static List<String> DaysOfWeekendNamesLowerCase = Arrays
			.asList("saturday", "sunday");

	/**
	 * The days of week names. e.g., Monday, Tuesday, and so on.
	 */
	public static List<String> DaysOfWeekNames = Arrays.asList("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday",
			"Sunday");

	public static List<String> DaysOfWeekNamesLowerCase = Arrays
			.asList("monday", "tuesday", "wednesday",
					"thursday", "friday", "saturday", "sunday");

	public static List<String> DaysOfWorkingWeekAbbreviatedNames = Arrays
			.asList("Mon", "Tue", "Wed", "Thu", "Fri");

	public static List<String> DaysOfWorkingWeekAbbreviatedNamesLowerCase = Arrays
			.asList("mon", "tue", "wed", "thu", "fri");

	public static List<String> DaysOfWorkingWeekNames = Arrays
			.asList("Monday", "Tuesday", "Wednesday",
					"Thursday", "Friday");

	public static List<String> DaysOfWorkingWeekNamesLowerCase = Arrays
			.asList("monday", "tuesday", "wednesday",
					"thursday", "friday");

	static Calendar _dateForValueWithValidFormats(String value,
			List<String> validFormats, TimeZone zone)
			throws NullPointerException, ParseException {
		if (value != null) {
			ParseException aParseException = null;

			for (String validFormat : validFormats) {
				SimpleDateFormat sdf = new SimpleDateFormat(validFormat);

				sdf.setLenient(false);
				sdf.setTimeZone(zone);
				try {
					Date datetime = sdf.parse(value);
					Calendar cal = Calendar.getInstance(zone);
					cal.setTime(datetime);
					return cal;

				} catch (ParseException e) {
					aParseException = e;
				}
			}
			if (aParseException != null) {
				throw aParseException;
			}
			throw new ParseException("the date could not be matched", -1);
		}
		throw new NullPointerException("a value is required");
	}

	/**
	 * @param day
	 *            - the day of week (1 - 7)
	 * @param longName
	 *            - whether to return the long or abbreviated name of the day
	 * @return the string name of the day of week for the current week in the
	 *         default timezone
	 */
	public static String dayOfWeek(int day, boolean longName) {
		return dayOfWeek(day, longName, TimeZone.getDefault());
	}

	/**
	 * @param day
	 *            - the day of week (1 - 7)
	 * @param longName
	 *            - whether to return the long or abbreviated name of the day
	 * @param zone
	 *            - the timezone for the day or null for default
	 * @return the string name of the day of week for the current week in the
	 *         given timezone
	 */
	public static String dayOfWeek(int day, boolean longName, TimeZone zone) {
		if (day < 1 || day > 7) {
			return null;
		}
		Calendar cal = Calendar.getInstance(tz(zone));
		cal.set(Calendar.DAY_OF_WEEK, day);

		return dayOfWeek(new Date(cal.getTimeInMillis()), longName, tz(zone));
	}

	/**
	 * @param timestamp
	 *            - the timestamp
	 * @param longName
	 *            - whether to return the long or abbreviated name of the day
	 * @return the string name of the day of week for the given timestamp in the
	 *         default timezone
	 */
	public static String dayOfWeek(Date timestamp, boolean longName) {
		return dayOfWeek(timestamp, longName, null);
	}

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
	 * @param t1
	 *            - timestamp one
	 * @param t2
	 *            - timestamp two
	 * @return the number of days between the two timestamps in the default
	 *         timezone
	 * @throws NullPointerException
	 */
	public static int daysBetweenDates(Date t1, Date t2)
			throws NullPointerException {
		return daysBetweenDates(t1, t2, null);
	}

	/**
	 * @param t1
	 *            - timestamp one
	 * @param t2
	 *            - timestamp two
	 * @param zone
	 *            - the timezone to calculate in
	 * @return the number of days between the two timestamps in the given
	 *         timezone
	 * @throws NullPointerException
	 */
	public static int daysBetweenDates(Date t1, Date t2, TimeZone zone)
			throws NullPointerException {
		TimeZone tz = tz(zone);

		Calendar a = Calendar.getInstance(tz);
		Calendar b = Calendar.getInstance(tz);

		int compare = t1.compareTo(t2);

		if (compare <= 0) {
			a.setTimeInMillis(t1.getTime());
			b.setTimeInMillis(t2.getTime());
		} else {
			b.setTimeInMillis(t1.getTime());
			a.setTimeInMillis(t2.getTime());
		}

		int days = 0;
		if (a.get(Calendar.YEAR) < b.get(Calendar.YEAR)) {
			days -= a.get(Calendar.DAY_OF_YEAR);
			while (a.get(Calendar.YEAR) < b.get(Calendar.YEAR)) {
				days += a.getActualMaximum(Calendar.DAY_OF_YEAR);
				a.add(Calendar.YEAR, 1);
			}
			days += b.get(Calendar.DAY_OF_YEAR);
		} else {
			days = b.get(Calendar.DAY_OF_YEAR) - a.get(Calendar.DAY_OF_YEAR);
		}
		return days;
	}

	/**
	 * @return the days of week abbreviated names.
	 * @see TimestampUtilities#DaysOfWeekAbbreviated.
	 */
	public static List<String> daysOfWeekAbbreviatedNames() {
		return DaysOfWeekAbbreviatedNames;
	}

	/**
	 * @return the days of week unabbreviated names
	 * @see TimestampUtilities#DaysOfWeek.
	 */
	public static List<String> daysOfWeekNames() {
		return DaysOfWeekNames;
	}

	/**
	 * @param when
	 * @param zone
	 * @return plain-text interpretation of date, eg 11:15, yesterday, 3 days
	 *         ago, 6 months ago
	 */
	public static String fuzzyDate(Date when, TimeZone zone) {
		if (when == null) {
			return null;
		}

		Date now = new Date();
		Date today = normalisedDate(now, zone);
		Date dayWhen = normalisedDate(when, zone);

		if (today.equals(dayWhen)) {
			return userPresentableDate(when, "hh:mm", zone);
		}

		Calendar c = Calendar.getInstance(zone);
		c.setTime(today);
		c.add(Calendar.DAY_OF_MONTH, -1);

		Date yesterday = c.getTime();

		if (yesterday.equals(dayWhen)) {
			return "yesterday";
		}
		try {
			int months = monthsBetweenDates(when, now, zone);
			if (months > 1) {
				return months + " months ago";
			}
		} catch (Exception e) {
			return null;
		}
		try {
			int days = daysBetweenDates(when, now, zone);
			return days + " days ago";
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * @param aDate
	 *            Date
	 * @return true if the day of week in the default time zone is Saturday or
	 *         Sunday
	 */
	public static boolean isWeekend(Date aDate) {
		return isWeekend(aDate, null);
	}

	/**
	 * @param aDate
	 *            - the timestamp
	 * @param zone
	 *            - the time zone
	 * @return true if the timestamp falls on the weekend in the given timezone
	 */
	public static boolean isWeekend(Date aDate, TimeZone zone) {
		Calendar gc = Calendar.getInstance(tz(zone));
		gc.setTime(aDate);
		int day = gc.get(Calendar.DAY_OF_WEEK);
		return day == 1 || day == 7;
	}

	/**
	 * @param t1
	 *            - timestamp one
	 * @param t2
	 *            - timestamp two
	 * @param zone
	 *            - the timezone to calculate in
	 * @return the number of days between the two timestamps in the given
	 *         timezone
	 * @throws NullPointerException
	 */
	public static int monthsBetweenDates(Date t1, Date t2, TimeZone zone)
			throws NullPointerException {
		TimeZone tz = tz(zone);
		Calendar a = Calendar.getInstance(tz);
		Calendar b = Calendar.getInstance(tz);

		int compare = t1.compareTo(t2);

		if (compare <= 0) {
			a.setTimeInMillis(t1.getTime());
			b.setTimeInMillis(t2.getTime());
		} else {
			b.setTimeInMillis(t1.getTime());
			a.setTimeInMillis(t2.getTime());
		}

		int months = 0;
		if (a.get(Calendar.YEAR) < b.get(Calendar.YEAR)) {
			months -= a.get(Calendar.MONTH);
			while (a.get(Calendar.YEAR) < b.get(Calendar.YEAR)) {
				months += a.getActualMaximum(Calendar.MONTH);
				a.add(Calendar.YEAR, 1);
			}
			months += b.get(Calendar.MONTH);
		} else {
			months = b.get(Calendar.MONTH) - a.get(Calendar.MONTH);
		}
		return months;
	}

	/**
	 * @param timestamp
	 *            - the timestamp to normalise
	 * @return the timestamp at midnight in the default timezone
	 */
	public static Date normalisedDate(Date timestamp) {
		return normalisedDate(timestamp, TimeZone.getDefault());
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
		int year = result.get(Calendar.YEAR);
		int month = result.get(Calendar.MONTH);
		int date = result.get(Calendar.DATE);
		result.clear();
		result.set(year, month, date);

		return new Date(result.getTimeInMillis());
	}

	/**
	 * @param timestamp
	 *            - the timestamp to normalise
	 * @return a timestamp at GMT midnight
	 */
	public static Date normalisedDateGMT(Date timestamp) {
		return normalisedDate(timestamp, TimeZone.getTimeZone("GMT"));
	}

	/**
	 * Method for converting date and time strings into a single Date. Used in
	 * WOComponents where users need to enter a date and time which is then
	 * converted into a value that can be stored in the database.
	 * 
	 * @param dateString
	 *            String to convert into the date component
	 * @param timeString
	 *            String to convert into the time component
	 * @return timestamp representing the strings passed
	 * @throws Exception
	 *             if conversion cannot happen.
	 */
	public static Date timestampForDateAndTimeStrings(String dateString,
			String timeString) throws NullPointerException, ParseException {
		return timestampForDateAndTimeStrings(dateString, timeString, null);
	}

	/**
	 * Method for converting date and time strings into a single Date. Used in
	 * WOComponents where users need to enter a date and time which is then
	 * converted into a value that can be stored in the database.
	 * 
	 * @param dateString
	 *            - the string to convert into a date
	 * @param timeString
	 *            - the string representing the time
	 * @param zone
	 *            - the timezone to interpret the time strings
	 * @return an equivalent timestamp for the strings given
	 * @throws NullPointerException
	 * @throws ParseException
	 */
	public static Date timestampForDateAndTimeStrings(String dateString,
			String timeString, TimeZone zone) throws NullPointerException,
			ParseException {

		int year, month, day, hour, minute, seconds;
		Calendar calDate = _dateForValueWithValidFormats(dateString,
				_DateFormats, zone);
		Calendar calTime = _dateForValueWithValidFormats(timeString,
				_TimeFormats, zone);

		year = calDate.get(Calendar.YEAR);
		month = calDate.get(Calendar.MONTH) + 1; // Month = 0 for January... 11
		// for December
		day = calDate.get(Calendar.DAY_OF_MONTH);

		hour = calTime.get(Calendar.HOUR_OF_DAY);
		minute = calTime.get(Calendar.MINUTE);
		seconds = 0;

		return toDate(year, month, day, hour, minute, seconds, tz(zone));
	}

	private static Date toDate(int year, int month, int day, int hour,
			int minute, int seconds, TimeZone zone) {
		Calendar cal = Calendar.getInstance(zone);
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.MONTH, month);
		cal.set(Calendar.DAY_OF_MONTH, day);
		cal.set(Calendar.HOUR, hour);
		cal.set(Calendar.MINUTE, minute);
		cal.set(Calendar.SECOND, seconds);
		return cal.getTime();
	}

	/**
	 * Method that converts a date string into a Date. Used in WOComponents
	 * where users enter dates in a "relatively" format free fashion.
	 * 
	 * @param dateString
	 *            String to convert into a date
	 * @return timestamp representing the string passed
	 * @throws Exception
	 *             if conversion cannot happen.
	 */
	public static Date timestampForDateString(String dateString)
			throws NullPointerException, ParseException {
		return timestampForDateString(dateString, null);
	}

	/**
	 * @param dateString
	 *            - the date string to parse
	 * @param zone
	 *            - the timezone to interpret the timestamp in
	 * @return a timestamp representing the given string
	 * @throws NullPointerException
	 *             if date string is null
	 * @throws ParseException
	 */
	@SuppressWarnings("unused")
	public static Date timestampForDateString(String dateString, TimeZone zone)
			throws NullPointerException, ParseException {

		int year, month, day, hour, minute, seconds;

		Calendar gcDate = _dateForValueWithValidFormats(dateString,
				_DateFormats, zone);

		year = gcDate.get(Calendar.YEAR);
		month = gcDate.get(Calendar.MONTH) + 1; // Month = 0 for January... 11
		// for December
		day = gcDate.get(Calendar.DAY_OF_MONTH);
		hour = minute = seconds = 0;

		return gcDate.getTime();
	}

	private static TimeZone tz(String zoneID) {
		return zoneID == null ? TimeZone.getDefault() : TimeZone
				.getTimeZone(zoneID);
	}

	private static TimeZone tz(TimeZone zone) {
		return zone == null ? TimeZone.getDefault() : zone;
	}

	/**
	 * @param days
	 *            - the days to order
	 * @return the set of days in the default order
	 * @see TimestampUtilities#DaysOfWeek
	 */
	public static Set<String> uniqueDaysInOrder(List<String> days) {
		Set<String> daysInOrder = new LinkedHashSet<>(daysOfWeekNames());

		Set<String> excludedDays = new LinkedHashSet<>(daysOfWeekNames());
		excludedDays.removeAll(days);

		daysInOrder.removeAll(excludedDays);

		return daysInOrder;
	}

	/**
	 * Formats data with pattern <code>hh:mm a dd/MM/yyyy</code>
	 * 
	 * @param date
	 *            - the date to format
	 * @param timezone
	 *            - an optional timezone
	 * @return the formatted date or empty string if date is null
	 */
	public static String userPresentableDate(Date date, String timezone) {
		return userPresentableDate(date, tz(timezone));
	}

	/**
	 * @param date
	 *            - the date to format
	 * @param format
	 *            - the format string
	 * @param timezone
	 *            - the id of the timezone to format in
	 * @return the formatted date string
	 */
	public static String userPresentableDate(Date date, String format,
			String timezone) {
		return userPresentableDate(date, format, tz(timezone));
	}

	/**
	 * Formats a given date with the given pattern.
	 * 
	 * @param date
	 *            - the date to format
	 * @param format
	 *            - the simple date format string
	 * @param timezone
	 *            - the optional timezone to use
	 * @return the formatted date or empty string if date is null
	 */
	public static String userPresentableDate(Date date, String format,
			TimeZone zone) {
		if (date != null) {
			SimpleDateFormat formatter = new SimpleDateFormat(format);
			formatter.setTimeZone(tz(zone));
			return formatter.format(date);
		}
		return "";
	}

	/**
	 * Formats a given date with the the pattern <code>hh:mm a dd/MM/yyyy</code>
	 * .
	 * 
	 * @param date
	 *            - the date to format
	 * @param timezone
	 *            - the timezone to format in
	 * @return the user presentable string
	 */
	public static String userPresentableDate(Date date, TimeZone timezone) {
		return userPresentableDate(date, "hh:mm a dd/MM/yyyy", tz(timezone));
	}

	/**
	 * Calculates the number of working days between the given timestamps.
	 * 
	 * @param start
	 *            - the start timestamp
	 * @param end
	 *            - the end timestamp
	 * @return the number of working days between the given timestamps
	 * @throws NullPointerException
	 *             if either start or end are null
	 */
	public static int workingDaysBetweenDates(Date start, Date end)
			throws NullPointerException {
		return workingDaysBetweenDates(start, end, null);
	}

	/**
	 * @param start
	 *            - the start timestamp
	 * @param end
	 *            - the end timestamp
	 * @param zone
	 *            - the timezone for calculation
	 * @return the number of working days between the given timestamps
	 * @throws NullPointerException
	 *             if start or end are null
	 */
	public static int workingDaysBetweenDates(Date start, Date end,
			TimeZone zone) {

		TimeZone tz = tz(zone);
		Calendar a = Calendar.getInstance(tz);
		a.setTimeInMillis(start.getTime());
		Calendar b = Calendar.getInstance(tz);
		b.setTimeInMillis(end.getTime());

		int daysBetweenDates = daysBetweenDates(start, end);
		int startDayOfWeek = a.get(Calendar.DAY_OF_WEEK);
		int endDayOfWeek = b.get(Calendar.DAY_OF_WEEK);

		daysBetweenDates -= 7 - startDayOfWeek;
		daysBetweenDates -= endDayOfWeek - 1;
		daysBetweenDates = daysBetweenDates / 5 * 5;
		daysBetweenDates += 5 - (startDayOfWeek - 2);
		if (endDayOfWeek == Calendar.SATURDAY) {
			daysBetweenDates += 5;
		} else if (endDayOfWeek > Calendar.SUNDAY) {
			daysBetweenDates += endDayOfWeek - 2;
		}
		return daysBetweenDates;
	}

	/**
	 * @param t1
	 *            - timestamp one
	 * @param t2
	 *            - timestamp two
	 * @return the number of years between the two timestamps in the given
	 *         timezone
	 * @throws NullPointerException
	 */
	public static int yearsBetweenDates(Date t1, Date t2)
			throws NullPointerException {
		Calendar a = Calendar.getInstance();
		Calendar b = Calendar.getInstance();

		int compare = t1.compareTo(t2);

		if (compare <= 0) {
			a.setTimeInMillis(t1.getTime());
			b.setTimeInMillis(t2.getTime());
		} else {
			b.setTimeInMillis(t1.getTime());
			a.setTimeInMillis(t2.getTime());
		}

		return b.get(Calendar.YEAR) - a.get(Calendar.YEAR);
	}
}
