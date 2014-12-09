/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.common.types;

import ish.common.util.DisplayableExtendedEnumeration;

/**
 * enumeration of available payment types
 * 
 * @PublicApi
 */
public enum PaymentType implements DisplayableExtendedEnumeration<Integer> {

	/**
	 * payments made in cash
	 * 
	 * @PublicApi
	 */
	CASH(0, "Cash"),
	
	/**
	 * payments made with cheque
	 * 
	 * @PublicApi
	 */
	CHEQUE(1, "Cheque"),
	
	/**
	 * payments made with credit card
	 * 
	 * @PublicApi
	 */
	CREDIT_CARD(2, "Credit card"),
	
	/**
	 * payments made by any means of electronic fund transfer (bank transfer etc.)
	 * 
	 * @PublicApi
	 */
	
	EFT(3, "EFT"),
	/**
	 * payments made using b-pay
	 * 
	 * @PublicApi
	 */
	BPAY(4, "B-Pay"),
	
	/**
	 * special payment type for payments automatically created, but having $0 total
	 * 
	 * @PublicApi
	 */
	INTERNAL(5, "Zero"),
	
	/**
	 * other types of payment (internal accounts transfer etc)
	 * 
	 * @PublicApi
	 */
	OTHER(6, "Other"),
	
	/**
	 * special payment created during class/enrolment refund. used to balance out the original and refund invoice.
	 * 
	 * @PublicApi
	 */
	CONTRA(7, "Contra"),
	
	/**
	 * special payment type created during voucher redemption.
	 * 
	 * @PublicApi
	 */
	VOUCHER(8, "Voucher"),
	
	/**
	 * payments using paypal
	 * 
	 * @PublicApi
	 */
	PAYPAL(9, "PayPal"),
	
	/**
	 * special payment type used to link original and reverse invoices during payment cancellation
	 * 
	 * @PublicApi
	 */
	REVERSE(10, "Reverse"),
	
	/**
	 * EFTPOS payments
	 * 
	 * @PublicApi
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
