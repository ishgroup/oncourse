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

import java.text.ParseException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.TimeZone;

public class LocalDateFormatter extends DefaultFormatter {

	public static final DateTimeFormatter FORMAT_DATE_E_d_MMM_u = DateTimeFormatter.ofPattern("E d MMM u");
	public DateTimeFormatter formatter;

	public LocalDateFormatter() {
		this(null);
	}

	public LocalDateFormatter(DateTimeFormatter formatter) {
		this.formatter = formatter;
	}

	public static String formatDate(LocalDate date, DateTimeFormatter pattern) {
		if (pattern != null) {
			return pattern.format(date);
		}
		return FORMAT_DATE_E_d_MMM_u.format(date);
	}

	public static LocalDate parseDate(String stringDate) {
		if (StringUtils.isNotBlank(stringDate)) {
			Calendar calendar = DateFormatter.parseDateToCal(stringDate, TimeZone.getDefault());
			if (calendar != null) {
				return calendar.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
			}
		}

		return null;
	}

	@Override
	public Object stringToValue(String string) throws ParseException {
		return parseDate(string);
	}

	@Override
	public String valueToString(Object value) {
		if (value == null || value.toString() == null) {
			return "";
		}

		if(formatter != null) {
			return  formatter.format((LocalDate) value);
		}

		LocalDate now = LocalDate.now();
		if (now.equals(value)) {
			return "today";
		} else if (now.plusDays(1).equals(value)) {
			return "tomorrow";
		} else if (now.plusDays(-1).equals(value)) {
			return "yesterday";
		}

		return formatDate((LocalDate) value, null);
	}


}
