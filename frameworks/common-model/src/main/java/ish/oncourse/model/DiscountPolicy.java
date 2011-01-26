package ish.oncourse.model;

import ish.math.Money;
import ish.oncourse.utils.DiscountUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;

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
	public DiscountPolicy(List<Discount> promotions) {
		this.promotions = promotions;
	}

	/**
	 * The main method of the policy, select the best variant of the applicable
	 * discounts from the given discounts of some courseClass. {@see
	 * CourseClass#getDiscountsToApply(DiscountPolicy)}
	 * 
	 * @param discounts
	 *            the discounts to filter.
	 * @return filtered discounts.
	 */
	public List<Discount> filterDiscounts(List<Discount> discounts) {
		List<Discount> result = new ArrayList<Discount>();

		result = getApplicableByPolicy(discounts);

		result = chooseBestDiscountsVariant(result);

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

	/**
	 * Chooses the best option (the max discount value) from the proposed
	 * discounts : uses the combination of "combinable" discounts, or uses the
	 * best one of "uncombinable".<br/>
	 * The processing of combined/notToCombine discounts is based on
	 * angel/client/ish.oncourse.cayenne.InvoiceLine.updateDiscount().
	 * 
	 * @param discounts
	 * @return
	 */

	private List<Discount> chooseBestDiscountsVariant(List<Discount> discounts) {
		Vector<Discount> chosenDiscounts = new Vector<Discount>();
		if (discounts != null && !discounts.isEmpty()) {
			// figure out the best deal for the customer.
			// first try all the discounts which could be combined.
			Expression exp = ExpressionFactory.matchExp(Discount.COMBINATION_TYPE_PROPERTY,
					Boolean.TRUE);
			List<Discount> discountsToCombine = (List<Discount>) exp.filterObjects(discounts);
			List<Discount> discountsNotToCombine = (List<Discount>) exp.notExp().filterObjects(
					discounts);

			Money maxDiscount = Money.ZERO;
			Discount bestDeal = null;
			Money feeExGst = new Money("100");
			maxDiscount = DiscountUtils.discountValueForList(discountsToCombine, feeExGst);

			for (Discount d : discountsNotToCombine) {
				Money val = DiscountUtils.discountValue(d, feeExGst);

				if (val.compareTo(maxDiscount) > 0) {
					bestDeal = d;
					maxDiscount = val;
				}
			}

			if (bestDeal == null) {
				// go with combined discounts, remove all not combinable
				chosenDiscounts.addAll(discountsToCombine);
			} else {
				// go with not combined discount
				chosenDiscounts.add(bestDeal);
			}
		}
		return chosenDiscounts;

	}

}
