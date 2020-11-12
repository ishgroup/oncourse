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
public enum AvetmissStudentLabourStatus implements DisplayableExtendedEnumeration<Integer> {

	// AVETMISS - page 48
	/**
	 * Database value: 0 Not stated
	 */
	@API
	DEFAULT_POPUP_OPTION(0, "not stated"),

	/**
	 * Database value: 1 Full-time employee
	 */
	@API
	FULL_TIME(1, "Full-time employee"),

	/**
	 *  Database value: 2 Part-time employee
	 */
	@API
	PART_TIME(2, "Part-time employee"),

	/**
	 * Database value: 3 Self-employed, not employing others
	 */
	@API
	SELF_EMPLOYED(3, "Self-employed, not employing others"),

	/**
	 * Database value: 4 Employer
	 */
	@API
	EMPLOYER(4, "Self-employed – employing others"),

	/**
	 * Database value: 5 Employed - unpaid in family business
	 */
	@API
	UNPAID_FAMILY_WORKER(5, "Employed - unpaid in family business"),

	/**
	 * Database value: 6 Unemployed - seeking full-time work
	 */
	@API
	UNEMPLOYED_SEEKING_FULL_TIME(6, "Unemployed - seeking full-time work"),

	/**
	 * Database value: 7 Unemployed - seeking part-time work
	 */
	@API
	UNEMPLOYED_SEEKING_PART_TIME(7, "Unemployed - seeking part-time work"),

	/**
	 * Database value: 8 Not employed - not seeking employment
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
