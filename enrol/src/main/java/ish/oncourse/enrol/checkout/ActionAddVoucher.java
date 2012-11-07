package ish.oncourse.enrol.checkout;

import ish.oncourse.model.Voucher;

import static ish.oncourse.enrol.checkout.PurchaseController.Error.voucherCannotBeUsed;
import static ish.oncourse.enrol.checkout.PurchaseController.Error.voucherNotFound;

public class ActionAddVoucher extends APurchaseAction {

	private String voucherCode;
	private Voucher voucher;

	@Override
	protected void makeAction() {
		getController().getVoucherRedemptionHelper().addVoucher(voucher);
		getController().getVoucherRedemptionHelper().calculateVouchersRedeemPayment();
		getModel().clearVoucherPayments();
		getModel().addVoucherPayments(getController().getVoucherRedemptionHelper().getPayments());
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
		if (voucher == null) {
			getController().addError(voucherNotFound, voucherCode);
			return false;
		} else if (!voucher.canBeUsedBy(getModel().getPayer())) {
			getController().addError(voucherCannotBeUsed, voucherCode);
			return false;
		} else {
			return true;
		}
	}
}
