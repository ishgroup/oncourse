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
 * Courses can either be applied for, or its classes can be directly enrolled in.
 *
 */
@API
public enum CourseEnrolmentType implements DisplayableExtendedEnumeration<Integer> {

	/**
	 * Database value: 1
	 *
	 * Allow students to enrol directly in classes for this course.
	 */
	@API
	OPEN_FOR_ENROLMENT(1, "Open enrolment"),

	/**
	 * Database value: 2
	 *
	 * Allow students to only apply for the class. Enrolment will be opened once the application is reviewed and accepted by college.
	 */
	@API
	ENROLMENT_BY_APPLICATION(2, "Enrolment by application");

	private String displayName;
	private int value;

	private CourseEnrolmentType(int value, String displayName) {
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
