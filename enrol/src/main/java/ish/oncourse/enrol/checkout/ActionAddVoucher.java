package ish.oncourse.enrol.checkout;

import ish.common.types.PaymentStatus;
import ish.oncourse.model.Contact;
import ish.oncourse.model.PaymentIn;
import ish.oncourse.model.Voucher;

import java.util.Date;
import java.util.List;

import static ish.oncourse.enrol.checkout.PurchaseController.Message.*;

public class ActionAddVoucher extends APurchaseAction {

	private String voucherCode;
	private Voucher voucher;

	@Override
	protected void makeAction() {
        getController().getVoucherRedemptionHelper().clear();
		getController().getVoucherRedemptionHelper().processAgainstInvoices();
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
			getController().addError(voucherNotMatch, voucherCode);
			return false;
		}
		if (!voucher.canBeUsedBy(getModel().getPayer())) {
			getController().addError(voucherNotMatch, voucherCode);
			return false;
		}
		if (voucher.isFullyRedeemed()) {
			getController().addError(voucherRedeemed, voucherCode);
			return false;
		}

		if (voucher.isInUse()) {
			Contact contact = getLockedContact();
			getController().addError(voucherLockedAnotherUser, contact.getFamilyName(), contact.getGivenName());
			return false;
		}

		if (voucher.getExpiryDate().before(new Date())) {
			getController().addError(voucherExpired);
			return false;
		}
		return true;
	}

	private Contact getLockedContact() {
		List<PaymentIn> payments = voucher.getPayments();
		for (PaymentIn paymentIn : payments) {
			if (!PaymentStatus.isFinalState(paymentIn.getStatus()))
				return paymentIn.getContact();
		}
		return null;
	}
}
