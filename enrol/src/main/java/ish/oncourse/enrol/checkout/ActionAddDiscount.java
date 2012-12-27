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
			{
				discount = getController().getDiscountService().getByCode(discountCode);
			}
		}
		if (discount != null)
			discount = getModel().localizeObject(discount);
	}

	@Override
	protected boolean validate() {
		if (discountCode == null) {
			getController().addError(PurchaseController.Message.codeEmpty, discountCode);
			return false;
		}
		if (discount == null) {
			getController().addError(PurchaseController.Message.discountNotFound, discountCode);
			return false;
		}
		if (getModel().containsDiscount(discount))
		{
			getController().addWarning(PurchaseController.Message.discountAlreadyAdded, discountCode);
			return false;
		}
		return true;
	}

	public void setDiscount(Discount discount) {
		this.discount = discount;
	}
}
