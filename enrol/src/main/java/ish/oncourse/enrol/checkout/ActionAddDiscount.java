package ish.oncourse.enrol.checkout;

public class ActionAddDiscount extends ADiscountAction {

	@Override
	protected void makeAction() {
		getModel().addDiscount(getModel().localizeObject(discount));
        getController().getDiscountService().addPromotion(discount);
		getController().recalculateEnrolmentInvoiceLines();
    }

    @Override
    protected boolean validate() {
        boolean result = super.validate();
        if (result && getModel().containsDiscount(discount.getId()))
        {
            getController().addWarning(PurchaseController.Message.discountAlreadyAdded, discount.getCode());
            result = false;
        }
        return result;
    }
}
