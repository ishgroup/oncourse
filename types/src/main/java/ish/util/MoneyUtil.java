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

import ish.math.Money;

import java.math.BigDecimal;

/**
 * colection of methods involving finance, calculating taxes etc.
 *
 * @author marcin
 */
public final class MoneyUtil {

	private MoneyUtil() {}

	/**
	 * returns the price value including the tax
	 *
	 * @param price
	 * @param taxRate
	 * @return calculated Money value
	 */
	public static Money calculatePriceExFromPriceInc(Money price, BigDecimal taxRate) {

		if (price == null) {
			return Money.ZERO;
		}

		if (taxRate == null) {
			return price;
		}

		Money result = price.divide(taxRate.add(BigDecimal.ONE));

		return result;
	}

	/**
	 * returns the price value including the tax
	 *
	 * @param price
	 * @param taxRate
	 * @param taxAdjustment
	 * @return calculated Money value
	 */
	public static Money getPriceIncTax(Money price, BigDecimal taxRate, Money taxAdjustment) {

		if (price == null) {
			return Money.ZERO;
		}

		if (taxRate == null) {
			return price;
		}

		Money result = price.multiply(taxRate.add(BigDecimal.ONE));

		if (taxAdjustment == null) {
			return result;
		}

		return result.add(taxAdjustment);
	}

	/**
	 * returns the tax value for a given price and tax adjustment
	 *
	 * @param price
	 * @param taxRate
	 * @param taxAdjustment
	 * @return calculated tax value
	 */
	public static Money getPriceTax(Money price, BigDecimal taxRate, Money taxAdjustment) {

		if (price == null) {
			return Money.ZERO;
		}

		if (taxRate == null) {
			return Money.ZERO;
		}

		Money result = price.multiply(taxRate);

		if (taxAdjustment == null) {
			return result;
		}

		return result.add(taxAdjustment);
	}

	/**
	 * calculates tax adjustment for a given prices and tax rate
	 *
	 * @param priceInc
	 * @param priceEx
	 * @param taxRate
	 * @return calculated tax adjustment
	 */
	public static Money calculateTaxAdjustment(Money priceInc, Money priceEx, BigDecimal taxRate) {
		if (priceEx == null || priceInc == null) {
			return Money.ZERO;
		}

		if (taxRate == null) {
			return Money.ZERO;
		}

		return priceInc.subtract(priceEx.multiply(taxRate.add(BigDecimal.ONE)));
	}

}
