/*
 * Copyright ish group pty ltd 2025.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.math.format;

import ish.math.Money;

import java.text.NumberFormat;
import java.util.Locale;

/**
 * Basic implementation of {@code MoneyFormatter}.
 * <p>
 * Formats {@code Money} instances into plain form using the pattern: ###,###.00.
 * </p>
 */
public class PlainMoneyDecimalFormatter implements MoneyFormatter {

	private final Locale locale;

	public PlainMoneyDecimalFormatter(Locale locale) {
		this.locale = locale;
	}

	@Override
	public String format(Money money) {
		NumberFormat xmlDecimal = NumberFormat.getNumberInstance(locale);
		xmlDecimal.setGroupingUsed(false);
		xmlDecimal.setMinimumFractionDigits(money.getContext().getMaxScale());
		xmlDecimal.setMaximumFractionDigits(money.getContext().getMaxScale());
		return xmlDecimal.format(money.doubleValue());
	}
}
