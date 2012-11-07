package ish.oncourse.enrol.checkout;

public class ActionCreditAccess extends APurchaseAction{

	@Override
	protected void makeAction() {
		//todo need specification
	}

	@Override
	protected void parse() {
	}

	@Override
	protected boolean validate() {
		getController().addError(PurchaseController.Error.creditAccessPasswordIsWrong);
		return false;
	}
}
