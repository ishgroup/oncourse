/*
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
public enum AvetmissStudentEnglishProficiency implements DisplayableExtendedEnumeration<Integer> {

	/**
	 */
	@API
	DEFAULT_POPUP_OPTION(0, "not stated"),

	/**
	 */
	@API
	VERY_WELL(1, "Very Well"),

	/**
	 */
	@API
	WELL(2, "Well"),

	/**
	 */
	@API
	NOT_WELL(3, "Not Well"),

	/**
	 */
	@API
	NOT_AT_ALL(4, "Not at all");

	private String displayName;
	private int value;

	private AvetmissStudentEnglishProficiency(int value, String displayName) {
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