/*
 * Copyright ish group pty ltd 2020.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
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
public enum AvetmissStudentSchoolLevel implements DisplayableExtendedEnumeration<Integer> {

	// AVETMISS - page 31
	/**
	 * Database value: 0 Not stated
	 */
	@API
	DEFAULT_POPUP_OPTION(0, "not stated"),

	/**
	 * Database value: 1
	 */
	@API
	DID_NOT_GO_TO_SCHOOL(1, "Did not go to school"),

	/**
	 * Database value: 2
	 */
	@API
	COMPLETED_YEAR_8_OR_BELOW(2, "Year 8 or below"),

	/**
	 * Database value: 3
	 */
	@API
	COMPLETED_YEAR_9(3, "Year 9"),

	/**
	 * Database value: 4
	 */
	@API
	COMPLETED_YEAR_10(4, "Year 10"),

	/**
	 * Database value: 5
	 */
	@API
	COMPLETED_YEAR_11(5, "Year 11"),

	/**
	 * Database value: 6
	 */
	@API
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
