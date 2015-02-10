/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.enrol.checkout;

import ish.common.types.ProductType;
import ish.math.Money;
import ish.oncourse.model.Contact;
import ish.oncourse.model.PaymentIn;
import ish.oncourse.model.Product;
import ish.oncourse.model.ProductItem;
import ish.oncourse.services.cookies.ICookiesService;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.*;

public class MultiplePurchaseProductTest extends ACheckoutTest {

	private Expression membershipExp;
	private Expression voucherExp;
	private Expression articleExp;

	@Before
	public void setup() throws Exception {
		setup("ish/oncourse/enrol/checkout/ProductItemTestDataSet.xml");

		membershipExp = ExpressionFactory.matchExp(ProductItem.TYPE_PROPERTY, ProductType.MEMBERSHIP.getDatabaseValue());
		voucherExp = ExpressionFactory.matchExp(ProductItem.TYPE_PROPERTY, ProductType.VOUCHER.getDatabaseValue());
		articleExp = ExpressionFactory.matchExp(ProductItem.TYPE_PROPERTY, ProductType.ARTICLE.getDatabaseValue());
	}

	@Test
	public void testMultiplePurchaseProduct() {

		init(Collections.<Long>emptyList(), Arrays.asList(8L, 9L, 11L), Collections.<Long>emptyList(), true);

		Contact contact1 = purchaseController.getModel().getPayer();

		assertEquals(3, purchaseController.getModel().getAllProductItems(contact1).size());

		Contact contact2 = Cayenne.objectForPK(purchaseController.getModel().getObjectContext(), Contact.class, 1189158);
		addContact(contact2);

		//voucher product added only for payer
		assertEquals(3, purchaseController.getModel().getEnabledProductItems(contact1).size());
		assertEquals(2, membershipExp.filterObjects(purchaseController.getModel().getEnabledProductItems(contact1)).size());
		assertEquals(1, voucherExp.filterObjects(purchaseController.getModel().getEnabledProductItems(contact1)).size());

		// other poduct added for all contact
		assertEquals(2, purchaseController.getModel().getEnabledProductItems(contact2).size());
		assertEquals(2, membershipExp.filterObjects(purchaseController.getModel().getEnabledProductItems(contact2)).size());
		assertEquals(0, voucherExp.filterObjects(purchaseController.getModel().getEnabledProductItems(contact2)).size());

		Contact contact3 = Cayenne.objectForPK(purchaseController.getModel().getObjectContext(), Contact.class, 1189159);
		addContact(contact3);

		// other poduct added for all contact
		assertEquals(2, purchaseController.getModel().getEnabledProductItems(contact3).size());
		assertEquals(2, membershipExp.filterObjects(purchaseController.getModel().getEnabledProductItems(contact3)).size());
		assertEquals(0, voucherExp.filterObjects(purchaseController.getModel().getEnabledProductItems(contact3)).size());

		proceedToPayment();

		//change payer
		PurchaseController.ActionParameter param = new PurchaseController.ActionParameter(PurchaseController.Action.changePayer);
		param.setValue(contact2);
		performAction(param);

		//removed voucher product for old payer, passed other poducts
		assertEquals(2, purchaseController.getModel().getEnabledProductItems(contact1).size());
		assertEquals(2, membershipExp.filterObjects(purchaseController.getModel().getEnabledProductItems(contact1)).size());
		assertEquals(0, voucherExp.filterObjects(purchaseController.getModel().getEnabledProductItems(contact1)).size());

		//added  voucher product for new payer
		assertEquals(3, purchaseController.getModel().getEnabledProductItems(contact2).size());
		assertEquals(2, membershipExp.filterObjects(purchaseController.getModel().getEnabledProductItems(contact2)).size());
		assertEquals(1, voucherExp.filterObjects(purchaseController.getModel().getEnabledProductItems(contact2)).size());

		//checked PaymerntIn
		PaymentIn paymentIn = purchaseController.getModel().getPayment();

		assertEquals(new Money("71"),paymentIn.getAmount());
		assertEquals(1, paymentIn.getPaymentInLines().size());
		assertEquals(7, paymentIn.getPaymentInLines().get(0).getInvoice().getInvoiceLines().size());
	}

