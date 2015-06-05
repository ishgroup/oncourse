/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */

package ish.oncourse.enrol.checkout.model;

import ish.math.Money;
import ish.oncourse.model.*;
import org.apache.cayenne.ObjectContext;
import org.apache.commons.lang3.time.DateUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class PaymentPlanBuilder {

	private Enrolment enrolment;
	private College college;
	private InvoiceLine invoiceLine;
	private Invoice invoice;
	private ObjectContext objectContext;

	private List<InvoiceDueDate> selectedDueDates;

	public PaymentPlanBuilder build() {

		// remove previous due dates
		objectContext.deleteObjects(invoice.getInvoiceDueDates());

		Money amountLeftToPay = enrolment.getInvoiceLines().get(0).getDiscountedPriceTotalIncTax();

		InvoiceDueDate lastDueDate = null;

		List<CourseClassPaymentPlanLine> paymentPlanLines = enrolment.getCourseClass().getPaymentPlanLines();
		CourseClassPaymentPlanLine.DAY_OFFSET.asc().orderList(paymentPlanLines);

		Date now = new Date();

		for (CourseClassPaymentPlanLine planLine : paymentPlanLines) {
			Money amount = amountLeftToPay.isGreaterThan(planLine.getAmount()) ? planLine.getAmount() : amountLeftToPay;

			Date dueDate;

			if (planLine.getDayOffset() == null) {
				dueDate = now;
			} else if (enrolment.getCourseClass().getStartDate() == null) {
				dueDate = DateUtils.addDays(now, planLine.getDayOffset());
			} else {
				dueDate = DateUtils.addDays(enrolment.getCourseClass().getStartDate(), planLine.getDayOffset());
			}

			// create invoice due lines only for non zero amounts
			if (!amount.isZero()) {
				lastDueDate = createDueDate(amount, dueDate, invoice);
			}

			amountLeftToPay = amountLeftToPay.subtract(amount);
		}

		// if something is still left to be paid - assign this to the last payment
		if (amountLeftToPay.isGreaterThan(Money.ZERO) && lastDueDate != null) {
			lastDueDate.setAmount(lastDueDate.getAmount().add(amountLeftToPay));
		}

		InvoiceDueDate.DUE_DATE.asc().orderList(invoice.getInvoiceDueDates());

		selectedDueDates = new ArrayList<>();

		for (InvoiceDueDate dueDate : invoice.getInvoiceDueDates()) {
			if (DateUtils.truncatedCompareTo(dueDate.getDueDate(), now, Calendar.DAY_OF_MONTH) <= 0) {
				selectedDueDates.add(dueDate);
			}
		}
		return this;
	}

	private InvoiceDueDate createDueDate(Money amount, Date date, Invoice invoice) {
		InvoiceDueDate dueDate = objectContext.newObject(InvoiceDueDate.class);

		dueDate.setCollege(college);
		dueDate.setInvoice(invoice);
		dueDate.setAmount(amount);
		dueDate.setDueDate(date);

		return dueDate;
	}

	public List<InvoiceDueDate> getSelectedDueDates() {
		return selectedDueDates;
	}

	public static PaymentPlanBuilder valueOf(Enrolment enrolment) {
		PaymentPlanBuilder paymentPlanBuilder = new PaymentPlanBuilder();
		paymentPlanBuilder.enrolment = enrolment;
		paymentPlanBuilder.college = enrolment.getCollege();
		paymentPlanBuilder.invoiceLine = enrolment.getInvoiceLines().get(0);
		paymentPlanBuilder.invoice = paymentPlanBuilder.invoiceLine.getInvoice();
		paymentPlanBuilder.objectContext = enrolment.getObjectContext();
		return paymentPlanBuilder;
	}
}
