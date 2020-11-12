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
 * Different types of payment
 *
 */
@API
public enum PaymentType implements DisplayableExtendedEnumeration<Integer> {

	/**
	 * Payments made in cash. These payments will need to be manually banked.
	 *
	 * Database value: 0
	 *
	 */
	@API
	CASH(0, "Cash"),

	/**
	 * Payments made with cheque.  These payments will need to be manually banked.
	 *
	 * Database value: 1
	 *
	 */
	@API
	CHEQUE(1, "Cheque"),

	/**
	 * Payments made with credit card.  These payments will need to be manually banked if the
	 * credit card gateway is not enabled. They will be marked as banked automatically if
	 * the gateway is available and the payment is successful.
	 *
	 * Database value: 2
	 *
	 */
	@API
	CREDIT_CARD(2, "Credit card"),

	/**
	 * Payments made by any means of electronic fund transfer (bank transfer etc.).  These payments will need to be manually banked.
	 *
	 * Database value: 3
	 *
	 */
	@API
	EFT(3, "EFT"),

	/**
	 * Payments made using b-pay.  These payments will need to be manually banked.
	 *
	 * Database value: 4
	 *
	 */
	@API
	BPAY(4, "B-Pay"),

	/**
	 * Internal payments (also known as "zero payments") are created by the system automatically, and cannot be created
	 * manually. They will be seen when you create an invoice in QuickEnrol or on the website where there is no payment
	 * and the payment is just created as a holding object to keep the workflow consistent with other regular payments.
	 *
	 * Additionally if you use a dollar type voucher, and there is no additional monetary payment, then an Internal
	 * payment will also be created with a $0 value.
	 *
	 * Payments of this type always have a total of $0 even though they may have payment lines which are not $0.
	 *
	 * Database value: 5
	 *
	 */
	@API
	INTERNAL(5, "Zero"),

	/**
	 * Other types of payment (internal accounts transfer etc)
	 * Database value: 6
	 *
	 */
	@API
	OTHER(6, "Other"),

	/**
	 * Contra payments are created by a user manually in the onCourse user interface. They are created by cancelling out
	 * a credit note (or part of an invoice) against an invoice (or part of an invoice).
	 *
	 * Contra payments can also be created when you cancel an enrolment, the invoice for that enrolment is unpaid and you
	 * choose to issue a credit note. Then the credit note and original invoice are linked by a Contra Payment automatically.
	 *
	 * Payments of this type always have a total of $0 even though they will have payment lines which are not $0.
	 *
	 * Database value: 7
	 *
	 */
	@API
	CONTRA(7, "Contra"),

	/**
	 * A special payment type created during voucher redemption. Banking is never needed.
	 *
	 * Database value: 8
	 *
	 */
	@API
	VOUCHER(8, "Voucher"),

	/**
	 * payments using paypal.  These payments will need to be manually banked.
	 * Database value: 9
	 *
	 */
	@API
	PAYPAL(9, "PayPal"),

	/**
	 * A reversal is a special payment type used to link original and reversed invoices during payment cancellation or failure.
	 * This type cannot be created manually, but will appear when there is a failure during a payment transaction with the bank
	 * or some other transaction processing error.
	 *
	 * In case of credit card failure, the operator is given a choice to keep the current invoice without payment,
	 * or cancel the enrolment and issue a credit note. In this case a REVERSE payment is created to link the invoice
	 * with the credit note and cancel out any amount owing.
	 *
	 * Payments of this type always have a total of $0 even though they will have paymentLines which are not $0.
	 *
	 * Database value: 10
	 *
	 */
	@API
	REVERSE(10, "Reverse"),

	/**
	 * EFTPOS payments.  These payments will need to be manually banked.
	 *
	 * Database value: 11
	 *
	 */
	@API
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
