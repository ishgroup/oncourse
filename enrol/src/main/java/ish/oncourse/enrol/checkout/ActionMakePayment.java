package ish.oncourse.enrol.checkout;

public class ActionMakePayment extends APurchaseAction {
	@Override
	protected void makeAction() {
		getController().setState(PurchaseController.State.paymentProgress);
	}

	@Override
	protected void parse() {
	}

	@Override
	protected boolean validate() {
        if (getController().isEditCorporatePass() &&
                getController().getModel().getCorporatePass() == null)
            return false;
        return true;
	}
}
