package ish.oncourse.enrol.checkout.paymentplan;

import ish.math.Money;
import ish.oncourse.enrol.checkout.ACheckoutTest;
import ish.oncourse.enrol.checkout.ActionChangePayNow;
import ish.oncourse.enrol.checkout.PurchaseController;
import ish.oncourse.enrol.checkout.model.InvoiceNode;
import ish.oncourse.enrol.checkout.model.PurchaseModel;
import ish.oncourse.enrol.checkout.payment.PaymentEditorDelegate;
import ish.oncourse.model.*;
import ish.oncourse.util.MoneyFormatter;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.PersistenceState;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.commons.lang3.time.DateUtils;
import org.dbunit.dataset.ReplacementDataSet;
import org.junit.Before;
import org.junit.Test;

import java.text.ParseException;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static ish.oncourse.enrol.checkout.PurchaseController.Action.changePayNow;
import static org.junit.Assert.*;

/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
public class SinglePaymentPlanEnrolmentTest extends ACheckoutTest {
	private PurchaseModel model;

	@Before
	public void setup() throws Exception {
		setup("ish/oncourse/enrol/checkout/paymentplan/SinglePaymentPlanEnrolmentTest.xml");
	}

	@Override
	protected void configDataSet(ReplacementDataSet dataSet) {
		super.configDataSet(dataSet);
		dataSet.addReplacementObject("[class1001_startDate]", DateUtils.addDays(new Date(), 1));
		dataSet.addReplacementObject("[class1001_endDate]", DateUtils.addDays(new Date(), 2));
	}


	@Test
	public void testCreditCardPayment() throws Exception {
		initEnrolment();


		Enrolment enrolment = purchaseController.getModel().getEnabledEnrolments(purchaseController.getModel().getPayer()).get(0);

		disableEnrolment(enrolment);
		enableEnrolment(enrolment);

		payNowLessThanRequired();
		payNowMoreThanRequired();
		payNowWrongValue();
		payNowCorrect();

		proceedToPayment();

		PaymentEditorDelegate delegate = purchaseController.getPaymentEditorDelegate();
		makeInvalidPayment();

		tryAgain(delegate);

		makeValidPayment();
	}

	@Test
	public void testCorporatePass() throws Exception {
		initEnrolment();


		Enrolment enrolment = purchaseController.getModel().getEnabledEnrolments(purchaseController.getModel().getPayer()).get(0);

		disableEnrolment(enrolment);
		enableEnrolment(enrolment);

		payNowLessThanRequired();
		payNowMoreThanRequired();
		payNowWrongValue();
		payNowCorrect();

		proceedToPayment();

		selectCorporatePassEditor();
		addCorporatePass("password");
		makeCorporatePass();

		assertEquals(PersistenceState.TRANSIENT, model.getInvoice().getPersistenceState());
		assertQueuedRecords(model.getPaymentPlanInvoices().get(0).getInvoice()
				, model.getPaymentPlanInvoices().get(0).getInvoiceLine()
				, model.getPaymentPlanInvoices().get(0).getEnrolment()
				, model.getPaymentPlanInvoices().get(0).getEnrolment().getCourseClass()
				, model.getPaymentPlanInvoices().get(0).getEnrolment().getStudent()
				, model.getPaymentPlanInvoices().get(0).getInvoice().getInvoiceDueDates().get(0)
				, model.getPaymentPlanInvoices().get(0).getInvoice().getInvoiceDueDates().get(1)
				, model.getPayer()
				, model.getCorporatePass()
				, model.getCorporatePass().getContact()
		);

	}


	protected void makeValidPayment() throws InterruptedException {
		super.makeValidPayment();
		assertEquals(PersistenceState.TRANSIENT, model.getInvoice().getPersistenceState());
		assertQueuedRecords(model.getPaymentPlanInvoices().get(0).getInvoice()
				, model.getPaymentPlanInvoices().get(0).getInvoiceLine()
				, model.getPaymentPlanInvoices().get(0).getEnrolment()
				, model.getPaymentPlanInvoices().get(0).getEnrolment().getCourseClass()
				, model.getPaymentPlanInvoices().get(0).getPaymentInLine()
				, model.getPaymentPlanInvoices().get(0).getInvoice().getInvoiceDueDates().get(0)
				, model.getPaymentPlanInvoices().get(0).getInvoice().getInvoiceDueDates().get(1)
				, model.getPayment()
				, model.getPayer()
				, model.getPayer().getStudent()
		);
	}

	protected void proceedToPayment() {
		super.proceedToPayment();
		List<QueuedRecord> records = ObjectSelect.query(QueuedRecord.class).select(model.getObjectContext());
		assertEquals(0, records.size());
	}

	protected void makeInvalidPayment() throws InterruptedException {
		super.makeInvalidPayment();
		assertQueuedRecords(model.getPaymentPlanInvoices().get(0).getEnrolment().getCourseClass()
				, model.getPayer()
				, model.getPayer().getStudent());
		model.getObjectContext().deleteObjects(ObjectSelect.query(QueuedRecord.class).select(model.getObjectContext()));
	}

