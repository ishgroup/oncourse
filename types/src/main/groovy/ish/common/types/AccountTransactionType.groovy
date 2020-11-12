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
 * General ledger Account Transactions have a 'type' depending on which entity created them.
 *
 */
@API
public enum AccountTransactionType implements DisplayableExtendedEnumeration<String> {

	/**
	 * Database value: I Transaction attached to Invoice line (e.g. payments performed during Quick Enrol or web enrol)
	 */
	@API
	INVOICE_LINE("I", "Invoice line"),

	/**
	 * Database value: P Transactions created by performing Payment In (e.g. payments for unpaid invoices)
	 */
	@API
	PAYMENT_IN_LINE("P", "Payment in line"),

	/**
	 * Database value: O Transactions created by performing Payment Out (e.g. payments for credit notes)
	 */
	@API
	PAYMENT_OUT_LINE("O", "Payment out line"),

	/**
	 * Database value: U
	 */
	@API
	PURCHASE_LINE("U", "Purchase line"),

	/**
	 * Database value: D
	 */
	@API
	DEPRECIATION("D", "Depreciation"),

	/**
	 * Database value: J Transaction added using 'Create GL journal entry' from Financial menu
	 */
	@API
	JOURNAL("J", "Journal");

	private String displayName;
	private String value;

	private AccountTransactionType(String value, String displayName) {
		this.value = value;
		this.displayName = displayName;
	}

	/**
	 * @see ish.common.util.DisplayableExtendedEnumeration#getDatabaseValue()
	 */
	@Override
	public String getDatabaseValue() {
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
