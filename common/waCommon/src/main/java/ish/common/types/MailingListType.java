/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.common.types;

import ish.common.util.DisplayableExtendedEnumeration;

/**
 * @PublicApi
 */
public enum MailingListType implements DisplayableExtendedEnumeration<Integer> {

	/**
	 * @PublicApi
	 */
	TYPE_UNDELIVERABLE(1, "Undeliverable"),
	
	/**
	 * @PublicApi
	 */
	TYPE_MANUAL(2, "Manual"),

	/**
	 * @PublicApi
	 */
	TYPE_BATCH(3, "Batch"),

	/**
	 * @PublicApi
	 */
	TYPE_WEB(4, "Web");

	private String displayName;
	private int value;

	private MailingListType(int value, String displayName) {
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
