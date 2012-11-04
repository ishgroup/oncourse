package ish.oncourse.enrol.checkout;

import ish.oncourse.model.ProductItem;
import ish.oncourse.model.Voucher;

public class ActionDisableProductItem extends APurchaseAction{
	private ProductItem productItem;

	@Override
	protected void makeAction() {
		if (productItem instanceof Voucher) {
			getModel().disableProductItem(productItem);
		} else {
			throw new IllegalArgumentException("Unsupported product type.");
		}
	}

	@Override
	protected void parse() {
		if (getParameter() != null)
			productItem = getParameter().getValue(ProductItem.class);
	}

	@Override
	protected boolean validate() {
		return getModel().isProductItemEnabled(productItem);
	}
}
