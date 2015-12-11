package ish.oncourse.model;

import ish.math.Money;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static ish.oncourse.utils.WebDiscountUtils.hasAnyFiltering;

/**
 * Use this policy to get the "potentially applicable" discounts: when student
 * for enrolment is not defined.
 * 
 * @author ksenia
 * 
 */
public class PotentialDiscountsPolicy extends DiscountPolicy {

	/**
	 * {@inheritDoc}
	 * 
	 * @param promotions
	 */
	public PotentialDiscountsPolicy(List<Discount> promotions) {
		super(promotions);
	}

	/**
	 * {@inheritDoc} Allows promotions only(when no student is defined we can't
	 * say about the applicability of "concession discounts").
	 * 
	 * @see ish.oncourse.model.DiscountPolicy#getApplicableByPolicy(java.util.List, ish.math.Money, ish.math.Money)
	 */
	@Override
	public List<DiscountCourseClass> getApplicableByPolicy(List<DiscountCourseClass> discountCourseClasses, Money feeExGst, Money feeGst) {
		List<DiscountCourseClass> result = new ArrayList<>();
		if (discountCourseClasses != null) {
			for (DiscountCourseClass discountCourseClass : discountCourseClasses) {
				Discount discount = discountCourseClass.getDiscount();
				if (discount.getMinEnrolments() > 1 || feeExGst.add(feeGst).compareTo(discount.getMinValue()) < 0) {
					continue;
				}

				if (discount.getCorporatePassDiscounts() != null && !discount.getCorporatePassDiscounts().isEmpty()) {
					continue;
				}

				if (discount.isPromotion()) {
					if (isPromotionAdded(discount)) {
						result.add(discountCourseClass);
					}
					continue;

				} else if (!hasAnyFiltering(discount) && !discount.getHideOnWeb()) {
					result.add(discountCourseClass);
				}
			}
		}
		return result;
	}
	
}
