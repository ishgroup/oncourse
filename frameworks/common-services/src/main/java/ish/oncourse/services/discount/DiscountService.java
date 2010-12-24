package ish.oncourse.services.discount;

import ish.math.Money;
import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Discount;
import ish.oncourse.model.DiscountCourseClass;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;

public class DiscountService implements IDiscountService {

	/**
	 * {@inheritDoc}
	 * 
	 * @see ish.oncourse.services.discount.IDiscountService#getApplicableDiscounts(ish.oncourse.model.CourseClass)
	 * 
	 *      The implementation is brought from
	 *      angel/client/ish.oncourse.cayenne.
	 *      Discount.getApplicableDiscounts(CourseClass courseClass)
	 */
	public List<Discount> getApplicableDiscounts(CourseClass courseClass) {
		List<Discount> results = new ArrayList<Discount>();

		results.addAll(courseClass.getDiscounts());

		Expression e = ExpressionFactory.matchExp(Discount.CODE_PROPERTY, null);
		e = e.andExp(getCurrentDateFilter());

		results = e.filterObjects(results);

		return results;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see ish.oncourse.services.discount.IDiscountService#filterDiscounts(java.util.List,
	 *      ish.oncourse.model.CourseClass)
	 */
	public List<Discount> filterDiscounts(List<Discount> discounts, CourseClass courseClass) {
		if (discounts == null) {
			return Collections.emptyList();
		}
		Expression e = ExpressionFactory.matchExp(Discount.DISCOUNT_COURSE_CLASSES_PROPERTY + "."
				+ DiscountCourseClass.COURSE_CLASS_PROPERTY, courseClass);
		e = e.andExp(getCurrentDateFilter());

		return e.filterObjects(discounts);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see ish.oncourse.services.discount.IDiscountService#chooseBestDiscountsVariant(java.util.List,
	 *      ish.oncourse.model.CourseClass) The processing of
	 *      combined/notToCombine discounts is based on
	 *      angel/client/ish.oncourse.cayenne.InvoiceLine.updateDiscount()
	 */
	public List<Discount> chooseBestDiscountsVariant(List<Discount> discounts, CourseClass aClass) {
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
			Money feeExGst = aClass.getFeeExGst();
			maxDiscount = discountValueForList(discountsToCombine, feeExGst);

			for (Discount d : discountsNotToCombine) {
				Money val = discountValue(d, feeExGst);

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
	 * {@inheritDoc}
	 * 
	 * @see ish.oncourse.services.discount.IDiscountService#discountValueForList(java.util.List,
	 *      ish.math.Money)
	 */
	public Money discountValueForList(List<Discount> discounts, Money price) {
		Money result = Money.ZERO;
		for (Discount d : discounts) {
			result = result.add(discountValue(d, price));
		}
		return result;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see ish.oncourse.services.discount.IDiscountService#discountedValueForList(java.util.List,
	 *      ish.math.Money)
	 */
	public Money discountedValueForList(List<Discount> discounts, Money price) {
		return price.subtract(discountValueForList(discounts, price));
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see ish.oncourse.services.discount.IDiscountService#getConcessionDiscounts(ish.oncourse.model.CourseClass)
	 */
	public List<Discount> getConcessionDiscounts(CourseClass aClass) {
		List<Discount> availableDiscounts = getApplicableDiscounts(aClass);

		List<Discount> discounts = new ArrayList<Discount>(availableDiscounts.size());
		for (Discount discount : availableDiscounts) {
			if (!discount.getDiscountConcessionTypes().isEmpty()) {
				discounts.add(discount);
			}
		}
		return discounts;
	}

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
	public Money discountValue(Discount discount, Money price) {
		Money discountValue = Money.ZERO;
		BigDecimal discountRate = discount.getDiscountRate();
		if (discountRate == null) {
			discountRate = discount.getDiscountAmount().divide(price).toBigDecimal();
		}
		discountValue = price.multiply(discountRate);
		Money maximumDiscount = discount.getMaximumDiscount();
		if (Money.ZERO.isLessThan(maximumDiscount) && discountValue.compareTo(maximumDiscount) > 0) {
			discountValue = maximumDiscount;
		} else {
			Money minimumDiscount = discount.getMinimumDiscount();
			if (Money.ZERO.isLessThan(minimumDiscount) && discountValue.compareTo(minimumDiscount) < 0) {
				discountValue = minimumDiscount;
			}
		}
		return discountValue;
	}

	/**
	 * Returns the discounted value for the given price if apply the given
	 * discount.
	 * 
	 * @param discount
	 *            - the given discount.
	 * @param price
	 *            - the price for discount
	 * @return the discounted value
	 */
	public Money discountedValue(Discount discount, Money price) {
		return price.subtract(discountValue(discount, price));
	}

	/**
	 * Returns filter for retrieving the current discounts(with valid or
	 * undefined date range)
	 * 
	 * @return expression
	 */
	private Expression getCurrentDateFilter() {
		Date now = new Date();

		Expression e = ExpressionFactory.greaterExp(Discount.VALID_TO_PROPERTY, now).orExp(
				ExpressionFactory.matchExp(Discount.VALID_TO_PROPERTY, null));
		e = e.andExp(ExpressionFactory.lessExp(Discount.VALID_FROM_PROPERTY, now).orExp(
				ExpressionFactory.matchExp(Discount.VALID_FROM_PROPERTY, null)));
		return e;
	}

}
