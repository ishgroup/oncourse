package ish.oncourse.utils;

import ish.math.Money;
import ish.oncourse.model.Discount;

import java.math.BigDecimal;
import java.util.List;

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

}
