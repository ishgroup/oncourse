/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
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
