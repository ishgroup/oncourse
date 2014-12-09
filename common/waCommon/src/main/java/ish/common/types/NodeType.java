/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.common.types;

import ish.common.util.DisplayableExtendedEnumeration;

/**
 * @PublicApi
 */
public enum NodeType implements DisplayableExtendedEnumeration<Integer> {

	/**
	 * @PublicApi
	 */
	TAG(1, "Tag"),

	/**
	 * @PublicApi
	 */
	WEBPAGE(2, "Web page");

	private String displayName;
	private int value;

	private NodeType(int value, String displayName) {
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
