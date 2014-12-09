/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.common.types;

import ish.common.util.DisplayableExtendedEnumeration;

/**
 * an enumeration describing student's school level<br/>
 * <br/>
 * AVETMISS - page 31
 * 
 * @PublicApi
 */
public enum AvetmissStudentSchoolLevel implements DisplayableExtendedEnumeration<Integer> {

	// AVETMISS - page 31
	/**
	 * @PublicApi
	 */
	DEFAULT_POPUP_OPTION(0, "not stated"),
	
	/**
	 * @PublicApi
	 */
	DID_NOT_GO_TO_SCHOOL(1, "Did not go to school"),

	/**
	 * @PublicApi
	 */
	COMPLETED_YEAR_8_OR_BELOW(2, "Year 8 or below"),

	/**
	 * @PublicApi
	 */
	COMPLETED_YEAR_9(3, "Year 9"),

	/**
	 * @PublicApi
	 */
	COMPLETED_YEAR_10(4, "Year 10"),

	/**
	 * @PublicApi
	 */
	COMPLETED_YEAR_11(5, "Year 11"),

	/**
	 * @PublicApi
	 */
	COMPLETED_YEAR_12(6, "Year 12");

	private String displayName;
	private int value;

	private AvetmissStudentSchoolLevel(int value, String displayName) {
		this.value = value;
		this.displayName = displayName;
	}

	/**
	 * @see ish.common.util.DisplayableExtendedEnumeration#getDatabaseValue()
	 */
	public Integer getDatabaseValue() {
		return this.value;
	}

	/**
	 * @see ish.common.util.DisplayableExtendedEnumeration#getDisplayName()
	 */
	public String getDisplayName() {
		return this.displayName;
	}

	/**
	 * used to verify if the student age is not enough to be able to complete given school level
	 * 
	 * @param highestSchoolLevel declared as completed by student
	 * @param studentAge declared age
	 * @return true if the combination of parameters is valid
	 */
	public static boolean isValid(AvetmissStudentSchoolLevel highestSchoolLevel, int studentAge) {
		if (highestSchoolLevel != null) {
			// avoid AVETMISS validation
			if (studentAge < 10) {
				return highestSchoolLevel.getDatabaseValue() < COMPLETED_YEAR_9.getDatabaseValue();
			}
			if (studentAge < 11) {
				return highestSchoolLevel.getDatabaseValue() < COMPLETED_YEAR_10.getDatabaseValue();
			}
			if (studentAge < 12) {
				return highestSchoolLevel.getDatabaseValue() < COMPLETED_YEAR_11.getDatabaseValue();
			}
			if (studentAge < 13) {
				return highestSchoolLevel.getDatabaseValue() < COMPLETED_YEAR_12.getDatabaseValue();
			}

		}
		return true;
	}

	/**
	 * @see java.lang.Enum#toString()
	 */
	@Override
	public String toString() {
		return getDisplayName();
	}

}
