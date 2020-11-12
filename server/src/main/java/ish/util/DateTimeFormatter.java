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

import javax.swing.text.DefaultFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 *
 */
public class DateTimeFormatter extends DefaultFormatter {

	private static final long serialVersionUID = 1L;

	private TimeZone timezone = null;

	@Deprecated
	public DateTimeFormatter() {
		this(null);
	}

	public DateTimeFormatter(TimeZone timezone) {
		super();
		setOverwriteMode(false);
		setValueClass(Date.class);
		this.timezone = timezone;
	}

	@Override
	public Object stringToValue(String inputString) {
		return parseDateTime(inputString, timezone);
	}

	@Override
	public String valueToString(Object value) {
		return formatDateTime((Date) value, timezone);
	}

	public static Date parseDateTime(String inputString, TimeZone tz) {
		if (inputString == null || inputString != null && inputString.isEmpty()) {
			return null;
		}

		inputString = inputString.trim();
		String dateString, timeString;

		int spaceIndex = inputString.lastIndexOf(" ");
		if (spaceIndex < 1) {
			dateString = inputString;
			timeString = "";
		} else {
			dateString = inputString.substring(0, spaceIndex).trim();
			timeString = inputString.substring(spaceIndex + 1).trim();
		}
		// if the time string is just "am", etc then let's grab a bit more of the string
		if (timeString.equalsIgnoreCase("am") || timeString.equalsIgnoreCase("pm") || timeString.equalsIgnoreCase("a") || timeString.equalsIgnoreCase("p")) {
			spaceIndex = inputString.lastIndexOf(" ", spaceIndex - 1);
			if (spaceIndex < 1) {
				dateString = inputString;
				timeString = "";
			} else {
				dateString = inputString.substring(0, spaceIndex).trim();
				timeString = inputString.substring(spaceIndex + 1).trim();
			}
		}

		Calendar dateResult = DateFormatter.parseDateToCal(dateString, tz);
		if (dateResult == null) {
			dateResult = DateFormatter.today(tz);
		}

		Calendar timeResult = TimeFormatter.parseTimeToCal(timeString, dateResult, tz);
		// merge the date and the time parts
		if (timeResult != null) {
			dateResult.set(Calendar.HOUR_OF_DAY, timeResult.get(Calendar.HOUR_OF_DAY));
			dateResult.set(Calendar.MINUTE, timeResult.get(Calendar.MINUTE));
		}

		return dateResult.getTime();
	}

	public static String formatDateTime(Date value, TimeZone tz) {
		return formatDateTime(value, false, tz);
	}

	public static String formatDateTime(Date value, boolean showFullAbsoluteValue, TimeZone tz) {
		StringBuilder buff = new StringBuilder();
		if (value != null) {
			String datePart = DateFormatter.formatDate(value, showFullAbsoluteValue, tz);
			if (datePart != null) {
				buff.append(datePart);
			}
			String timePart = TimeFormatter.formatTime(value, tz);
			if (timePart != null && timePart != null && !timePart.isEmpty()) {
				if (buff.length() > 0) {
					buff.append(' ');
				}
				buff.append(timePart);
			}
		}
		return buff.toString();
	}


	/**
	 * @param tz
	 * @return short name of the timezome, basically just a city or region name.
	 */
	public static String getUserReadableTimezoneName(TimeZone tz) {
		String timezoneName = tz.getID();
		return timezoneName.substring(timezoneName.lastIndexOf("/")+1)+" time";
	}

	public TimeZone getTimezone() {
		return timezone;
	}

	@Override
	public String toString() {
		return super.toString()+" tz:"+timezone;
	}
}
