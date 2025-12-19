/*
 * Copyright ish group pty ltd 2020.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 */
package ish.util;

import ish.common.payable.IInvoiceLineInterface;
import ish.common.types.DiscountType;
import ish.math.Money;
import ish.math.MoneyRounding;
import ish.oncourse.cayenne.DiscountCourseClassInterface;
import ish.oncourse.cayenne.DiscountInterface;
import org.apache.cayenne.exp.Expression;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public class DiscountUtils {

	/**
	 * Apply discount to Invoice line, set DiscountEachExTax and adjust TaxEach amount to achieve the required rounding
	 * @param classDiscount
	 * @param invoiceLine
	 * @param taxRate
	 * @param taxAdjustments
	 */
	public static void applyDiscounts(DiscountCourseClassInterface classDiscount, IInvoiceLineInterface invoiceLine, BigDecimal taxRate, Money taxAdjustments) {

		Money discountValue = discountValue(classDiscount, invoiceLine.getPriceEachExTax(), taxRate);
		MoneyRounding rounding = classDiscount.getDiscount().getRounding();

		Money tax = InvoiceUtil.calculateTaxEachForInvoiceLine(invoiceLine.getPriceEachExTax(), discountValue, taxRate, taxAdjustments);
		if (tax.isNegative()) {
			tax = Money.ZERO;
		}
		if (rounding.getDatabaseValue() > 0) {
			Money total = invoiceLine.getPriceEachExTax().subtract(discountValue).add(tax);
			total = total.round(rounding);
			tax = total.subtract(invoiceLine.getPriceEachExTax().subtract(discountValue));
		}

		invoiceLine.setDiscountEachExTax(discountValue);
		invoiceLine.setTaxEach(tax);
	}

	/**
	 * Calculate final discounted and rounded price including tax (uses for show on UI)
	 * @param classDiscount
	 * @param priceExTax
	 * @param taxRate
	 * @return
	 */
	public static Money getDiscountedFee(DiscountCourseClassInterface classDiscount, Money priceExTax,  BigDecimal taxRate) {
		if (taxRate == null) {
			taxRate = new BigDecimal(0);
		}
		if (classDiscount == null || priceExTax.isZero()) {
			return priceExTax.multiply(BigDecimal.ONE.add(taxRate));
		}

		Money discountAmount = Money.ZERO;

		discountAmount = discountAmount.add(discountValue(classDiscount, priceExTax));

		if (discountAmount.compareTo(priceExTax) >= 0) {
			return  Money.ZERO;
		}

		MoneyRounding rounding = classDiscount.getDiscount().getRounding();

		discountAmount = discountAmount.multiply(BigDecimal.ONE.add(taxRate));

		Money total = Money.of(priceExTax.multiply(BigDecimal.ONE.add(taxRate)).toBigDecimal()).subtract(discountAmount);
		return total.round(rounding);
	}

	/**
	 * Calculate discount amount (DiscountEachExTax) for final price
	 * @param classDiscount
	 * @param priceExTax
	 * @param taxRate
	 * @return
	 */
	public static Money discountValue(DiscountCourseClassInterface classDiscount, Money priceExTax, BigDecimal taxRate) {
		if (classDiscount == null ) {
			return  Money.ZERO;
		}
		Money total = getDiscountedFee(classDiscount, priceExTax, taxRate);
		return priceExTax.subtract(total.divide(BigDecimal.ONE.add(taxRate)));
	}

	private static Money discountValue(DiscountCourseClassInterface classDiscount, Money price) {
		if (price.isZero()) {
			return price;
		}
		Money discountValue = Money.ZERO;

		if (classDiscount.getDiscountDollar() != null) {
			discountValue = classDiscount.getDiscountDollar();
		} else {
			DiscountInterface discount = classDiscount.getDiscount();
			BigDecimal discountRate = discount.getDiscountPercent();
			DiscountType discountType = discount.getDiscountType();

			if (discountType == null) {
				if (discountRate == null) {
					discountValue = discount.getDiscountDollar();
				} else {
					discountValue = price.multiply(discountRate);
				}
			} else {
				switch (discountType) {
					case FEE_OVERRIDE:
						discountValue = price.subtract(discount.getDiscountDollar());
						break;
					case DOLLAR:
						discountValue = discount.getDiscountDollar();
						break;
					case PERCENT:
						discountValue = price.multiply(discountRate);
						break;
				}
			}

			Money maximumDiscount = discount.getDiscountMax();
			if (Money.ZERO.isLessThan(maximumDiscount) && discountValue.compareTo(maximumDiscount) > 0) {
				discountValue = maximumDiscount;
			} else {
				Money minimumDiscount = discount.getDiscountMin();
				if (Money.ZERO.isLessThan(minimumDiscount) && discountValue.compareTo(minimumDiscount) < 0) {
					discountValue = minimumDiscount;
				}
			}
		}



		if (price.compareTo(discountValue) < 0) {
			return price;
		}
		return discountValue;
	}

	/**
	 * Chooses single discount from the proposed
	 * -if there is a negative discount which applies to the current enrolment, then that discount always beats a normal discount. It also beats having no discount at all
	 * -if there are two or more negative discounts which apply, then the higher (as an absolute value) applies
	 * -if there are only normal discounts, then the higher (which has max discount value) one wins
	 *
	 * To implement this logic just split all available discounts into positive and negative,
	 * If the list of negative discounts is not empty then select the higher (as an absolute value)
	 * Else select the higher from positive discounts list.
	 * @param classDiscounts
	 * @param feeExGst
	 * @param taxRate
	 * @return
	 */

	public static DiscountCourseClassInterface chooseDiscountForApply(List<? extends DiscountCourseClassInterface> classDiscounts, Money feeExGst, BigDecimal taxRate) {
		if (classDiscounts != null && !classDiscounts.isEmpty()) {

			List<DiscountCourseClassInterface> negativeDiscounts = new LinkedList<>();
			for (DiscountCourseClassInterface classDiscount : classDiscounts) {
				Money overriddenValue = classDiscount.getDiscountDollar();

				if (overriddenValue != null) {
					if (overriddenValue.isLessThan(Money.ZERO))
					negativeDiscounts.add(classDiscount);
				} else {
					DiscountInterface discount = classDiscount.getDiscount();
					switch (discount.getDiscountType()) {
						case DOLLAR:
							if (discount.getDiscountDollar().isLessThan(Money.ZERO)) {
								negativeDiscounts.add(classDiscount);
							}
							break;
						case PERCENT:
							if (discount.getDiscountPercent().compareTo(BigDecimal.ZERO) < 0) {
								negativeDiscounts.add(classDiscount);
							}
							break;
						case FEE_OVERRIDE:
							if (discount.getDiscountDollar().isGreaterThan(feeExGst)) {
								negativeDiscounts.add(classDiscount);
							}
							break;
						default: throw new IllegalArgumentException("Unsupported discount type");
					}
				}
			}

			return getByAbsoluteValue(!negativeDiscounts.isEmpty() ? negativeDiscounts : classDiscounts, feeExGst, taxRate);
		}
		return null;
	}

	private static DiscountCourseClassInterface getByAbsoluteValue(List<? extends DiscountCourseClassInterface> classDiscounts, Money feeExGst, BigDecimal taxRate) {
		Money maxDiscount = Money.ZERO;
		DiscountCourseClassInterface bestDeal = null;
		for (DiscountCourseClassInterface classDiscount : classDiscounts) {
			Money val = discountValue(classDiscount, feeExGst, taxRate).abs();

			if (val.compareTo(maxDiscount) > 0) {
				bestDeal = classDiscount;
				maxDiscount = val;
			}
		}
		return bestDeal;
	}
}
