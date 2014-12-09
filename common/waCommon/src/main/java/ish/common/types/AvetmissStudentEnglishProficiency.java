/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.common.types;

import ish.common.util.DisplayableExtendedEnumeration;

/**
 * an enumeration describing student english proficiency <br/>
 * <br/>
 * AVETMISS - page 79
 * 
 * @PublicApi
 */
public enum AvetmissStudentEnglishProficiency implements DisplayableExtendedEnumeration<Integer> {

	/**
	 * @PublicApi
	 */
	DEFAULT_POPUP_OPTION(0, "not stated"),

	/**
	 * @PublicApi
	 */
	VERY_WELL(1, "Very Well"),

	/**
	 * @PublicApi
	 */
	WELL(2, "Well"),

	/**
	 * @PublicApi
	 */
	NOT_WELL(3, "Not Well"),

	/**
	 * @PublicApi
	 */
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