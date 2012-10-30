package ish.oncourse.enrol.checkout.contact;

import ish.oncourse.enrol.checkout.PurchaseController;

import java.util.ArrayList;
import java.util.Map;

public class AddContactController implements AddContactDelegate{

	private PurchaseController purchaseController;

	private ContactCredentials contactCredentials = new ContactCredentials();


	@Override
	public void cancelEditing() {
		purchaseController.performAction(new PurchaseController.ActionParameter(PurchaseController.Action.CANCEL_ADD_CONTACT));
	}

	@Override
	public void saveEditing(Map<String,String> errors) {
		PurchaseController.ActionParameter actionParameter = new PurchaseController.ActionParameter(PurchaseController.Action.ADD_CONTACT);
		actionParameter.setErrors(new ArrayList<String>(errors.values()));
		actionParameter.setValue(contactCredentials);
		purchaseController.performAction(actionParameter);
	}

	public PurchaseController getPurchaseController() {
		return purchaseController;
	}

	public void setPurchaseController(PurchaseController purchaseController) {
		this.purchaseController = purchaseController;
	}

	public ContactCredentials getContactCredentials() {
		return contactCredentials;
	}
}
