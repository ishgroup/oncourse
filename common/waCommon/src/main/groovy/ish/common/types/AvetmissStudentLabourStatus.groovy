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
public enum AvetmissStudentLabourStatus implements DisplayableExtendedEnumeration<Integer> {

	// AVETMISS - page 48
	/**
	 */
	@API
	DEFAULT_POPUP_OPTION(0, "not stated"),
	
	/**
	 */
	@API
	FULL_TIME(1, "Full-time employee"),

	/**
	 */
	@API
	PART_TIME(2, "Part-time employee"),

	/**
	 */
	@API
	SELF_EMPLOYED(3, "Self-employed, not employing others"),

	/**
	 */
	@API
	EMPLOYER(4, "Employer"),

	/**
	 */
	@API
	UNPAID_FAMILY_WORKER(5, "Employed - unpaid in family business"),

	/**
	 */
	@API
	UNEMPLOYED_SEEKING_FULL_TIME(6, "Unemployed - seeking full-time work"),

	/**
	 */
	@API
	UNEMPLOYED_SEEKING_PART_TIME(7, "Unemployed - seeking part-time work"),

	/**
	 */
	@API
	UNEMPLOYED_NOT_SEEKING(8, "Not employed - not seeking employment");

	private String displayName;
	private int value;

	private AvetmissStudentLabourStatus(int value, String displayName) {
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