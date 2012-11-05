package ish.oncourse.enrol.checkout;

public class ActionFinishPayment extends APurchaseAction {
	@Override
	protected void makeAction() {
		getController().setState(PurchaseController.State.finalized);
	}

	@Override
	protected void parse() {
	}

	@Override
	protected boolean validate() {
		return true;
	}
}
