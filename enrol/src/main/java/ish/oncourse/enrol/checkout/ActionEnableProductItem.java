package ish.oncourse.enrol.checkout;

import ish.oncourse.model.InvoiceLine;
import ish.oncourse.model.ProductItem;
import ish.oncourse.model.Voucher;

public class ActionEnableProductItem extends APurchaseAction {
	private ProductItem productItem;

	@Override
	protected void makeAction() {
		if (productItem instanceof Voucher) {
			Voucher voucher = (Voucher) productItem;
			if (voucher.getInvoiceLine() == null) {
				InvoiceLine il = getController().getInvoiceProcessingService().createInvoiceLineForVoucher(voucher,
						getModel().getPayer());
				il.setInvoice(getModel().getInvoice());
				voucher.setInvoiceLine(il);
			}
		} else {
			throw new IllegalArgumentException("Unsupported product type.");
		}
		getModel().enableProductItem(productItem);
	}

	@Override
	protected void parse() {

		if (getParameter() != null)
			productItem = getParameter().getValue(ProductItem.class);
	}

	@Override
	protected boolean validate() {
		return !getModel().isProductItemEnabled(productItem);
	}
}
