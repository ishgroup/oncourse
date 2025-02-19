/*
 * Copyright ish group pty ltd 2025.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.math.format;

import ish.math.Money;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Basic implementation of {@code MoneyFormatter}.
 * <p>
 * Formats {@code Money} instances into full form using the pattern: (SYMBOL)###,###.00.
 * </p>
 */
public class DefaultMoneyFormatter implements MoneyFormatter {

    private static final String EMPTY_SYMBOL = "";
    private static final String MINUS_SYMBOL = "-";
    private static final String CLEAR_UP_REGEX = "[-\\(\\)\\s]";
    private static final char MONEY_DECIMAL_SEPARATOR = '.';
    private static final char MONEY_THOUSAND_SEPARATOR = ',';

    private final NumberFormat formatter;

    public DefaultMoneyFormatter(Locale locale, String currencySymbol) {
        this.formatter = initializeFormatter(locale, currencySymbol);
    }

    /**
     * Formats {@code Money} into a string representation.
     *
     * @param money the {@code Money} instance to format.
     * @return the formatted money.
     */
    @Override
    public String format(Money money) {
        String value = formatter.format(money.toBigDecimal()).replaceAll(CLEAR_UP_REGEX, EMPTY_SYMBOL);
        return money.isNegative() ? MINUS_SYMBOL + value : value;
    }

    /**
     * Initializes formatter of number values for {@code Money} type based on the current locale and currency symbol.
     * <p>
     * The format follows the pattern: (SYMBOL)###,###.00.
     * </p>
     *
     * @param locale locale used for formatting.
     * @param currencySymbol symbol that used for formatting.
     * @return a localized {@code NumberFormat} instance for money formatting.
     */
    private NumberFormat initializeFormatter(Locale locale, String currencySymbol) {
        // formatTemplate defines which formatting structure will be used
        DecimalFormat formatTemplate = (DecimalFormat) NumberFormat.getCurrencyInstance(locale);
        DecimalFormatSymbols formatSymbols = formatTemplate.getDecimalFormatSymbols();
        formatSymbols.setMonetaryDecimalSeparator(MONEY_DECIMAL_SEPARATOR);
        formatSymbols.setGroupingSeparator(MONEY_THOUSAND_SEPARATOR);
        formatSymbols.setCurrencySymbol(currencySymbol);
        formatTemplate.setDecimalFormatSymbols(formatSymbols);
        return formatTemplate;
    }
}
