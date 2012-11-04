package ish.oncourse.enrol.checkout;

import ish.oncourse.enrol.checkout.contact.AddContactController;

import static ish.oncourse.enrol.checkout.PurchaseController.State.ADD_CONTACT;

public class ActionStartAddContact extends APurchaseAction{
	@Override
	protected void makeAction() {
		AddContactController addContactController = new AddContactController();
		addContactController.setPurchaseController(getController());
		getController().setAddContactController(addContactController);
		getController().setState(ADD_CONTACT);

	}

	@Override
	protected void parse() {
	}

	@Override
	protected boolean validate() {
		return true;
	}
}
