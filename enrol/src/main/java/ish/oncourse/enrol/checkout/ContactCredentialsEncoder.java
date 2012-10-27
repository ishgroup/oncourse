package ish.oncourse.enrol.checkout;

import ish.oncourse.enrol.checkout.contact.ContactCredentials;
import ish.oncourse.model.College;
import ish.oncourse.model.Contact;
import org.apache.cayenne.ObjectContext;

public class ContactCredentialsEncoder {

	private ContactCredentials contactCredentials;
	private PurchaseController purchaseController;

	private Contact contact;


	public void encode()
	{
		contact = purchaseController.getStudentService().getStudentContact(contactCredentials.getFirstName(), contactCredentials.getLastName(), contactCredentials.getEmail());

		/**
		 * The following changes can be canceled, so we needs child context to do it
		 */
		ObjectContext objectContext = purchaseController.getModel().getObjectContext().createChildContext();

		if (contact != null) {
			contact = (Contact) objectContext.localObject(contact.getObjectId(), null);
			if (contact.getStudent() == null) {
				contact.createNewStudent();
			}
		} else {
			contact = objectContext.newObject(Contact.class);

			contact.setCollege((College)objectContext.localObject(purchaseController.getModel().getCollege().getObjectId(),null));

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
