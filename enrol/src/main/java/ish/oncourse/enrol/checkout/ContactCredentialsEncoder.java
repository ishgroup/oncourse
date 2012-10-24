package ish.oncourse.enrol.checkout;

import ish.oncourse.enrol.checkout.contact.ContactCredentials;
import ish.oncourse.model.Contact;

public class ContactCredentialsEncoder {

	private ContactCredentials contactCredentials;
	private Contact contact;

	private PurchaseController purchaseController;

	public void encode()
	{
		contact = purchaseController.getStudentService().getStudentContact(contactCredentials.getFirstName(), contactCredentials.getLastName(), contactCredentials.getEmail());

		if (contact != null) {
			contact = purchaseController.getModel().localizeObject(contact);
			if (contact.getStudent() == null) {
				contact.createNewStudent();
			}
		} else {
			contact = purchaseController.getModel().getObjectContext().newObject(Contact.class);

			contact.setCollege(purchaseController.getModel().getCollege());

			contact.setGivenName(contactCredentials.getFirstName());
			contact.setFamilyName(contactCredentials.getLastName());
			contact.setEmailAddress(contactCredentials.getEmail());
			contact.createNewStudent();
			contact.setIsMarketingViaEmailAllowed(true);
			contact.setIsMarketingViaPostAllowed(true);
			contact.setIsMarketingViaSMSAllowed(true);
		}

	}

	public ContactCredentials getContactCredentials() {
		return contactCredentials;
	}

	public void setContactCredentials(ContactCredentials contactCredentials) {
		this.contactCredentials = contactCredentials;
	}

	public Contact getContact() {
		return contact;
	}

	public PurchaseController getPurchaseController() {
		return purchaseController;
	}

	public void setPurchaseController(PurchaseController purchaseController) {
		this.purchaseController = purchaseController;
	}


	public static enum ERRORS
	{
		ALREADY_CONTAINS,

	}
}
