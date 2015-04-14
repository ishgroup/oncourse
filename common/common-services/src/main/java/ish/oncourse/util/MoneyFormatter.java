/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.util;

import ish.math.Country;
import ish.math.Money;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;

public class MoneyFormatter extends DefaultFormatter {

	private static final long serialVersionUID = 1L;

	private Country serverLocation = Country.AUSTRALIA ;
	private boolean useBracketsForNegative;
	private boolean useThousandsDelimiter = true;

	/**
	 * @return shared instance of money formatter with negative values rendered with minus sign
	 */
	public static MoneyFormatter getInstance() {
		return getInstance(false);
	}

	/**
	 * @param useBracketsForNegative whether returned formatter should use minus sign or brackets for negative values
	 * @return shared instance of money formatter
	 */
	public static MoneyFormatter getInstance(boolean useBracketsForNegative) {
		return getInstance(useBracketsForNegative, true);
	}

	public static MoneyFormatter getInstance(boolean useBracketsForNegative, boolean useThousandsDelimiter) {
		return new MoneyFormatter(useBracketsForNegative, useThousandsDelimiter);
	}

	/**
	 * comprehensive constructor allows to choose whether the money is rendered with minus sign or in brackets.
	 *
	 * @param useBracketsForNegative
	 * @param useThousandsDelimeter
	 */
	protected MoneyFormatter(Boolean useBracketsForNegative,  boolean useThousandsDelimeter) {
		super();
		this.useBracketsForNegative = useBracketsForNegative;
		this.useThousandsDelimiter = useThousandsDelimeter;
		setValueClass(Money.class);
	}

	@Override
	public Object stringToValue(String aString) throws ParseException {
		Money result;
		String string = aString.trim();

		if (string == null || string.length() == 0) {
			result = Money.ZERO;
		} else {
			if (this.useBracketsForNegative && string.startsWith("(") && string.endsWith(")")) {
				string = string.replace("(", "");
				string = string.replace(")", "");
				string = "-" + string;
			}
			if (this.serverLocation.currencyShortSymbol() != null) {
				string = string.replace(this.serverLocation.currencyShortSymbol(), "").trim();
			}

			string = string.replace(",", ".");

			try {
				result = new Money(string);
			} catch (Exception e) {
				throw new ParseException(String.format("Unable to parse '%s' to Money", string), -1);
			}
		}
		return result;
	}

	@Override
	public String valueToString(Object value) {
		double v = Money.ZERO.doubleValue();
		if (value instanceof Money) {
			v = ((Money) value).doubleValue();
		} else if (value instanceof Number) {
			v = ((Number) value).doubleValue();
		}

		NumberFormat format = NumberFormat.getCurrencyInstance(this.serverLocation.locale());
		if (!useThousandsDelimiter)
			format = new DecimalFormat("$0.00", DecimalFormatSymbols.getInstance(this.serverLocation.locale()));


		if (this.useBracketsForNegative && v < 0) {
			return "(" + format.format(0 - v) + ")";
		}

		return  format.format(v);
	}
}

