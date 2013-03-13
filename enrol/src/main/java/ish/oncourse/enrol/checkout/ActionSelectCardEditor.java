package ish.oncourse.enrol.checkout;

import ish.common.types.PaymentType;

import static ish.oncourse.enrol.checkout.PurchaseController.State.editPayment;

public class ActionSelectCardEditor extends APurchaseAction{
    @Override
    protected void makeAction() {
        getModel().getPayment().setType(PaymentType.CREDIT_CARD);
        getModel().setCorporatePass(null);
        getController().setState(editPayment);
    }

    @Override
    protected void parse() {
    }

    @Override
    protected boolean validate() {
        return true;
    }
}
