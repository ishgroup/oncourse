package ish.oncourse.utils;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class DateUtils {

	// Using EN locale as constants below are hardcoded
	private static final DateTimeFormatter LONG_WEEK_DAY_FORMAT = DateTimeFormatter.ofPattern("EEEE", Locale.ENGLISH);
	private static final DateTimeFormatter SHORT_WEEK_DAY_FORMAT = DateTimeFormatter.ofPattern("EEE", Locale.ENGLISH);

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

	public static boolean isTheSameTime(Calendar cal1, Calendar cal2) {
    	if (cal1 == null || cal2 == null) {
            throw new IllegalArgumentException("The date must not be null");
        }
    	if (cal1.getTimeZone().equals(cal2.getTimeZone())) {
    		return (cal1.get(Calendar.MILLISECOND) == cal2.get(Calendar.MILLISECOND) &&
                    cal1.get(Calendar.SECOND) == cal2.get(Calendar.SECOND) &&
                    cal1.get(Calendar.MINUTE) == cal2.get(Calendar.MINUTE) &&
                    cal1.get(Calendar.HOUR) == cal2.get(Calendar.HOUR) &&
                    cal1.get(Calendar.ERA) == cal2.get(Calendar.ERA) &&
                    cal1.getClass() == cal2.getClass());
    	}
    	//return false for check in different timezones
    	return false;
    }

	public static Date startOfMonth(Date month) {
		ZonedDateTime dateTime = ZonedDateTime.ofInstant(month.toInstant(), ZoneId.systemDefault());
		return new Date(dateTime.with(LocalTime.MIN).withDayOfMonth(1).toInstant().toEpochMilli());
	}

	public static Date endOfMonth(Date month) {
		ZonedDateTime dateTime = ZonedDateTime.ofInstant(month.toInstant(), ZoneId.systemDefault());
		return new Date(dateTime.with(LocalTime.MAX).withDayOfMonth(dateTime.toLocalDate().lengthOfMonth()).toInstant().toEpochMilli());
	}

	public static boolean isCurrentMonth(Date month) {
		return LocalDate.now().getMonth()
				== ZonedDateTime.ofInstant(month.toInstant(), ZoneId.systemDefault()).getMonth();
	}

	public static Date startOfDay(Date day) {
		return startOfDay(day, TimeZone.getDefault().getID());
	}
	
	public static Date startOfDay(Date day, String timeZone) {
		ZonedDateTime dateTime = ZonedDateTime.ofInstant(day.toInstant(), ZoneId.of(timeZone));
		return new Date(dateTime.with(LocalTime.MIN).toInstant().toEpochMilli());
	}

	public static Date endOfDay(Date day) {
		return endOfDay(day, TimeZone.getDefault().getID());
	}

	public static Date endOfDay(Date day, String timeZone) {
		ZonedDateTime dateTime = ZonedDateTime.ofInstant(day.toInstant(), ZoneId.of(timeZone));
		return new Date(dateTime.with(LocalTime.MAX).toInstant().toEpochMilli());
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
	public static String dayOfWeek(Date timestamp, boolean longName, TimeZone zone) {
		ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(timestamp.toInstant(), ZoneId.of(zone.getID()));
		if(longName) {
			return LONG_WEEK_DAY_FORMAT.format(zonedDateTime);
		} else {
			return SHORT_WEEK_DAY_FORMAT.format(zonedDateTime);
		}
	}

	/**
	 * @param timestamp
	 *            - the timestamp to normalise
	 * @return the timestamp at midnight in the default timezone
	 */
	public static Date normalisedDate(Date timestamp) {
		ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(timestamp.toInstant(), ZoneId.systemDefault());
		return new Date(zonedDateTime.with(LocalTime.MIN).toInstant().toEpochMilli());
	}

	/**
	 * @param timestamp
	 *            - the timestamp to normalise
	 * @param zone
	 *            - the timezone to normalise in
	 * @return a timestamp at midnight in the given timezone
	 */
	public static Date normalisedDate(Date timestamp, TimeZone zone) {
		ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(timestamp.toInstant(), ZoneId.of(zone.getID()));
		return new Date(zonedDateTime.with(LocalTime.MIN).toInstant().toEpochMilli());
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