	@Test
	public void testCloneModel() throws InterruptedException {
		init(Collections.<Long>emptyList(), Arrays.asList(8L, 9L, 11L, 12L), Collections.<Long>emptyList(), false);
		PurchaseModel model = purchaseController.getModel();

		//add first contact (payer)
		Contact contact1 = Cayenne.objectForPK(purchaseController.getModel().getObjectContext(), Contact.class, 1189157);
		addContact(contact1);
		Contact contact2 = Cayenne.objectForPK(purchaseController.getModel().getObjectContext(), Contact.class, 1189158);
		addContact(contact2);


		assertEquals(7, model.getAllEnabledProductItems().size());

		//disable any productItems
		Product disabledProduct1 = disableProductItem(12L, contact1);
		Product disabledProduct2 = disableProductItem(9L, contact2);

		proceedToPayment();

		//check model before clone
		assertEquals(3,model.getAllProductItems(contact1).size());
		assertEquals(0,model.getDisabledProductItems(contact1).size());
		assertEquals(2,model.getAllProductItems(contact2).size());
		assertEquals(0,model.getDisabledProductItems(contact2).size());

		assertEquals(1,voucherExp.filterObjects(model.getAllProductItems(contact1)).size());
		assertEquals(0,voucherExp.filterObjects(model.getAllProductItems(contact2)).size());

		assertNull(model.getProductItemBy(contact1, disabledProduct1));
		assertNull(model.getProductItemBy(contact2, disabledProduct2));

		// make invalid payment and try again (when user press on "try again" link purchase model cloned and proceed to payment)
		makeInvalidPayment();
		purchaseController.getPaymentEditorDelegate().tryAgain();

		//check model after clone
		assertEquals(3,model.getAllProductItems(contact1).size());
		assertEquals(0,model.getDisabledProductItems(contact1).size());
		assertEquals(2,model.getAllProductItems(contact2).size());
		assertEquals(0,model.getDisabledProductItems(contact2).size());

		assertEquals(1,voucherExp.filterObjects(model.getAllProductItems(contact1)).size());
		assertEquals(0,voucherExp.filterObjects(model.getAllProductItems(contact2)).size());


		assertNull(model.getProductItemBy(contact1, disabledProduct1));
		assertNull(model.getProductItemBy(contact2, disabledProduct2));

	}

	@Test
	public void testBackToCheckout() {
		PurchaseModel model = init(Collections.<Long>emptyList(), Arrays.asList(8L, 9L, 11L, 12L), Collections.<Long>emptyList(), false).getModel();

		//add payer
		Contact contact1 = Cayenne.objectForPK(purchaseController.getModel().getObjectContext(), Contact.class, 1189157);
		addContact(contact1);

		Contact contact2 = Cayenne.objectForPK(purchaseController.getModel().getObjectContext(), Contact.class, 1189158);
		addContact(contact2);

		assertEquals(4, model.getAllProductItems(contact1).size());
		assertEquals(3, model.getAllProductItems(contact2).size());

		//disable any productItems
		disableProductItem(8L, contact1);
		disableProductItem(11L, contact1);
		disableProductItem(9L, contact2);


		//remove all disabled productItems, go to the payment page
		proceedToPayment();

		//back to edit checkout, restores all disabled productItems
		PurchaseController.ActionParameter parameter = new PurchaseController.ActionParameter(PurchaseController.Action.backToEditCheckout);
		parameter.setValue(PurchaseController.Action.enableProductItem);
		performAction(parameter);


		//checked model afer restoration disabled productItems
		assertEquals(4, model.getAllProductItems(contact1).size());
		assertEquals(2, model.getEnabledProductItems(contact1).size());
		assertEquals(2, model.getDisabledProductItems(contact1).size());
		assertEquals(1, voucherExp.filterObjects(model.getDisabledProductItems(contact1)).size());
		assertEquals((Object) 11L, voucherExp.filterObjects(model.getDisabledProductItems(contact1)).get(0).getProduct().getId());
		assertEquals(1, membershipExp.filterObjects(model.getDisabledProductItems(contact1)).size());
		assertEquals((Object) 8L, membershipExp.filterObjects(model.getDisabledProductItems(contact1)).get(0).getProduct().getId());

		assertEquals(3, model.getAllProductItems(contact2).size());
		assertEquals(1, model.getDisabledProductItems(contact2).size());
		assertEquals(1, membershipExp.filterObjects(model.getDisabledProductItems(contact2)).size());
		assertEquals((Object) 9L, membershipExp.filterObjects(model.getDisabledProductItems(contact2)).get(0).getProduct().getId());
	}


