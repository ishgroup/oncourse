package ish.oncourse.enrol.components.checkout;

import ish.oncourse.enrol.checkout.PurchaseController;
import ish.oncourse.model.Contact;
import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;

import java.util.List;

public class ContactList {

	@Parameter (required = true)
	@Property
	private PurchaseController purchaseController;

	@Parameter(required = false)
	@Property
	private Block blockToRefresh;


	@Property
	private Contact contact;

	@Property
	private Integer index;

	public List<Contact> getContacts() {
		return purchaseController.getModel().getContacts();
	}

}
