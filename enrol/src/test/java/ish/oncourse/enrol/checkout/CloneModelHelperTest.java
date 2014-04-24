package ish.oncourse.enrol.checkout;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

public class CloneModelHelperTest extends ACheckoutTest {
    @Before
    public void setup() throws Exception {
        setup("ish/oncourse/enrol/checkout/CloneModelHelperTest.xml");
    }


    @Test
    public void test() {
        PurchaseController purchaseController = init(Arrays.asList(1001L, 1002L), Arrays.asList(1001L, 1002L), Arrays.asList(1001L, 1002L), false);
        addFirstContact(1001);
        addContact(1002);
        addCode("v1003");
        addCode("v1004");

        PurchaseModel purchaseModel = purchaseController.getModel();
        assertEquals(4, purchaseModel.getAllEnabledEnrolments().size());
        assertEquals(2, purchaseModel.getAllEnabledProductItems().size());
        assertEquals(2, purchaseModel.getContacts().size());
        assertEquals(2, purchaseModel.getDiscounts().size());
        assertEquals(2, purchaseModel.getVouchers().size());

        CloneModelHelper cloneModelHelper = new CloneModelHelper();
        cloneModelHelper.setPurchaseController(purchaseController);
        cloneModelHelper.setObjectContext(purchaseController.getCayenneService().newContext());
        cloneModelHelper.cloneModel();

        purchaseModel = purchaseController.getModel();
        assertEquals(4, purchaseModel.getAllEnabledEnrolments().size());
        assertEquals(2, purchaseModel.getAllEnabledProductItems().size());
        assertEquals(2, purchaseModel.getContacts().size());
        assertEquals(2, purchaseModel.getDiscounts().size());
        assertEquals(2, purchaseModel.getVouchers().size());
        assertEquals(0, purchaseModel.getVoucherPayments().size());
    }

}
