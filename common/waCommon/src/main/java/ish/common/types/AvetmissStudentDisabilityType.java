/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.common.types;

import ish.common.util.DisplayableExtendedEnumeration;

/**
 * an enumeration describing student disability type<br/>
 * <br/>
 * AVETMISS - page 43
 * 
 * @PublicApi
 */
public enum AvetmissStudentDisabilityType implements DisplayableExtendedEnumeration<Integer> {

	/**
	 * @PublicApi
	 */
	DEFAULT_POPUP_OPTION(0, "not stated"),

	/**
	 * @PublicApi
	 */
	NONE(100, "none"),

	/**
	 * @PublicApi
	 */
	HEARING(1, "Hearing/Deaf"),

	/**
	 * @PublicApi
	 */
	PHYSICAL(2, "Physical"),

	/**
	 * @PublicApi
	 */
	INTELLECTUAL(3, "Intellectual"),

	/**
	 * @PublicApi
	 */
	LEARNING(4, "Learning"),

	/**
	 * @PublicApi
	 */
	MENTAL(5, "Mental illness"),

	/**
	 * @PublicApi
	 */
	BRAIN_IMPAIRMENT(6, "Acquired brain impairment"),

	/**
	 * @PublicApi
	 */
	VISION(7, "Vision"),

	/**
	 * @PublicApi
	 */
	MEDICAL_CONDITION(8, "Medical condition"),

	/**
	 * @PublicApi
	 */
	OTHER(9, "Other");

	private String displayName;
	private int value;

	private AvetmissStudentDisabilityType(int value, String displayName) {
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
