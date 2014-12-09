/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.common.types;

import ish.common.util.DisplayableExtendedEnumeration;

import java.util.Arrays;
import java.util.List;

/**
 * enumeration of account types available
 * 
 * @PublicApi
 */
public enum AccountType implements DisplayableExtendedEnumeration<Integer> {

	/**
	 * @PublicApi
	 */
	ASSET(1, "asset"),

	/**
	 * @PublicApi
	 */
	LIABILITY(2, "liability"),

	/**
	 * @PublicApi
	 */
	EQUITY(3, "equity"),

	/**
	 * @PublicApi
	 */
	INCOME(4, "income"),

	/**
	 * @PublicApi
	 */
	COS(5, "COS"),

	/**
	 * @PublicApi
	 */
	EXPENSE(6, "expense");

	public static final List<AccountType> DEBIT_TYPES = Arrays.asList(ASSET, COS, EQUITY);

	public static final List<AccountType> CREDIT_TYPES = Arrays.asList(LIABILITY, INCOME, EXPENSE);

	private String displayName;
	private int value;

	private AccountType(int value, String displayName) {
		this.value = value;
		this.displayName = displayName;
	}

	/**
	 * @see ish.common.util.DisplayableExtendedEnumeration#getDatabaseValue()
	 */
	@Override
	public Integer getDatabaseValue() {
		return this.value;
	}

	/**
	 * @see ish.common.util.DisplayableExtendedEnumeration#getDisplayName()
	 */
	@Override
	public String getDisplayName() {
		return this.displayName;
	}

	/**
	 * @see java.lang.Enum#toString()
	 */
	@Override
	public String toString() {
		return getDisplayName();
	}
}
