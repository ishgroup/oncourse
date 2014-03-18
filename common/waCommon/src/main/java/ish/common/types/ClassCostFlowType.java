/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.common.types;

import ish.common.util.DisplayableExtendedEnumeration;

/**
 * @author marcin
 */
public enum ClassCostFlowType implements DisplayableExtendedEnumeration<Integer> {

	EXPENSE(0, "Expense/Cost"),
	INCOME(1, "Income/Funding"),
	WAGES(2, "Wages"),
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