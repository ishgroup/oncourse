package ish.oncourse.enrol.checkout;

import ish.oncourse.model.Invoice;
import ish.oncourse.model.InvoiceLine;
import ish.oncourse.model.PaymentInLine;
import ish.oncourse.model.Queueable;

import java.util.HashSet;
import java.util.Set;

/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
public class GetInvoiceTransaction {
	private Invoice invoice;
	private Set<Queueable> queueables = new HashSet<>();

	public Set<Queueable>  get() {
		queueables.add(invoice);
		queueables.addAll(invoice.getInvoiceDueDates());
		queueables.add(invoice.getContact());

		//payment relations
		queueables.addAll(invoice.getPaymentInLines());
		for (PaymentInLine line: invoice.getPaymentInLines()) {
			queueables.add(line.getPaymentIn());
			queueables.add(line.getPaymentIn().getContact());
		}


		for (Invoice ri: invoice.getRefundedInvoices()) {
			if (!queueables.contains(ri)) {
				queueables.addAll(GetInvoiceTransaction.valueOf(ri, queueables).get());
			}
		}


		//get invoice lines relations
		queueables.addAll(invoice.getInvoiceLines());
		for (InvoiceLine line: invoice.getInvoiceLines()) {
			if (line.getEnrolment() != null) {
				queueables.add(line.getEnrolment());
				queueables.add(line.getEnrolment().getStudent().getContact());
			}
			queueables.addAll(line.getProductItems());
		}
		return queueables;
	}

	public static GetInvoiceTransaction valueOf(Invoice invoice, Set<Queueable> queueables) {
		GetInvoiceTransaction result = new GetInvoiceTransaction();
		result.invoice = invoice;
		result.queueables.addAll(queueables);
		return result;
	}
}
