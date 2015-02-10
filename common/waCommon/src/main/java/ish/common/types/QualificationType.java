/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.common.types;

import ish.common.util.DisplayableExtendedEnumeration;

/**
 * Qualifications must be one of these types as per the AVETMISS standard
 * 
 * @PublicApi
 */
public enum QualificationType implements DisplayableExtendedEnumeration<Integer> {

	/**
	 * Qualification.
	 * 
	 * Database value: 0
	 * @PublicApi
	 */
	QUALIFICATION_TYPE(0, "Qualification"),

	/**
	 * Accredited course.
	 * 
	 * Database value: 1
	 * @PublicApi
	 */
	COURSE_TYPE(1, "Accredited course"),
	
	/**
	 * Skill set.
	 * 
	 * Database value: 2
	 * @PublicApi
	 */
	SKILLSET_TYPE(2, "Skill set"),

	/**
	 * Local skill set.
	 * 
	 * Database value: 3
	 * @PublicApi
	 */
	SKILLSET_LOCAL_TYPE(3, "Local skill set"),

	/**
	 * Higher education.
	 * 
	 * Database value: 4
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
