/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.common.types;

import ish.common.util.DisplayableExtendedEnumeration;

/**
 * enumeration of available enrolment types to course
 */
public enum CourseEnrolmentType implements DisplayableExtendedEnumeration<Integer> {

	OPEN_FOR_ENROLMENT(1, "Open enrolment"),

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
