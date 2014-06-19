/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.common.types;

import ish.common.util.DisplayableExtendedEnumeration;

/**
 * enumeration of available payment types
 */
public enum PaymentType implements DisplayableExtendedEnumeration<Integer> {

	/**
	 * payments made in cash
	 */
	CASH(0, "Cash"),
	/**
	 * payments made with cheque
	 */
	CHEQUE(1, "Cheque"),
	/**
	 * payments made with credit card
	 */
	CREDIT_CARD(2, "Credit card"),
	/**
	 * payments made by any means of electronic fund transfer (bank transfer etc.)
	 */
	EFT(3, "EFT"),
	/**
	 * payments made using b-pay
	 */
	BPAY(4, "B-Pay"),
	/**
	 * special payment type for payments automatically created, but having $0 total
	 */
	INTERNAL(5, "Zero"),
	/**
	 * other types of payment (internal accounts transfer etc)
	 */
	OTHER(6, "Other"),
	/**
	 * special payment created during class/enrolment refund. used to balance out the original and refund invoice.
	 */
	CONTRA(7, "Contra"),
	/**
	 * special payment type created during voucher redemption.
	 */
	VOUCHER(8, "Voucher"),
	/**
	 * payments using paypal
	 */
	PAYPAL(9, "PayPal"),
	/**
	 * special payment type used to link original and reverse invoices during payment cancellation
	 */
	REVERSE(10, "Reverse"),
	/**
	 * EFTPOS payments
	 */
	EFTPOS(11, "EFTPOS");

	private String displayName;
	private int value;

	private PaymentType(int value, String displayName) {
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
