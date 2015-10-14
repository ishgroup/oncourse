/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
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
	 * An email for this record is not yet sent.
	 * 
	 */
	@API
	NOT_SENT(1, "Not sent"),

	/**
	 * An email for this record has been sent.
	 * 
	 */
	@API
	SENT(2, "Sent"),

	/**
	 * A user has chosen to prevent an email being sent for this record.
	 * 
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
