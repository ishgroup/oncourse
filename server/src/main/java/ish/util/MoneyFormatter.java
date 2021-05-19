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

import ish.math.Country;
import ish.math.Money;
import ish.persistence.CommonPreferenceController;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;

/**
 * ensures the money field is rendered in the same way accross the application
 *
 */
public class MoneyFormatter extends DefaultFormatter {

	private static final long serialVersionUID = 1L;

	private static final Logger logger = LogManager.getLogger(MoneyFormatter.class);
	private static final String CLEAR_UP_REGEX = "[-\\(\\)\\s]";
	private static final String SPACE_SYMBOL = "\\s";
	private static final String EMPTY_SYMBOL = "";
	private static final String MINUS_SIGN = "-";

	private Country serverLocation;
	private boolean useBracketsForNegative;
	private boolean useThousandsDelimiter = true;
	private boolean nullIfBlank = false;

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
	 * default constructor rendering negative values with minus character
	 */
	protected MoneyFormatter() {
		this(false, true);
	}

	protected MoneyFormatter(Boolean useBracketsForNegative,  boolean useThousandsDelimeter) {
		this(useBracketsForNegative, useThousandsDelimeter, false);
	}

	protected MoneyFormatter(Boolean useBracketsForNegative,  boolean useThousandsDelimeter, boolean nullIfBlank) {
		super();
		if (CommonPreferenceController.getController() != null) {
			this.serverLocation = CommonPreferenceController.getController().getCountry();
		}
		

		// if preference not set default to australia
		if (this.serverLocation == null) {
			this.serverLocation = Country.AUSTRALIA;
		}

		this.useBracketsForNegative = useBracketsForNegative;
		this.useThousandsDelimiter = useThousandsDelimeter;
		this.nullIfBlank = nullIfBlank;
		setValueClass(Money.class);
	}

	/**
	 * @see DefaultFormatter#stringToValue(String)
	 */
	@Override
	public Object stringToValue(String aString) throws ParseException {
		Money result;
		String string = StringUtils.trimToEmpty(aString);

		if (StringUtils.isBlank(string)) {
			result = nullIfBlank ? null : Money.ZERO;
		} else {
			if (this.useBracketsForNegative && string.startsWith("(") && string.endsWith(")")) {
				string = string.replace("(", "");
				string = string.replace(")", "");
				string = MINUS_SIGN + string;
			}
			if (this.serverLocation.currencyShortSymbol() != null) {
				string = string.replace(this.serverLocation.currencyShortSymbol(), "").trim();
			}

			string = string.replace(",", ".");

			//if user started enter negative amount with minus character
			if (MINUS_SIGN.equals(string)) {
				string = StringUtils.EMPTY;
			}

			try {
				result = new Money(string);
			} catch (Exception e) {
				throw new ParseException(String.format("Unable to parse '%s' to Money", string), -1);
			}
		}
		return result;
	}

	/**
	 * @see DefaultFormatter#valueToString(Object)
	 */
	@Override
	public String valueToString(Object value) {
		if (nullIfBlank && value == null) {
			return null;
		}

		double v = Money.ZERO.doubleValue();
		if (value instanceof Money) {
			v = ((Money) value).doubleValue();
		} else if (value instanceof Number) {
			v = ((Number) value).doubleValue();
		}

		// Use australian dollar format - (CURRENCY_SYMBOL)(DIGITS)
		DecimalFormat format = (DecimalFormat) NumberFormat.getCurrencyInstance(Country.AUSTRALIA.locale());

		format.setNegativePrefix(MINUS_SIGN + serverLocation.currencyShortSymbol());
		format.setPositivePrefix(serverLocation.currencyShortSymbol());
		DecimalFormatSymbols symbols = format.getDecimalFormatSymbols();
		symbols.setMonetaryDecimalSeparator('.');
		symbols.setGroupingSeparator(',');
		symbols.setCurrencySymbol(serverLocation.currencyShortSymbol());
		format.setDecimalFormatSymbols(symbols);

		if (!useThousandsDelimiter) {
			format = new DecimalFormat("$0.00", DecimalFormatSymbols.getInstance(this.serverLocation.locale()));
		}


		String result;
		if (this.useBracketsForNegative && v < 0) {
			result = "(" + format.format(0 - v) + ")";
			return result.replaceAll(SPACE_SYMBOL, EMPTY_SYMBOL);
		}

		result = format.format(v)
				.replaceAll(SPACE_SYMBOL, EMPTY_SYMBOL)
				.replaceAll("\\(", EMPTY_SYMBOL)
				.replaceAll("\\)", EMPTY_SYMBOL);
		return result;
	}

}
