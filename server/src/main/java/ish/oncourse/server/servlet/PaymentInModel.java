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
package ish.oncourse.server.servlet;

import ish.oncourse.server.cayenne.Enrolment;
import ish.oncourse.server.cayenne.Invoice;
import ish.oncourse.server.cayenne.PaymentIn;
import ish.payment.PaymentInRequest;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.ObjectId;
import org.apache.cayenne.query.SelectById;

import java.util.ArrayList;
import java.util.List;

public class PaymentInModel {



	private PaymentIn paymentIn;

	private List<Invoice> invoices = new ArrayList<>();
	private List<Enrolment> enrolments = new ArrayList<>();

	private PaymentInModel(){}

	public static PaymentInModel valueOf(PaymentInRequest request,  ObjectContext context) {
		var model = new PaymentInModel();

		model.setPaymentIn(SelectById.query(PaymentIn.class, request.getPaymentIn()).selectOne(context));

		for (var invoiceId : request.getInvoices()) {
			model.getInvoices().add(SelectById.query(Invoice.class, invoiceId).selectOne(context));
		}

		for (var enrolmentId : request.getEnrolments()) {
			model.getEnrolments().add(SelectById.query(Enrolment.class, enrolmentId).selectOne(context));
		}

		return model;
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

	private void setPaymentIn(PaymentIn paymentIn) {
		this.paymentIn = paymentIn;
	}

}
