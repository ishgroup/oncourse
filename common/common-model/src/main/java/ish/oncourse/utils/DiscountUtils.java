package ish.oncourse.utils;

import ish.common.types.DiscountType;
import ish.math.Money;
import ish.oncourse.model.Discount;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
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
		if (price.isZero()) {
			return price;
		}
		Money discountValue = Money.ZERO;
		BigDecimal discountRate = discount.getDiscountRate();
		DiscountType discountType = discount.getDiscountType();

		if (discountType == null) {
			if (discountRate == null) {
				discountValue = discount.getDiscountAmount();
			} else {
				discountValue = price.multiply(discountRate);
			}
		} else {
			switch (discountType) {
			case FEE_OVERRIDE:
				discountValue = price.subtract(discount.getDiscountAmount());
				break;
			case DOLLAR:
				discountValue = discount.getDiscountAmount();
				break;
			case PERCENT:
				discountValue = price.multiply(discountRate);
				break;
			}
		}

		Money maximumDiscount = discount.getMaximumDiscount();
		if (Money.ZERO.isLessThan(maximumDiscount) && discountValue.compareTo(maximumDiscount) > 0) {
			discountValue = maximumDiscount;
		} else {
			Money minimumDiscount = discount.getMinimumDiscount();
			if (Money.ZERO.isLessThan(minimumDiscount) && discountValue.compareTo(minimumDiscount) < 0) {
				discountValue = minimumDiscount;
			}
		}
		if (price.compareTo(discountValue) < 0) {
			return price;
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
		if (discounts != null) {
			for (Discount d : discounts) {
				result = result.add(discountValue(d, price));
			}
		}
		if (price.compareTo(result) < 0) {
			return price;
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
	 * @param feeExGst
	 * @return
	 */

	public static List<Discount> chooseBestDiscountsVariant(List<Discount> discounts, Money feeExGst) {
		Vector<Discount> chosenDiscounts = new Vector<Discount>();
		if (discounts != null && !discounts.isEmpty()) {
			// figure out the best deal for the customer.
			// first try all the discounts which could be combined.
			Expression exp = ExpressionFactory.matchExp(Discount.COMBINATION_TYPE_PROPERTY, Boolean.TRUE);
			List<Discount> discountsToCombine = (List<Discount>) exp.filterObjects(discounts);
			List<Discount> discountsNotToCombine = (List<Discount>) exp.notExp().filterObjects(discounts);

			Money maxDiscount = Money.ZERO;
			Discount bestDeal = null;
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

	/**
	 * Retrieves the earliest expiry date of a bunch of discounts to apply.
	 * Useful when a bunch of combinable discounts applied to a price. when this
	 * is just a one discount send, just returns its expiry date. If no expiry
	 * date is defined, returns null;
	 * 
	 * @param discounts
	 * @return
	 */
	public static Date earliestExpiryDate(List<Discount> discounts) {
		Date result = null;
		for (Discount discount : discounts) {

			Date expiry = discount.getValidTo();
			if (expiry != null) {
				Calendar expiryDate = Calendar.getInstance();
				expiryDate.setTime(expiry);
				// the whole last day discount should be available
				expiryDate.set(Calendar.HOUR_OF_DAY, 23);
				expiryDate.set(Calendar.MINUTE, 59);
				expiryDate.set(Calendar.SECOND, 59);
				if (result == null) {
					result = expiryDate.getTime();
				} else {
					if (expiryDate.getTime().before(result)) {
						result = expiry;
					}
				}
			}
		}
		return result;
	}

	/**
	 * Retrieves the eligibility conditions of a bunch of discounts to apply.
	 * Useful when a bunch of combinable discounts applied to a price. when this
	 * is just a one discount send, just returns its eligibility condition. If
	 * no contact filter is defined, returns null;
	 * 
	 * @param discounts
	 * @return
	 */
	public static String getEligibilityConditions(List<Discount> applicableDiscounts) {
		StringBuffer result = new StringBuffer("for students: ");
		boolean hasFilter = false;
		for (Discount discount : applicableDiscounts) {
			if (discount.hasEligibilityFilter() && result.indexOf(discount.getEligibilityConditions()) == -1) {
				if(hasFilter){
					result.append("; ");
				}
				hasFilter = true;
				result.append(discount.getEligibilityConditions());
			}
		}
		if (hasFilter) {
			return result.toString();
		}
		return null;
	}
}
