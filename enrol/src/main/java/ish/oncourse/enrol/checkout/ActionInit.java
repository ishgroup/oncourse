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
		if (getModel().getClasses().size() < 1 && getModel().getProducts().size() < 1)
		{
			getController().addError(PurchaseController.Error.noSelectedItemForPurchase);
			getController().setState(PurchaseController.State.finalized);
			return false;
		}
		return true;
	}
}
