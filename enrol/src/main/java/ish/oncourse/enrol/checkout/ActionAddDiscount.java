package ish.oncourse.enrol.checkout;

import ish.oncourse.model.Discount;
import org.apache.commons.lang.StringUtils;

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
			discountCode = StringUtils.trimToNull(getParameter().getValue(String.class));
			if (discountCode != null)
				discount = getController().getDiscountService().getByCode(discountCode);
		}
	}

	@Override
	protected boolean validate() {
		if (discountCode == null) {
			getController().addError(PurchaseController.Error.codeEmpty, discountCode);
			return false;
		}
		if (discount == null) {
			getController().addError(PurchaseController.Error.discountNotFound, discountCode);
			return false;
		} else
			return true;
	}
}
