/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */

package ish.common.types;

import ish.common.util.DisplayableExtendedEnumeration;
import ish.oncourse.API;

/**
 * A USI can be validated against the government verification service.
 */
@API
public enum UsiStatus implements DisplayableExtendedEnumeration<Integer> {

	/**
	 */
	@API
	DEFAULT_NOT_SUPPLIED(0, "Not supplied"),

	/**
	 */
	@API
	NON_VERIFIED(1, "Not verified"),

	/**
	 */
	@API
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
