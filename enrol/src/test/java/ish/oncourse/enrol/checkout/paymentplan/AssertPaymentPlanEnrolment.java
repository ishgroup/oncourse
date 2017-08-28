package ish.oncourse.enrol.checkout.paymentplan;

import ish.common.types.PaymentType;
import ish.math.Money;
import ish.oncourse.enrol.checkout.AssertEnabledEnrolments;
import ish.oncourse.enrol.checkout.PurchaseController;
import ish.oncourse.enrol.checkout.model.InvoiceNode;
import ish.oncourse.enrol.checkout.model.PurchaseModel;
import ish.oncourse.model.Contact;
import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Enrolment;
import ish.oncourse.model.PaymentInLine;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
public class AssertPaymentPlanEnrolment {
	private Enrolment enrolment;
	private PurchaseModel model;
	private PurchaseController purchaseController;

	public void assertValue(){
		CourseClass courseClass = enrolment.getCourseClass();
		Contact contact = enrolment.getStudent().getContact();

		//assert Enrolment
		assertEquals(1, purchaseController.getModel().getEnabledEnrolments(contact).size());
		assertEquals(0, purchaseController.getModel().getDisabledEnrolments(contact).size());
		AssertEnabledEnrolments.valueOf(contact, 1, purchaseController).assertValue();

		//assert Invoices
		InvoiceNode invoiceNode = purchaseController.getModel().getPaymentPlanInvoiceBy(enrolment);
		assertNotNull(invoiceNode);
		assertNotNull(invoiceNode.getEnrolment());
		assertEquals(contact, invoiceNode.getEnrolment().getStudent().getContact());

		assertNotNull(invoiceNode.getInvoiceLine());
		assertEquals(courseClass.getFeeIncGst(null), invoiceNode.getInvoiceLine().getPriceTotalIncTax());

		assertNotNull(invoiceNode.getInvoice());
		assertEquals(contact, invoiceNode.getInvoice().getContact());
		assertEquals(courseClass.getFeeIncGst(null), invoiceNode.getInvoice().getTotalGst());

		assertEquals(1, invoiceNode.getSelectedDueDates().size());
		Money amountDueDates = invoiceNode.getSelectedDueDates().get(0).getAmount();

		//assert PaymentLine
		assertNotNull(invoiceNode.getPaymentInLine());
		assertEquals(courseClass.getFeeIncGst(null), invoiceNode.getInvoiceLine().getPriceTotalIncTax());
		assertEquals(2, model.getPayment().getPaymentInLines().size());
		for (PaymentInLine paymentInLine : purchaseController.getModel().getPayment().getPaymentInLines()) {
			if (paymentInLine == invoiceNode.getPaymentInLine()) {
				assertEquals("PaymentPlan paymentInLine", amountDueDates, paymentInLine.getAmount());
			} else {
				assertEquals("default paymentInLine", Money.ZERO, paymentInLine.getAmount());
			}
		}

		//assert paymentIn
		assertEquals("Payment amount equals the dueDates's amount", amountDueDates, model.getPayment().getAmount());
		assertEquals(PaymentType.CREDIT_CARD, model.getPayment().getType());

		assertEquals(courseClass.getFeeIncGst(null), model.getTotalGst());
		assertEquals(amountDueDates, purchaseController.getPayNow());
	}

	public static AssertPaymentPlanEnrolment valueOf(Enrolment enrolment,
	                                                 PurchaseController purchaseController,
	                                                 PurchaseModel model) {
		AssertPaymentPlanEnrolment result = new AssertPaymentPlanEnrolment();
		result.enrolment = enrolment;
		result.purchaseController = purchaseController;
		result.model = model;
		return result;

	}
}
