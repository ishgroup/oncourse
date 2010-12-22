package ish.oncourse.services.discount;

import ish.math.Money;
import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Discount;

import java.math.BigDecimal;
import java.util.ArrayList;
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
	@Override
	public List<Discount> getApplicableDiscounts(CourseClass courseClass) {
		List<Discount> results = new ArrayList<Discount>();

		results.addAll(courseClass.getDiscounts());

		Date now = new Date();
		Expression e = ExpressionFactory.matchExp(Discount.CODE_PROPERTY, null);
		e = e.andExp(ExpressionFactory.greaterExp(Discount.VALID_TO_PROPERTY, now).orExp(
				ExpressionFactory.matchExp(Discount.VALID_TO_PROPERTY, null)));
		e = e.andExp(ExpressionFactory.lessExp(Discount.VALID_FROM_PROPERTY, now).orExp(
				ExpressionFactory.matchExp(Discount.VALID_FROM_PROPERTY, null)));

		results = e.filterObjects(results);

		return results;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see ish.oncourse.services.discount.IDiscountService#chooseDiscounts(java.util.List,
	 *      ish.oncourse.model.CourseClass) The processing of
	 *      combined/notToCombine discounts is based on
	 *      angel/client/ish.oncourse.cayenne.InvoiceLine.updateDiscount()
	 */
	@Override
	public List<Discount> chooseDiscounts(List<Discount> discounts, CourseClass aClass) {
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
	 * @see ish.oncourse.services.discount.IDiscountService#discountedFeeExTax(java.util.List,
	 *      ish.oncourse.model.CourseClass)
	 */
	@Override
	public Money discountedFeeExTax(List<Discount> discounts, CourseClass aClass) {
		Money feeExGst = aClass.getFeeExGst();
		Money result = aClass.getFeeExGst().subtract(
				discountValueForList(chooseDiscounts(discounts, aClass), feeExGst));
		return result;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see ish.oncourse.services.discount.IDiscountService#discountedFeeIncTax(java.util.List,
	 *      ish.oncourse.model.CourseClass)
	 */
	@Override
	public Money discountedFeeIncTax(List<Discount> discounts, CourseClass aClass) {
		Money feeIncGst = aClass.getFeeIncGst();
		Money result = aClass.getFeeIncGst().subtract(
				discountValueForList(chooseDiscounts(discounts, aClass), feeIncGst));
		return result;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see ish.oncourse.services.discount.IDiscountService#discountValueForList(java.util.List,
	 *      ish.math.Money)
	 */
	@Override
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
	 * @see ish.oncourse.services.discount.IDiscountService#discountValueForListFiltered(java.util.List,
	 *      ish.oncourse.model.CourseClass)
	 */
	@Override
	public Money discountValueForListFiltered(List<Discount> discounts, CourseClass aClass) {
		return discountValueForList(chooseDiscounts(discounts, aClass), aClass.getFeeExGst());
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see ish.oncourse.services.discount.IDiscountService#discountValue(ish.oncourse.model.Discount,
	 *      ish.math.Money)
	 */
	@Override
	public Money discountValue(Discount discount, Money price) {
		Money discountValue = Money.ZERO;
		BigDecimal discountRate = discount.getDiscountRate();
		if (discountRate == null) {
			discountRate = discount.getDiscountAmount().divide(price.toBigDecimal());
		}
		discountValue = price.multiply(discountRate);

		return discountValue;
	}

}
