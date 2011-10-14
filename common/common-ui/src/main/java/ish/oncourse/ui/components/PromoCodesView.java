package ish.oncourse.ui.components;

import ish.oncourse.model.College;
import ish.oncourse.model.Discount;
import ish.oncourse.services.discount.IDiscountService;
import ish.oncourse.services.site.IWebSiteService;

import java.util.List;

import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

/**
 * The component that displays the list of discounts added by promocodes and
 * enables user to add another promotion.
 * 
 * @author ksenia
 * 
 */
public class PromoCodesView {

	@Inject
	private Request request;

	/**
	 * The service to manipulate with discounts.
	 */
	@Inject
	private IDiscountService discountService;

	/**
	 * The list of promotions to be displayed.
	 */
	@Property
	private List<Discount> promotions;

	/**
	 * The item used for iteration through {@link #promotions}.
	 */
	@Property
	private Discount promotion;

	@Inject
	private IWebSiteService siteService;

	@Property
	private College college;

	/**
	 * Initializes list of promotions to be displayed.
	 */
	@SetupRender
	void beforeRender() {
		promotions = discountService.getPromotions();
		college = siteService.getCurrentCollege();
	}

	public String getServerName() {
		return request.getServerName();
	}

}
