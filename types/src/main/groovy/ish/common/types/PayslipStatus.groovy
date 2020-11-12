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
 * Payslips go through a workflow set of statuses.
 *
 */
@API
public enum PayslipStatus implements DisplayableExtendedEnumeration<Integer> {

	/**
	 * These payslips are still being edited.
	 * Database value: 1
	 *
	 */
	@API
	HOLLOW(1, "New"),

	/**
	 * These payslips are marked as complete and ready to be approved.
	 * Database value: 2
	 *
	 */
	@API
	COMPLETED(2, "Completed"),

	/**
	 * Once approved by an onCourse user with sufficient permissions, the payslip is ready to be exported.
	 * Database value: 3
	 *
	 */
	@API
	APPROVED(3, "Approved"),

	/**
	 * Once exported, payslips may no longer be edited.
	 * Database value: 4
	 *
	 */
	@API
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
