/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.enrol.checkout.paymentplan;

import ish.math.Money;
import ish.oncourse.enrol.checkout.ACheckoutTest;
import org.apache.commons.lang3.time.DateUtils;
import org.dbunit.dataset.ReplacementDataSet;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;

import static org.testng.AssertJUnit.assertEquals;


public class PaymentPlanAndDiscountTest extends ACheckoutTest {

	@Override
	protected void configDataSet(ReplacementDataSet dataSet) {
		super.configDataSet(dataSet);
		dataSet.addReplacementObject("[class1001_startDate]", DateUtils.addDays(new Date(), -5));
		dataSet.addReplacementObject("[class1001_endDate]", DateUtils.addDays(new Date(), 5));
	}
	
	@Before
	public void setup() throws Exception {
		setup("ish/oncourse/enrol/checkout/paymentplan/PaymentPlanAndDiscountTest.xml");
	}
	
	@Test
	public void test() {

		createPurchaseController(1001);
		addFirstContact(1000);

		proceedToPayment();

		assertEquals(1, getModel().getAllEnabledEnrolments().size());

		assertEquals(1, getModel().getPaymentPlanInvoices().size());

		assertEquals(2, getModel().getPaymentPlanInvoices().get(0).getSelectedDueDates().size());
		assertEquals(new Money("100.00"), getModel().getPaymentPlanInvoices().get(0).getSelectedDueDates().get(0).getAmount());
		assertEquals(new Money("100.00"), getModel().getPaymentPlanInvoices().get(0).getSelectedDueDates().get(1).getAmount());

		assertEquals(new Money("200.00"), getModel().getPayment().getAmount());
		
	}
}
