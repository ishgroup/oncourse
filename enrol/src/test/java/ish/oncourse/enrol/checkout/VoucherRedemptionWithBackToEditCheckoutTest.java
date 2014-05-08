/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */

package ish.oncourse.enrol.checkout;

import ish.common.types.ProductStatus;
import ish.oncourse.model.Contact;
import ish.oncourse.model.Enrolment;
import ish.oncourse.model.ProductItem;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class VoucherRedemptionWithBackToEditCheckoutTest extends ACheckoutTest {

	@Before
	public void setup() throws Exception {
		setup("ish/oncourse/enrol/checkout/VoucherRedemptionWithBackToEditCheckoutTest.xml");
	}

	private void backToEditChekout() {
		PurchaseController.ActionParameter actionParameter = new PurchaseController.ActionParameter(PurchaseController.Action.backToEditCheckout);
		actionParameter.setValue(PurchaseController.Action.enableProductItem);
		performAction(actionParameter);
	}

	private void disableEnrolment(Enrolment enrolment) {
		PurchaseController.ActionParameter disableEnrol = new PurchaseController.ActionParameter(PurchaseController.Action.disableEnrolment);
		disableEnrol.setValue(enrolment);
		performAction(disableEnrol);
	}

	private void enableEnrolment(Enrolment enrolment) {
		PurchaseController.ActionParameter enableEnrol = new PurchaseController.ActionParameter(PurchaseController.Action.enableEnrolment);
		enableEnrol.setValue(enrolment);
		performAction(enableEnrol);
	}

	private void disableProductItem(ProductItem productItem) {
		PurchaseController.ActionParameter disableProductItem = new PurchaseController.ActionParameter(PurchaseController.Action.disableProductItem);
		disableProductItem.setValue(productItem);
		performAction(disableProductItem);
	}

	private void enableProductItem(ProductItem productItem) {
		PurchaseController.ActionParameter enableProductItem = new PurchaseController.ActionParameter(PurchaseController.Action.enableProductItem);
		enableProductItem.setValue(productItem);
		enableProductItem.setValue(productItem.getProduct().getPriceIncTax());
		performAction(enableProductItem);
	}

	//AddContact->AddCode->ProceedToPayment->BackToEdit->ProceedToPayment->MakePayment
	@Test
	public void testVoucherRedemptionWithOneContact() throws InterruptedException {
		PurchaseController purchaseController = init(Arrays.asList(1001L), Collections.EMPTY_LIST, Collections.EMPTY_LIST, false);
		addFirstContact(1001L);
		PurchaseModel model = purchaseController.getModel();
		assertTrue(model.getSelectedVouchers().isEmpty());
		addCode("v1003");
		assertEquals(1, model.getSelectedVouchers().size());
		proceedToPayment();
		assertTrue(model.getVoucherPayments().get(0).getVoucherPaymentIns().size() > 0);
		backToEditChekout();
		assertTrue(model.getVoucherPayments().get(0).getVoucherPaymentIns().size() > 0);
		proceedToPayment();
		makeInvalidPayment();
		assertTrue(purchaseController.isPaymentResult());
		assertTrue(purchaseController.isFinished());
		assertEquals(1, model.getAllEnabledEnrolments().size());
		assertEquals(ProductStatus.REDEEMED, model.getVouchers().get(0).getStatus());
	}

	//AddContact->AddCode->AddContact->ProceedToPayment->BackToEdit->ProceedToPayment->MakePayment
	@Test
	public void testVoucherRedemptionWithManyContacts() throws InterruptedException {
		PurchaseController purchaseController = init(Arrays.asList(1001L), Collections.EMPTY_LIST, Collections.EMPTY_LIST, false);
		addFirstContact(1001L);
		PurchaseModel model = purchaseController.getModel();
		assertTrue(model.getSelectedVouchers().isEmpty());
		addCode("v1004");
		addContact(1002L);
		addContact(1003L);
		addContact(1004L);
		assertEquals(1, model.getSelectedVouchers().size());
		proceedToPayment();
		backToEditChekout();
		proceedToPayment();
		makeInvalidPayment();
		assertTrue(purchaseController.isPaymentResult());
		assertTrue(purchaseController.isFinished());
		assertEquals(2, model.getAllEnabledEnrolments().size());
		assertEquals(ProductStatus.REDEEMED, model.getVouchers().get(0).getStatus());
	}

	//AddContact->AddCode->AddContact->ProceedToPayment->BackToEdit->ProceedToPayment->MakePayment
	@Test
	public void testMoneyVoucherRedemptionWithManyContacts() throws InterruptedException {
		PurchaseController purchaseController = init(Arrays.asList(1001L), Collections.EMPTY_LIST, Collections.EMPTY_LIST, false);
		addFirstContact(1001L);
		PurchaseModel model = purchaseController.getModel();
		assertTrue(model.getSelectedVouchers().isEmpty());
		addCode("v1005");
		addContact(1002L);
		addContact(1003L);
		assertEquals(1, model.getSelectedVouchers().size());
		proceedToPayment();
		backToEditChekout();
		proceedToPayment();
		makeInvalidPayment();
		assertTrue(purchaseController.isPaymentResult());
		assertTrue(purchaseController.isFinished());
		assertEquals(2, model.getAllEnabledEnrolments().size());
		assertEquals(100, model.getVouchers().get(0).getValueRemaining().intValue());
		assertEquals(ProductStatus.ACTIVE, model.getVouchers().get(0).getStatus());
	}

	/*
	Enrol+Enrol->AddContact->AddCode->ProceedToPayment->BackToEdit->DisableEnrol->ProceedToPayment->
	->BackToEdit->EnableEnrol->ProceedToPayment->MakePayment
	*/
	@Test
	public void testVoucherRedemptionWithTwoEnrolments() throws InterruptedException {
		PurchaseController purchaseController = init(Arrays.asList(1001L, 1002L), Collections.EMPTY_LIST, Collections.EMPTY_LIST, false);
		addFirstContact(1001L);
		PurchaseModel model = purchaseController.getModel();
		assertTrue(model.getSelectedVouchers().isEmpty());
		addCode("v1005");
		assertEquals(1, model.getSelectedVouchers().size());
		proceedToPayment();
		assertEquals(2, model.getAllEnabledEnrolments().size());
		backToEditChekout();

		Enrolment enrolment = model.getAllEnabledEnrolments().get(0);
		Contact contact = enrolment.getStudent().getContact();

		disableEnrolment(enrolment);
		proceedToPayment();
		assertEquals(1, model.getAllEnabledEnrolments().size());
		backToEditChekout();

		assertEquals(1, model.getDisabledEnrolments(contact).size());
		Enrolment disabledEnrolment = model.getDisabledEnrolments(contact).get(0);

		enableEnrolment(disabledEnrolment);
		proceedToPayment();
		assertEquals(2, model.getAllEnabledEnrolments().size());
		makeInvalidPayment();
		assertTrue(purchaseController.isPaymentResult());
		assertTrue(purchaseController.isFinished());
		assertEquals(ProductStatus.REDEEMED, model.getVouchers().get(0).getStatus());
	}

	/*
	ProdItem+ProdItem->AddContact->AddCode->ProceedToPayment->BackToEdit->DisableProdItem->ProceedToPayment->
	->BackToEdit->EnableProdItem->ProceedToPayment->MakePayment
	*/
	@Test
	public void testVoucherRedemptionWithTwoProductItems() throws InterruptedException {
		PurchaseController purchaseController = init(Collections.EMPTY_LIST, Arrays.asList(101L, 102L), Collections.EMPTY_LIST, false);
		addFirstContact(1001L);
		PurchaseModel model = purchaseController.getModel();
		assertTrue(model.getSelectedVouchers().isEmpty());
		addCode("v1005");
		assertEquals(1, model.getSelectedVouchers().size());
		proceedToPayment();
		assertEquals(2, model.getAllEnabledProductItems().size());
		backToEditChekout();

		ProductItem productItem = model.getAllEnabledProductItems().get(0);
		Contact contact = productItem.getContact();

		disableProductItem(productItem);
		proceedToPayment();
		assertEquals(1, model.getAllEnabledProductItems().size());
		backToEditChekout();

		assertEquals(1, model.getDisabledProductItems(contact).size());
		ProductItem disabledProductItem = model.getDisabledProductItems(contact).get(0);

		enableProductItem(disabledProductItem);
		proceedToPayment();
		assertEquals(2, model.getAllEnabledProductItems().size());
		assertEquals(1, model.getSelectedVouchers().size());
		makeInvalidPayment();
		assertTrue(purchaseController.isPaymentResult());
		assertTrue(purchaseController.isFinished());
		assertEquals(ProductStatus.REDEEMED, model.getVouchers().get(0).getStatus());
	}
}
