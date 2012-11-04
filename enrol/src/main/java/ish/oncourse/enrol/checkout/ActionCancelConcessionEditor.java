package ish.oncourse.enrol.checkout;

import static ish.oncourse.enrol.checkout.PurchaseController.State.EDIT_CHECKOUT;

public class ActionCancelConcessionEditor extends APurchaseAction{
	@Override
	protected void makeAction() {
		getController().setConcessionEditorController(null);
		getController().setState(EDIT_CHECKOUT);
	}

	@Override
	protected void parse() {
	}

	@Override
	protected boolean validate() {
		return true;
	}
}
