package ish.oncourse.enrol.checkout;

import ish.common.types.ProductType;
import ish.common.types.TypesUtil;
import ish.math.Money;
import ish.oncourse.model.ProductItem;
import ish.oncourse.model.Voucher;

public class ActionDisableProductItem extends APurchaseAction{
	private ProductItem productItem;

	@Override
	protected void makeAction() {
		/*
		 we need to clear voucher redemption data because during the action structure of invoiceLines was changed
		 (invoiceLine for the productItem is removed)
		*/
		getController().getVoucherRedemptionHelper().clear();
		ProductType type = TypesUtil.getEnumForDatabaseValue(productItem.getType(), ProductType.class);
		switch (type) {
			case ARTICLE:
			case MEMBERSHIP:
				getModel().disableProductItem(productItem);
				break;
			case VOUCHER:
				if(getController().getVoucherService().isVoucherWithoutPrice(((Voucher) productItem).getVoucherProduct())) {
					((Voucher) productItem).setRedemptionValue(Money.ZERO);
				}
				getModel().disableProductItem(productItem);
				break;
			default:
				throw new IllegalArgumentException("Unsupported product type.");
		}
		getController().updateDiscountApplied();
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

	public ProductItem getProductItem() {
		return productItem;
	}

	public void setProductItem(ProductItem productItem) {
		this.productItem = productItem;
	}
}
