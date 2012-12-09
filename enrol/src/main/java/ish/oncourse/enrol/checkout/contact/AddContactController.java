package ish.oncourse.enrol.checkout.contact;

import ish.oncourse.enrol.checkout.ADelegate;
import ish.oncourse.enrol.checkout.PurchaseController;

public class AddContactController extends ADelegate implements AddContactDelegate{


	private ContactCredentials contactCredentials = new ContactCredentials();


	@Override
	public void resetContact() {
		getPurchaseController().performAction(new PurchaseController.ActionParameter(PurchaseController.Action.cancelAddContact));
	}

	@Override
	public void addContact() {
		PurchaseController.ActionParameter actionParameter = new PurchaseController.ActionParameter(PurchaseController.Action.addContact);
		actionParameter.setErrors(getErrors());
		actionParameter.setValue(contactCredentials);
		getPurchaseController().performAction(actionParameter);
	}

	public ContactCredentials getContactCredentials() {
		return contactCredentials;
	}
}
