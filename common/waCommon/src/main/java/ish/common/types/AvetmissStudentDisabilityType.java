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
 */
public enum AvetmissStudentDisabilityType implements DisplayableExtendedEnumeration<Integer> {

	DEFAULT_POPUP_OPTION(0, "not stated"),
	NONE(100, "none"),
	HEARING(1, "Hearing/Deaf"),
	PHYSICAL(2, "Physical"),
	INTELLECTUAL(3, "Intellectual"),
	LEARNING(4, "Learning"),
	MENTAL(5, "Mental illness"),
	BRAIN_IMPAIRMENT(6, "Acquired brain impairment"),
	VISION(7, "Vision"),
	MEDICAL_CONDITION(8, "Medical condition"),
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
