/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */

package ish.oncourse.enrol.checkout;

import ish.common.types.EnrolmentStatus;
import ish.common.types.PaymentType;
import ish.common.types.ProductStatus;
import ish.oncourse.enrol.checkout.contact.ContactCredentials;
import ish.oncourse.enrol.checkout.payment.PaymentEditorDelegate;
import ish.oncourse.model.Contact;
import ish.oncourse.model.Voucher;
import org.apache.cayenne.Cayenne;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

import static ish.oncourse.enrol.checkout.PurchaseController.Action.selectCorporatePassEditor;
import static org.junit.Assert.*;

public class PesonalVoucherRedemptionTest extends ACheckoutTest {

	@Before
	public void setup() throws Exception {
		setup("ish/oncourse/enrol/checkout/PersonalVoucherRedemptionTest.xml");
	}

	private void tryAgain() {
		PaymentEditorDelegate paymentEditorDelegate = purchaseController.getPaymentEditorDelegate();
		paymentEditorDelegate.tryAgain();
		assertTrue(purchaseController.isEditPayment());
	}

	private void changePayer() {
		PurchaseController.ActionParameter actionParameter = new PurchaseController.ActionParameter(PurchaseController.Action.addPersonPayer);
		performAction(actionParameter);
		assertTrue(purchaseController.isAddContact());

		Contact newPayer = Cayenne.objectForPK(purchaseController.getCayenneService().newContext(), Contact.class, 1002L);
		ContactCredentials credential = purchaseController.getAddContactDelegate().getContactCredentials();
		credential.setEmail(newPayer.getEmailAddress());
		credential.setFirstName(newPayer.getGivenName());
		credential.setLastName(newPayer.getFamilyName());

		purchaseController.getAddContactDelegate().addContact();

		assertTrue(purchaseController.isEditPayment());
	}

	private void selectCorporatePassEditor() {
		PurchaseController.ActionParameter parameter = new PurchaseController.ActionParameter(selectCorporatePassEditor);
		performAction(parameter);
		assertEquals(PaymentType.INTERNAL, purchaseController.getModel().getPayment().getType());
		assertTrue(purchaseController.getModel().getPayment().isZeroPayment());
		assertTrue(purchaseController.isEditCorporatePass());
	}

	private void addCorporatePass(String corporatePass) {
		PurchaseController.ActionParameter parameter = new PurchaseController.ActionParameter(PurchaseController.Action.addCorporatePass);
		parameter.setValue(corporatePass);
		purchaseController.performAction(parameter);
		assertNotNull(purchaseController.getModel().getInvoice().getCorporatePassUsed());
		assertPayer(purchaseController.getModel().getCorporatePass().getContact());
	}

	private void makePayment() {
		PurchaseController.ActionParameter actionParameter = new PurchaseController.ActionParameter(PurchaseController.Action.makePayment);
		purchaseController.performAction(actionParameter);
		assertTrue(purchaseController.isPaymentResult());
		assertTrue(purchaseController.isFinished());
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

	@Test
	public void testVoucherRedemptionWithTryAnotherCard() throws InterruptedException {
		PurchaseController purchaseController = init(Arrays.asList(1001L), Collections.EMPTY_LIST, Collections.EMPTY_LIST, false);
		addFirstContact(1001L);
		PurchaseModel model = purchaseController.getModel();
		assertTrue(model.getSelectedVouchers().isEmpty());
		addCode("v1004");
		assertEquals(1, model.getSelectedVouchers().size());
		proceedToPayment();
		makeInvalidPayment();
		assertTrue(purchaseController.isPaymentResult());

		tryAgain();

		PurchaseModel newModel = purchaseController.getModel();
		makeValidPayment();
		assertTrue(purchaseController.isPaymentResult());
		assertTrue(purchaseController.isFinished());
		assertEquals(0, newModel.getVouchers().get(0).getValueRemaining().intValue());
		assertEquals(ProductStatus.REDEEMED, newModel.getVouchers().get(0).getStatus());
	}

	@Test
	public void testVoucherRedemptionWithTryAnotherPayer() throws InterruptedException {
		PurchaseController purchaseController = init(Arrays.asList(1001L), Collections.EMPTY_LIST, Collections.EMPTY_LIST, false);
		addFirstContact(1001L);
		PurchaseModel model = purchaseController.getModel();
		assertTrue(model.getSelectedVouchers().isEmpty());
		addCode("v1003");
		assertEquals(1, model.getSelectedVouchers().size());
		proceedToPayment();

		changePayer();

		makeValidPayment();
		assertTrue(purchaseController.isPaymentResult());
		assertTrue(purchaseController.isFinished());
		assertEquals(450, model.getVouchers().get(0).getValueRemaining().intValue());
		assertEquals(ProductStatus.ACTIVE, model.getVouchers().get(0).getStatus());
	}

	@Test
	public void testVoucherRedemptionWithCorporatePass() throws InterruptedException {
		PurchaseController purchaseController = init(Arrays.asList(1001L), Collections.EMPTY_LIST, Collections.EMPTY_LIST, false);
		addFirstContact(1001L);
		PurchaseModel model = purchaseController.getModel();
		assertTrue(model.getSelectedVouchers().isEmpty());
		addCode("v1004");
		assertEquals(1, model.getSelectedVouchers().size());

		proceedToPayment();
		selectCorporatePassEditor();
		assertEquals(0, model.getSelectedVouchers().size());

		Voucher voucher = model.getVouchers().get(0);
		model.selectVoucher(voucher);
		addCorporatePass("password1");
		assertEquals(1, model.getSelectedVouchers().size());

		makePayment();
		assertEquals(1, model.getSelectedVouchers().size());


		assertEquals(400, model.getVouchers().get(0).getValueRemaining().intValue());
		assertEquals(ProductStatus.ACTIVE, model.getVouchers().get(0).getStatus());
		assertEquals(EnrolmentStatus.SUCCESS, model.getAllEnabledEnrolments().get(0).getStatus());

	}
}