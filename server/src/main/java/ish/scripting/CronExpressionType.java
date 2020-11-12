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

package ish.scripting;

import ish.common.util.DisplayableExtendedEnumeration;

public enum CronExpressionType implements DisplayableExtendedEnumeration<String> {

	DAILY_MORNING("@daily-morning", "Daily (about 6am)"),
	DAILY_EVENING("@daily-evening", "Daily (about 8pm)"),
	WEEKLY_MONDAY("@weekly", "Weekly (Monday about 7am)"),
	HOURLY("@hourly", "Hourly"),
	CUSTOM("", "Custom");

	private String value;
	private String displayName;

	CronExpressionType(String value, String displayName) {
		this.value = value;
		this.displayName = displayName;
	}

	@Override
	public String getDisplayName() {
		return displayName;
	}
	@Override
	public String getDatabaseValue() {
		return value;
	}
}
