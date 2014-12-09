/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.common.types;

import ish.common.util.DisplayableExtendedEnumeration;

/**
 * @PublicApi
 */
public enum ConfirmationStatus implements DisplayableExtendedEnumeration<Integer> {

	/**
	 * @PublicApi
	 */
	NOT_SENT(1, "Not sent"),

	/**
	 * @PublicApi
	 */
	SENT(2, "Sent"),

	/**
	 * @PublicApi
	 */
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
