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
 * Messages can either be an email, SMS or postal mail. If postal email, only the message subject
 * has relevant as a note to show what was sent.
 *
 */
@API
public enum MessageType implements DisplayableExtendedEnumeration<Integer> {

	/**
	 * Database value: 1
	 *
	 * Messages sent to contact email.
	 */
	@API
	EMAIL(1, "email"),

	/**
	 * Database value: 2
	 *
	 * Messages sent ti contact address.
	 */
	@API
	SMS(2, "sms"),

	/**
	 * Database value: 3
	 *
	 * Messages sent to contact mobile phone number.
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
