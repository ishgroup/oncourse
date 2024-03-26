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

package ish.persistence;

import java.util.Calendar;
import java.util.Date;

public class CommonExpressionFactory {

	public static Date nextMidnight(Date date) {
		Date result = null;
		if (date != null) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			cal.add(Calendar.DATE, 1);
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
			result = cal.getTime();
		}
		return result;
	}

	public static Date nextMidnightMinusOne(Date date) {
		Date result = null;
		if (date != null) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(nextMidnight(date));
			cal.add(Calendar.MILLISECOND, -1);
			result = cal.getTime();
		}
		return result;
	}

	public static Date nextMidnightPlusDay(Date date) {
		Date result = null;
		if (date != null) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(nextMidnight(date));
			cal.add(Calendar.DATE, 1);
			result = cal.getTime();
		}
		return result;
	}

	public static Date previousMidnight(Date date) {
		Date result = null;
		if (date != null) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
			result = cal.getTime();
		}
		return result;
	}

	public static Date previousMidnightMinusDay(Date date) {
		Date result = null;
		if (date != null) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(previousMidnight(date));
			cal.add(Calendar.DATE, -1);
			result = cal.getTime();
		}
		return result;
	}
}
