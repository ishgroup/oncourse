package ish.oncourse.enrol.checkout;

import ish.oncourse.model.Voucher;
import org.apache.cayenne.Cayenne;

import static ish.oncourse.enrol.checkout.PurchaseController.Message.*;

public class ActionSelectVoucher extends APurchaseAction {

    private Voucher voucher;

    @Override
    protected void makeAction() {
         getModel().selectVoucher(voucher);
    }

    @Override
    protected void parse() {

        if (getParameter() != null) {
            Long voucherId = getParameter().getValue(Long.class);
            if (voucherId != null) {
                this.voucher = Cayenne.objectForPK(getModel().getObjectContext(), Voucher.class, voucherId);
            }
        }
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
        return true;
    }

    public void setVoucher(Voucher voucher) {
        this.voucher = voucher;
    }
}
