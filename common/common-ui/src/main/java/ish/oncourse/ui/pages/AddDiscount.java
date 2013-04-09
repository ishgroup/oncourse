package ish.oncourse.ui.pages;

import ish.oncourse.model.Discount;
import ish.oncourse.services.discount.IDiscountService;
import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.*;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

import java.net.MalformedURLException;
import java.util.List;

/**
 * The page for adding the promotion to the cookies.
 *
 * @author ksenia
 */
public class AddDiscount {

	/**
	 * The service to manipulate with discounts.
	 */
	@Inject
	private IDiscountService discountService;

	@Inject
	private Request request;

	/**
	 * The input value by which the promotion to add is been searched.
	 */
	@Property
	@Persist("client")
	private String promoCode;

	@Property
	private String errorMessage;

	/**
	 * Already added by this load of page promotions.
	 */

	@Property
	private List<Discount> promotionsList;

	/**
	 * The item used for iteration through {@link #promotionsList}.
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

	@Inject
	@Id("discountForm")
	@Property
	private Block discountForm;


	/**
	 * Setup initial values.
	 */
	@SetupRender
	void beforeRender() {
		promotionsList = discountService.getPromotions();
	}

	/**
	 * Validates {@link #addDiscountForm}. If the inputed {@link #promoCode} is
	 * valid(not empty and the correspondent discount exists) and the discount
	 * with such a code hasn't already been added to the list, the validation
	 * passes successfully.
	 */
	//@OnEvent(component = "addDiscountForm", value = "validate")
	void validateDiscount() {
		if (promoCode == null || promoCode.equals("")) {
			errorMessage = "Enter discount code, please";
		} else {
			promotion = discountService.getByCode(promoCode);
			if (promotion == null) {
				errorMessage = String.format("Discount for code \"%s\" is unavailable.", promoCode);
			} else if (discountService.getPromotions().contains(promotion)) {
				errorMessage = String.format("Discount for code \"%s\" already exists in list.",
						promoCode);
			}
		}
	}

	/**
	 * Invokes on submit of the {@link #addDiscountForm}. Performs the adding of
	 * {@link #promotion} if the {@link #validateDiscount()} passes successfully
	 * and updates the {@link #discountForm}.
	 *
	 * @return
	 * @throws MalformedURLException
	 */
	@OnEvent(value = "addDiscountEvent")
	Object addDiscount() throws MalformedURLException {
		/**
		 *  promotionsList should be intialized allways because we use this value in template.
		 */
		promotionsList = discountService.getPromotions();

		if (request.isXHR()) {
			promoCode = request.getParameter("promo");

			validateDiscount();
			if (errorMessage == null) {

				discountService.addPromotion(promotion);
				promotionsList.add(0, promotion);
			}
			return discountForm;
		} else
			return this;
	}
}
