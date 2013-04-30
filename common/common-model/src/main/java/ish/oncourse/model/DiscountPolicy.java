package ish.oncourse.model;

import ish.math.Money;
import ish.oncourse.utils.DiscountUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Defines the rules of choosing appropriate discounts. {@see
 * #filterDiscounts(List)}.
 * 
 * @author ksenia
 * 
 */
public abstract class DiscountPolicy {

	/**
	 * Discounts that have codes, don't need any concession. Inputed by user.
	 */
	protected List<Discount> promotions;

	/**
	 * Default constructor that has the user-defined promotions as a parameter.
	 * 
	 * @param promotions
	 *            the list to init the {@link #promotions}.
	 */
	@SuppressWarnings("unchecked")
	public DiscountPolicy(List<Discount> promotions) {
		if (promotions != null) {
			this.promotions = promotions;
		} else {
			this.promotions = Collections.EMPTY_LIST;
		}
	}

	/**
	 * The main method of the policy, select the best variant of the applicable
	 * discounts from the given discounts of some courseClass. {@see
	 * CourseClass#getDiscountsToApply(DiscountPolicy)}
	 * 
	 * @param discounts
	 *            the discounts to filter.
	 * @param feeExGst 
	 * @return filtered discounts.
	 */
	public List<Discount> filterDiscounts(List<Discount> discounts, Money feeExGst) {
		List<Discount> result = new ArrayList<>();

		result = getApplicableByPolicy(discounts);

		result = DiscountUtils.chooseBestDiscountsVariant(result, feeExGst);

		return result;
	}

	/**
	 * Chooses the discounts applicable by the concrete policy.
	 * 
	 * @param discounts
	 *            the discounts to filter.
	 * @return filtered discounts.
	 */
	protected abstract List<Discount> getApplicableByPolicy(List<Discount> discounts);

	public boolean isPromotionAdded(Discount discount) {
		for (Discount promotion : promotions) {
			if (promotion.getCode().equals(discount.getCode())) {
				return true;
			}
		}
		return false;
	}
}
