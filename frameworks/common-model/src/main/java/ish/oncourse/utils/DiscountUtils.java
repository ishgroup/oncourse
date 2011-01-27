package ish.oncourse.utils;

import ish.math.Money;
import ish.oncourse.model.Discount;

import java.math.BigDecimal;
import java.util.List;
import java.util.Vector;

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;

/**
 * Util class for calculating discounts applying results.
 * 
 * @author ksenia
 * 
 */
public class DiscountUtils {
	/**
	 * Returns the discount value for the given price if apply the given
	 * discount.
	 * 
	 * @param discount
	 *            - the given discount.
	 * @param price
	 *            - the price for discount
	 * @return the discount value
	 */
	public static Money discountValue(Discount discount, Money price) {
		Money discountValue = Money.ZERO;
		BigDecimal discountRate = discount.getDiscountRate();
		if (discountRate == null) {
			discountValue = discount.getDiscountAmount();
		} else {
			discountValue = price.multiply(discountRate);
		}
		Money maximumDiscount = discount.getMaximumDiscount();
		if (Money.ZERO.isLessThan(maximumDiscount) && discountValue.compareTo(maximumDiscount) > 0) {
			discountValue = maximumDiscount;
		} else {
			Money minimumDiscount = discount.getMinimumDiscount();
			if (Money.ZERO.isLessThan(minimumDiscount)
					&& discountValue.compareTo(minimumDiscount) < 0) {
				discountValue = minimumDiscount;
			}
		}
		return discountValue;
	}

	/**
	 * Returns the discount value for the given price if apply the discounts
	 * from the given list(doesn't check the applicability, just calculate).
	 * 
	 * @param discounts
	 *            - a collection of discounts to apply.
	 * @param price
	 *            - the price for discount
	 * @return the discount value
	 */
	public static Money discountValueForList(List<Discount> discounts, Money price) {
		Money result = Money.ZERO;
		for (Discount d : discounts) {
			result = result.add(discountValue(d, price));
		}
		return result;
	}

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

	public static List<Discount> chooseBestDiscountsVariant(List<Discount> discounts) {
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
