/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.common.types;

import ish.common.util.DisplayableExtendedEnumeration;

/**
 * Each class cost has a type which determines its behaviour in the budget.
 * 
 * @PublicApi
 */
public enum ClassCostFlowType implements DisplayableExtendedEnumeration<Integer> {

	/**
	 * A regular expense in the budget, other than wages.
	 * 
	 * Database value: 0
	 * @PublicApi
	 */
	EXPENSE(0, "Expense/Cost"),

	/**
	 * Additional funding other than class fees.
	 * 
	 * Database value: 1
	 * @PublicApi
	 */
	INCOME(1, "Income/Funding"),

	/**
	 * Money paid to tutors which will appear on the payroll export.
	 * 
	 * Database value: 2
	 * @PublicApi
	 */
	WAGES(2, "Wages"),

	/**
	 * Discounts expected to be offered. 
	 * 
	 * Database value: 3
	 * @PublicApi
	 */
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