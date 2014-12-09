/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.common.types;

import ish.common.util.DisplayableExtendedEnumeration;

/**
 * Qualifications must be one of these types
 * 
 * @PublicApi
 */
public enum QualificationType implements DisplayableExtendedEnumeration<Integer> {

	/**
	 * @PublicApi
	 */
	QUALIFICATION_TYPE(0, "Qualification"),

	/**
	 * @PublicApi
	 */
	COURSE_TYPE(1, "Accredited course"),
	
	/**
	 * @PublicApi
	 */
	SKILLSET_TYPE(2, "Skill set"),

	/**
	 * @PublicApi
	 */
	SKILLSET_LOCAL_TYPE(3, "Local skill set"),

	/**
	 * @PublicApi
	 */
	HIGHER_TYPE(4, "Higher education");

	private String displayName;
	private int value;

	private QualificationType(int value, String displayName) {
		this.value = value;
		this.displayName = displayName;
	}

	@Override
	public Integer getDatabaseValue() {
		return this.value;
	}

	@Override
	public String getDisplayName() {
		return this.displayName;
	}

	@Override
	public String toString() {
		return getDisplayName();
	}
}
