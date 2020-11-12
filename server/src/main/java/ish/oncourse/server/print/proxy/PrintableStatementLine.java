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
package ish.oncourse.server.print.proxy;

import ish.common.types.PaymentStatus;
import ish.common.types.PaymentType;
import ish.math.Money;
import ish.oncourse.cayenne.ContactInterface;
import ish.oncourse.cayenne.FinancialItem;
import ish.oncourse.cayenne.InvoiceInterface;
import ish.oncourse.cayenne.PaymentInterface;
import ish.oncourse.server.cayenne.Invoice;
import ish.oncourse.server.cayenne.PaymentIn;
import ish.oncourse.server.cayenne.PaymentOut;
import ish.oncourse.server.cayenne.glue.CayenneDataObject;
import ish.print.PrintableObject;
import ish.util.ContactUtils;

import java.time.LocalDate;
import java.util.Date;

public class PrintableStatementLine extends CayenneDataObject implements PrintableObject, FinancialItem {

	public static final String PAYMENT_TYPE = "paymentType";

	private FinancialItemType itemType;
	private PaymentStatus status;
	private PaymentType paymentType;

	private LocalDate date;
	private String description;
	private Money amount;
	private Money total;
	private Date createdOn;


	/**
	 * {@link ContactUtils#getFinancialItems(ContactInterface, Class)}
	 */
	public PrintableStatementLine(PaymentInterface payment) {
		status = payment.getStatus();
		amount = payment.getAmount();
		date = payment instanceof PaymentIn ? ((PaymentIn) payment).getPaymentDate() : ((PaymentOut) payment).getPaymentDate() ;
		createdOn = payment.getCreatedOn();
		paymentType = payment.getPaymentMethod().getType();

		if (PaymentInterface.TYPE_IN.equals(payment.getTypeOfPayment())) {
			var paymentIn = (PaymentIn) payment;
			itemType = FinancialItemType.PAYMENT_IN;
			description = "PaymentIn " + paymentIn.getId();
		} else {
			var paymentOut = (PaymentOut) payment;
			itemType = FinancialItemType.PAYMENT_OUT;
			description = "PaymentOut " + paymentOut.getId();

		}
	}

	/**
	 * {@link ContactUtils#getFinancialItems(ContactInterface, Class)}
	 */
	public PrintableStatementLine(InvoiceInterface iInvoice) {
		itemType = FinancialItemType.INVOICE;
		var invoice = (Invoice) iInvoice;
		date  = invoice.getInvoiceDate();
		createdOn = ((Invoice) iInvoice).getCreatedOn();
		amount = invoice.getTotalIncTax();
		description = String.format("%s %d", amount.isNegative() ? "Credit note " : "Invoice ", invoice.getInvoiceNumber());
	}


	/**
	 * @see PrintableObject#getShortRecordDescription()
	 */
	public String getShortRecordDescription() {
		return null;
	}

	/**
	 * @see PrintableObject#getValueForKey(String)
	 */
	@Override
	public Object getValueForKey(String key) {
		switch (key) {
			case DATE:
				return getDate();
			case DESCRIPTION:
				return getDescription();
			case AMOUNT:
				return getAmount();
			case TOTAL:
				return getTotal();
			case PAYMENT_TYPE:
				return getPaymentType();
			case FinancialItem.CREATED_ON:
				return getCreatedOn();
			default:
				return null;
		}
	}

	@Override
	public LocalDate getDate() {
		return date;
	}

	public String getDescription() {
		return description;
	}

	@Override
	public Money getAmount() {
		return amount;
	}

	public Money getTotal() {
		return total;
	}

	@Override
	public FinancialItemType getFinancialItemType() {
		return itemType;
	}

	@Override
	public PaymentStatus getPaymentStatus() {
		return status;
	}

	public PaymentType getPaymentType() {
		return paymentType;
	}

	@Override
	public void setTotal(Money total) {
		this.total = total;
	}

	@Override
	public Date getCreatedOn() {
		return createdOn;
	}
}
