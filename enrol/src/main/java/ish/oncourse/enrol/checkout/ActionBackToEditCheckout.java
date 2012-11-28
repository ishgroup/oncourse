package ish.oncourse.enrol.checkout;

import static ish.oncourse.enrol.checkout.PurchaseController.Error.illegalState;

public class ActionBackToEditCheckout extends APurchaseAction {

	private PurchaseController.Action action;

	@Override
	protected void makeAction() {
		getController().setState(PurchaseController.State.editCheckout);
	}

	@Override
	protected void parse() {
		if (getParameter() != null)
			action = getParameter().getValue(PurchaseController.Action.class);
	}

	@Override
	protected boolean validate() {

		if (getController().getState() == PurchaseController.State.editPayment
				&& PurchaseController.COMMON_ACTIONS.contains(action)) {
			getController().setState(PurchaseController.State.editCheckout);
			return true;
		}
		getController().addError(illegalState);
		return false;
	}
}
