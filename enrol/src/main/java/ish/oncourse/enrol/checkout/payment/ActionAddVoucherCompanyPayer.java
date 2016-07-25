package ish.oncourse.enrol.checkout.payment;

import ish.oncourse.enrol.checkout.ActionAddCompanyPayer;
import ish.oncourse.enrol.checkout.PurchaseController;

public class ActionAddVoucherCompanyPayer extends ActionAddCompanyPayer {

    @Override
    protected PurchaseController.State getFinalState() {
        return PurchaseController.State.editCheckout;
    }
}
