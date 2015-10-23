/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.common.types;

import ish.common.util.DisplayableExtendedEnumeration;
import ish.oncourse.API;

import java.util.Arrays;
import java.util.List;

/**
 * General ledger accounts have a type that defines their behaviour. This has two main effects:
 * 
 * * filtering the accounts so they are only available in appropriate places (eg. income for student fee)
 * * ensuring negative and positive signs are applied properly to balanced transaction pairs
 * 
 */
@API
public enum AccountType implements DisplayableExtendedEnumeration<Integer> {

	/**
	 * Database value: 1
	 */
	@API
	ASSET(1, "asset"),

	/**
	 * Database value: 2
	 */
	@API
	LIABILITY(2, "liability"),

	/**
	 * Database value: 3
	 */
	@API
	EQUITY(3, "equity"),

	/**
	 * Database value: 4
	 */
	@API
	INCOME(4, "income"),

	/**
	 * Database value: 5
	 * 
	 * COS is a type of expense.
	 */
	@API
	COS(5, "COS"),

	/**
	 */
	@API
	EXPENSE(6, "expense");

	public static final List<AccountType> DEBIT_TYPES = Arrays.asList(ASSET, COS, EQUITY, EXPENSE);

	public static final List<AccountType> CREDIT_TYPES = Arrays.asList(LIABILITY, INCOME);

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
