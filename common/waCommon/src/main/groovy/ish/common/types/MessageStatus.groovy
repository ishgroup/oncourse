/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.common.types;

import ish.common.util.DisplayableExtendedEnumeration;
import ish.oncourse.API;

/**
 * Each MessagePerson object (the delivery record) starts as queued and then
 * is processed by a special process which wakes up every few seconds.
 * 
 */
@API
public enum MessageStatus implements DisplayableExtendedEnumeration<Integer> {

	/**
	 */
	@API
	QUEUED(1, "queued"),
	
	/**
	 */
	@API
	SENT(2, "sent"),

	/**
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
