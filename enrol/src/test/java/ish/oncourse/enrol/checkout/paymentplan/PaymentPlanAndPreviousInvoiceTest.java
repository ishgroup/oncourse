package ish.oncourse.enrol.checkout.paymentplan;

import ish.math.Money;
import ish.oncourse.enrol.checkout.ACheckoutTest;
import ish.oncourse.enrol.checkout.ActionChangePayNow;
import ish.oncourse.enrol.checkout.model.PurchaseModel;
import ish.oncourse.model.Contact;
import ish.oncourse.model.CourseClass;
import ish.oncourse.util.MoneyFormatter;
import org.apache.cayenne.PersistenceState;
import org.apache.commons.lang3.time.DateUtils;
import org.dbunit.dataset.ReplacementDataSet;
import org.junit.Before;
import org.junit.Ignore;

import java.util.Date;

import static ish.oncourse.enrol.checkout.PurchaseController.Action.changePayNow;
import static org.junit.Assert.assertEquals;

/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
public class PaymentPlanAndPreviousInvoiceTest extends ACheckoutTest {
	private PurchaseModel model;

	@Before
	public void setup() throws Exception {
		setup("ish/oncourse/enrol/checkout/paymentplan/PaymentPlanAndPreviousInvoiceTest.xml");
	}

	@Override
	protected void configDataSet(ReplacementDataSet dataSet) {
		super.configDataSet(dataSet);
		dataSet.addReplacementObject("[class1001_startDate]", DateUtils.addDays(new Date(), 1));
		dataSet.addReplacementObject("[class1001_endDate]", DateUtils.addDays(new Date(), 2));
	}


	@Ignore //task 25115
	public void test() throws Exception{
		initEnrolment();
		proceedToPayment();
		assertEquals(160, model.getPayment().getAmount().doubleValue(), 0);

		Money value = (Money) MoneyFormatter.getInstance().stringToValue("$200.00");
		ActionChangePayNow action = changePayNow.createAction(purchaseController);
		action.setPayNow(value);
		purchaseController.performAction(action, changePayNow);

		assertEquals(200, model.getPayment().getAmount().doubleValue(), 0);
		assertEquals(110, model.getTotalGst().doubleValue(), 0);
		assertEquals(90, model.getPaymentPlanInvoices().get(0).getPaymentAmount().doubleValue(), 0);
		assertEquals(90, model.getPaymentPlanInvoices().get(0).getPaymentInLine().getAmount().doubleValue(), 0);
		assertEquals(1, model.getPayment().getPaymentInLines().size());
		makeValidPayment();

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

	private Contact initEnrolment() {
		CourseClass courseClass = createPurchaseController(1001);
		model = getModel();
		Contact contact = addFirstContact(1001);
		AssertPaymentPlanEnrolment.valueOf(model.getEnrolmentBy(contact, courseClass),purchaseController, model).assertValue();
		return contact;
	}

}
