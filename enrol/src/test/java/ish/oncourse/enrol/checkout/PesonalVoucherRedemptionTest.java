/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */

package ish.oncourse.enrol.checkout;

import ish.common.types.ProductStatus;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class PesonalVoucherRedemptionTest extends ACheckoutTest {

	@Before
	public void setup() throws Exception {
		setup("ish/oncourse/enrol/checkout/PersonalVoucherRedemptionTest.xml");
	}

	@Test
	public void testPersonalVoucherRedemption() throws InterruptedException {
		PurchaseController purchaseController = init(Arrays.asList(1001L), Collections.EMPTY_LIST, Collections.EMPTY_LIST, false);
		assertTrue("purchaseController is not in state 'AddContact'", purchaseController.isAddContact());
		addFirstContact(1001L);
		assertTrue("purchaseController is not in state 'EditCheckout'", purchaseController.isEditCheckout());
		PurchaseModel model = purchaseController.getModel();
		assertTrue(model.getSelectedVouchers().isEmpty());
		addCode("v1003");
		assertEquals(1, model.getSelectedVouchers().size());
		proceedToPayment();
		assertTrue("purchaseController is not in state 'EditPayment'", purchaseController.isEditPayment());
		makeInvalidPayment();
		assertTrue(purchaseController.isPaymentResult());
		assertTrue(purchaseController.isFinished());
		assertEquals(0, model.getVouchers().get(0).getValueRemaining().intValue());
		assertEquals(ProductStatus.REDEEMED, model.getVouchers().get(0).getStatus());
	}

}
