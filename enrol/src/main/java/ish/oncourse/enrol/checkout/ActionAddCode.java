package ish.oncourse.enrol.checkout;

import ish.oncourse.model.Discount;
import ish.oncourse.model.Voucher;

public class ActionAddCode extends APurchaseAction {

    private String code;

    private APurchaseAction action;
    private Discount discount;
    private Voucher voucher;

    @Override
    protected void makeAction() {
        action.action();
    }

    @Override
    protected void parse() {
        if (getParameter() != null) {
            code = getParameter().getValue(String.class);
        }

        if (code != null) {
            discount = getController().getDiscountService().getByCode(code);
            if (discount != null) {
                action = new ActionAddDiscount();
                action.setController(getController());
                ((ActionAddDiscount) action).setDiscount(discount);
            } else {
                voucher = getModel().localizeObject(getController().getVoucherService().getVoucherByCode(code));
                if (voucher != null) {
                    voucher = getModel().localizeObject(voucher);
                    action = new ActionAddVoucher();
                    action.setController(getController());
                    ((ActionAddVoucher) action).setVoucher(voucher);
                }
            }
        }
    }


    @Override
    protected boolean validate() {
        if (action == null) {
            getController().addWarning(PurchaseController.Message.incorrectCode);
            return false;
        }
        return true;
    }
}
