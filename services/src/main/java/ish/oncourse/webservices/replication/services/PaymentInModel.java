/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.webservices.replication.services;

import ish.oncourse.model.Enrolment;
import ish.oncourse.model.Invoice;
import ish.oncourse.model.PaymentIn;

import java.util.ArrayList;
import java.util.List;

public class PaymentInModel {

	private PaymentIn paymentIn;

	private List<Invoice> invoices = new ArrayList<>();
	private List<Enrolment> enrolments = new ArrayList<>();
	
	public void setPaymentIn(PaymentIn paymentIn) {
		this.paymentIn = paymentIn;
	}
	
	public PaymentIn getPaymentIn() {
		return paymentIn;
	}

	public List<Invoice> getInvoices() {
		return invoices;
	}

	public List<Enrolment> getEnrolments() {
		return enrolments;
	}

	
}
