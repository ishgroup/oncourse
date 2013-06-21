package ish.oncourse.enrol.checkout;

import ish.oncourse.model.Discount;
import ish.oncourse.utils.StringUtilities;

public abstract class ADiscountAction extends APurchaseAction {
    protected Discount discount;
    protected String discountCode;

    @Override
    protected void parse() {
        if (getParameter() != null) {
            discountCode = StringUtilities.cutToNull(getParameter().getValue(String.class));
            if (discountCode != null)
            {
                discount = getController().getDiscountService().getByCode(discountCode);
            }
        }
        if (discount != null)
            discount = getModel().localizeObject(discount);
    }

    @Override
    protected boolean validate() {
        if (discountCode == null && discount == null) {
            getController().addError(PurchaseController.Message.codeEmpty, discountCode);
            return false;
        }
        if (discount == null) {
            getController().addError(PurchaseController.Message.discountNotFound, discountCode);
            return false;
        }
        return true;
    }

    public void setDiscount(Discount discount) {
        this.discount = discount;
    }
}
