package ish.oncourse.ui.components;

import java.text.Format;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ish.math.Money;
import ish.oncourse.model.Course;
import ish.oncourse.model.Product;
import ish.oncourse.model.VoucherProduct;
import ish.oncourse.services.cookies.ICookiesService;
import ish.oncourse.services.html.IPlainTextExtractor;
import ish.oncourse.services.preference.PreferenceController;
import ish.oncourse.services.textile.ITextileConverter;
import ish.oncourse.util.FormatUtils;
import ish.oncourse.util.ValidationErrors;

import org.apache.commons.lang.StringUtils;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;

public class ProductItem {
	private static final int DETAILS_LENGTH = 490;
	
	@Parameter
	@Property
	private Product product;

	@Parameter
	@Property
	private boolean isList;
	
	@SuppressWarnings("all")
	@Property
	private Course course;
	
	@Inject
	private PreferenceController preferenceController;
	
	@Inject
	private ICookiesService cookiesService;
	
	@Inject
	private ITextileConverter textileConverter;
	
	@Inject
	private IPlainTextExtractor extractor;
	
	@Inject
	private Messages messages;
	
	//@Property
	private Format feeFormat;
	
	public boolean isPaymentGatewayEnabled() {
		return product != null && preferenceController.isPaymentGatewayEnabled();
	}
	
	public boolean isAddedProduct() {
		List<Long> voucherProductIds = cookiesService.getCookieCollectionValue(Product.SHORTLIST_COOKIE_KEY, Long.class);
		return voucherProductIds.contains(product.getId());
	}
	
	public String getProductItemClass() {
		if (isList) {
			return "new_course_item small_class_detail clearfix";
		} else {
			return "new_course_item";
		}
	}
	
	public String getPurchaseHoverTitle() {
		return messages.get("message.hover.button.purchase.current");
	}
	
	public String getDetailText() {
		String detail = textileConverter.convertCustomTextile(product.getDescription(), new ValidationErrors());
		if (detail == null) {
			return StringUtils.EMPTY;
		}

		if (isList) {
			String plainText = extractor.extractFromHtml(detail);
			String result = StringUtils.abbreviate(plainText, DETAILS_LENGTH);
			return result;
		} else {
			return detail;
		}
	}
	
	public boolean isCourseVoucherProduct() {
		return !getRedemptionCourses().isEmpty();
	}
	
	public List<Course> getRedemptionCourses() {
		final List<Course> result = new ArrayList<Course>();
		if (product instanceof VoucherProduct) {
			VoucherProduct vproduct = (VoucherProduct) product;
			return Collections.unmodifiableList(vproduct.getRedemptionCourses());
		}
		return result;
	}
	
	public String getCourseVoucherDescription() {
		if (!isCourseVoucherProduct()) {
			return StringUtils.EMPTY;
		}
		return String.format("%s Classes from the following", ((VoucherProduct) product).getRedemptionCourses().size());
	}
	
	public Money getPrice() {
		Money priceExTax = product.getPriceExTax();
		if (priceExTax == null) {
			//product or membership product
			if (!isCourseVoucherProduct()) {
				//TODO: ask here
				priceExTax = Money.ZERO;
			} else {
				priceExTax = Money.ZERO;
			}
		}
		this.feeFormat = FormatUtils.chooseMoneyFormat(priceExTax);
		return priceExTax;
	}

	public Format getFeeFormat() {
		return feeFormat;
	}
	
	
}
