/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.enrol.checkout;

import ish.common.types.ProductType;
import ish.math.Money;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import ish.oncourse.model.Contact;
import ish.oncourse.model.ProductItem;
import ish.oncourse.model.PaymentIn;
import static org.junit.Assert.*;

public class MultiplePurchaseProductTest extends ACheckoutTest {

	private Expression membershipExp;
	private Expression voucherExp;

	@Before
	public void setup() throws Exception {
		setup("ish/oncourse/enrol/checkout/ProductItemTestDataSet.xml");

		membershipExp = ExpressionFactory.matchExp(ProductItem.TYPE_PROPERTY, ProductType.MEMBERSHIP.getDatabaseValue());
		voucherExp = ExpressionFactory.matchExp(ProductItem.TYPE_PROPERTY, ProductType.VOUCHER.getDatabaseValue());
	}

	@Test
	public void testMultiplayPurchaseProduct() {

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
}