	@Test
	public void testAddProduct() {
		PurchaseModel model = init(Collections.<Long>emptyList(), Arrays.asList(8L, 9L),  Collections.<Long>emptyList(), true).getModel();

		Contact contact1 = model.getPayer();

		Contact contact2 = Cayenne.objectForPK(purchaseController.getModel().getObjectContext(), Contact.class, 1189158);
		addContact(contact2);

		//back to Products page and add any products
		ICookiesService cookiesService = getPageTester().getRegistry().getService("testCookiesService", ICookiesService.class);
		cookiesService.appendValueToCookieCollection(Product.SHORTLIST_COOKIE_KEY, "12");
		cookiesService.appendValueToCookieCollection(Product.SHORTLIST_COOKIE_KEY, "11");

		//go to checkout page and checked model
		purchaseControllerBuilder.updatePurchaseItems(purchaseController);

		assertEquals(4, model.getEnabledProductItems(contact1).size());
		assertEquals((Object) 11L, voucherExp.filterObjects(model.getEnabledProductItems(contact1)).get(0).getProduct().getId());
		assertEquals((Object) 12L, articleExp.filterObjects(model.getEnabledProductItems(contact1)).get(0).getProduct().getId());

		assertEquals(3, model.getEnabledProductItems(contact2).size());

		//Voucher added only for payer
		assertTrue(voucherExp.filterObjects(model.getEnabledProductItems(contact2)).isEmpty());

		assertEquals((Object) 12L, articleExp.filterObjects(model.getEnabledProductItems(contact2)).get(0).getProduct().getId());

	}

	@Test
	public void testAddPayerAfterProccedToPayment() {

		PurchaseModel model = init(Collections.<Long>emptyList(), Arrays.asList(8L, 9L, 11L, 12L), Collections.<Long>emptyList(), true).getModel();

		Contact oldPayer = model.getPayer();

		proceedToPayment();

		//add a different payer on Payment page
		Contact newPayer = addPayer(1189158);

		//added Voucher for payer
		assertEquals(1, model.getEnabledProductItems(newPayer).size());
		assertEquals((Object) 11L, model.getEnabledProductItems(newPayer).get(0).getProduct().getId());

		//removed Voucher for other contacts
		assertEquals(3, model.getEnabledProductItems(oldPayer).size());
		assertTrue(voucherExp.filterObjects(model.getAllProductItems(oldPayer)).isEmpty());
	}

    private Product disableProductItem(Long productId, Contact contact) {

		PurchaseController.ActionParameter actionParameter = new PurchaseController.ActionParameter(PurchaseController.Action.disableProductItem);
		Product disabledProduct1 = Cayenne.objectForPK(purchaseController.getModel().getObjectContext(), Product.class, productId);
		actionParameter.setValue(purchaseController.getModel().getProductItemBy(contact, disabledProduct1));
		performAction(actionParameter);
		return disabledProduct1;
	}
}
