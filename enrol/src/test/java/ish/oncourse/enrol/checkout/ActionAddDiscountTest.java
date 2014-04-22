package ish.oncourse.enrol.checkout;

import ish.oncourse.model.Discount;
import ish.oncourse.model.InvoiceLineDiscount;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static ish.oncourse.enrol.checkout.PurchaseController.Action.addCode;
import static ish.oncourse.enrol.checkout.PurchaseController.ActionParameter;
import static org.junit.Assert.*;

public class ActionAddDiscountTest extends ACheckoutTest {


	@Before
	public void setup() throws Exception {
		super.setup("ish/oncourse/enrol/checkout/ActionAddDiscountTest.xml");
	}

	@Test
	public void test() {
		createPurchaseController(1001);
		addFirstContact(1001);

		ActionParameter parameter = new ActionParameter(addCode);
		//discount code does not exists
		parameter.setValue("1002");
		purchaseController.performAction(parameter);
		assertTrue(purchaseController.isEditCheckout());
		assertFalse(purchaseController.getErrors().isEmpty());
		purchaseController.getErrors().containsKey(PurchaseController.Message.discountNotFound.name());
		assertTrue(purchaseController.getModel().getDiscounts().isEmpty());


		parameter = new ActionParameter(addCode);
		//discount code  exists
		parameter.setValue("1001");
		purchaseController.performAction(parameter);
		assertTrue(purchaseController.isEditCheckout());
		assertTrue(purchaseController.getErrors().isEmpty());
        List<Discount> discounts =  purchaseController.getModel().getDiscounts();
		assertFalse(discounts.isEmpty());
		assertEquals(1, discounts.size());
        Discount discount = discounts.get(0);
        assertEquals(1, discount.getInvoiceLineDiscounts().size());
        InvoiceLineDiscount invoiceLineDiscount = discount.getInvoiceLineDiscounts().get(0);
        assertNotNull(invoiceLineDiscount.getCollege());
        assertNotNull(invoiceLineDiscount.getDiscount());
        assertNotNull(invoiceLineDiscount.getInvoiceLine());

		parameter = new ActionParameter(addCode);
		//try add the same discount code does not exists
		parameter.setValue("1001");
		purchaseController.performAction(parameter);
		assertTrue(purchaseController.isEditCheckout());
		assertTrue(purchaseController.getErrors().isEmpty());
		assertFalse(purchaseController.getWarnings().isEmpty());
		purchaseController.getWarnings().containsKey(PurchaseController.Message.discountAlreadyAdded.name());
		assertFalse(purchaseController.getModel().getDiscounts().isEmpty());
		assertEquals(1, purchaseController.getModel().getDiscounts().size());


	}


}
