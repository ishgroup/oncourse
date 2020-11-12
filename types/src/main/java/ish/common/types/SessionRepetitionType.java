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
package ish.common.types;

import ish.common.util.DisplayableExtendedEnumeration;

import java.util.Calendar;

@Deprecated
public enum SessionRepetitionType implements DisplayableExtendedEnumeration<Integer> {


	HOUR_CHOICE(Calendar.HOUR_OF_DAY, "hour"),
	DAY_CHOICE(Calendar.DAY_OF_YEAR, "day"),
	WEEK_CHOICE(Calendar.WEEK_OF_YEAR, "week"),
	MONTH_CHOICE(Calendar.MONTH, "month"),
	YEAR_CHOICE(Calendar.YEAR, "year"),
	CUSTOM_CHOICE(-1, "Custom"),
	NONE_CHOICE(-2, "None");

	private String displayName;
	private int value;

	private SessionRepetitionType(int value, String displayName) {
		this.value = value;
		this.displayName = displayName;
	}

	@Override
	public Integer getDatabaseValue() {
		return this.value;
	}

	@Override
	public String getDisplayName() {
		return this.displayName;
	}

	@Override
	public String toString() {
		return getDisplayName();
	}
}
