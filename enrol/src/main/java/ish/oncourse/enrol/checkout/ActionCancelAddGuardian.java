package ish.oncourse.enrol.checkout;

import static ish.oncourse.enrol.checkout.PurchaseController.State.editCheckout;

public class ActionCancelAddGuardian extends APurchaseAction{
	@Override
	protected void makeAction() {
		getController().setAddContactController(null);
		getController().setState(editCheckout);
	}

	@Override
	protected void parse() {
	}

	@Override
	protected boolean validate() {
		if (getModel().getContacts().size() == 0)
		{
			getController().addError(PurchaseController.Message.payerNotDefined);
			return false;
		}
		return true;
	}
}
