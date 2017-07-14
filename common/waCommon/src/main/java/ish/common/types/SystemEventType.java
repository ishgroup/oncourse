/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.common.types;

import ish.common.util.DisplayableExtendedEnumeration;

/**
 * Enumeration containing all available onCourse system events.
 */
public enum SystemEventType implements DisplayableExtendedEnumeration<Integer> {

	ENROLMENT_SUCCESSFUL(1, "Enrolment successful"),
	ENROLMENT_CANCELLED(2, "Enrolment cancelled"),
	CLASS_CANCELLED(3, "Class cancelled"),
	CLASS_PUBLISHED(4, "Class published");

	private int value;
	private String displayName;

	SystemEventType(int value, String displayName) {
		this.value = value;
		this.displayName = displayName;
	}

	@Override
	public String getDisplayName() {
		return displayName;
	}

	@Override
	public Integer getDatabaseValue() {
		return value;
	}
}
