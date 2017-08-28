package ish.oncourse.enrol.checkout.paymentplan;

import ish.common.types.PaymentType;
import ish.math.Money;
import ish.oncourse.enrol.checkout.ACheckoutTest;
import ish.oncourse.enrol.checkout.ActionChangePayNow;
import ish.oncourse.enrol.checkout.GetInvoiceTransaction;
import ish.oncourse.enrol.checkout.PurchaseController;
import ish.oncourse.enrol.checkout.model.InvoiceNode;
import ish.oncourse.enrol.checkout.model.PurchaseModel;
import ish.oncourse.enrol.checkout.payment.PaymentEditorDelegate;
import ish.oncourse.model.*;
import ish.oncourse.util.MoneyFormatter;
import org.apache.cayenne.ObjectContext;
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
public class PaymentPlanAndRegularEnrolmentTest extends ACheckoutTest {

	private PurchaseModel model;
	private CourseClass regularClass;
	private CourseClass paymentPlanClass;
	private Contact contact;

	@Before
	public void setup() throws Exception {
		setup("ish/oncourse/enrol/checkout/paymentplan/PaymentPlanAndRegularEnrolmentTest.xml");
	}

	@Override
	protected void configDataSet(ReplacementDataSet dataSet) {
		super.configDataSet(dataSet);
		dataSet.addReplacementObject("[class1001_startDate]", DateUtils.addDays(new Date(), 1));
		dataSet.addReplacementObject("[class1001_endDate]", DateUtils.addDays(new Date(), 2));
		dataSet.addReplacementObject("[class1002_startDate]", DateUtils.addDays(new Date(), 1));
		dataSet.addReplacementObject("[class1002_endDate]", DateUtils.addDays(new Date(), 2));
	}


