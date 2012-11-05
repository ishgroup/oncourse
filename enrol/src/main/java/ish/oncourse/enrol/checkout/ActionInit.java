package ish.oncourse.enrol.checkout;

public class ActionInit extends APurchaseAction {
	@Override
	protected void makeAction() {
		getController().getVoucherRedemptionHelper().setInvoice(getModel().getInvoice());
		ActionStartAddContact action = PurchaseController.Action.startAddContact.createAction(getController());
		action.action();
	}

	@Override
	protected void parse() {
	}

	@Override
	protected boolean validate() {
		if (getModel().getPayer() != null)
			return false;
		if (getModel().getContacts().size() > 0)
			return false;
		if (getModel().getClasses().size() < 1 && getModel().getProducts().size() < 1)
			return false;
		return true;
	}
}
