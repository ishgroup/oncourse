package ish.oncourse.enrol.checkout;

import ish.common.types.PaymentType;

import java.util.Collections;

import static ish.oncourse.enrol.checkout.PurchaseController.State.editPayment;

public class ActionSelectCardEditor extends APurchaseAction{
    @Override
    protected void makeAction() {
        getModel().getPayment().setType(PaymentType.CREDIT_CARD);
        getModel().setCorporatePass(null);
        getController().setState(editPayment);
        if (getController().getPaymentEditorDelegate() != null)
            getController().getPaymentEditorDelegate().setErrors(Collections.EMPTY_MAP);
    }

    @Override
    protected void parse() {
    }

    @Override
    protected boolean validate() {
        return true;
    }
}
