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
	 * Database value: 1
	 * 
	 * @PublicApi
	 */
	HOLLOW(1, "New"),
	
	/**
	 * These payslips are marked as complete and ready to be approved.
	 * Database value: 2
	 * 
	 * @PublicApi
	 */
	COMPLETED(2, "Completed"),

	/**
	 * Once approved by an onCourse user with sufficient permissions, the payslip is ready to be exported.
	 * Database value: 3
	 * 
	 * @PublicApi
	 */
	APPROVED(3, "Approved"),

	/**
	 * Once exported, payslips may no longer be edited.
	 * Database value: 4
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
