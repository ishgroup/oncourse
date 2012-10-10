package ish.oncourse.enrol.components.checkout;

import ish.math.Money;
import ish.oncourse.enrol.utils.PurchaseController;
import ish.oncourse.model.Course;
import ish.oncourse.model.Product;
import ish.oncourse.model.VoucherProduct;
import ish.oncourse.util.FormatUtils;

import java.text.Format;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;

public class ProductItem {
	private static final Logger LOGGER = Logger.getLogger(ProductItem.class);
	@Parameter(required = true)
	private PurchaseController purchaseController;

	@Property
	@Parameter(required = true)
	private ish.oncourse.model.ProductItem productItem;
	
	@Property
	private boolean isEnabled;
	
	@Property
	private Format feeFormat;
	
	@Property
	private Course course;
	
	@Property
	private Money priceValue;
	
	@SetupRender
	void beforeRender() {
		isEnabled = purchaseController.getModel().isProductItemEnabled(productItem);
		Money definedPrice = getPrice();
		if (priceValue == null && definedPrice.isZero()) {
			priceValue = definedPrice;
		}
	}
	
	public boolean isVoucherProduct() {
		return productItem.getProduct() instanceof VoucherProduct;
	}
	
	public List<Course> getRedemptionCourses() {
		final List<Course> result = new ArrayList<Course>();
		if (isVoucherProduct()) {
			VoucherProduct vproduct = (VoucherProduct) productItem.getProduct();
			return Collections.unmodifiableList(vproduct.getRedemptionCourses());
		}
		return result;
	}
	
	public boolean isVoucherWithPrice() {
		return !getPrice().isZero();
	}
	
	public boolean isMoneyVoucher() {
		return isVoucherProduct() && isVoucherWithPrice() && !isCourseVoucher();
	}
	
	public boolean isGiftSertificate() {
		return isVoucherProduct() && !isVoucherWithPrice() && !isCourseVoucher();
	}
	
	public boolean isCourseVoucher() {
		return !getRedemptionCourses().isEmpty();
	}
	
	public Money getPrice() {
		Product product = productItem.getProduct();
		Money priceExTax = product.getPriceExTax();
		if (priceExTax == null) {
			if (isVoucherProduct()) {
				priceExTax = Money.ZERO;
			} else {
				LOGGER.error(String.format("Empty price for product with name %s and sku %s", product.getName(), product.getSku()));
				throw new IllegalStateException(String.format("Empty price for product with name %s and sku %s", product.getName(), product.getSku()));
			}
		}
		this.feeFormat = FormatUtils.chooseMoneyFormat(priceExTax);
		return priceExTax;
	}
}
