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

package ish.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

/**
 * A formatter which understands a variety of time entry formats
 */
public class TimeFormatter extends DefaultFormatter {

	private static final Logger logger = LogManager.getLogger();
	private static final long serialVersionUID = 1L;

	private static SimpleDateFormat HOUR_MIN_FORMAT = new SimpleDateFormat("h:mma");
	private static SimpleDateFormat HOUR_ONLY_FORMAT = new SimpleDateFormat("ha");

	public static final Map<Integer, String> MAINTENANCE_TIMES;
	static {
		Map<Integer, String> maintenanceTimes = new LinkedHashMap<>();
		String[] ampm = new String[] { "am", "pm" };
		for (int i = 0; i < ampm.length; i++) {
			for (int j = 0; j < 12; j++) {
				// keys are minutes after midnight(database values)
				int key = i * 12 + j;
				// values are human readable
				String value = j == 0 ? "12" : (j < 10 ? " " : "") + j;
				maintenanceTimes.put(key * 60, value + ampm[i]);
			}
		}
		MAINTENANCE_TIMES = Collections.unmodifiableMap(maintenanceTimes);
	}

	private TimeZone timezone = null;

	/**
	 * Construct a new time formatter, with specific timezone
	 */
	public TimeFormatter(TimeZone timezone) {
		super();
		logger.debug("init");
		setOverwriteMode(false);

		this.timezone = timezone;
	}

	/**
	 * @param string
	 * @return the parsed value
	 * @throws ParseException
	 */
	@Override
	public Object stringToValue(String string) throws ParseException {
		return parseTime(string, timezone);
	}

	/**
	 * @param value
	 * @return a string representing the given value
	 * @throws ClassCastException if the value is not a Date or Calendar
	 */
	@Override
	public String valueToString(Object value) {
		if (value instanceof Date) {
			return formatTime((Date) value, timezone);
		} else if (value instanceof Calendar) {
			return formatTime(((Calendar) value).getTime(), ((Calendar) value).getTimeZone());
		}
		throw new ClassCastException("value is not a date or calendar");
	}

	/**
	 * @param dateToFormat
	 * @param tz timezone to format the date in
	 * @return a string representing the given date
	 */
	public static String formatTime(Date dateToFormat, TimeZone tz) {
		if (dateToFormat == null) {
			return "";
		}

		Calendar calToFormat = Calendar.getInstance();
		calToFormat.setTime(dateToFormat);
		SimpleDateFormat format = HOUR_MIN_FORMAT;

		if (tz != null) {
			calToFormat.setTimeZone(tz);
		}

		int min = calToFormat.get(Calendar.MINUTE);
		if (min == 0) {
			format = HOUR_ONLY_FORMAT;
		}

		if (tz != null) {
			format = (SimpleDateFormat) format.clone();
			format.setTimeZone(tz);
		}

		return format.format(dateToFormat).toLowerCase();
	}

	/**
	 * @param stringValue
	 * @return the parsed date
	 * @throws ParseException
	 */
	public static Date parseTime(String stringValue, TimeZone tz) throws ParseException {
		if (stringValue == null || stringValue.isEmpty()) {
			return null;
		}

		return parseTimeToCal(stringValue, tz).getTime();
	}

	/**
	 * @param stringValue
	 * @return the parsed calendar
	 * @throws ParseException
	 */
	public static Calendar parseTimeToCal(String stringValue, TimeZone tz)  {
		return parseTimeToCal(stringValue, null, tz);
	}

	/**
	 * @param stringValue the string representation of the time
	 * @param date the date on which to set the time (if null, then today is assumed)
	 * @return the time represented by this string on the date passed
	 */
	public static Calendar parseTimeToCal(String stringValue, Calendar date, TimeZone tz) {
		stringValue = stringValue.trim();
		if (stringValue == null || stringValue.isEmpty()) {
			return null;
		}

		Calendar today = (Calendar) (date == null ? Calendar.getInstance(tz) : date.clone());
		Calendar calendar = (Calendar) today.clone();
		calendar.clear(); // this will make all the fields not defined
		if (tz != null) {
		 	calendar.setTimeZone(tz);
		}
		calendar.set(Calendar.YEAR, today.get(Calendar.YEAR));
		calendar.set(Calendar.MONTH, today.get(Calendar.MONTH));
		calendar.set(Calendar.DATE, today.get(Calendar.DATE));
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);

		if (stringValue.equalsIgnoreCase("now")) {
			calendar.set(Calendar.HOUR_OF_DAY, today.get(Calendar.HOUR_OF_DAY));
			calendar.set(Calendar.MINUTE, today.get(Calendar.MINUTE));
		} else {

			Pattern p = Pattern.compile("[\\p{Punct}\\s]");
			String[] elements = p.split(stringValue);
			int hour = -1;
			boolean minuteSet = false;

			if (elements.length > 0) {

				for (String element : elements) {
					if (minuteSet) {
						break;
					}
					String anElement = element.replaceAll("[^0-9]", "");
					int parsedInt;
					if (anElement != null && !anElement.isEmpty()) {
						try {
							parsedInt = Integer.parseInt(anElement);
							if (hour > -1) {
								calendar.set(Calendar.MINUTE, parsedInt);
								minuteSet = true;
							} else {
								hour = parsedInt;
							}
						} catch (NumberFormatException e) {
							logger.warn(e);
						} // we can just ignore any parsing exceptions
					}
				}

				if (hour > 12) {
					calendar.set(Calendar.HOUR_OF_DAY, hour);

				} else if (stringValue.indexOf('p') > 0 || stringValue.indexOf('P') > 0) {
					if (hour == 12) {
						hour = 0;
					}
					calendar.set(Calendar.HOUR_OF_DAY, hour + 12);

				} else {
					if (hour == 12) {
						hour = 0;
					}
					calendar.set(Calendar.HOUR_OF_DAY, hour);

				}
			}
		}
		return calendar;
	}
}
