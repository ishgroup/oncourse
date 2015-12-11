package ish.oncourse.model;

import ish.math.Money;
import ish.oncourse.cayenne.DiscountCourseClassInterface;
import ish.oncourse.utils.WebDiscountUtils;
import ish.util.DiscountUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
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
	 * The main method of the policy, select single discount to apply
	 * from the given discounts of some courseClass. {@see
	 * CourseClass#getDiscountsToApply(DiscountPolicy)}
	 * 
	 * @param discountCourseClasses
	 *            the discounts to filter.
	 * @param feeExGst 
	 * @return single discount to apply.
	 */
	public DiscountCourseClass filterDiscounts(List<DiscountCourseClass> discountCourseClasses, Money feeExGst, Money getFeeGst, BigDecimal taxRate) {
		List<DiscountCourseClass> result;

		result = getApplicableByPolicy(discountCourseClasses, feeExGst, getFeeGst);

		return (DiscountCourseClass) DiscountUtils.chooseDiscountForApply(result, feeExGst, taxRate);
	}

	/**
	 * Chooses the discounts applicable by the concrete policy.
	 * 
	 * @param discountCourseClasses
	 *            the discounts to filter.
	 * @return filtered discounts.
	 */
	protected abstract List<DiscountCourseClass> getApplicableByPolicy(List<DiscountCourseClass> discountCourseClasses, Money feeExGst, Money getFeeGst);

	public boolean isPromotionAdded(Discount discount) {
		for (Discount promotion : promotions) {
			if (promotion.getCode().equals(discount.getCode())) {
				return true;
			}
		}
		return false;
	}
}
