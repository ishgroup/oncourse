package ish.oncourse.enrol.checkout;

import ish.common.types.EnrolmentStatus;
import ish.common.types.PaymentType;
import ish.oncourse.model.Contact;
import ish.oncourse.model.Enrolment;
import ish.oncourse.model.PaymentIn;
import org.apache.cayenne.PersistenceState;
import org.apache.cayenne.query.SelectQuery;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

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
		addCorporatePass("password1");

        selectCardEditor();
        selectCorporatePassEditor();
		addCorporatePass("password1");

		PurchaseController.ActionParameter actionParameter = new PurchaseController.ActionParameter(PurchaseController.Action.makePayment);
		purchaseController.performAction(actionParameter);

		assertNull(purchaseController.getPaymentEditorDelegate());
		SelectQuery query = new SelectQuery(PaymentIn.class);
		assertEquals(0, purchaseController.getModel().getObjectContext().performQuery(query).size());

		List<Enrolment> enrolments = purchaseController.getModel().getAllEnabledEnrolments();
		for (Enrolment enrolment : enrolments) {
			assertEquals(EnrolmentStatus.SUCCESS, enrolment.getStatus());
			assertEquals(PersistenceState.COMMITTED , enrolment.getPersistenceState());
		}
	}

	private void addCorporatePass(String corporatePass) {
		ActionParameter parameter = new ActionParameter(Action.addCorporatePass);
		parameter.setValue(corporatePass);
		purchaseController.performAction(parameter);
		assertNotNull(purchaseController.getModel().getInvoice().getCorporatePassUsed());
		assertPayer(purchaseController.getModel().getCorporatePass().getContact());
	}

	private void selectCardEditor() {
        ActionParameter parameter = new ActionParameter(selectCardEditor);
        performAction(parameter);
        assertEquals(PaymentType.CREDIT_CARD, purchaseController.getModel().getPayment().getType());
        assertFalse(purchaseController.getModel().getPayment().isZeroPayment());
        assertTrue(purchaseController.isEditPayment());
        assertNull(purchaseController.getModel().getInvoice().getCorporatePassUsed());

		Contact contact = purchaseController.getModel().getContacts().get(0);
		assertPayer(contact);
    }

    private void selectCorporatePassEditor() {
        ActionParameter parameter = new ActionParameter(selectCorporatePassEditor);
        performAction(parameter);

        assertEquals(PaymentType.INTERNAL, purchaseController.getModel().getPayment().getType());
        assertTrue(purchaseController.getModel().getPayment().isZeroPayment());
        assertTrue(purchaseController.isEditCorporatePass());
    }
}
