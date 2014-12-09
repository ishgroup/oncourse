/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.common.types;

import ish.common.util.DisplayableExtendedEnumeration;

/**
 * enumeration of discount expire types
 * 
 * @PublicApi
 */
public enum ExpiryType implements DisplayableExtendedEnumeration<Integer> {

	/**
	 * @PublicApi
	 */
	DAYS(1, "Days"),

	/**
	 * @PublicApi
	 */
	FIRST_JANUARY(2, "1st January"),

	/**
	 * @PublicApi
	 */
	FIRST_JULY(3, "1st July"),

	/**
	 * @PublicApi
	 */
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
