/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.common.types;

import ish.common.util.DisplayableExtendedEnumeration;
import ish.oncourse.API;

/**
 * Messages can either be an email, SMS or postal mail. If postal email, only the message subject
 * has relevant as a note to show what was sent.
 * 
 */
@API
public enum MessageType implements DisplayableExtendedEnumeration<Integer> {

	/**
	 */
	@API
	EMAIL(1, "email"),
	
	/**
	 */
	@API
	SMS(2, "sms"),

	/**
	 */
	@API
	POST(3, "post");

	private String displayName;
	private int value;

	private MessageType(int value, String displayName) {
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
