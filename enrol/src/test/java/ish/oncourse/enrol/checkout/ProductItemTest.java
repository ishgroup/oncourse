package ish.oncourse.enrol.checkout;


import ish.common.types.PaymentStatus;
import ish.common.types.ProductStatus;
import ish.math.Money;
import ish.oncourse.enrol.checkout.PurchaseController.ActionParameter;
import ish.oncourse.model.*;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static ish.oncourse.enrol.checkout.PurchaseController.Message.duplicatedMembership;
import static ish.oncourse.enrol.checkout.PurchaseController.Message.enterVoucherPrice;
import static org.junit.Assert.*;


/**
 * User: artem
 * Date: 9/25/13
 * Time: 10:09 AM
 */
public class ProductItemTest extends ACheckoutTest {
	PurchaseController purchaseController;
	PurchaseModel model;
	Product product;
	Contact contact;

	@Before
	public void setup() throws Exception {
		setup("ish/oncourse/enrol/checkout/ProductItemTestDataSet.xml");
	}

	@Test
	public void testPurchaseModel() throws InterruptedException {
		purchaseController = init(Collections.EMPTY_LIST, Arrays.asList(9L), Collections.EMPTY_LIST, false);
		model = purchaseController.getModel();

		product = Cayenne.objectForPK(model.getObjectContext(), Product.class, 9L);

		assertEquals(1, model.getProducts().size());
		assertEquals(product, model.getProducts().get(0));
		assertTrue(model.getProducts().get(0) instanceof MembershipProduct);
		assertEquals(Collections.EMPTY_LIST, model.getContacts());
		assertEquals(Collections.EMPTY_LIST, model.getAllEnabledProductItems());
		assertEquals(Collections.EMPTY_LIST, model.getContacts());

		addFirstContact(1189156);

		contact = Cayenne.objectForPK(model.getObjectContext(), Contact.class, 1189156);

		assertEquals(1, model.getContacts().size());
		assertEquals(contact, model.getContacts().get(0));

		contact = model.getPayer();

		assertNotNull(model.getPayer());
		assertEquals(model.getPayer(), model.getContacts().get(0));

		assertEquals(1, model.getAllEnabledProductItems().size());
		assertEquals(product, model.getAllEnabledProductItems().get(0).getProduct());


		List<Product> products = model.getProducts();
		List<InvoiceLine> invoiceLines = model.getInvoice().getInvoiceLines();
		for (int i = 0; i < products.size(); i++) {
			InvoiceLine il = invoiceLines.get(i);
			Product p = products.get(i);
			ProductItem productItem = model.getProductItemBy(model.getPayer(), p);

			assertEquals(model.getProductItemBy(model.getPayer(), p), il.getProductItems().get(i));

			assertNotNull(productItem.getCollege());
			assertEquals(p.getCollege(), productItem.getCollege());
			assertEquals(ProductStatus.NEW, productItem.getStatus());
			assertNotNull(productItem.getType());
			assertNotNull(productItem.getInvoiceLine());
			assertEquals(p, productItem.getProduct());


			assertNotNull(il.getCollege());
			assertEquals(p.getCollege(), il.getCollege());
			assertNotNull(il.getDescription());
			assertNotNull(il.getTitle());
			assertEquals(p.getPriceExTax(), il.getPriceEachExTax());
			assertEquals(p.getPriceIncTax(), il.getPriceEachIncTax());
			assertNotNull(il.getDiscountEachExTax());
			assertNotNull(il.getTaxEach());
			assertNotNull(il.getQuantity());


			if (productItem instanceof Membership) {
				assertEquals(model.getPayer(), ((Membership) productItem).getContact());
				assertEquals((Integer) 2, productItem.getType());
			} else if (productItem instanceof Voucher) {
				assertEquals((Integer) 3, productItem.getType());
				assertEquals(model.getPayer(), ((Voucher) productItem).getContact());
			} else
				throw new IllegalArgumentException();

		}
	}
	
