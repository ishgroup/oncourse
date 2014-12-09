/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.common.types;

import ish.common.util.DisplayableExtendedEnumeration;

/**
 * enumeration of available message types
 * 
 * @PublicApi
 */
public enum MessageType implements DisplayableExtendedEnumeration<Integer> {

	/**
	 * @PublicApi
	 */
	EMAIL(1, "email"),
	
	/**
	 * @PublicApi
	 */
	SMS(2, "sms"),

	/**
	 * @PublicApi
	 */
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