	@Test
	public void test() throws Exception{
		initEnrolments();

		Enrolment enrolment = purchaseController.getModel().getEnabledEnrolments(purchaseController.getModel().getPayer()).get(0);

		enrolment = disableEnrolment(enrolment);
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

	protected void makeValidPayment() throws InterruptedException {
		super.makeValidPayment();
		HashSet<Queueable> queueables = new HashSet<>();
		queueables.addAll(GetInvoiceTransaction.valueOf(model.getInvoice(),queueables).get());
		queueables.addAll(GetInvoiceTransaction.valueOf(model.getPaymentPlanInvoices().get(0).getInvoice(),queueables).get());
	}

	private void tryAgain(PaymentEditorDelegate delegate) {
		Set<Queueable> queueables = new HashSet<>();
		Invoice rInvoice = model.getPaymentPlanInvoices().get(0).getInvoice();
		Invoice ppInvoice = model.getPaymentPlanInvoices().get(0).getInvoice();

		PurchaseModel oldModel = model;
		delegate.tryAgain();

		queueables.addAll(GetInvoiceTransaction.valueOf(rInvoice, queueables).get());
		queueables.addAll(GetInvoiceTransaction.valueOf(ppInvoice, queueables).get());


		model = purchaseController.getModel();
		assertFalse(oldModel == model);

		assertQueuedRecords(queueables.toArray(new Queueable[queueables.size()]));
		model.getObjectContext().deleteObjects(ObjectSelect.query(QueuedRecord.class).select(model.getObjectContext()));

		assertEquals(1, model.getPaymentPlanInvoices().size());
	}




	protected void makeInvalidPayment() throws InterruptedException {
		super.makeInvalidPayment();
		assertQueuedRecords(regularClass, paymentPlanClass
				, model.getPayer()
				, model.getPayer().getStudent());
		model.getObjectContext().deleteObjects(ObjectSelect.query(QueuedRecord.class).select(model.getObjectContext()));
	}

	protected void proceedToPayment() {
		super.proceedToPayment();
		List<QueuedRecord> records = ObjectSelect.query(QueuedRecord.class).select(model.getObjectContext());
		assertEquals(0, records.size());
	}


	private void payNowCorrect() throws ParseException {
		Money value = (Money) MoneyFormatter.getInstance().stringToValue("180");
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
				assertEquals("PaymentPlan paymentInLine", value.subtract(regularClass.getFeeIncGst(null)), paymentInLine.getAmount());
			} else {
				assertEquals("default paymentInLine", regularClass.getFeeIncGst(null), paymentInLine.getAmount());
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

	private Contact initEnrolments() {
		List<CourseClass> courseClasses = createPurchaseController(1001, 1002);
		model = getModel();
		contact = addFirstContact(1001);
		for (CourseClass courseClass : courseClasses) {
			if (courseClass.getId().equals(1001L)) {
				paymentPlanClass = courseClass;
			}
			else if (courseClass.getId().equals(1002L)) {
				regularClass = courseClass;
			}
		}
		assertEnableEnrolmnets();

		return contact;
	}

	private void assertEnableEnrolmnets() {
		assertEquals(2, purchaseController.getModel().getEnabledEnrolments(contact).size());
		assertEquals(0, purchaseController.getModel().getDisabledEnrolments(contact).size());
		assertEnabledEnrolments(contact, 2);

		//assert paymentPlan Enrolmnet
		//assert Enrolment

		//assert Invoices
		InvoiceNode invoiceNode = purchaseController.getModel().getPaymentPlanInvoiceBy(model.getEnrolmentBy(contact, paymentPlanClass));
		assertNotNull(invoiceNode);
		assertNotNull(invoiceNode.getEnrolment());
		assertEquals(contact, invoiceNode.getEnrolment().getStudent().getContact());

		assertNotNull(invoiceNode.getInvoiceLine());
		assertEquals(paymentPlanClass.getFeeIncGst(null), invoiceNode.getInvoiceLine().getPriceTotalIncTax());

		assertNotNull(invoiceNode.getInvoice());
		assertEquals(contact, invoiceNode.getInvoice().getContact());
		//assertEquals(courseClass.getFeeIncGst(), invoiceNode.getInvoice().getAmountOwing());
		assertEquals(paymentPlanClass.getFeeIncGst(null), invoiceNode.getInvoice().getTotalGst());

		assertEquals(1, invoiceNode.getSelectedDueDates().size());
		Money amountDueDates = invoiceNode.getSelectedDueDates().get(0).getAmount();

		//assert PaymentLine
		assertNotNull(invoiceNode.getPaymentInLine());
		assertEquals(paymentPlanClass.getFeeIncGst(null), invoiceNode.getInvoiceLine().getPriceTotalIncTax());
		assertEquals(2, model.getPayment().getPaymentInLines().size());
		for (PaymentInLine paymentInLine : purchaseController.getModel().getPayment().getPaymentInLines()) {
			if (paymentInLine == invoiceNode.getPaymentInLine()) {
				assertEquals("PaymentPlan paymentInLine", amountDueDates, paymentInLine.getAmount());
			} else {
				assertEquals("default paymentInLine", regularClass.getFeeIncGst(null), paymentInLine.getAmount());
			}
		}

		//assert paymentIn
		assertEquals("Payment amount equals the dueDates's amount", amountDueDates.add(regularClass.getFeeIncGst(null)), model.getPayment().getAmount());
		assertEquals(PaymentType.CREDIT_CARD, model.getPayment().getType());

		assertEquals(paymentPlanClass.getFeeIncGst(null).add(regularClass.getFeeIncGst(null)), model.getTotalGst());
		assertEquals(amountDueDates.add(regularClass.getFeeIncGst(null)), purchaseController.getPayNow());
	}

	protected Enrolment disableEnrolment(Enrolment enrolment) {
		Contact contact = enrolment.getStudent().getContact();
		enrolment = super.disableEnrolment(enrolment);

		//assert enrolment
		assertEquals(1, model.getEnabledEnrolments(contact).size());
		assertEquals(1, model.getDisabledEnrolments(contact).size());
		assertDisabledEnrolment(model.getDisabledEnrolments(contact).get(0));

		//assert default invoice
		assertEquals(regularClass.getFeeIncGst(null), model.getInvoice().getTotalGst());
		assertEquals(regularClass.getFeeExGst(), model.getInvoice().getTotalExGst());

		//assert payment
		assertEquals(regularClass.getFeeIncGst(null), model.getPayment().getAmount());
		assertEquals(1, model.getPayment().getPaymentInLines().size());
		assertEquals(regularClass.getFeeIncGst(null), model.getPayment().getPaymentInLines().get(0).getAmount());


		//assert payment plan invoice
		assertTrue(purchaseController.getModel().getPaymentPlanInvoices().isEmpty());


		//assert objectContext
		ObjectContext objectContext = purchaseController.getModel().getObjectContext();
		for (Object object: objectContext.uncommittedObjects()) {
			if (object instanceof PaymentInLine) {
				assertEquals(model.getPayment().getPaymentInLines().get(0), object);
			} else if (object instanceof PaymentIn) {
				assertEquals(model.getPayment(), object);
			} else if (object instanceof Invoice) {
				assertEquals(model.getInvoice(), model.getInvoice());
			} else if (object instanceof InvoiceLine) {
				assertFalse("InvoiceLine cannot be on the step", Boolean.FALSE);
			} else if (object instanceof Enrolment) {
				if (((Enrolment)object).getCourseClass().getId().equals(1001L)) {
					assertEquals(model.getDisabledEnrolments(contact).get(0), object);
				} else {
					assertEquals(model.getEnabledEnrolments(contact).get(0), object);
				}
			}
		}
		return enrolment;
	}

}
