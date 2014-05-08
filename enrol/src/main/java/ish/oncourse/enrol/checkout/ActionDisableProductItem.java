package ish.oncourse.enrol.checkout;

import ish.math.Money;
import ish.oncourse.model.Article;
import ish.oncourse.model.Membership;
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
		if (productItem instanceof Voucher) {
            if(getController().getVoucherService().isVoucherWithoutPrice(((Voucher) productItem).getVoucherProduct())) {
                ((Voucher) productItem).setRedemptionValue(Money.ZERO);
            }
			getModel().disableProductItem(productItem);
		}else if(productItem instanceof Membership || productItem instanceof Article) {
            getModel().disableProductItem(productItem);
        }  else {
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

	public ProductItem getProductItem() {
		return productItem;
	}

	public void setProductItem(ProductItem productItem) {
		this.productItem = productItem;
	}
}
