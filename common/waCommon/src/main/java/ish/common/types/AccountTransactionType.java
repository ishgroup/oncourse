/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.common.types;

import ish.common.util.DisplayableExtendedEnumeration;

/**
 * GL transactions have a 'type' depending on which entity created them.
 * 
 * @PublicApi
 */
public enum AccountTransactionType implements DisplayableExtendedEnumeration<String> {

	/**
	 * Database value: I
	 * @PublicApi
	 */
	INVOICE_LINE("I", "Invoice line"),

	/**
	 * Database value: P
	 * @PublicApi
	 */
	PAYMENT_IN_LINE("P", "Payment in line"),

	/**
	 * Database value: O
	 * @PublicApi
	 */
	PAYMENT_OUT_LINE("O", "Payment out line"),

	/**
	 * Database value: U
	 * @PublicApi
	 */
	PURCHASE_LINE("U", "Purchase line"),

	/**
	 * Database value: D
	 * @PublicApi
	 */
	DEPRECIATION("D", "Depreciation"),

	/**
	 * Database value: J
	 * @PublicApi
	 */
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
