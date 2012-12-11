package ish.oncourse.model;

import static ish.oncourse.utils.DiscountUtils.hasAnyFiltering;

import java.util.ArrayList;
import java.util.List;

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
	 * @see ish.oncourse.model.DiscountPolicy#getApplicableByPolicy(java.util.List)
	 */
	@Override
	public List<Discount> getApplicableByPolicy(List<Discount> discounts) {
		List<Discount> result = new ArrayList<Discount>();
		if (discounts != null) {
			for (Discount discount : discounts) {
				if (discount.isPromotion()) {
					if (isPromotionAdded(discount)) {
						result.add(discount);
					}
					continue;

				} else if (!hasAnyFiltering(discount) && !discount.getHideOnWeb()) {
					result.add(discount);
				}
			}
		}
		return result;
	}
	
}
