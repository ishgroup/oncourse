/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.enrol.checkout;

import ish.common.types.ProductType;
import ish.oncourse.enrol.checkout.contact.AddContactController;
import ish.oncourse.enrol.checkout.contact.ContactEditorDelegate;
import ish.oncourse.model.Contact;
import ish.oncourse.model.ProductItem;
import ish.oncourse.services.preference.PreferenceController;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.PersistenceState;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;


public class ActionAddCompanyPayerTest extends ACheckoutTest {
	
	private Contact contact;
	private PurchaseController.ActionParameter param;
	private Contact company;

	@Before
	public void setup() throws Exception {
		setup("ish/oncourse/enrol/checkout/ActionAddCompanyPayerTest.xml");
	}
	
	@Test
	public void testExistingCommpany() {
		
		// reach the payment page and press 'add business payer' button
		prepareModel();

		// enter credentials for existing company and press 'OK' button 
		purchaseController.getAddContactDelegate().getContactCredentials().setLastName("Company Name");
		purchaseController.getAddContactDelegate().getContactCredentials().setEmail("company@email.com");
		purchaseController.getAddContactDelegate().addContact();

		// ensure that we came back on PaymentEdit page
		assertEquals(PurchaseController.State.editPayment, purchaseController.getState());
		assertNull(purchaseController.getAddContactDelegate());
		assertNotNull(purchaseController.getPaymentEditorDelegate());
		
		//extract company directly from db for checking payer
		company = Cayenne.objectForPK(purchaseController.getModel().getObjectContext(), Contact.class, 999L);

		checkModel();
	}



	@Test
	public void testNewCommpany() {
		// reach the payment page and press 'add business payer' button
		prepareModel();
		
		// enter credentials for new company and press 'OK' button 
		purchaseController.getAddContactDelegate().getContactCredentials().setLastName("New Company");
		purchaseController.getAddContactDelegate().getContactCredentials().setEmail("company@email.com");
		purchaseController.getAddContactDelegate().addContact();
		
		//we must get to the EditContact page in this case
		
		assertEquals(PurchaseController.State.editContact, purchaseController.getState());
		assertNull(purchaseController.getAddContactDelegate());
		ContactEditorDelegate delegate = purchaseController.getContactEditorDelegate();
		assertNotNull(delegate);
		
		// new company should be created
		company = delegate.getContact();

		assertNotNull(company);
		assertTrue(company.getIsCompany());
		assertEquals(company.getPersistenceState(), PersistenceState.NEW);

		// make sure that company fiedlset is displayed correctly
		for (PreferenceController.FieldDescriptor field: PreferenceController.FieldDescriptor.COMPANY_FIELDS) {
			assertTrue(delegate.getVisibleFields().contains(field.name()));
		}
		
		// ignore this step and press 'OK' button (no required field here)
		delegate.saveContact();
		
		// ensure that we came back on PaymentEdit page
		assertEquals(PurchaseController.State.editPayment, purchaseController.getState());
		assertNull(purchaseController.getContactEditorDelegate());
		assertNotNull(purchaseController.getPaymentEditorDelegate());

		checkModel();
	}
	
	private void prepareModel() {
		init(Arrays.asList(1002L), Arrays.asList(7L, 8L, 9L), Collections.EMPTY_LIST, false);

		contact = Cayenne.objectForPK(purchaseController.getModel().getObjectContext(), Contact.class, 1001L);

		//add contact
	 	param = new PurchaseController.ActionParameter(PurchaseController.Action.addContact);
		param.setValue(createContactCredentialsBy(contact));
		performAction(param);

		proceedToPayment();

		param = new PurchaseController.ActionParameter(PurchaseController.Action.addCompanyPayer);
		performAction(param);


		assertEquals(PurchaseController.State.addContact, purchaseController.getState());
		assertNotNull(purchaseController.getAddContactDelegate());
		assertTrue(purchaseController.getAddContactDelegate() instanceof AddContactController);
		assertTrue(purchaseController.getAddContactDelegate().isCompanyPayer());
	}


	private void checkModel(){
		
		//check that Payer was really changed
		assertNotNull(purchaseController.getModel().getPayer());
		assertNotEquals(purchaseController.getModel().getPayer().getPersistenceState(), PersistenceState.NEW);
		assertEquals(purchaseController.getModel().getPayer().getId(), company.getId());
		
		//reset 'company' reference because payer reference was localized for new company case 
		company = purchaseController.getModel().getPayer();
		
		//voucher products should be reassigned on payer
		assertEquals(3, purchaseController.getModel().getAllEnabledProductItems().size());

		assertEquals(1, purchaseController.getModel().getAllProductItems(company).size());
		assertEquals(ProductType.VOUCHER.getDatabaseValue(), purchaseController.getModel().getAllProductItems(company).get(0).getType());
		assertEquals(2, purchaseController.getModel().getAllProductItems(contact).size());

		//check that enrolnemts is not created for company
		assertEquals(1, purchaseController.getModel().getAllEnabledEnrolments().size());
		assertEquals(1, purchaseController.getModel().getAllEnrolments(contact).size());
		assertEquals(0, purchaseController.getModel().getAllEnrolments(company).size());

		// press on 'Summary' tab to return on EditCheckout page
		param = new PurchaseController.ActionParameter(PurchaseController.Action.backToEditCheckout);
		param.setValue(PurchaseController.Action.enableEnrolment);
		performAction(param);

		// ensure that we came back on EditCheckout page
		assertEquals(PurchaseController.State.editCheckout, purchaseController.getState());

		//check that enrolnemts is not created for company on this step
		assertEquals(1, purchaseController.getModel().getAllEnrolments(contact).size());
		assertEquals(0, purchaseController.getModel().getAllEnrolments(company).size());

		assertEquals(2, purchaseController.getModel().getAllProductItems(contact).size());
		//remaining productItems should be created for company in this case (but they should be unticked)
		assertEquals(3, purchaseController.getModel().getAllProductItems(company).size());
		//but voucher products should be ticked 
		for (ProductItem productItem : purchaseController.getModel().getAllProductItems(company)) {
			if (ProductType.VOUCHER.getDatabaseValue() == productItem.getType()) {
				assertTrue(purchaseController.getModel().isProductItemEnabled(productItem));
			} else {
				assertFalse(purchaseController.getModel().isProductItemEnabled(productItem));
			}
		}
	}
}
