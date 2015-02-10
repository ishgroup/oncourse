/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.common.types;

import ish.common.util.DisplayableExtendedEnumeration;

/**
 * Courses can either be applied for {@see Application}, or its classes can be directly enrolled in.
 * 
 * @PublicApi
 */
public enum CourseEnrolmentType implements DisplayableExtendedEnumeration<Integer> {

	/**
	 * Allow students to enrol directly in classes for this course.
	 * 
	 * Database value: 1
	 * @PublicApi
	 */
	OPEN_FOR_ENROLMENT(1, "Open enrolment"),

	/**
	 * Allow students to only apply to be allowed to enrol.
	 * 
	 * Database value: 2
	 * @PublicApi
	 */
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
