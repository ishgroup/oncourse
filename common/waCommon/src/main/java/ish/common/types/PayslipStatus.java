/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.common.types;

import ish.common.util.DisplayableExtendedEnumeration;

/**
 * @author marcin
 */
public enum PayslipStatus implements DisplayableExtendedEnumeration<Integer> {

	HOLLOW(1, "New"),
	COMPLETED(2, "Completed"),
	APPROVED(3, "Approved"),
	FINALISED(4, "Paid/Exported");

	private String displayName;
	private int value;

	private PayslipStatus(int value, String displayName) {
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
