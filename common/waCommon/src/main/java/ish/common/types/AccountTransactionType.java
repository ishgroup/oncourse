/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.common.types;

import ish.common.util.DisplayableExtendedEnumeration;

/**
 * enumeration of available types of account transactions
 * 
 * @author marcin
 */
public enum AccountTransactionType implements DisplayableExtendedEnumeration<String> {

	INVOICE_LINE("I", "Invoice line"),
	PAYMENT_IN_LINE("P", "Payment in line"),
	PAYMENT_OUT_LINE("O", "Payment out line"),
	PURCHASE_LINE("U", "Purchase line"),
	DEPRECIATION("D", "Depreciation"),
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
