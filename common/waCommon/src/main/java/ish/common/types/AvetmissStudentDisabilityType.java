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
public enum AvetmissStudentDisabilityType implements DisplayableExtendedEnumeration<Integer> {

	/**
	 * Database value: 0
	 * @PublicApi
	 */
	DEFAULT_POPUP_OPTION(0, "not stated"),

	/**
	 * Database value: 100
	 * @PublicApi
	 */
	NONE(100, "none"),

	/**
	 * Database value: 1
	 * @PublicApi
	 */
	HEARING(1, "Hearing/Deaf"),

	/**Database value: 2
	 * @PublicApi
	 */
	PHYSICAL(2, "Physical"),

	/**
	 * Database value: 3
	 * @PublicApi
	 */
	INTELLECTUAL(3, "Intellectual"),

	/**
	 * Database value: 4
	 * @PublicApi
	 */
	LEARNING(4, "Learning"),

	/**
	 * Database value: 5
	 * @PublicApi
	 */
	MENTAL(5, "Mental illness"),

	/**
	 * Database value: 6
	 * @PublicApi
	 */
	BRAIN_IMPAIRMENT(6, "Acquired brain impairment"),

	/**
	 * Database value: 7
	 * @PublicApi
	 */
	VISION(7, "Vision"),

	/**
	 * Database value: 8
	 * @PublicApi
	 */
	MEDICAL_CONDITION(8, "Medical condition"),

	/**
	 * Database value: 9
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
