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
import ish.oncourse.API;

/**
 * Memberships can expire after a certain period of time. Once expired, new sales will not be allowed to
 * take advantage of the membership.
 *
 */
@API
public enum ExpiryType implements DisplayableExtendedEnumeration<Integer> {

	/**
	 * Database value: 1
	 *
	 * This membership expires after a certain numbers of days.
	 */
	@API
	DAYS(1, "Days"),

	/**
	 * Database value: 2
	 *
	 * This membership lasts until the beginning of the new year.
	 */
	@API
	FIRST_JANUARY(2, "1st January"),

	/**
	 * Database value: 3
	 *
	 * This membership lasts until the 1 July.
	 */
	@API
	FIRST_JULY(3, "1st July"),

	/**
	 * Database value: 4
	 *
	 * This membership never expires.
	 */
	@API
	LIFETIME(4, "Never (Lifetime)");

	private String displayName;
	private int value;

	private ExpiryType(int value, String displayName) {
		this.value = value;
		this.displayName = displayName;
	}

	/**
	 * @see ish.common.util.DisplayableExtendedEnumeration#getDatabaseValue()
	 */
	@Override
	public Integer getDatabaseValue() {
		return this.value;
	}

	/**
	 * @see ish.common.util.DisplayableExtendedEnumeration#getDisplayName()
	 */
	@Override
	public String getDisplayName() {
		return this.displayName;
	}

	/**
	 * @see java.lang.Enum#toString()
	 */
	@Override
	public String toString() {
		return getDisplayName();
	}
}
