package ish.oncourse.enrol.checkout;

import ish.common.types.PaymentType;

import java.util.Collections;

import static ish.oncourse.enrol.checkout.PurchaseController.State.editPayment;

public class ActionSelectCardEditor extends APurchaseAction{
    @Override
    protected void makeAction() {
		getModel().getInvoice().setCorporatePassUsed(null);
        getModel().setCorporatePass(null);

		getModel().getPayment().setType(PaymentType.CREDIT_CARD);
		getModel().setPayer(getModel().getContacts().get(0));

        getController().getPaymentEditorDelegate().setErrors(Collections.EMPTY_MAP);
		getController().setState(editPayment);
    }

    @Override
    protected void parse() {
    }

    @Override
    protected boolean validate() {
        return !getController().isEditPayment();
    }
}
