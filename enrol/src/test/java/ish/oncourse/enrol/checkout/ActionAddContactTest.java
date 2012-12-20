package ish.oncourse.enrol.checkout;

import ish.oncourse.enrol.checkout.contact.ContactCredentials;
import ish.oncourse.model.Contact;
import ish.oncourse.model.CourseClass;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

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
		ObjectContext context = cayenneService.newContext();
		CourseClass courseClass = Cayenne.objectForPK(context, CourseClass.class, 1001);
		PurchaseModel model = createModel(context, Arrays.asList(courseClass), Collections.EMPTY_LIST, null);
		createPurchaseController(model);

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
		assertTrue(purchaseController.isEditContact());
		assertEquals(PurchaseController.State.editContact,purchaseController.getState());

		purchaseController.getContactEditorDelegate().saveContact();
		assertNull(purchaseController.getContactEditorDelegate());
		assertTrue(purchaseController.getModel().containsContactWith(contactCredentials));
	}
}
