package ish.oncourse.enrol.components.checkout;

import ish.math.Money;
import ish.oncourse.enrol.checkout.PurchaseController;
import ish.oncourse.model.Course;
import ish.oncourse.model.Product;
import ish.oncourse.model.VoucherProduct;
import ish.oncourse.util.FormatUtils;
import org.apache.log4j.Logger;
import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

import java.text.Format;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ProductItem {
	private static final Logger LOGGER = Logger.getLogger(ProductItem.class);
	@Parameter(required = true)
	private PurchaseController purchaseController;

	@Property
	@Parameter(required = true)
	private ish.oncourse.model.ProductItem productItem;

	@Parameter(required = true)
	@Property
	private Integer contactIndex;

	@Parameter(required = true)
	@Property
	private Integer productItemIndex;

	@Parameter(required = false)
	private ProductItemDelegate delegate;

	@Property
	@Parameter(required = true)
	private Boolean checked;

	@Parameter
	private Block blockToRefresh;

	@Property
	private Format feeFormat;
	
	@Property
	private Course course;
	
	@Property
	private Money priceValue;

	@Inject
	private Request request;


	@SetupRender
	void beforeRender() {
		Money definedPrice = getPrice();
		if (priceValue == null && definedPrice.isZero()) {
			priceValue = definedPrice;
		}
	}
	
	public boolean isVoucherProduct() {
		return productItem.getProduct() instanceof VoucherProduct;
	}
	
	public List<Course> getRedemptionCourses() {
		final List<Course> result = new ArrayList<>();
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


	@OnEvent(value = "selectProductEvent")
	public Object selectProductItem(Integer contactIndex, Integer productItemIndex) {
		if (!request.isXHR())
			return null;

		if (delegate != null) {
			delegate.onChange(contactIndex, productItemIndex);
			if (blockToRefresh != null)
				return blockToRefresh;
		}
		return null;
	}


	public static interface ProductItemDelegate {
		public void onChange(Integer contactIndex, Integer productItemIndex);
	}

    public Integer[] getActionContext() {
        return new Integer[]{contactIndex, productItemIndex};
    }

}
