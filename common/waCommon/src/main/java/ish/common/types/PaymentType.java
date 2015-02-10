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
	 * Database value: 0
	 * 
	 * @PublicApi
	 */
	CASH(0, "Cash"),
	
	/**
	 * Payments made with cheque.  These payments will need to be manually banked.
	 * Database value: 1
	 * 
	 * @PublicApi
	 */
	CHEQUE(1, "Cheque"),
	
	/**
	 * Payments made with credit card.  These payments will need to be manually banked if the
	 * credit card gateway is not enabled. They will be marked as banked automatically if
	 * the gateway is available and the payment is successful.
	 * Database value: 2
	 * 
	 * @PublicApi
	 */
	CREDIT_CARD(2, "Credit card"),
	
	/**
	 * Payments made by any means of electronic fund transfer (bank transfer etc.).  These payments will need to be manually banked.
	 * Database value: 3
	 * 
	 * @PublicApi
	 */
	
	EFT(3, "EFT"),
	/**
	 * payments made using b-pay.  These payments will need to be manually banked.
	 * Database value: 4
	 * 
	 * @PublicApi
	 */
	BPAY(4, "B-Pay"),
	
	/**
	 * A zero dollar payment which can be created for $0 invoices under some circumstances.
	 * Database value: 5
	 * 
	 * @PublicApi
	 */
	INTERNAL(5, "Zero"),
	
	/**
	 * Other types of payment (internal accounts transfer etc).  These payments will need to be manually banked.
	 * Database value: 6
	 * 
	 * @PublicApi
	 */
	OTHER(6, "Other"),
	
	/**
	 * Special payment created during class/enrolment refund. used to balance out the original and refund invoice. They aren't real
	 * payments but represent a link between an invoice and a credit note where payment from one was applied to the other.
	 * Database value: 7
	 * 
	 * @PublicApi
	 */
	CONTRA(7, "Contra"),
	
	/**
	 * special payment type created during voucher redemption. Banking is not needed.
	 * Database value: 8
	 * 
	 * @PublicApi
	 */
	VOUCHER(8, "Voucher"),
	
	/**
	 * payments using paypal.  These payments will need to be manually banked.
	 * Database value: 9
	 * 
	 * @PublicApi
	 */
	PAYPAL(9, "PayPal"),
	
	/**
	 * special payment type used to link original and reverse invoices during payment cancellation or failure.
	 * This type cannot be created manually, but will appear when there is a failure during a payment transaction with the bank
	 * or some other transaction processing error.
	 * Database value: 10
	 * 
	 * @PublicApi
	 */
	REVERSE(10, "Reverse"),
	
	/**
	 * EFTPOS payments.  These payments will need to be manually banked.
	 * Database value: 11
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
