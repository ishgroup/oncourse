package ish.oncourse.enrol.checkout.model;

import ish.math.Money;
import ish.oncourse.enrol.checkout.PurchaseController;
import ish.oncourse.enrol.services.invoice.IInvoiceProcessingService;
import ish.oncourse.model.*;
import org.apache.cayenne.ObjectContext;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
public class CreatePaymentPlanInvoiceNode {
	private Enrolment enrolment;
	private Contact contact;
	private WebSite webSite;
	private PaymentIn paymentIn;
	private College college;
	private IInvoiceProcessingService invoiceProcessingService;

	private ObjectContext objectContext;

	public InvoiceNode create() {

		Invoice invoice = CreateInvoice.valueOf(college, contact, webSite, objectContext).create();

		InvoiceLine invoiceLine = invoiceProcessingService.createInvoiceLineForEnrolment(enrolment,  contact.getTaxOverride());
		invoiceLine.setInvoice(invoice);
		invoiceLine.setEnrolment(enrolment);

		PaymentInLine paymentInLine = initPaymentInLine();
		paymentInLine.setInvoice(invoice);

		List<InvoiceDueDate> selectedDueDates = PaymentPlanBuilder.valueOf(enrolment).build().getSelectedDueDates();
		return InvoiceNode.valueOf(invoice, paymentInLine, invoiceLine, enrolment, selectedDueDates, null);
	}

	private PaymentInLine initPaymentInLine() {
		PaymentInLine paymentInLine = objectContext.newObject(PaymentInLine.class);
		paymentInLine.setCollege(college);
		paymentInLine.setPaymentIn(paymentIn);
		paymentInLine.setAmount(Money.ZERO);
		return paymentInLine;
	}

	public static CreatePaymentPlanInvoiceNode valueOf(Enrolment enrolment, PurchaseController purchaseController) {
		CreatePaymentPlanInvoiceNode result = new CreatePaymentPlanInvoiceNode();
		result.enrolment = enrolment;
		result.contact = purchaseController.getModel().getPayer();
		result.webSite = purchaseController.getModel().getWebSite();
		result.paymentIn = purchaseController.getModel().getPayment();
		result.college = purchaseController.getModel().getCollege();
		result.objectContext = purchaseController.getModel().getObjectContext();
		result.invoiceProcessingService = purchaseController.getInvoiceProcessingService();
		return result;
	}
}
