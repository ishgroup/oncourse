/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.common.types;

import ish.common.util.DisplayableExtendedEnumeration;

/**
 * A set of values for AVETMISS reporting from the version 7 standard.
 * Consult the AVETMISS documentation for more detail about these options.
 * 
 * @PublicApi
 */
public enum AvetmissStudentIndigenousStatus implements DisplayableExtendedEnumeration<Integer> {

	/**
	 * @PublicApi
	 */
	DEFAULT_POPUP_OPTION(0, "not stated"),

	/**
	 * @PublicApi
	 */
	ABORIGINAL(1, "Aboriginal"),

	/**
	 * @PublicApi
	 */
	TORRES(2, "Torres Strait Islander"),

	/**
	 * @PublicApi
	 */
	ABORIGINAL_AND_TORRES(3, "Aboriginal and Torres Strait Islander"),

	/**
	 * @PublicApi
	 */
	NEITHER(4, "Neither");

	private String displayName;
	private int value;

	private AvetmissStudentIndigenousStatus(int value, String displayName) {
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

	@Override
	public String toString() {
		return getDisplayName();
	}
}