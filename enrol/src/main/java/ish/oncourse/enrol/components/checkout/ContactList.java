package ish.oncourse.enrol.components.checkout;

import ish.oncourse.enrol.utils.PurchaseController;
import ish.oncourse.model.Contact;

import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;

import java.util.List;

public class ContactList {

	@Parameter (required = true)
	@Property
	private PurchaseController purchaseController;

	@Property
	private Contact contact;
	
	public List<Contact> getContacts() {
		return purchaseController.getModel().getContacts();
	}

}
