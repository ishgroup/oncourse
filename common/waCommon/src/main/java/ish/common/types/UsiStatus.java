/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */

package ish.common.types;

import ish.common.util.DisplayableExtendedEnumeration;

/**
 * enumeration with options describing usi status
 */
public enum UsiStatus implements DisplayableExtendedEnumeration<Integer> {

	DEFAULT_NOT_SUPPLIED(0, "Not supplied"),
	NON_VERIFIED(1, "Manual"),
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
