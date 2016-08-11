package ish.oncourse.enrol.checkout;

import ish.math.Money;
import ish.oncourse.model.Contact;
import ish.oncourse.model.Voucher;
import org.apache.cayenne.Cayenne;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;

import static org.junit.Assert.assertEquals;

public class ActionEnableProductItemTest extends ACheckoutTest {

	@Before
	public void setup() throws Exception {
		setup("ish/oncourse/enrol/checkout/ActionEnableProductItemTest.xml");
	}

	@Test
	public void testCloneModelWithVoucherWithoutPrice() throws InterruptedException {
		init(Collections.<Long>emptyList(), Collections.singletonList(13L), Collections.<Long>emptyList(), false);
		Contact contact1 = Cayenne.objectForPK(purchaseController.getModel().getObjectContext(), Contact.class, 1189157);
		addContact(contact1);
		assertEquals(1, purchaseController.getModel().getAllEnabledProductItems().size());
		Voucher voucher = (Voucher) purchaseController.getModel().getAllEnabledProductItems().get(0);
		assertEquals(new Money("100.00"), voucher.getValueOnPurchase());

		proceedToPayment();

		makeInvalidPayment();
		purchaseController.getPaymentEditorDelegate().tryAgain();
	}
}
