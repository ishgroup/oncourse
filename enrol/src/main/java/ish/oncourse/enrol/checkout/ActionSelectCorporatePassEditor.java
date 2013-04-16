package ish.oncourse.enrol.checkout;

import ish.common.types.PaymentType;

public class ActionSelectCorporatePassEditor extends APurchaseAction{
    @Override
    protected void makeAction() {
        getModel().getPayment().setType(PaymentType.INTERNAL);
        getController().setState(PurchaseController.State.editCorporatePass);
    }

    @Override
    protected void parse() {
    }

    @Override
    protected boolean validate() {
		return !getController().isEditCorporatePass();
	}
}
