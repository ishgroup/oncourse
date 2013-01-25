package ish.oncourse.enrol.checkout;

import static ish.oncourse.enrol.checkout.PurchaseController.Action.addContact;

public class ActionInit extends APurchaseAction {
	@Override
	protected void makeAction() {
		getController().getVoucherRedemptionHelper().setInvoice(getModel().getInvoice());
		ActionAddContact action = addContact.createAction(getController());
		action.action();
	}

	@Override
	protected void parse() {
	}

	@Override
	protected boolean validate() {
		if (getModel().getClasses().size() < 1 && getModel().getProducts().size() < 1)
		{
			getController().addError(PurchaseController.Message.noSelectedItemForPurchase);
			getController().setState(PurchaseController.State.paymentResult);
			return false;
		}
		return true;
	}
}
