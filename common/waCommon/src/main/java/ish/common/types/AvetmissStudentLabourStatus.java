/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.common.types;

import ish.common.util.DisplayableExtendedEnumeration;

/**
 * enum representing the AvetmissStudentLabourStatus
 * 
 * @author marcin
 */
public enum AvetmissStudentLabourStatus implements DisplayableExtendedEnumeration<Integer> {

	// AVETMISS - page 48
	DEFAULT_POPUP_OPTION(0, "not stated"),
	FULL_TIME(1, "Full-time employee"),
	PART_TIME(2, "Part-time employee"),
	SELF_EMPLOYED(3, "Self-employed, not employing others"),
	EMPLOYER(4, "Employer"),
	UNPAID_FAMILY_WORKER(5, "Employed - unpaid in family business"),
	UNEMPLOYED_SEEKING_FULL_TIME(6, "Unemployed - seeking full-time work"),
	UNEMPLOYED_SEEKING_PART_TIME(7, "Unemployed - seeking part-time work"),
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