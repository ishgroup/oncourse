package ish.oncourse.enrol.checkout;

import ish.oncourse.enrol.services.invoice.InvoiceProcessingService;
import ish.oncourse.model.Contact;
import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Enrolment;
import ish.oncourse.model.InvoiceLine;
import org.apache.cayenne.Cayenne;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static ish.oncourse.enrol.checkout.PurchaseController.ActionParameter;
import static org.junit.Assert.*;

public class ActionEnableEnrolmentTest extends ACheckoutTest {


    @Before
    public void setup() throws Exception {
		setup("ish/oncourse/enrol/checkout/ActionEnableEnrolmentTest.xml");
    }

    @Test
    public void testDuplicateEnrolment() {

		CourseClass courseClass = createPurchaseController(1001);

		Contact contact = addFirstContact(1001);

        //test duplicate enrolment
        assertNotNull(purchaseController.getModel().getErrorBy(purchaseController.getModel().getEnrolmentBy(contact, courseClass)));
        assertEquals(0, purchaseController.getModel().getEnabledEnrolments(contact).size());
        assertEquals(1, purchaseController.getModel().getDisabledEnrolments(contact).size());
        List<Enrolment> enrolments = purchaseController.getModel().getAllEnrolments(contact);
        for (Enrolment enrolment : enrolments) {
            assertTrue(enrolment.getObjectId().isTemporary());
        }

        contact = Cayenne.objectForPK(purchaseController.getModel().getObjectContext(), Contact.class, 1002);
        addContact(contact);
        assertNotNull(purchaseController.getModel().getErrorBy(purchaseController.getModel().getEnrolmentBy(contact, courseClass)));
        assertEquals(0, purchaseController.getModel().getEnabledEnrolments(contact).size());
        assertEquals(1, purchaseController.getModel().getDisabledEnrolments(contact).size());
        enrolments = purchaseController.getModel().getAllEnrolments(contact);
        for (Enrolment enrolment : enrolments) {
            assertDisabledEnrolment(enrolment);
        }


        ActionParameter actionParameter = new ActionParameter(PurchaseController.Action.proceedToPayment);
        actionParameter.setValue(purchaseController.getModel().getPayment());
        purchaseController.performAction(actionParameter);
        assertTrue("All enrolments are disabled", purchaseController.getErrors().containsKey(PurchaseController.Message.noEnabledItemForPurchase.name()));

        List<Contact> contacts = purchaseController.getModel().getContacts();
        for (Contact c : contacts) {
            assertDisabledEnrolments(c, 1);
        }
    }

    @Test
    public void testCourseClassEnded() {
		CourseClass courseClass = createPurchaseController(1002);

		Contact contact = addFirstContact(1001);
        assertNotNull(purchaseController.getModel().getErrorBy(purchaseController.getModel().getEnrolmentBy(contact, courseClass)));
        assertEquals(0, purchaseController.getModel().getEnabledEnrolments(contact).size());
        assertDisabledEnrolments(contact, 1);
    }

    @Test
    public void testCourseClassHasNoPlaces() {
        //class has only three places and one is already used
		CourseClass courseClass = createPurchaseController(1003);

        //add first contact
        Contact contact = addFirstContact(1001);

        assertNull(purchaseController.getModel().getErrorBy(purchaseController.getModel().getEnrolmentBy(contact, courseClass)));
        assertEquals(0, purchaseController.getModel().getDisabledEnrolments(contact).size());
        assertEnabledEnrolments(contact, 1, false);
        assertEquals(1, purchaseController.getModel().getAllEnabledEnrolments().size());
        Enrolment enrolment = purchaseController.getModel().getAllEnabledEnrolments().get(0);
        InvoiceLine invoiceLine = enrolment.getInvoiceLine();
        assertNotNull(invoiceLine);
        assertEquals("Test invoiceLine title", String.format(InvoiceProcessingService.INVOICE_LINE_TITLE_TEMPALTE,
                contact.getGivenName(), contact.getFamilyName() ,
                courseClass.getCourse().getCode(),courseClass.getCode(),courseClass.getCourse().getName()),
                invoiceLine.getTitle());


        //add second contact
        contact = Cayenne.objectForPK(purchaseController.getModel().getObjectContext(), Contact.class, 1002);
        addContact(contact);
        assertNull(purchaseController.getModel().getErrorBy(purchaseController.getModel().getEnrolmentBy(contact, courseClass)));
        assertEquals(0, purchaseController.getModel().getDisabledEnrolments(contact).size());
        assertEnabledEnrolments(contact, 1, false);
        assertEquals(2, purchaseController.getModel().getAllEnabledEnrolments().size());

        //add third contact
        contact = Cayenne.objectForPK(purchaseController.getModel().getObjectContext(), Contact.class, 1003);
        addContact(contact);
        assertNotNull(purchaseController.getModel().getErrorBy(purchaseController.getModel().getEnrolmentBy(contact, courseClass)));
        assertEquals(0, purchaseController.getModel().getEnabledEnrolments(contact).size());
        assertDisabledEnrolments(contact, 1);
        assertEquals(2, purchaseController.getModel().getAllEnabledEnrolments().size());
    }


    @Test
    public void testDeleteDisabledEnrolments() {
        testCourseClassHasNoPlaces();
        proceedToPayment();
        assertTrue(purchaseController.getErrors().isEmpty());
        assertTrue(purchaseController.isEditPayment());
        assertEquals(2, purchaseController.getModel().getAllEnabledEnrolments().size());

        Contact contact1 = Cayenne.objectForPK(purchaseController.getModel().getObjectContext(), Contact.class, 1001);
        assertEquals(1, purchaseController.getModel().getEnabledEnrolments(contact1).get(0).getInvoiceLine().getInvoiceLineDiscounts().size());
        assertEnabledEnrolments(contact1, 1, true);

        Contact contact2 = Cayenne.objectForPK(purchaseController.getModel().getObjectContext(), Contact.class, 1002);
        assertEquals(1, purchaseController.getModel().getEnabledEnrolments(contact2).get(0).getInvoiceLine().getInvoiceLineDiscounts().size());
        assertEnabledEnrolments(contact2, 1, true);

        Contact contact3 = Cayenne.objectForPK(purchaseController.getModel().getObjectContext(), Contact.class, 1003);
        assertEnabledEnrolments(contact3, 0, false);

        //emulate back button
        purchaseController.adjustState(PurchaseController.Action.enableEnrolment);
        assertEquals(1, purchaseController.getModel().getEnabledEnrolments(contact1).get(0).getInvoiceLine().getInvoiceLineDiscounts().size());
        assertEnabledEnrolments(contact1, 1, true);

        assertEnabledEnrolments(contact2, 1, true);
        assertEquals(1, purchaseController.getModel().getEnabledEnrolments(contact2).get(0).getInvoiceLine().getInvoiceLineDiscounts().size());

        assertDisabledEnrolments(contact3, 1);

        //disable enrolment with discount and proceedToPayment (the test for InvoiceLineDiscount.isAsyncReplicationAllowed() which can be performed on postRemove)
        ActionParameter actionParameter = new ActionParameter(PurchaseController.Action.disableEnrolment);
        actionParameter.setValue(purchaseController.getModel().getEnabledEnrolments(contact1).get(0));
        purchaseController.performAction(actionParameter);

        proceedToPayment();
        assertDisabledEnrolments(contact1, 0);
    }
}
