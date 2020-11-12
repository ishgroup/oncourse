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
package ish.payment;

import org.apache.cayenne.ObjectId;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PaymentInRequest implements Serializable {

	private ObjectId paymentInId;

	private List<ObjectId> invoicesIds = new ArrayList<>();
	private List<ObjectId> enrolmentsIds = new ArrayList<>();



	public void setPaymentIn(ObjectId paymentInId) {
		this.paymentInId = paymentInId;
	}

	public ObjectId getPaymentIn() {
		return paymentInId;
	}

	public List<ObjectId> getInvoices() {
		return invoicesIds;
	}

	public List<ObjectId> getEnrolments() {
		return enrolmentsIds;
	}

	public void addInvoice(ObjectId objectId) {
		invoicesIds.add(objectId);
	}

	public void setInvoices(List<ObjectId> invoices) {
		invoicesIds = invoices;
	}

	public void addEnrolment(ObjectId objectId) {
		enrolmentsIds.add(objectId);
	}

	public void setEnrolments(List<ObjectId> enrolments) {
		enrolmentsIds = enrolments;
	}
}
