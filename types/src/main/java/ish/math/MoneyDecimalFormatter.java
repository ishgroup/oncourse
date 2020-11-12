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
package ish.math;

import org.apache.commons.lang3.StringUtils;

import java.text.NumberFormat;
import java.util.Locale;

public class MoneyDecimalFormatter {

	public String valueToString(Money money, Integer scale) {
		NumberFormat xmlDecimal = NumberFormat.getNumberInstance(Locale.US);
		xmlDecimal.setGroupingUsed(false);
		xmlDecimal.setMinimumFractionDigits(scale);
		xmlDecimal.setMaximumFractionDigits(scale);
		return xmlDecimal.format(money.doubleValue());
	}

	public Money stringToValue(String value) {
		if (StringUtils.trimToNull(value) != null) {
			return new Money(value);
		} else {
			return Money.ZERO;
		}
	}
}
