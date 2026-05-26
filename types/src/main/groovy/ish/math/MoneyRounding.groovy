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
	ROUNDING_1D(3, "Nearest dollar"),

	/**
	 * Database value: 4
	 *
	 * Rounding to nearest even value, e.g. $38.005 becomes $38.00
	 */
	@API
	ROUNDING_EVEN(4, "Nearest two digits");

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
