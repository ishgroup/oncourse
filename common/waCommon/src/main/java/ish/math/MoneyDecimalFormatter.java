/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
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
