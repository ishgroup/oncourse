package ish.oncourse.enrol.checkout;

import ish.oncourse.model.Voucher;

import static ish.oncourse.enrol.checkout.PurchaseController.Message.voucherAlreadyAdded;
import static ish.oncourse.enrol.checkout.PurchaseController.Message.voucherNotMatch;

public class ActionAddVoucher extends APurchaseAction {

	private String voucherCode;
	private Voucher voucher;

    private ActionSelectVoucher actionSelectVoucher = new ActionSelectVoucher();

	@Override
	protected void makeAction() {

        getModel().addVoucher(voucher);
        actionSelectVoucher.setVoucher(voucher);
        actionSelectVoucher.setController(getController());
        actionSelectVoucher.action();
	}

	@Override
	protected void parse() {
		if (getParameter() != null) {
			voucherCode = getParameter().getValue(String.class);
			voucher = getController().getVoucherService().getVoucherByCode(voucherCode);
		}
	}

	@Override
	protected boolean validate() {
        if (getController().getModel().getVouchers().contains(voucher))
        {
            getController().addWarning(voucherAlreadyAdded);
            return false;
        }

		if (voucher == null) {
			getController().addWarning(voucherNotMatch, voucherCode);
			return false;
		}
        return true;
	}

    public Voucher getVoucher() {
        return voucher;
    }

    public void setVoucher(Voucher voucher) {
        this.voucher = voucher;
    }
}
