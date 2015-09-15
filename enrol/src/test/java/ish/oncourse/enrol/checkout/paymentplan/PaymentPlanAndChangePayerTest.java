/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.enrol.checkout.paymentplan;

import ish.oncourse.enrol.checkout.ACheckoutTest;
import ish.oncourse.enrol.checkout.PurchaseController;
import ish.oncourse.model.CourseClass;
import org.apache.commons.lang3.time.DateUtils;
import org.dbunit.dataset.ReplacementDataSet;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;
import java.util.List;

import static junit.framework.TestCase.assertEquals;


public class PaymentPlanAndChangePayerTest extends ACheckoutTest {

	@Before
	public void setup() throws Exception {
		setup("ish/oncourse/enrol/checkout/paymentplan/PaymentPlanAndChangePayerTest.xml");
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
	public void test() {
		List<CourseClass> courseClasses = createPurchaseController(1001, 1002);
		addFirstContact(1000);
		
		proceedToPayment();
		
		PurchaseController.ActionParameter param = new PurchaseController.ActionParameter(PurchaseController.Action.addPersonPayer);
		performAction(param);
		
		purchaseController.getAddContactDelegate().getContactCredentials().setFirstName("Alex");
		purchaseController.getAddContactDelegate().getContactCredentials().setLastName("Ledger");
		purchaseController.getAddContactDelegate().getContactCredentials().setEmail("email@noname.au");
		purchaseController.getAddContactDelegate().addContact();

		//check that enrolments and invoices (including payment plan invoices) assigned correctly
		assertEquals(2, getModel().getContacts().size());
		assertEquals(2, getModel().getAllEnabledEnrolments().size());
		assertEquals(0, getModel().getEnabledEnrolments(getModel().getPayer()).size());
		assertEquals(getModel().getInvoice().getContact(), getModel().getPayer());
		assertEquals(1, getModel().getPaymentPlanInvoices().size());
		assertEquals(getModel().getPayer(), getModel().getPaymentPlanInvoices().get(0).getInvoice().getContact());
		
	}
}
