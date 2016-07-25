package ish.oncourse.enrol.checkout.payment;

import ish.oncourse.enrol.checkout.ActionAddPersonPayer;
import ish.oncourse.enrol.checkout.PurchaseController;

public class ActionAddVoucherPersonPayer extends ActionAddPersonPayer {

    @Override
    protected PurchaseController.State getFinalState() {
        return PurchaseController.State.editCheckout;
    }

}
