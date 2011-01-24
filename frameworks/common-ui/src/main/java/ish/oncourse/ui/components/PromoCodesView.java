package ish.oncourse.ui.components;

import ish.oncourse.model.Discount;
import ish.oncourse.services.discount.IDiscountService;
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
public class PromoCodesView {
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

	/**
	 * Initializes list of promotions to be displayed.
	 */
	@SetupRender
	void beforeRender() {
		promotions = discountService.getPromotions();
	}

}
