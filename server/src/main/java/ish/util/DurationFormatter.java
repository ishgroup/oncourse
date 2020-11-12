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

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.text.DefaultFormatter;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Date;
import java.util.regex.Pattern;

/**
 * performs translations between duration represented as 'Long milliseconds' and human readable Strings.
 */
public class DurationFormatter extends DefaultFormatter {
	private static final Logger logger = LogManager.getLogger();
	private static final long serialVersionUID = 1L;
	public static final String FORMAT_DD_HH_MM = "d'd' H'h' m'min'";
	public static final String FORMAT_HH_MM = "H'h' m'min'";
	public static final String FORMAT_MM = "m'min'";

	public static final long MILLISECOND = 1l;
	public static final long SECOND = 1000l;
	public static final long MINUTE = SECOND * 60l;
	public static final long HOUR = MINUTE * 60l;
	public static final long DAY = HOUR * 24l;
	private String displayFormat = FORMAT_HH_MM;
	private long unitsAdjustment = MILLISECOND;

	private static final BigDecimal _24 = new BigDecimal("24");
	private static final BigDecimal _60 = new BigDecimal("60");

	/**
	 * constructs a new formatter
	 *
	 * @param displayFormat to be used
	 */
	public DurationFormatter(String displayFormat) {
		setOverwriteMode(false);
		this.displayFormat = displayFormat;
	}

	public DurationFormatter(String displayFormat, long unitsAdjustment) {
		this(displayFormat);
		this.unitsAdjustment = unitsAdjustment;
	}

	/**
	 * translates given input string into number of milliseconds representation of duration. Uses default 'day hour min' format.
	 *
	 * @param string String
	 * @return Long number of milliseconds
	 * @throws ParseException
	 */
	@Override
	public Object stringToValue(String string) throws ParseException {
		return parseDuration(string, unitsAdjustment);
	}


	/**
	 * Formats number of milliseconds into a human readable string. Uses default 'day hour min' format.
	 *
	 * @param value to convert
	 * @return human readable String
	 */
	@Override
	public String valueToString(Object value) {
		return formatDuration((Number) value, unitsAdjustment, this.displayFormat);
	}

	/**
	 * Formats number of milliseconds into a human readable string. Uses default 'day hour min' format.
	 *
	 * @param milliseconds to convert
	 * @param format to use
	 * @return human readable String
	 */
	public static String formatDuration(Number milliseconds, String format) {
		if (!(milliseconds instanceof Number)) {
			return "";
		}

		String result = DurationFormatUtils.formatDuration(milliseconds.longValue(), format, false);

		// to make output prettier need to clear leading zeroes in the output
		if (FORMAT_DD_HH_MM.equals(format)) {
			result = result.replace("0d 0h", "");
			result = result.replace("0d", "");
		} else if (FORMAT_HH_MM.equals(format)) {
			result = result.replace("0h", "");
		}

		return result.trim();
	}

	/**
	 * Converts specified time amount to milliseconds and formats number of milliseconds into a human readable string. Uses default 'day hour min' format.
	 *
	 * @param timeAmount to convert
	 * @param unitsAdjustment
	 * @param format to use
	 * @return human readable String
	 */
	public static String formatDuration(Number timeAmount, long unitsAdjustment, String format) {
		if (timeAmount == null) {
			return "";
		}

		return formatDuration(timeAmount.longValue() * unitsAdjustment, format);
	}

