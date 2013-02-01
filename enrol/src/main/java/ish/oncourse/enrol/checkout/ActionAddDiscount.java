package ish.oncourse.enrol.checkout;

public class ActionAddDiscount extends ADiscountAction {

	private boolean publishErrors = true;

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
			if (isPublishErrors())
            	getController().addWarning(PurchaseController.Message.discountAlreadyAdded, discount.getCode());
            result = false;
        }
        return result;
    }

	public boolean isPublishErrors() {
		return publishErrors;
	}

	public void setPublishErrors(boolean publishErrors) {
		this.publishErrors = publishErrors;
	}
}
