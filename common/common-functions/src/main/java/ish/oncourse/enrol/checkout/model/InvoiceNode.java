package ish.oncourse.enrol.checkout.model;

import ish.math.Money;
import ish.oncourse.model.*;

import java.util.List;

/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
public class InvoiceNode {
	private Invoice invoice;
	private PaymentInLine paymentInLine;
	private InvoiceLine invoiceLine;
	private Enrolment enrolment;
	private List<InvoiceDueDate> selectedDueDates;

	private Money paymentAmount;

	public Invoice getInvoice() {
		return invoice;
	}

	public PaymentInLine getPaymentInLine() {
		return paymentInLine;
	}

	public InvoiceLine getInvoiceLine() {
		return invoiceLine;
	}

	public Enrolment getEnrolment() {
		return enrolment;
	}

	public List<InvoiceDueDate> getSelectedDueDates() {
		return selectedDueDates;
	}

	public Money getMinPaymentAmount() {
		Money amount = Money.ZERO;
		for (InvoiceDueDate invoiceDueDate : this.getSelectedDueDates()) {
			amount = amount.add(invoiceDueDate.getAmount());
		}
		return amount;
	}

	public Money getPaymentAmount() {
		return paymentAmount != null ? paymentAmount: getMinPaymentAmount();
	}

	public void setPaymentAmount(Money paymentAmount) {
		this.paymentAmount = paymentAmount;
	}


	public static InvoiceNode valueOf(Invoice invoice, PaymentInLine paymentInLine, InvoiceLine invoiceLine, Enrolment enrolment,
	                                  List<InvoiceDueDate> selectedDueDates,
	                                  Money paymentAmount) {
		InvoiceNode node = new InvoiceNode();
		node.invoice = invoice;
		node.paymentInLine = paymentInLine;
		node.invoiceLine = invoiceLine;
		node.enrolment = enrolment;
		node.selectedDueDates = selectedDueDates;
		node.paymentAmount = paymentAmount;
		return node;
	}

}
