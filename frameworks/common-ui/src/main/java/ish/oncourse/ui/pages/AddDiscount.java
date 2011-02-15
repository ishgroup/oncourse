package ish.oncourse.ui.pages;

import ish.oncourse.model.Discount;
import ish.oncourse.services.discount.IDiscountService;

import java.util.ArrayList;
import java.util.List;

import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.annotations.Inject;

/**
 * The page for adding the promotion to the cookies.
 * 
 * @author ksenia
 * 
 */
public class AddDiscount {

	/**
	 * The service to manipulate with discounts.
	 */
	@Inject
	private IDiscountService discountService;

	/**
	 * The input value by which the promotion to add is been searched.
	 */
	@Property
	private String promoCode;

	/**
	 * Already added by this load of page promotions.
	 */

	@Property
	private List<Discount> addedPromotions;

	@Persist("client")
	private List<Long> addedPromotionsIds;

	/**
	 * The item used for iteration through {@link #addedPromotions}.
	 */
	@Property
	private Discount addedPromotion;

	/**
	 * The promotion to add.
	 */
	private Discount promotion;

	/**
	 * Form component for the promotion adding.
	 */
	@InjectComponent
	private Form addDiscountForm;

	/**
	 * Zone component for udation the changed area.
	 */
	@InjectComponent
	private Zone addDiscountZone;

	/**
	 * Setup initial values.
	 */
	@SetupRender
	void beforeRender() {
		addedPromotions = new ArrayList<Discount>();
		addedPromotionsIds = new ArrayList<Long>();
	}

	/**
	 * Validates {@link #addDiscountForm}. If the inputed {@link #promoCode} is
	 * valid(not empty and the correspondent discount exists) and the discount
	 * with such a code hasn't already been added to the list, the validation
	 * passes successfully.
	 */
	@OnEvent(component = "addDiscountForm", value = "validate")
	void validateDiscount() {
		if (promoCode == null || promoCode.equals("")) {
			addDiscountForm.recordError("Enter discount code, please");
		} else {
			promotion = discountService.getByCode(promoCode);
			if (promotion == null) {
				addDiscountForm.recordError(String.format(
						"Discount for code \"%s\" is unavailable.", promoCode));
			} else if (addedPromotionsIds.contains(promotion.getId())
					|| discountService.getPromotions().contains(promotion)) {
				addDiscountForm.recordError(String.format(
						"Discount for code \"%s\" already exists in list.", promoCode));
			}
		}
	}

	/**
	 * Invokes on submit of the {@link #addDiscountForm}. Performs the adding of
	 * {@link #promotion} if the {@link #validateDiscount()} passes successfully
	 * and updates the {@link #addDiscountZone}.
	 * 
	 * @return
	 */
	@OnEvent(component = "addDiscountForm", value = "submit")
	Object addDiscount() {
		addedPromotions = discountService.loadByIds(addedPromotionsIds.toArray());
		if (!addDiscountForm.getHasErrors()) {
			discountService.addPromotion(promotion);
			addedPromotions.add(promotion);
			addedPromotionsIds.add(promotion.getId());
		}

		return addDiscountZone.getBody();
	}
}
