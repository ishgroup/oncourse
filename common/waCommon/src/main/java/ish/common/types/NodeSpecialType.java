/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.common.types;

import ish.common.util.DisplayableExtendedEnumeration;

/**
 * @PublicApi
 */
public enum NodeSpecialType implements DisplayableExtendedEnumeration<Integer> {

	/**
	 * @PublicApi
	 */
	SUBJECTS(1, "Subjects"),

	/**
	 * @PublicApi
	 */
	HOME_WEBPAGE(2, "Home web page"),

	/**
	 * @PublicApi
	 */
	MAILING_LISTS(3, "Mailing lists"),

	/**
	 * @PublicApi
	 */
	PAYROLL_WAGE_INTERVALS(4, "Payroll wage intervals");

	private String displayName;
	private int value;

	private NodeSpecialType(int value, String displayName) {
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
