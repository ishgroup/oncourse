package ish.oncourse.enrol.checkout;

import ish.oncourse.model.Discount;

public class ActionAddDiscount extends APurchaseAction {
	private String discountCode;
	private Discount discount;

	@Override
	protected void makeAction() {
		getModel().addDiscount(getModel().localizeObject(discount));
		getController().recalculateEnrolmentInvoiceLines();
	}

	@Override
	protected void parse() {
		if (getParameter() != null) {
			discountCode = getParameter().getValue(String.class);
			discount = getController().getDiscountService().getByCode(discountCode);
		}
	}

	@Override
	protected boolean validate() {
		if (discount == null) {
			getController().addError(PurchaseController.Error.discountNotFound, discountCode);
			return false;
		} else
			return true;
	}
}
