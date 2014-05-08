package ish.oncourse.enrol.checkout;

import ish.oncourse.model.Voucher;

import static ish.oncourse.enrol.checkout.PurchaseController.Message.*;

public class ActionEnableVoucher extends APurchaseAction {

    private Voucher voucher;

    @Override
    protected void makeAction() {
         getModel().selectVoucher(voucher);
    }

    @Override
    protected void parse() {

    }

    @Override
    protected boolean validate() {
        if (!voucher.canBeUsedBy(getModel().getPayer())) {
            getController().addError(voucherWrongPayer, voucher.getContact().getFullName());
            return false;
        }

        if (voucher.isFullyRedeemed()) {
            getController().addError(voucherRedeemed);
            return false;
        }

        if (voucher.isInUse()) {
            getController().addError(voucherAlreadyBeingUsed);
            return false;
        }
        if (PurchaseController.State.editCorporatePass == getController().getState())
        {
            getController().addWarning(voucherRedeemNotAllow);
            return false;
        }
        return true;
    }

    public void setVoucher(Voucher voucher) {
        this.voucher = voucher;
    }
}
