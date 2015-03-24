/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */

package ish.common.types;

import ish.common.util.DisplayableExtendedEnumeration;

/**
 * A USI can be validated against the government verification service
 * 
 * @PublicApi
 */
public enum UsiStatus implements DisplayableExtendedEnumeration<Integer> {

	/**
	 * @PublicApi
	 */
	DEFAULT_NOT_SUPPLIED(0, "Not supplied"),

	/**
	 * @PublicApi
	 */
	NON_VERIFIED(1, "Not verified"),

	/**
	 * @PublicApi
	 */
	VERIFIED(2, "Verified");

	private String displayName;
	private int value;

	private UsiStatus(int value, String displayName) {
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
