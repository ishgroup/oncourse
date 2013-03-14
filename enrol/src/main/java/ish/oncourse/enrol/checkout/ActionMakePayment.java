package ish.oncourse.enrol.checkout;

import static ish.oncourse.enrol.checkout.PurchaseController.Message.corporatePassShouldBeEntered;

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
        {
            getController().addError(corporatePassShouldBeEntered);
            return false;
        }
        return true;
	}
}
