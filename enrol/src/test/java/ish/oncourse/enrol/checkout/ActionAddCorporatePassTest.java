package ish.oncourse.enrol.checkout;

import ish.common.types.PaymentType;
import org.junit.Before;
import org.junit.Test;

import static ish.oncourse.enrol.checkout.PurchaseController.Action;
import static ish.oncourse.enrol.checkout.PurchaseController.Action.selectCardEditor;
import static ish.oncourse.enrol.checkout.PurchaseController.Action.selectCorporatePassEditor;
import static ish.oncourse.enrol.checkout.PurchaseController.ActionParameter;
import static org.junit.Assert.*;

public class ActionAddCorporatePassTest extends ACheckoutTest {

    @Before
    public void setup() throws Exception {
        setup("ish/oncourse/enrol/checkout/ActionAddCorporatePassTest.xml");
    }

    @Test
    public void test() {
        createPurchaseController(1001);
        addFirstContact(1001);
        proceedToPayment();

        selectCorporatePassEditor();

        selectCardEditor();

        selectCorporatePassEditor();

        ActionParameter parameter = new ActionParameter(Action.addCorporatePass);
        parameter.setValue("password1");
        purchaseController.performAction(parameter);
        assertNotNull(purchaseController.getModel().getInvoice().getCorporatePassUsed());

        selectCardEditor();

        selectCorporatePassEditor();


    }

    private void selectCardEditor() {
        ActionParameter parameter = new ActionParameter(selectCardEditor);
        performAction(parameter);
        assertEquals(PaymentType.CREDIT_CARD, purchaseController.getModel().getPayment().getType());
        assertFalse(purchaseController.getModel().getPayment().isZeroPayment());
        assertTrue(purchaseController.isEditPayment());
        assertNull(purchaseController.getModel().getInvoice().getCorporatePassUsed());
    }

    private void selectCorporatePassEditor() {
        ActionParameter parameter = new ActionParameter(selectCorporatePassEditor);
        performAction(parameter);

        assertEquals(PaymentType.INTERNAL, purchaseController.getModel().getPayment().getType());
        assertTrue(purchaseController.getModel().getPayment().isZeroPayment());
        assertTrue(purchaseController.isEditCorporatePass());
    }
}