	private void tryAgain(PaymentEditorDelegate delegate) {
		Set<Queueable> queueables = new HashSet<>();
		Invoice invoice = model.getPaymentPlanInvoices().get(0).getInvoice();
		queueables.add(invoice);
		queueables.add(model.getPaymentPlanInvoices().get(0).getEnrolment());
		queueables.add(model.getPayment());

		PurchaseModel oldModel = model;
		delegate.tryAgain();
		model = purchaseController.getModel();
		assertFalse(oldModel == model);

		queueables.addAll(invoice.getInvoiceDueDates());
		queueables.addAll(invoice.getRefundedInvoices());
		queueables.addAll(invoice.getRefundedInvoices().get(0).getInvoiceLines());
		queueables.addAll(invoice.getRefundedInvoices().get(0).getPaymentInLines());
		queueables.addAll(invoice.getRefundedInvoices().get(1).getInvoiceLines());
		queueables.addAll(invoice.getRefundedInvoices().get(1).getPaymentInLines());
		queueables.add(invoice.getPaymentInLines().get(0).getPaymentIn());
		queueables.add(invoice.getPaymentInLines().get(1).getPaymentIn());
		queueables.add(invoice.getContact());
		queueables.add(invoice.getContact().getStudent());
		assertQueuedRecords(queueables.toArray(new Queueable[queueables.size()]));
		model.getObjectContext().deleteObjects(ObjectSelect.query(QueuedRecord.class).select(model.getObjectContext()));

		assertEquals(1, model.getPaymentPlanInvoices().size());
	}

	private void payNowCorrect() throws ParseException {
		Money value = (Money) MoneyFormatter.getInstance().stringToValue("70");
		ActionChangePayNow action = changePayNow.createAction(purchaseController);
		action.setPayNow(value);
		purchaseController.performAction(action, changePayNow);
		assertTrue(purchaseController.getErrors().isEmpty());

		InvoiceNode invoiceNode = model.getPaymentPlanInvoices().get(0);
		//asset payment
		assertEquals(value, model.getPayment().getAmount());
		//asset paymentLines
		assertEquals(2, model.getPayment().getPaymentInLines().size());
		for (PaymentInLine paymentInLine : purchaseController.getModel().getPayment().getPaymentInLines()) {
			if (paymentInLine == invoiceNode.getPaymentInLine()) {
				assertEquals("PaymentPlan paymentInLine", value, paymentInLine.getAmount());
			} else {
				assertEquals("default paymentInLine", Money.ZERO, paymentInLine.getAmount());
			}
		}
	}

	private void payNowWrongValue() {
		Money value = null;
		try {
			value = (Money) MoneyFormatter.getInstance().stringToValue("DDDDDD");
		} catch (ParseException e) {
		}
		ActionChangePayNow action = changePayNow.createAction(purchaseController);
		action.setPayNow(value);
		purchaseController.performAction(action, changePayNow);
		assertTrue(purchaseController.getErrors().containsKey(PurchaseController.Message.payNowWrong.name()));
	}

	private void payNowMoreThanRequired() throws ParseException {
		Money value = (Money) MoneyFormatter.getInstance().stringToValue("$120.55");
		ActionChangePayNow action = changePayNow.createAction(purchaseController);
		action.setPayNow(value);
		purchaseController.performAction(action, changePayNow);
		assertTrue(purchaseController.getErrors().containsKey(PurchaseController.Message.payNowWrong.name()));
	}

	private void payNowLessThanRequired() throws ParseException {
		Money value = (Money) MoneyFormatter.getInstance().stringToValue("$0.55");
		ActionChangePayNow action = changePayNow.createAction(purchaseController);
		action.setPayNow(value);
		purchaseController.performAction(action, changePayNow);
		assertTrue(purchaseController.getErrors().containsKey(PurchaseController.Message.payNowWrong.name()));
	}

	private void enableEnrolment(Enrolment enrolment) {
		PurchaseController.ActionParameter param = new PurchaseController.ActionParameter(PurchaseController.Action.enableEnrolment);
		param.setValue(enrolment);
		performAction(param);
		AssertPaymentPlanEnrolment.valueOf(enrolment, purchaseController, model).assertValue();
	}

	private Contact initEnrolment() {
		CourseClass courseClass = createPurchaseController(1001);
		model = getModel();
		Contact contact = addFirstContact(1001);
		AssertPaymentPlanEnrolment.valueOf(model.getEnrolmentBy(contact, courseClass), purchaseController, model);
		return contact;
	}

	private void disableEnrolment(Enrolment enrolment) {
		Contact contact = enrolment.getStudent().getContact();
		PurchaseController.ActionParameter param = new PurchaseController.ActionParameter(PurchaseController.Action.disableEnrolment);
		param.setValue(enrolment);
		performAction(param);

		//assert enrolment
		assertEquals(0, model.getEnabledEnrolments(contact).size());
		assertEquals(1, model.getDisabledEnrolments(contact).size());
		assertDisabledEnrolment(model.getDisabledEnrolments(contact).get(0));

		//assert default invoice
		assertEquals(Money.ZERO, model.getInvoice().getTotalGst());
		assertEquals(Money.ZERO, model.getInvoice().getTotalExGst());

		//assert payment
		assertEquals(Money.ZERO, model.getPayment().getAmount());
		assertEquals(1, model.getPayment().getPaymentInLines().size());
		assertEquals(Money.ZERO, model.getPayment().getPaymentInLines().get(0).getAmount());


		//assert payment plan invoice
		assertTrue(purchaseController.getModel().getPaymentPlanInvoices().isEmpty());


		//assert objectContext
		ObjectContext objectContext = purchaseController.getModel().getObjectContext();
		for (Object object : objectContext.uncommittedObjects()) {
			if (object instanceof PaymentInLine) {
				assertEquals(model.getPayment().getPaymentInLines().get(0), object);
			} else if (object instanceof PaymentIn) {
				assertEquals(model.getPayment(), object);
			} else if (object instanceof Invoice) {
				assertEquals(model.getInvoice(), model.getInvoice());
			} else if (object instanceof InvoiceLine) {
				assertFalse("InvoiceLine cannot be on the step", Boolean.FALSE);
			} else if (object instanceof Enrolment) {
				assertEquals(model.getDisabledEnrolments(contact).get(0), object);
			}
		}
	}

}
