/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.common.types;

import ish.common.util.DisplayableExtendedEnumeration;
import ish.oncourse.API;

/**
 * A set of values for AVETMISS reporting from the version 7 standard.
 * Consult the AVETMISS documentation for more detail about these options.
 * 
 */
@API
public enum AvetmissStudentIndigenousStatus implements DisplayableExtendedEnumeration<Integer> {

	/**
	 */
	@API
	DEFAULT_POPUP_OPTION(0, "not stated"),

	/**
	 */
	@API
	ABORIGINAL(1, "Aboriginal"),

	/**
	 */
	@API
	TORRES(2, "Torres Strait Islander"),

	/**
	 */
	@API
	ABORIGINAL_AND_TORRES(3, "Aboriginal and Torres Strait Islander"),

	/**
	 */
	@API
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