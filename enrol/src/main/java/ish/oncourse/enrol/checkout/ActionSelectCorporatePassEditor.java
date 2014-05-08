package ish.oncourse.enrol.checkout;

import ish.common.types.PaymentType;
import ish.oncourse.model.Voucher;

import java.util.List;

import static ish.oncourse.enrol.checkout.PurchaseController.Message.corporatePassNotEnabled;

public class ActionSelectCorporatePassEditor extends APurchaseAction{
    @Override
    protected void makeAction() {
        getModel().getPayment().setType(PaymentType.INTERNAL);
        getController().setState(PurchaseController.State.editCorporatePass);
		getController().getModel().removeAllProductItems(getModel().getPayer());

        ActionDeselectVoucher actionDeselectVoucher = new ActionDeselectVoucher();
        List<Voucher> vouchers = getModel().getVouchers();
        for (Voucher voucher : vouchers) {
            actionDeselectVoucher.setVoucher(voucher);
            actionDeselectVoucher.setController(getController());
            actionDeselectVoucher.makeAction();
        }
	}

    @Override
    protected void parse() {
    }

    @Override
    protected boolean validate() {
		if (!getController().isCorporatePassPaymentEnabled())
		{
			getController().addError(corporatePassNotEnabled);
			return false;
		}
		return !getController().isEditCorporatePass();
	}
}
