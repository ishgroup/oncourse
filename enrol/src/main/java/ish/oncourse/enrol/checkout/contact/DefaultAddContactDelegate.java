package ish.oncourse.enrol.checkout.contact;

import ish.oncourse.enrol.checkout.PurchaseController;

public class DefaultAddContactDelegate implements AddContactDelegate{

	private PurchaseController purchaseController;

	private ContactCredentials contactCredentials = new ContactCredentials();


	@Override
	public void cancelEditing() {
		purchaseController.performAction(new PurchaseController.ActionParameter(PurchaseController.Action.CANCEL_ADD_CONTACT));
	}

	@Override
	public void saveEditing() {
		PurchaseController.ActionParameter actionParameter = new PurchaseController.ActionParameter(PurchaseController.Action.ADD_CONTACT);
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
