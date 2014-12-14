/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.common.types;

import ish.common.util.DisplayableExtendedEnumeration;

/**
 * Payslips go through a workflow set of statuses.
 * 
 * @PublicApi
 */
public enum PayslipStatus implements DisplayableExtendedEnumeration<Integer> {

	/**
	 * These payslips are still being edited.
	 * 
	 * @PublicApi
	 */
	HOLLOW(1, "New"),
	
	/**
	 * These payslips are created and ready to be approved.
	 * 
	 * @PublicApi
	 */
	COMPLETED(2, "Completed"),

	/**
	 * The approval process has not yet been created in onCourse. But this step is a placeholder ready for that.
	 * 
	 * @PublicApi
	 */
	APPROVED(3, "Approved"),

	/**
	 * Once exported, payslips may no longer be edited.
	 * 
	 * @PublicApi
	 */
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
