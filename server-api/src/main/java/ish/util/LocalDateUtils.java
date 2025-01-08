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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class LocalDateUtils {

	/**
	 * use this date/date time patterns for DTO stubs generation see
	 * /trunk/buildSrc/swagger/src/main/resources/swaggerTemplates/java/pojo.mustache
	 */
	public static final String DATE_PATTERN ="yyyy-MM-dd";
	public static final String DATE_TIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

	public static final ZoneId UTC = ZoneId.of("UTC");

	/**
	 * use this format for transaction lock date only
	 */
	@Deprecated
	public static final DateTimeFormatter formatter = new DateTimeFormatterBuilder()
			.parseCaseInsensitive()
			.appendPattern("dd-MMM-yyyy")
			.toFormatter(Locale.ENGLISH);

	private static final DateTimeFormatter DATE_FORMAT = new DateTimeFormatterBuilder()
			.parseCaseInsensitive()
			.appendPattern(DATE_PATTERN)
			.toFormatter(Locale.ENGLISH);

	private static final DateTimeFormatter  DATE_TIME_FORMAT = new DateTimeFormatterBuilder()
			.parseCaseInsensitive()
			.appendPattern(DATE_TIME_PATTERN)
			.toFormatter(Locale.ENGLISH);

	/**
	 * Deprecated to use
	 * Use valueToString(LocalDate localDate) with standart date format
	 * This one we use for transaction lock date only.
	 */
	@Deprecated
	public static String valueToString(LocalDate localDate,  DateTimeFormatter customFormatter) {
		return customFormatter.format(localDate);
	}
	@Deprecated
	public static LocalDate stringToValue(String stringValue, DateTimeFormatter customFormatter) {
		return LocalDate.parse(stringValue, customFormatter);
	}

	public static String valueToString(LocalDate localDate) {
		return DATE_FORMAT.format(localDate);
	}

	public static String timeValueToString(LocalDateTime localDateTile) {
		return DATE_TIME_FORMAT.format(localDateTile);
	}

	public static LocalDate stringToValue(String stringValue ) {
		return LocalDate.parse(stringValue, DATE_FORMAT);
	}

	public static LocalDateTime stringToTimeValue(String stringValue) {
		return LocalDateTime.parse(stringValue, DATE_TIME_FORMAT);
	}

	public static LocalDate dateToValue(Date date) {
		return date == null ? null : date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
	}

	public static Date valueToDate(LocalDate localDate) {
		return valueToDate(localDate, false);
	}

	public static Date valueToDate(LocalDate localDate, boolean toEndOfDay) {
		if (localDate == null) {
			return null;
		}
		LocalDateTime localDateTime = toEndOfDay ? localDate.atTime(23, 59, 59) : localDate.atStartOfDay();
		return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
	}

	public static LocalDate calendarToValue(Calendar calendar) {
		return calendar == null ? null : calendar.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
	}

	public static Date valueToDateAtNoon(LocalDate localDate) {
		return localDate == null ? null : Date.from(localDate.atTime(12, 0).atZone(ZoneId.systemDefault()).toInstant());
	}

	public static Date timeValueToDate(LocalDateTime localDateTime) {
		return localDateTime == null ? null :
				Date.from(localDateTime.atZone(UTC).toInstant());
	}

	public static LocalDateTime dateToTimeValue(Date date) {
		return date == null ? null :
				date.toInstant().atZone(UTC).toLocalDateTime();
	}

	public static LocalDateTime dateToTimeValue(Date date, ZoneId timeZone) {
		return date == null ? null :
				date.toInstant().atZone(timeZone).toLocalDateTime();
	}

}
