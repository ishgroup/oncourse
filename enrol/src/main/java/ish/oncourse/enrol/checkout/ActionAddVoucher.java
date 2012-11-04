package ish.oncourse.enrol.checkout;

import ish.oncourse.model.Voucher;

public class ActionAddVoucher extends APurchaseAction {

	public static final String ERROR_KEY_voucherNotFound = "error-voucherNotFound";
	public static final String ERROR_KEY_voucherCannotBeUsed = "error-voucherCannotBeUsed";

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
			getController().getErrors().add(getController().getMessages().format(ERROR_KEY_voucherNotFound, voucherCode));
			return false;
		} else if (!voucher.canBeUsedBy(getModel().getPayer())) {
			getController().getErrors().add(getController().getMessages().format(ERROR_KEY_voucherCannotBeUsed, voucherCode));
			return false;
		} else {
			return true;
		}
	}
}
