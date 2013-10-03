package ish.oncourse.enrol.checkout;




import ish.oncourse.model.*;
import static ish.oncourse.enrol.checkout.PurchaseController.Message.*;
import static ish.oncourse.enrol.checkout.PurchaseController.State.*;
import ish.oncourse.enrol.checkout.PurchaseController.ActionParameter;
import org.apache.cayenne.Cayenne;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;




/**
 * User: artem
 * Date: 9/25/13
 * Time: 10:09 AM
 */
public class ProductItemTest extends PurchaseControllerTest {
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
         purchaseController = init(Collections.EMPTY_LIST, Arrays.asList(9L),Collections.EMPTY_LIST, false);
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

        contact=model.getPayer();

        assertNotNull(model.getPayer());
        assertEquals(model.getPayer(), model.getContacts().get(0));

        assertEquals(1, model.getAllEnabledProductItems().size());
        assertEquals(product, model.getAllEnabledProductItems().get(0).getProduct());


        List<Product> products = model.getProducts();
        List<InvoiceLine> invoiceLines=model.getInvoice().getInvoiceLines();
        for (int i=0;i<products.size();i++)
        {
            InvoiceLine il= invoiceLines.get(i);
            Product p=products.get(i);
            ProductItem productItem = model.getProductItemBy(model.getPayer(), p);


            assertEquals(model.getProductItemBy(model.getPayer(), p),il.getProductItem().get(i));

            assertNotNull(productItem.getCollege());
            assertEquals(p.getCollege(), productItem.getCollege());
            assertEquals("New", productItem.getStatus().getDisplayName());
            assertNotNull(productItem.getType());
            assertNotNull(productItem.getInvoiceLine());
            assertEquals(p, productItem.getProduct());


            assertNotNull(il.getCollege());
            assertEquals(p.getCollege(), il.getCollege());
            assertNotNull(il.getDescription());
            assertNotNull(il.getTitle());
            assertEquals(p.getPriceExTax(), il.getPriceEachExTax());
            assertNotNull(il.getDiscountEachExTax());
            assertNotNull(il.getTaxEach());
            assertNotNull(il.getQuantity());


            if (productItem instanceof Membership) {
                assertEquals(model.getPayer(), ((Membership) productItem).getContact());
                assertEquals((Integer) 2,productItem.getType());
            }else if(productItem instanceof Voucher){
                assertEquals((Integer) 3,productItem.getType());
                assertEquals(model.getPayer(), ((Voucher) productItem).getContact());
            }else
                throw new IllegalArgumentException();

        }
    }

    @Test
    public void testAddProduct() throws InterruptedException {
        testPurchaseModel();



        ActionParameter parameter=new ActionParameter(PurchaseController.Action.addProduct);
        product = Cayenne.objectForPK(model.getObjectContext(), Product.class, 10L);
        parameter.setValue(product);
        performAction(parameter);

        assertEquals(2, model.getProducts().size());
        assertEquals(2, model.getAllProductItems(model.getPayer()).size());
        assertEquals(1, model.getAllEnabledProductItems().size());

        assertEquals(1,purchaseController.getErrors().size());
        assertEquals("Enter price for voucher: my test voucher product part 6.", purchaseController.getErrors().get(enterVoucherPrice.name()));


        proceedToPayment();

        makeValidPayment();

    }





    @Test
    public void testDuplicateMembership() throws InterruptedException {
        testAddProduct();

        purchaseController = init(Collections.EMPTY_LIST, Arrays.asList(9L),Collections.EMPTY_LIST,false);
        PurchaseModel model = purchaseController.getModel();

        addContact(contact);

        assertEquals(0, model.getAllEnabledProductItems().size());
        assertEquals(1, purchaseController.getErrors().size());
        assertEquals("Vladimir Putin is already has this membership 23458.", purchaseController.getErrors().get(duplicatedMembership.name()));
    }






}
