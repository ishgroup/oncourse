package ish.oncourse.enrol.checkout;

import ish.oncourse.enrol.checkout.contact.ContactCredentials;
import ish.oncourse.enrol.checkout.contact.ContactEditorController;
import ish.oncourse.model.*;
import ish.oncourse.services.preference.PreferenceController;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.SelectQuery;
import org.apache.commons.lang3.time.DateUtils;
import org.dbunit.dataset.ReplacementDataSet;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;
import java.util.List;

import static ish.oncourse.enrol.checkout.PurchaseController.Action;
import static ish.oncourse.enrol.checkout.PurchaseController.ActionParameter;
import static org.junit.Assert.*;

public class ActionAddContactTest extends ACheckoutTest {

    @Before
    public void setup() throws Exception {
        setup("ish/oncourse/enrol/checkout/ActionAddContactTest.xml");
    }

    @Override
    protected void configDataSet(ReplacementDataSet dataSet) {
        super.configDataSet(dataSet);
        dataSet.addReplacementObject("[parentDateOfBirth]", DateUtils.addYears(new Date(), -19));
        dataSet.addReplacementObject("[childDateOfBirth]", DateUtils.addYears(new Date(), -10));
    }

    @Test
    public void test() {
        createPurchaseController(1001);

        ContactCredentials contactCredentials = createContactCredentialsBy("StudentFirstName2", "StudentLastName2", "Student2@Student2.de");
        ActionParameter parameter = new ActionParameter(Action.addContact);
        parameter.setValue(contactCredentials);
        ActionAddContact actionAddContact = Action.addContact.createAction(purchaseController, parameter);
        actionAddContact.parse();
        Contact contact = actionAddContact.getContact();
        assertNotNull(actionAddContact.getContactCredentials());
        assertNotNull(contact);
        assertNotNull(contact.getStudent());
        assertNotNull(contact.getCollege());
        assertNotNull(contact.getStudent().getCollege());
        assertTrue(contact.getObjectId().isTemporary());
        assertTrue(contact.getStudent().getObjectId().isTemporary());
        assertEquals(contactCredentials.getFirstName(), contact.getGivenName());
        assertEquals(contactCredentials.getLastName(), contact.getFamilyName());
        assertEquals(contactCredentials.getEmail(), contact.getEmailAddress());
        assertFalse(purchaseController.getModel().isApplyPrevOwing());

        assertTrue(actionAddContact.validate());

        actionAddContact.makeAction();

        assertNotNull(purchaseController.getContactEditorDelegate());
        assertNull(purchaseController.getAddContactDelegate());
        assertNotNull(purchaseController.getContactEditorDelegate().getContact());
        assertFalse(purchaseController.getContactEditorDelegate().isFillRequiredProperties());
        assertFalse(purchaseController.getContactEditorDelegate().getVisibleFields().isEmpty());
        assertEquals(PreferenceController.ContactFiledsSet.enrolment, ((ContactEditorController) purchaseController.getContactEditorDelegate()).getContactFiledsSet());
        assertNotNull(purchaseController.getContactEditorDelegate().getConcessionDelegate());

        assertTrue(purchaseController.isEditContact());
        assertEquals(PurchaseController.State.editContact, purchaseController.getState());

        purchaseController.getContactEditorDelegate().saveContact();
        assertNull(purchaseController.getContactEditorDelegate());
        assertTrue(purchaseController.getModel().containsContactWith(contactCredentials));
        assertFalse(purchaseController.isApplyPrevOwing());
    }

    @Test
    public void test_contactAlreadyAdded() {
        createPurchaseController(1001);
        ActionParameter parameter = new ActionParameter(Action.addContact);
        ContactCredentials contactCredentials = createContactCredentialsBy("Tutor1", "Tutor1", "Tutor1@Tutor1.net");
        parameter.setValue(contactCredentials);
        performAction(parameter);

        assertNotNull(purchaseController.getModel().getPayer());
        assertNotNull(purchaseController.getModel().getPayer().getStudent());
        assertFalse(purchaseController.getModel().getPayer().getObjectId().isTemporary());
    }


    @Test
    public void test_addParentAddChild() {

        createPurchaseController(1002);
        purchaseController.getPreferenceController().setCollectParentDetails(true);
        purchaseController.getPreferenceController().setContactAgeWhenNeedParent(18);

        //contact 1002
        ActionParameter parameter = new ActionParameter(Action.addContact);
        ContactCredentials contactCredentials = createContactCredentialsBy("Parent1", "Parent1", "Parent1@Parent1.net");
        parameter.setValue(contactCredentials);
        performAction(parameter);

        PurchaseController.ActionParameter param = new PurchaseController.ActionParameter(PurchaseController.Action.addContact);
        performAction(param);

        //contact 1003
        parameter = new ActionParameter(Action.addContact);
        contactCredentials = createContactCredentialsBy("Child1", "Parent1", "Child1@Parent1.net");
        parameter.setValue(contactCredentials);
        performAction(parameter);

        SelectQuery selectQuery = new SelectQuery(QueuedRecord.class);
        ObjectContext objectContext = cayenneService.newContext();

        List<QueuedRecord> queuedRecords = objectContext.performQuery(selectQuery);
        for (QueuedRecord queuedRecord : queuedRecords) {
            Queueable queueable = queuedRecord.getLinkedRecord();
            if (queueable instanceof Preference)
                continue;
            if (queueable instanceof Contact) {
                assertTrue(queueable.getId() == 1002 || queueable.getId() == 1003);
                continue;
            }
            if (queueable instanceof ContactRelation) {
                assertTrue(((ContactRelation) queueable).getFromContact().getId() == 1002);
                assertTrue(((ContactRelation) queueable).getToContact().getId() == 1003);
                continue;
            }

            if (queueable instanceof Student) {
                assertTrue((((Student) queueable).getContact().getId() == 1002 || ((Student) queueable).getContact().getId() == 1003));
                continue;
            }
            assertFalse(String.format("Unexpexted queued record: %s", queueable), true);

        }


    }
}
