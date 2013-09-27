package ish.oncourse.enrol.checkout;

import ish.math.Money;
import ish.oncourse.model.*;

import java.util.List;

import static ish.oncourse.enrol.checkout.PurchaseController.Message.*;


public class
        ActionEnableProductItem extends APurchaseAction {
	private ProductItem productItem;
    //the value which payer entered in price field on gui for voucher without price
    private Money price;

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
		} else if (productItem instanceof Membership){
            Membership membership = (Membership) productItem;
            if (membership.getInvoiceLine() == null) {
                InvoiceLine il = getController().getInvoiceProcessingService().createInvoiceLineForMembership(membership,
                        getModel().getPayer());
                il.setInvoice(getModel().getInvoice());
                membership.setInvoiceLine(il);
            }
        }else{
			throw new IllegalArgumentException("Unsupported product type.");
		}
		getModel().enableProductItem(productItem);
	}

	@Override
	protected void parse() {

		if (getParameter() != null)
			productItem = getParameter().getValue(ProductItem.class);
	}


	public ProductItem getProductItem() {
		return productItem;
	}

	public void setProductItem(ProductItem productItem) {
		this.productItem = productItem;
	}

    @Override
    protected boolean validate() {
        if (getModel().isProductItemEnabled(productItem))
            return false;
        return validateProductItem();
    }

    public boolean validateProductItem() {
        if (productItem instanceof Membership)
            return validateMembership();
        else if (productItem instanceof Voucher)
            return validateVoucher();
        else
            throw new IllegalArgumentException();
    }

    private boolean validateMembership() {
        Contact contact = ((Membership) productItem).getContact();
        List<Membership> memberships = contact.getMemberships();
        for (Membership membership : memberships) {
            if (membership == productItem)
                continue;
            if (membership.getProduct().equals(productItem.getProduct()))
            {
                String message = duplicatedMembership.getMessage(getController().getMessages(), ((Membership) productItem).getContact().getFullName(), membership.getProduct().getSku());
                getController().getErrors().put(duplicatedMembership.name(), message);
                return false;
            }
        }
        return true;
    }

    private boolean validateVoucher() {
        Voucher voucher = (Voucher) productItem;
        boolean withoutPrice = voucher.getVoucherProduct().getRedemptionCourses().isEmpty() && voucher.getVoucherProduct().getPriceExTax() == null;
        if (withoutPrice && price == null)
        {
            String message = enterVoucherPrice.getMessage(getController().getMessages(), productItem.getProduct().getName());
            getController().getErrors().put(enterVoucherPrice.name(), message);
            return false;
        }
        return true;
    }

    public Money getPrice() {
        return price;
    }

    public void setPrice(Money price) {
        this.price = price;
    }
}
