package ish.oncourse.enrol.checkout;

import ish.oncourse.enrol.checkout.contact.ContactCredentials;
import ish.oncourse.enrol.checkout.contact.ContactEditorController;
import ish.oncourse.model.Contact;
import ish.oncourse.services.preference.PreferenceController;
import org.junit.Before;
import org.junit.Test;

import static ish.oncourse.enrol.checkout.PurchaseController.Action;
import static ish.oncourse.enrol.checkout.PurchaseController.ActionParameter;
import static org.junit.Assert.*;

public class ActionAddContactTest extends ACheckoutTest {

	@Before
	public void setup() throws Exception {
		setup("ish/oncourse/enrol/checkout/ActionAddContactTest.xml");
	}

	@Test
	public void test()
	{
		createPurchaseController(1001);

		ContactCredentials contactCredentials = createContactCredentialsBy("StudentFirstName2","StudentLastName2", "Student2@Student2.de");
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

		assertTrue(actionAddContact.validate());

		actionAddContact.makeAction();

		assertNotNull(purchaseController.getContactEditorDelegate());
		assertNull(purchaseController.getAddContactDelegate());
		assertNotNull(purchaseController.getContactEditorDelegate().getContact());
        assertFalse(purchaseController.getContactEditorDelegate().isFillRequiredProperties());
        assertFalse(purchaseController.getContactEditorDelegate().getVisibleFields().isEmpty());
        assertEquals(PreferenceController.ContactFiledsSet.enrolment, ((ContactEditorController)purchaseController.getContactEditorDelegate()).getContactFiledsSet());
        assertNotNull(purchaseController.getContactEditorDelegate().getConcessionDelegate());

		assertTrue(purchaseController.isEditContact());
		assertEquals(PurchaseController.State.editContact,purchaseController.getState());

		purchaseController.getContactEditorDelegate().saveContact();
		assertNull(purchaseController.getContactEditorDelegate());
		assertTrue(purchaseController.getModel().containsContactWith(contactCredentials));
        assertFalse(purchaseController.isApplyPrevOwing());
	}

	@Test
	public void test_contactAlreadyAdded()
	{
		createPurchaseController(1001);
		ActionParameter parameter = new ActionParameter(Action.addContact);
		ContactCredentials contactCredentials = createContactCredentialsBy("Tutor1","Tutor1", "Tutor1@Tutor1.net");
		parameter.setValue(contactCredentials);
		performAction(parameter);

		assertNotNull(purchaseController.getModel().getPayer());
		assertNotNull(purchaseController.getModel().getPayer().getStudent());
		assertFalse(purchaseController.getModel().getPayer().getObjectId().isTemporary());
	}
}
