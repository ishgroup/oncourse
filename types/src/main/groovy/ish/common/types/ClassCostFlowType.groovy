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
 * Each class cost has a type which determines its behaviour in the budget.
 *
 */
@API
public enum ClassCostFlowType implements DisplayableExtendedEnumeration<Integer> {

	/**
	 * Database value: 0
	 *
	 * A regular expense in the budget, other than wages.
	 */
	@API
	EXPENSE(0, "Expense/Cost"),

	/**
	 * Database value: 1
	 *
	 * Additional funding other than class fees.
	 */
	@API
	INCOME(1, "Income/Funding"),

	/**
	 * Database value: 2
	 *
	 * Money paid to tutors which will appear on the payroll export.
	 */
	@API
	WAGES(2, "Wages"),

	/**
	 * Database value: 3
	 *
	 * Discounts expected to be offered.
	 */
	@API
	DISCOUNT(3, "Discount");

	private String displayName;
	private int value;

	private ClassCostFlowType(int value, String displayName) {
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
