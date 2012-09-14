package ish.oncourse.ui.components;

import java.util.List;

import ish.oncourse.model.VoucherProduct;
import ish.oncourse.services.cookies.ICookiesService;
import ish.oncourse.services.preference.PreferenceController;

import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;

public class VoucherProductItem {
	
	@Parameter
	@Property
	private VoucherProduct voucherProduct;

	@Parameter
	@Property
	private boolean isList;
	
	@Inject
	private PreferenceController preferenceController;
	
	@Inject
	private ICookiesService cookiesService;
	
	@Inject
	private Messages messages;
	
	public boolean isPaymentGatewayEnabled() {
		return voucherProduct != null && preferenceController.isPaymentGatewayEnabled();
	}
	
	public boolean isAddedVoucherProduct() {
		List<Long> voucherProductIds = cookiesService.getCookieCollectionValue(VoucherProduct.SHORTLIST_COOKIE_KEY, Long.class);
		return voucherProductIds.contains(voucherProduct.getId());
	}
	
	public String getVoucherProductItemClass() {
		if (isList) {
			return "new_course_item small_class_detail clearfix";
		} else {
			return "new_course_item";
		}
	}
	
	public String getPurchaseHoverTitle() {
		return messages.get("message.hover.button.purchase.current");
	}
}
