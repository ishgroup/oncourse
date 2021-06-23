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

/**
 * Enumeration containing all available onCourse system events.
 */
public enum SystemEventType implements DisplayableExtendedEnumeration<Integer> {

	ENROLMENT_SUCCESSFUL(1, "Enrolment successful"),
	ENROLMENT_CANCELLED(2, "Enrolment cancelled"),
	CLASS_CANCELLED(3, "Class cancelled"),
	CLASS_PUBLISHED(4, "Class published"),
	PAYSLIP_APPROVED(5, "Payslip approved"),
	VALIDATE_CHECKOUT(6, "Validate checkout"),
	PAYSLIP_PAID(7, "Payslip paid"),
	USER_LOGGED_IN(8, "System user logged in"),
	USER_LOGGED_OUT(9, "System user logged out");
	
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
