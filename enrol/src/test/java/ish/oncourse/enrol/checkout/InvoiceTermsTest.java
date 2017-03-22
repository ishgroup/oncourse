/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.enrol.checkout;

import org.apache.commons.lang3.time.DateUtils;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class InvoiceTermsTest extends ACheckoutTest {
	@Before
	public void setup() throws Exception {
		setup("ish/oncourse/enrol/checkout/InvoiceTermsTest.xml");
	}

	@Test
	public void testContactTermsBeforeDefault() throws InterruptedException {
		PurchaseController purchaseController = init(Arrays.asList(1002L), Collections.EMPTY_LIST, Collections.EMPTY_LIST, false);
		
		addFirstContact(1001L);

		proceedToPayment();
		assertTrue("purchaseController is not in state 'EditPayment'", purchaseController.isEditPayment());
		makeValidPayment();

		assertEquals(DateUtils.truncate(DateUtils.addDays(new Date(), 2), Calendar.DATE), DateUtils.truncate(purchaseController.getModel().getInvoice().getDateDue(), Calendar.DATE));
		
	}

	@Test
	public void testContactTermsAfterDefault() throws InterruptedException {
		PurchaseController purchaseController = init(Arrays.asList(1002L), Collections.EMPTY_LIST, Collections.EMPTY_LIST, false);

		addFirstContact(1002L);

		proceedToPayment();
		assertTrue("purchaseController is not in state 'EditPayment'", purchaseController.isEditPayment());
		makeValidPayment();

		assertEquals(DateUtils.truncate(DateUtils.addDays(purchaseController.getModel().getInvoice().getInvoiceDate(), 20), Calendar.DATE), DateUtils.truncate(purchaseController.getModel().getInvoice().getDateDue(), Calendar.DATE));

	}

	@Test
	public void testDefaultTerms() throws InterruptedException {
		PurchaseController purchaseController = init(Arrays.asList(1002L), Collections.EMPTY_LIST, Collections.EMPTY_LIST, false);

		addFirstContact(1003L);

		proceedToPayment();
		assertTrue("purchaseController is not in state 'EditPayment'", purchaseController.isEditPayment());
		makeValidPayment();

		assertEquals(DateUtils.truncate(DateUtils.addDays(new Date(), 10), Calendar.DATE), DateUtils.truncate(purchaseController.getModel().getInvoice().getDateDue(), Calendar.DATE));

	}
}
