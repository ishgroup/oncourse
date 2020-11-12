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
 * A set of values for AVETMISS reporting for Victoria only.
 * Consult the AVETMISS documentation for more detail about these options.
 *
 */
@API
public enum CourseClassAttendanceType implements DisplayableExtendedEnumeration<Integer> {

	/**
     * Database value: 0
	 *
	 * For OUA and non-higher degree research student use only
	 */
	@API
	OUA_AND_NOT_HIGHER_DEGREE_REASEARCH_STUDENT_USE(0, "For OUA and non-higher degree research student use only",
		"For OUA and non-higher degree research student use only"),

	/**
     * Database value: 1
	 *
	 * Full-time attendance for: higher degree research student, higher education course completions for all full time students and VET student
	 */
	@API
	FULL_TIME_ATTENDANCE(1, "Full-time attendance",
		"Full-time attendance for: higher degree research student, higher education course completions for all full time students and VET student"),

	/**
     * Database value: 2
	 *
	 * Part-time attendance for: higher degree research student, higher education course completions for all part time students and VET student
	 */
	@API
	PART_TIME_ATTENDANCE(2, "Part-time attendance",
		"Part-time attendance for: higher degree research student, higher education course completions for all part time students and VET student"),

	/**
     * Database value: 9
	 *
	 * No information
	 */
	@API
	NO_INFORMATION(9, "No information", "No information");

	private String displayValue;
	private String description;
	private int value;

	private CourseClassAttendanceType(int value, String displayValue, String description) {
		this.displayValue = displayValue;
		this.description = description;
		this.value = value;
	}

	@Override
	public Integer getDatabaseValue() {
		return value;
	}

	@Override
	public String getDisplayName() {
		return displayValue;
	}

	public String getDescription() {
		return description;
	}

	@Override
	public String toString() {
		return getDisplayName();
	}
}
