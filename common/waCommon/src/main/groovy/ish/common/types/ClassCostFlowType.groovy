/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
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