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
 * Each Message object (the delivery record) starts as queued and then
 * is processed by a special process which wakes up every few seconds.
 *
 */
@API
public enum MessageStatus implements DisplayableExtendedEnumeration<Integer> {

	/**
	 * Database value: 1
	 *
	 * Message hasn't been sent by onCourse server yet.
	 */
	@API
	QUEUED(1, "queued"),

	/**
	 * Database value: 2
	 *
	 * Message was sent to contact successfully.
	 */
	@API
	SENT(2, "sent"),

	/**
	 * Database value: 3
	 *
	 * Message sending failed and won't be attempted again.
	 */
	@API
	FAILED(3, "failed");

	private String displayName;
	private int value;

	private MessageStatus(int value, String displayName) {
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
