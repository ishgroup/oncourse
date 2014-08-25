package ish.oncourse.enrol.checkout;

import ish.oncourse.enrol.checkout.contact.ContactCredentials;
import ish.oncourse.enrol.checkout.contact.ContactEditorController;
import ish.oncourse.model.*;
import ish.oncourse.services.preference.PreferenceController;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.Ordering;
import org.apache.cayenne.query.SelectQuery;
import org.apache.cayenne.query.SortOrder;
import org.apache.commons.lang3.time.DateUtils;
import org.dbunit.dataset.ReplacementDataSet;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

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

        SelectQuery selectQuery = new SelectQuery(QueuedTransaction.class);
        selectQuery.addOrdering(new Ordering(QueuedTransaction.CREATED_PROPERTY, SortOrder.ASCENDING));
        ObjectContext objectContext = cayenneService.newContext();
        List<QueuedTransaction> queuedTransactions = objectContext.performQuery(selectQuery);
        //five transaction: preference, preference, Student-Contact, Student-Contact, Contact-ContactRelation-Contact
        assertEquals(5, queuedTransactions.size());

        assertEquals(1, queuedTransactions.get(0).getQueuedRecords().size());
        assertEquals(Preference.class.getSimpleName(), queuedTransactions.get(0).getQueuedRecords().get(0).getEntityIdentifier());

        assertEquals(1, queuedTransactions.get(1).getQueuedRecords().size());
        assertEquals(Preference.class.getSimpleName(), queuedTransactions.get(0).getQueuedRecords().get(0).getEntityIdentifier());

        //Student-Contact.id=1002
        Contact contact1002 = Cayenne.objectForPK(purchaseController.getModel().getObjectContext(), Contact.class, 1002);
        assertQueuedTransactions(queuedTransactions.get(2), 2, contact1002, contact1002.getStudent());

        //Student-Contact.id=1003
        Contact contact1003 = Cayenne.objectForPK(purchaseController.getModel().getObjectContext(), Contact.class, 1003);
        assertQueuedTransactions(queuedTransactions.get(3), 2, contact1003, contact1003.getStudent());

        //Contact.1002-ContactRelation-Contact.1003
        assertQueuedTransactions(queuedTransactions.get(4), 3, contact1003, contact1003.getFromContacts().get(0), contact1003.getFromContacts().get(0).getFromContact());
    }

    public void assertQueuedTransactions(QueuedTransaction transaction, int countRecords, Queueable... queueables) {
        Comparator<QueuedRecord> comparator = new Comparator<QueuedRecord>() {
            @Override
            public int compare(QueuedRecord o1, QueuedRecord o2) {
                int result = o1.getEntityIdentifier().compareTo(o2.getEntityIdentifier());
                return (result != 0 ? result :  o1.getEntityWillowId().compareTo(o2.getEntityWillowId()));
            }
        };
        ArrayList<QueuedRecord> records = new ArrayList<>(transaction.getQueuedRecords());
        Collections.sort(records, comparator);

        assertEquals(String.format("Count queued records for %s should be %s", transaction, countRecords), countRecords, transaction.getQueuedRecords().size());
        for (final Queueable queueable : queueables) {
            int result = Collections.binarySearch(records,
                    new QueuedRecord(QueuedRecordAction.CREATE, queueable.getObjectId().getEntityName(), queueable.getId()), comparator);
            assertTrue(String.format("Queueable: %s found in records: %s", queueable, transaction.getQueuedRecords()), result >= 0);
        }
    }


    @Test
    public void test_parentContactWithoutStudentRelation()
    {
        createPurchaseController(1001);
        purchaseController.getPreferenceController().setCollectParentDetails(true);
        purchaseController.getPreferenceController().setContactAgeWhenNeedParent(18);

        ActionParameter parameter = new ActionParameter(Action.addContact);
        ContactCredentials contactCredentials = createContactCredentialsBy("Child2", "Parent2WithoutStudent", "Child2@Parent2WithoutStudent.net");
        parameter.setValue(contactCredentials);
        performAction(parameter);
    }
}