	/**
	 * translates given input string into number of milliseconds representation of duration, there is no need for format specification, everything is converted smartly
	 *
	 * @param input String
	 * @return Long number of milliseconds
	 * @throws ParseException
	 */
	public static Long parseDuration(String input) throws ParseException {
		if (StringUtils.isEmpty(input)) {
			return null;
		}
		String stringValue = input;

		// possible input:
		// 2min, 2m, 2h 2min, 2h, 2, 2 2
		stringValue = stringValue.toLowerCase(); // easier to do matching below
		boolean daysPresent = stringValue.indexOf("d") > 0;
		boolean hoursPresent = stringValue.indexOf("h") > 0;
		boolean minutesPresent = stringValue.indexOf("m") > 0;
		stringValue = stringValue.replaceAll("d", "");
		stringValue = stringValue.replaceAll("hr", "");
		stringValue = stringValue.replaceAll("h", "");
		stringValue = stringValue.replaceAll("min", "");
		stringValue = stringValue.replaceAll("m", "");
		try {
			Pattern p = Pattern.compile("[\\p{Punct}\\s]");
			String[] elements = p.split(stringValue);

			if (elements.length == 0) {
				return 0L;
			} else if (elements.length == 1) {
				long result;
				if (daysPresent) {
					result = Long.parseLong(elements[0]) * DAY;
				} else if (hoursPresent) {
					result = Long.parseLong(elements[0]) * HOUR;
				} else if (minutesPresent) {
					result = Long.parseLong(elements[0]) * MINUTE;
				} else {
					long temp = Long.parseLong(elements[0]);
					if (temp >= 10) {
						result = temp * MINUTE;
					} else {
						result = temp * HOUR;
					}
				}

				return result;
			} else if (elements.length == 2) {
				return Long.parseLong(elements[0]) * HOUR + Long.parseLong(elements[1]) * MINUTE;
			} else if (elements.length == 3) {
				return Long.parseLong(elements[0]) * DAY + Long.parseLong(elements[1]) * HOUR + Long.parseLong(elements[2]) * MINUTE;
			} else {
				logger.warn("wrong input");
			}

		} catch (NumberFormatException e) {
			// we can just ignore any parsing exceptions
			// logger.debug("Ignoring the parse exception: ", e);
		}

		return 0L;
	}

	/**
	 * translates given input string into number of milliseconds representation of duration divided specified adjustment,
	 * there is no need for format specification, everything is converted smartly
	 *
	 * @param input String
	 * @param unitsAdjustment long
	 * @return Long number of milliseconds
	 * @throws ParseException
	 */
	public static Long parseDuration(String input, long unitsAdjustment) throws ParseException {
		Long durationMilliseconds = parseDuration(input);

		if (durationMilliseconds != null) {
			return durationMilliseconds / unitsAdjustment;
		}

		return 0L;
	}

	/**
	 * arbitrarly calculates number of days, hours, minutes in a given milliseconds.
	 *
	 * @param mil milliseconds to decode
	 * @return array of [days|hours|minutes|seconds|milliseconds]
	 */
	public static int[] splitMillisecondsIntoDaysHoursMinutes(long mil) {

		long days = mil / DAY;
		long hours = (mil - days * DAY) / HOUR;
		long minutes = (mil - days * DAY - hours * HOUR) / MINUTE;

		return new int[] { (int) days, (int) hours, (int) minutes };
	}

	/**
	 * converts two dates to number of minutes between them
	 *
	 * @param from
	 * @param to
	 * @return minutes
	 */
	public static Integer parseDurationInMinutes(Date from, Date to) {
		if (from == null || to == null) {
			return 0;
		}

		return parseDurationInMinutes(to.getTime() - from.getTime());
	}

	/**
	 * converts miliseconds to minutes.
	 *
	 * @param miliseconds
	 * @return minutes
	 */
	public static Integer parseDurationInMinutes(long miliseconds) {
		int[] t = DurationFormatter.splitMillisecondsIntoDaysHoursMinutes(miliseconds);
		return t[0] * 24 * 60 + t[1] * 60 + t[2];
	}

	/**
	 * converts miliseconds to hours.
	 *
	 * @param miliseconds
	 * @return hours
	 */
	public static BigDecimal parseDurationInHours(long miliseconds) {
		int[] t = DurationFormatter.splitMillisecondsIntoDaysHoursMinutes(miliseconds);

		//days
		BigDecimal result = new BigDecimal(String.valueOf(t[0])).multiply(_24);
		//hours
		result = result.add(new BigDecimal(String.valueOf(t[1])));
		//minutes
		result = result.add(new BigDecimal(String.valueOf(t[2])).divide(_60, 2, BigDecimal.ROUND_HALF_UP));
		//ignore seconds etc.

		return result;
	}

}
