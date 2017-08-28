package ish.oncourse.enrol.checkout;

import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Voucher;
import ish.oncourse.model.VoucherProduct;
import org.apache.cayenne.query.SelectById;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
public class ActionSelectCorporatePassEditorTest extends ACheckoutTest {

    @Before
    public void setup() throws Exception {
        setup("ish/oncourse/enrol/checkout/ActionSelectCorporatePassEditorTest.xml");
    }

    @Test
    public void testCorporatePassForCourseClassesAndProducts() {
        CourseClass courseClass = createPurchaseController(1001);
        addFirstContact(1001);

        ActionAddProduct actionAddProduct = new ActionAddProduct();

        VoucherProduct product = SelectById.query(VoucherProduct.class, 1001)
                .selectOne(getModel().getObjectContext());
        actionAddProduct.setProduct(product);
        performAction(actionAddProduct, PurchaseController.Action.addProduct);

        proceedToPayment();
        assertEquals(courseClass.getFeeIncGst(null).add(product.getPriceIncTax()), getModel().getInvoice().getTotalGst());

        selectCorporatePassEditor();
        assertEquals(courseClass.getFeeIncGst(null).add(product.getPriceIncTax()), getModel().getInvoice().getTotalGst());

        selectCardEditor();
        assertEquals(courseClass.getFeeIncGst(null).add(product.getPriceIncTax()), getModel().getInvoice().getTotalGst());

        selectCorporatePassEditor();
        addCorporatePass("password1");
        assertEquals(courseClass.getFeeIncGst(null).add(product.getPriceIncTax()), getModel().getInvoice().getTotalGst());
        assertNotNull(getModel().getAllEnabledProductItems().get(0));
        assertTrue(getModel().getAllEnabledProductItems().get(0) instanceof Voucher);

        selectCardEditor();
        selectCorporatePassEditor();
        addCorporatePass("password1");

        makeCorporatePass();
    }
}