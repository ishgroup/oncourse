/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.util;

import ish.common.payable.IInvoiceLineInterface;
import ish.common.types.DiscountType;
import ish.math.Money;
import ish.math.MoneyRounding;
import ish.oncourse.cayenne.DiscountInterface;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class DiscountUtils {

	/**
	 * Apply set of discounts to Invoice line, set DiscountEachExTax and adjust TaxEach amount to achieve the required rounding
	 * @param discounts
	 * @param invoiceLine
	 * @param taxRate
	 * @param taxAdjustments
	 */
	public static void applyDiscounts(List<? extends DiscountInterface> discounts, IInvoiceLineInterface invoiceLine, BigDecimal taxRate, Money taxAdjustments) {		
		
		Money discountValue = discountValue(discounts, invoiceLine.getPriceEachExTax(), taxRate);
		// In case there are multiple discounts rounding modes we should be the broadest one and use it
		MoneyRounding rounding = getBroadestRounding(discounts);

		Money tax = InvoiceUtil.calculateTaxEachForInvoiceLine(invoiceLine.getPriceEachExTax(), discountValue, taxRate, taxAdjustments);
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
	 * @param discounts
	 * @param priceExTax
	 * @param taxRate
	 * @return
	 */
	public static Money getDiscountedFee(List<? extends DiscountInterface> discounts, Money priceExTax,  BigDecimal taxRate) {
		if (discounts == null || discounts.isEmpty() || priceExTax == null || priceExTax.isZero()) {
			return priceExTax;
		}
		if (taxRate == null) {
			taxRate = new BigDecimal(0);
		}

		Money discountAmount = Money.ZERO;

		for (DiscountInterface discount : discounts) {
			discountAmount = discountAmount.add(discountValue(discount, priceExTax));
		}

		if (discountAmount.compareTo(priceExTax) >= 0) {
			return  Money.ZERO;
		}

		// In case there are multiple discounts rounding modes we should be the broadest one and use it
		MoneyRounding rounding = getBroadestRounding(discounts);

		Money total = priceExTax.subtract(discountAmount).multiply(BigDecimal.ONE.add(taxRate));
		return total.round(rounding);
	}

	/**
	 * Calculate discount amount (DiscountEachExTax) for final price 
	 * @param discounts
	 * @param priceExTax
	 * @param taxRate
	 * @return
	 */
	public static Money discountValue(List<? extends DiscountInterface> discounts, Money priceExTax, BigDecimal taxRate) {
		Money total = getDiscountedFee(discounts, priceExTax, taxRate);
		return priceExTax.subtract(total.divide(BigDecimal.ONE.add(taxRate)));
	}

	private static Money discountValue(DiscountInterface  discount, Money price) {
		if (price.isZero()) {
			return price;
		}
		Money discountValue = Money.ZERO;
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
		if (price.compareTo(discountValue) < 0) {
			return price;
		}
		return discountValue;
	}
	
	private static MoneyRounding getBroadestRounding(List<? extends DiscountInterface> discounts) {
		Collections.sort(discounts, new Comparator<DiscountInterface>() {
			@Override
			public int compare(DiscountInterface o1, DiscountInterface o2) {
				if (o2.getRounding() == null && o1.getRounding() == null) {
					return 0;
				} else if (o2.getRounding() == null) {
					return -1;
				} else if (o1.getRounding() == null) {
					return 1;
				}
				
				return o2.getRounding().getDatabaseValue().compareTo(o1.getRounding().getDatabaseValue());

			}
		});
		return discounts.get(0).getRounding() != null ? discounts.get(0).getRounding() : MoneyRounding.ROUNDING_NONE;
	}
}
