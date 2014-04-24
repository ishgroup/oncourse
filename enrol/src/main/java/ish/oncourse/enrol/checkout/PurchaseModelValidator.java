package ish.oncourse.enrol.checkout;

import ish.oncourse.model.Enrolment;
import ish.oncourse.model.ProductItem;
import ish.oncourse.model.Voucher;

import java.util.List;

import static ish.oncourse.enrol.checkout.PurchaseController.Action.*;

public class PurchaseModelValidator {

    private PurchaseController purchaseController;

    private boolean validateEnrolments(boolean showErrors) {
        ActionEnableEnrolment actionEnableEnrolment = enableEnrolment.createAction(purchaseController);
        List<Enrolment> enrolments = this.getModel().getAllEnabledEnrolments();
        boolean result = true;
        for (Enrolment enrolment : enrolments) {
            actionEnableEnrolment.setEnrolment(enrolment);
            boolean valid = actionEnableEnrolment.validateEnrolment(showErrors);
            if (!valid) {
                ActionDisableEnrolment actionDisableEnrolment = disableEnrolment.createAction(purchaseController);
                actionDisableEnrolment.setEnrolment(enrolment);
                actionDisableEnrolment.action();
                getModel().removeEnrolment(enrolment);
            }
            result = result && valid;
        }
        return result;
    }

    private boolean validateProductItems() {
        ActionEnableProductItem actionEnableProductItem = PurchaseController.Action.enableProductItem.createAction(purchaseController);
        List<ProductItem> items = this.getModel().getEnabledProductItems(getModel().getPayer());
        boolean result = true;
        for (ProductItem item : items) {
            actionEnableProductItem.setProductItem(item);
            //this step needed, because possible exist voucher product without price
            actionEnableProductItem.setPrice(item.getInvoiceLine().getPriceEachExTax());
            boolean valid = actionEnableProductItem.validateProductItem();
            if (!valid) {
                ActionDisableProductItem actionDisableProductItem = disableProductItem.createAction(purchaseController);
                actionDisableProductItem.setProductItem(item);
                actionDisableProductItem.action();
                getModel().removeProductItem(getModel().getPayer(), item);
            }
            result = result && valid;
        }
        return result;
    }

    private boolean validateRedeemingVouchers() {
        ActionSelectVoucher actionSV = PurchaseController.Action.selectVoucher.createAction(purchaseController);

        List<Voucher> vouchers = this.getModel().getVouchers();
        boolean result = true;
        for (Voucher voucher : vouchers) {
            actionSV.setVoucher(voucher);
            boolean valid = actionSV.validate();
            if (!valid) {
                ActionDeselectVoucher actionDV = deselectVoucher.createAction(purchaseController);
                actionDV.setVoucher(voucher);
                actionDV.action();
            }
            result = result && valid;
        }
        return result;
    }

    public PurchaseController getPurchaseController() {
        return purchaseController;
    }

    public void setPurchaseController(PurchaseController purchaseController) {
        this.purchaseController = purchaseController;
    }

    public PurchaseModel getModel() {
        return purchaseController.getModel();
    }

    public boolean validate() {
        return validateEnrolments(true) && validateProductItems() && validateRedeemingVouchers();
    }
}
