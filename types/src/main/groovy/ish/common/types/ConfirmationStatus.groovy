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
 * Records such as enrolment, invoice and application can be created from a website
 * or onCourse interaction. Sometimes (such as in the QE window) a user can prevent
 * email notifications to the student.
 *
 */
@API
public enum ConfirmationStatus implements DisplayableExtendedEnumeration<Integer> {

	/**
	 * Database value: 1
	 *
	 * An email for this record is not yet sent.
	 */
	@API
	NOT_SENT(1, "Not sent"),

	/**
	 * Database value: 2
	 *
	 * An email for this record has been sent.
	 */
	@API
	SENT(2, "Sent"),

	/**
	 * Database value: 2
	 *
	 * A user has chosen to prevent an email being sent for this record.
	 */
	@API
	DO_NOT_SEND(3, "Don't send");

	private int value;
	private String displayName;

	private ConfirmationStatus(int value, String displayName) {
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
