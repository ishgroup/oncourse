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
import java.math.RoundingMode;
import java.text.ParseException;
import java.time.Duration;
import java.util.Date;
import java.util.regex.Pattern;

/**
 * performs translations between duration represented as 'Long milliseconds' and human readable Strings.
 */
public class DurationFormatter {
	public static final String FORMAT_DD_HH_MM = "d'd' H'h' m'min'";
	public static final String FORMAT_HH_MM = "H'h' m'min'";

	public static final long SECOND = 1000l;
	public static final long MINUTE = SECOND * 60l;
	public static final long HOUR = MINUTE * 60l;
	public static final long DAY = HOUR * 24l;

	private static final BigDecimal _60 = new BigDecimal("60");

	

	/**
	 * Formats number of milliseconds into a human readable string. Uses default 'day hour min' format.
	 *
	 * @param milliseconds to convert
	 * @param format to use
	 * @return human readable String
	 */
	public static String formatDuration(Number milliseconds, String format) {
		
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
	
	public static BigDecimal durationInHoursBetween(Date startDatetime, Date endDatetime) {
		int minutes = durationInMinutesBetween(startDatetime, endDatetime);
		return durationInHoursFromMinutes(minutes);
	}
	
	/**
	 * converts two dates to number of minutes between them
	 *
	 * @param startDatetime
	 * @param endDatetime
	 * @return minutes
	 */
	public static Integer durationInMinutesBetween(Date startDatetime, Date endDatetime) {
		if (startDatetime == null || endDatetime == null) {
			return 0;
		}
		return Long.valueOf(Duration.between(endDatetime.toInstant(), startDatetime.toInstant()).toMinutes()).intValue();
	}

	public static BigDecimal durationInHoursFromMinutes(Integer minutes) {
		BigDecimal decimalValue = new BigDecimal(minutes).setScale(4, RoundingMode.HALF_UP);
		return decimalValue.divide(_60, RoundingMode.HALF_UP);
	}
}
