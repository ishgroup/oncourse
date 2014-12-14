/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.common.types;

import ish.common.util.DisplayableExtendedEnumeration;

/**
 * Different types of payment
 * 
 * @PublicApi
 */
public enum PaymentType implements DisplayableExtendedEnumeration<Integer> {

	/**
	 * Payments made in cash. These payments will need to be manually banked.
	 * 
	 * @PublicApi
	 */
	CASH(0, "Cash"),
	
	/**
	 * Payments made with cheque.  These payments will need to be manually banked.
	 * 
	 * @PublicApi
	 */
	CHEQUE(1, "Cheque"),
	
	/**
	 * Payments made with credit card.  These payments will need to be manually banked if the
	 * credit card gateway is not enabled. They will be marked as banked automatically if
	 * the gateway is available and the payment is successful.
	 * 
	 * @PublicApi
	 */
	CREDIT_CARD(2, "Credit card"),
	
	/**
	 * Payments made by any means of electronic fund transfer (bank transfer etc.).  These payments will need to be manually banked.
	 * 
	 * @PublicApi
	 */
	
	EFT(3, "EFT"),
	/**
	 * payments made using b-pay.  These payments will need to be manually banked.
	 * 
	 * @PublicApi
	 */
	BPAY(4, "B-Pay"),
	
	/**
	 * special payment type for payments automatically created, but having $0 total. They are not real payments but
	 * just represent a cancellation of a credit note against an invoice. They should be suppressed from most reports.
	 *  These payments will not need to be banked.
	 * 
	 * @PublicApi
	 */
	INTERNAL(5, "Zero"),
	
	/**
	 * other types of payment (internal accounts transfer etc).  These payments will need to be manually banked.
	 * 
	 * @PublicApi
	 */
	OTHER(6, "Other"),
	
	/**
	 * special payment created during class/enrolment refund. used to balance out the original and refund invoice. They
	 * work in a very similar way to "Zero" type.
	 * 
	 * @PublicApi
	 */
	CONTRA(7, "Contra"),
	
	/**
	 * special payment type created during voucher redemption. Banking is not needed.
	 * 
	 * @PublicApi
	 */
	VOUCHER(8, "Voucher"),
	
	/**
	 * payments using paypal.  These payments will need to be manually banked.
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
	 * EFTPOS payments.  These payments will need to be manually banked.
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
