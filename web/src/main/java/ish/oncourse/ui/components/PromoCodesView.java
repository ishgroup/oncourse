package ish.oncourse.ui.components;

import ish.oncourse.model.College;
import ish.oncourse.model.Discount;
import ish.oncourse.services.discount.IDiscountService;
import ish.oncourse.services.preference.PreferenceController;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.ui.base.ISHCommon;
import org.apache.tapestry5.annotations.Cached;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;

import java.util.List;

/**
 * The component that displays the list of discounts added by promocodes and
 * enables user to add another promotion.
 * 
 * @author ksenia
 * 
 */
public class PromoCodesView extends ISHCommon {

	/**
	 * The service to manipulate with discounts.
	 */
	@Inject
	private IDiscountService discountService;

    @Inject
    private PreferenceController preferenceController;

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


	@Cached
    public boolean isPaymentGatewayEnabled() {
        return preferenceController.isPaymentGatewayEnabled();
    }
}
