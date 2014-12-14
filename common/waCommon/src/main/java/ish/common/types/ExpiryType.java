/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.common.types;

import ish.common.util.DisplayableExtendedEnumeration;

/**
 * Discounts can expire after a certain period of time. Once expired, new sales will not be allowed to
 * take advantage of the discount.
 * 
 * @PublicApi
 */
public enum ExpiryType implements DisplayableExtendedEnumeration<Integer> {

	/**
	 * This discount expires after a certain numbers of days.
	 * 
	 * @PublicApi
	 */
	DAYS(1, "Days"),

	/**
	 * This discount lasts until the beginning of the new year.
	 * 
	 * @PublicApi
	 */
	FIRST_JANUARY(2, "1st January"),

	/**
	 * This discount lasts until the 1 July.
	 * 
	 * @PublicApi
	 */
	FIRST_JULY(3, "1st July"),

	/**
	 * This discount never expires.
	 * 
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
