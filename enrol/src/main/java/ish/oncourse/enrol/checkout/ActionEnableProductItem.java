package ish.oncourse.enrol.checkout;

import ish.common.types.ProductStatus;
import ish.math.Money;
import ish.oncourse.model.*;

import java.util.List;

import static ish.oncourse.enrol.checkout.PurchaseController.Message.duplicatedMembership;
import static ish.oncourse.enrol.checkout.PurchaseController.Message.enterVoucherPrice;


public class ActionEnableProductItem extends APurchaseAction {
	private ProductItem productItem;
    //the value which payer entered in price field on gui for voucher without price
    private Money price;

	@Override
	protected void makeAction() {
		if (productItem instanceof Voucher) {
            enableVoucher();
		} else if (productItem instanceof Membership){
            enableMembership();
        }else if (productItem instanceof  Article) {
			enableArticle();
		} else {
			throw new IllegalArgumentException("Unsupported product type.");
		}
		getModel().enableProductItem(productItem, productItem.getContact());
	}

	private void enableArticle() {
		Article article = (Article) productItem;
		InvoiceLine il = getController().getInvoiceProcessingService().createInvoiceLineForArticle(article,
				productItem.getContact());
		il.setInvoice(getModel().getInvoice());
		article.setInvoiceLine(il);
	}

	private void enableMembership() {
        Membership membership = (Membership) productItem;
        InvoiceLine il = getController().getInvoiceProcessingService().createInvoiceLineForMembership(membership,
				productItem.getContact());
        il.setInvoice(getModel().getInvoice());
        membership.setInvoiceLine(il);
    }

    private void enableVoucher() {
        Voucher voucher = (Voucher) productItem;

        InvoiceLine il;
        if (getController().getVoucherService().isVoucherWithoutPrice(voucher.getVoucherProduct()))
        {
            voucher.setRedemptionValue(price);
			voucher.setValueOnPurchase(price);
            il = getController().getInvoiceProcessingService().createInvoiceLineForVoucher(voucher,
                    getModel().getPayer(), price);
        } else
            il = getController().getInvoiceProcessingService().createInvoiceLineForVoucher(voucher,
                    getModel().getPayer());
        il.setInvoice(getModel().getInvoice());
        voucher.setInvoiceLine(il);
    }

    @Override
	protected void parse() {

		if (getParameter() != null)
        {
			productItem = getParameter().getValue(ProductItem.class);
            price = getParameter().getValue(Money.class);
        }
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
        if (productItem instanceof Membership) {
            return validateMembership();
		} else if (productItem instanceof Voucher) {
            return validateVoucher();
		} else if (productItem instanceof Article) {
			return validateArticle();
		}
        else {
            throw new IllegalArgumentException();
		}
    }

	private boolean validateArticle() {
		return true;
	}

	private boolean validateMembership() {
        Contact contact = ((Membership) productItem).getContact();
        List<Membership> memberships = contact.getMemberships();
        for (Membership membership : memberships) {
            if (membership == productItem)
                continue;
			if (membership.getStatus() != ProductStatus.ACTIVE)
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
        boolean withoutPrice = getController().getVoucherService().isVoucherWithoutPrice(voucher.getVoucherProduct());
        if (withoutPrice && (price.isZero() || price.isLessThan(Money.ZERO)))
        {
            String message = enterVoucherPrice.getMessage(getController().getMessages(), voucher.getProduct().getName());
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