	@Test
	public void testVoucherPurchase() throws InterruptedException {
		purchaseController = init(Collections.EMPTY_LIST, Arrays.asList(7L), Collections.EMPTY_LIST, false);
		PurchaseModel model = purchaseController.getModel();

		assertEquals(1, model.getProducts().size());
		assertTrue(model.getProducts().get(0) instanceof VoucherProduct);

		Contact contact = Cayenne.objectForPK(purchaseController.getModel().getObjectContext(), Contact.class, 1189159);
		addContact(contact);

		//check purchaseModel
		assertEquals(1,model.getAllEnabledProductItems().size());
		assertEquals(1,model.getAllProductItems(contact).size());
		assertEquals(1,model.getEnabledProductItems(contact).size());
		assertEquals(0,model.getDisabledProductItems(contact).size());
		assertTrue(model.getEnabledProductItems(contact).get(0) instanceof Voucher);

		//check persistence state
		assertEquals(ProductStatus.NEW, model.getEnabledProductItems(contact).get(0).getStatus());

		//proceed to payment
		proceedToPayment();

		assertEquals(1,model.getEnabledProductItems(contact).size());

		//check payment
		assertEquals(new Money("10.0"), model.getPayment().getAmount());
		assertEquals(1, model.getPayment().getPaymentInLines().size());
		assertEquals(1, model.getInvoice().getInvoiceLines().size());

		//make payment
		makeValidPayment();

		ProductItem productItem = model.getAllEnabledProductItems().get(0);
		
		assertTrue(productItem instanceof Voucher);
		assertEquals(ProductStatus.ACTIVE, productItem.getStatus());
		assertEquals(new Money("100.0"), ((Voucher) productItem).getRedemptionValue());
		assertEquals(new Money("100.0"), ((Voucher) productItem).getValueRemaining());
		assertEquals(new Money("100.0"), ((Voucher) productItem).getValueOnPurchase());
		
		assertEquals(PaymentStatus.SUCCESS, purchaseController.getPaymentEditorDelegate().getPaymentIn().getStatus());
	}

	@Test
	public void testAddProduct() throws InterruptedException {
		testPurchaseModel();


		ActionParameter parameter = new ActionParameter(PurchaseController.Action.addProduct);
		product = Cayenne.objectForPK(model.getObjectContext(), Product.class, 10L);
		parameter.setValue(product);
		performAction(parameter);

		assertEquals(2, model.getProducts().size());
		assertEquals(2, model.getAllProductItems(model.getPayer()).size());
		assertEquals(1, model.getAllEnabledProductItems().size());

		assertEquals(1, purchaseController.getErrors().size());
		assertEquals("Please enter the correct price for voucher: my test voucher product part 6.", purchaseController.getErrors().get(enterVoucherPrice.name()));


		proceedToPayment();
		assertEquals(2, model.getProducts().size());
		assertEquals(1, model.getAllProductItems(model.getPayer()).size());
		assertEquals(1, model.getAllEnabledProductItems().size());

		makeValidPayment();

		List<ProductItem> items = model.getAllProductItems(model.getPayer());
		for (ProductItem productItem : items) {
			assertEquals(ProductStatus.ACTIVE, productItem.getStatus());
		}
	}

	@Test
	public void testDuplicateMembership() throws InterruptedException {
		testAddProduct();

		purchaseController = init(Collections.EMPTY_LIST, Arrays.asList(9L), Collections.EMPTY_LIST, false);
		PurchaseModel model = purchaseController.getModel();

		addContact(contact);

		assertEquals(0, model.getAllEnabledProductItems().size());
		assertEquals(1, purchaseController.getErrors().size());
		assertEquals("Vladimir Putin is already has this membership 23458.", purchaseController.getErrors().get(duplicatedMembership.name()));
	}

	@Test
	public void testArticleProduct() throws InterruptedException {
		purchaseController = init(Collections.EMPTY_LIST, Arrays.asList(12L), Collections.EMPTY_LIST, false);
		PurchaseModel model = purchaseController.getModel();

		assertEquals(1, model.getProducts().size());
		assertTrue(model.getProducts().get(0) instanceof ArticleProduct);

		Contact contact = Cayenne.objectForPK(purchaseController.getModel().getObjectContext(), Contact.class, 1189159);
		addContact(contact);

		//check purchaseModel
		assertEquals(1,model.getAllEnabledProductItems().size());
		assertEquals(1,model.getAllProductItems(contact).size());
		assertEquals(1,model.getEnabledProductItems(contact).size());
		assertEquals(0,model.getDisabledProductItems(contact).size());
		assertTrue(model.getEnabledProductItems(contact).get(0) instanceof Article);

		//check persistence state
		assertEquals(ProductStatus.NEW, model.getEnabledProductItems(contact).get(0).getStatus());

		//proceed to payment
		proceedToPayment();

		assertEquals(1,model.getEnabledProductItems(contact).size());

		//check payment
		assertEquals(new Money("11"), model.getPayment().getAmount());
		assertEquals(1, model.getPayment().getPaymentInLines().size());
		assertEquals(1, model.getInvoice().getInvoiceLines().size());

		//make payment
		makeValidPayment();

		assertEquals(ProductStatus.ACTIVE ,model.getAllEnabledProductItems().get(0).getStatus());
		assertEquals(PaymentStatus.SUCCESS, purchaseController.getPaymentEditorDelegate().getPaymentIn().getStatus());

		ObjectContext objectContext = purchaseController.getModel().getObjectContext();
		SelectQuery selectQuery = new SelectQuery(QueuedRecord.class, ExpressionFactory.matchExp(QueuedRecord.ENTITY_IDENTIFIER_PROPERTY, "Article"));
		List list = objectContext.performQuery(selectQuery);
		assertFalse("QueuedRecords for article entity should exist in the queue", list.isEmpty());
	}

}
