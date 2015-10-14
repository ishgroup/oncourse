/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.math;

import ish.common.util.DisplayableExtendedEnumeration;
import ish.oncourse.API;

/**
 * enumeration of options for rounding money fields.
 * 
 */
@API
public enum MoneyRounding implements DisplayableExtendedEnumeration<Integer> {

	/**
	 */
	@API
	ROUNDING_NONE(0, "No Rounding"),

	/**
	 */
	@API
	ROUNDING_10C(1, "Nearest 10 cents"),

	/**
	 */
	@API
	ROUNDING_50C(2, "Nearest 50 cents"),

	/**
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