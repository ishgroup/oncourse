/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.enrol.checkout;


import ish.oncourse.enrol.checkout.contact.ContactCredentials;
import ish.oncourse.model.Contact;
import ish.oncourse.model.StudentConcession;
import org.apache.cayenne.CayenneRuntimeException;
import org.apache.cayenne.validation.ValidationException;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class ConcessionDelegateTest extends ACheckoutTest{


	@Before
	public void setup() throws Exception {

		setup("ish/oncourse/enrol/checkout/ConcessionDelegateTest.xml");
	}

	@Test
	public void test() {
		createPurchaseController(1001);


		ContactCredentials contactCredentials = createContactCredentialsBy("StudentFirstName2","StudentLastName2", "Student2@Student2.de");
		PurchaseController.ActionParameter parameter = new PurchaseController.ActionParameter(PurchaseController.Action.addContact);
		parameter.setValue(contactCredentials);
		ActionAddContact actionAddContact = PurchaseController.Action.addContact.createAction(purchaseController, parameter);
		actionAddContact.parse();
		Contact contact = actionAddContact.getContact();
		
		actionAddContact.makeAction();


		StudentConcession concession = purchaseController.getContactEditorDelegate().getConcessionDelegate().getStudentConcession();


		assertEquals(concession.getStudent(), contact.getStudent());
		//verify that college Id is set
		assertNotNull(concession.getCollege());
		assertEquals(concession.getCollege(), contact.getCollege());

		concession.setCollege(null);
		try{
			purchaseController.getContactEditorDelegate().saveContact();
			//if commit was successful
			assertTrue("Commit can not be successful because college is not set", false);
		} catch (Exception e){
			assertTrue(e instanceof CayenneRuntimeException);
			assertEquals(((ValidationException) e).getValidationResult().toString(), "Validation failure for ish.oncourse.model.StudentConcession.college: \"college\"  is required.");
		}
				
	}

}