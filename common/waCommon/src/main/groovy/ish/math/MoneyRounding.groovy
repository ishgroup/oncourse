/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.math;

import ish.common.util.DisplayableExtendedEnumeration;
import ish.oncourse.API;

/**
 * Enumeration of options for rounding money fields, e.g. after applying discounts.
 * 
 */
@API
public enum MoneyRounding implements DisplayableExtendedEnumeration<Integer> {

	/**
	 * Database value: 0
	 *
	 * No rounding is applied
	 */
	@API
	ROUNDING_NONE(0, "No Rounding"),

	/**
	 * Database value: 1
	 *
	 * Rounding to nearest 10 cents, e.g. $1.93 becomes $1.90
	 */
	@API
	ROUNDING_10C(1, "Nearest 10 cents"),

	/**
	 * Database value: 2
	 *
	 * Rounding to nearest 40 cents, e.g. $1.34 becomes $1.50
	 */
	@API
	ROUNDING_50C(2, "Nearest 50 cents"),

	/**
	 * Database value: 3
	 *
	 * Rounding to nearest dollar, e.g. $9.70 becomes $10.00
	 */
	@API
	ROUNDING_1D(3, "Nearest dollar");

	private String displayName;
	private int value;

	private MoneyRounding(int value, String displayName) {
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