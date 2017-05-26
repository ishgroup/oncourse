/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.util.payment;

import ish.oncourse.model.Enrolment;
import ish.oncourse.model.Invoice;
import ish.oncourse.model.PaymentIn;
import org.apache.cayenne.ObjectContext;

import java.util.ArrayList;
import java.util.List;

public class PaymentInModel {

	private PaymentIn paymentIn;

	private List<Invoice> invoices = new ArrayList<>();
	private List<Enrolment> enrolments = new ArrayList<>();
	private List<PaymentIn> voucherPayments = new ArrayList<>();

	
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

	public List<PaymentIn> getVoucherPayments() {
		return voucherPayments;
	}

	public ObjectContext getObjectContext() {
		return paymentIn.getObjectContext();
	}

	public static PaymentInModel valueOf(PaymentInModel model) {
		PaymentInModel result = new PaymentInModel();
		PaymentIn paymentIn = model.getPaymentIn().makeCopy();
		result.paymentIn = paymentIn;
		result.invoices = new ArrayList<>(model.invoices);
		result.enrolments = new ArrayList<>(model.enrolments);
		result.voucherPayments = new ArrayList<>(model.getVoucherPayments());
		return  result;
	}


}
