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
public enum AvetmissStudentDisabilityType implements DisplayableExtendedEnumeration<Integer> {

	/**
	 * Database value: 0
	 */
	@API
	DEFAULT_POPUP_OPTION(0, "not stated"),

	/**
	 * Database value: 100
	 */
	@API
	NONE(100, "none"),

	/**
	 * Database value: 1
	 */
	@API
	HEARING(1, "Hearing/Deaf"),

	/**Database value: 2
	 */
	@API
	PHYSICAL(2, "Physical"),

	/**
	 * Database value: 3
	 */
	@API
	INTELLECTUAL(3, "Intellectual"),

	/**
	 * Database value: 4
	 */
	@API
	LEARNING(4, "Learning"),

	/**
	 * Database value: 5
	 */
	@API
	MENTAL(5, "Mental illness"),

	/**
	 * Database value: 6
	 */
	@API
	BRAIN_IMPAIRMENT(6, "Acquired brain impairment"),

	/**
	 * Database value: 7
	 */
	@API
	VISION(7, "Vision"),

	/**
	 * Database value: 8
	 */
	@API
	MEDICAL_CONDITION(8, "Medical condition"),

	/**
	 * Database value: 9
	 */
	@API
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
