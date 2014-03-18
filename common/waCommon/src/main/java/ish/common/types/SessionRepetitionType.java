/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.common.types;

import ish.common.util.DisplayableExtendedEnumeration;

import java.util.Calendar;

/**
 * @author marcin
 */
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
