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

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;

/**
 * Default Ish currency formatter.
 */
public class DefaultCurrencyFormatter implements CurrencyFormatter {

    private static final String EMPTY_SYMBOL = "";
    private static final String MINUS_SYMBOL = "-";
    private static final String CLEAR_UP_REGEX = "[-\\(\\)\\s]";
    private static final char MONEY_DECIMAL_SEPARATOR = '.';
    private static final char MONEY_THOUSAND_SEPARATOR = ',';

    protected Locale currentLocale;
    private NumberFormat formatter;

    public DefaultCurrencyFormatter() {
        currentLocale = Locale.getDefault();
        formatter = initializeFormatter(currentLocale);
    }

    /**
     * Formats money instance.
     * @param money for formatting.
     * @return formatted money.
     */
    @Override
    public String format(Money money) {
        String value = formatter
                .format(money.toBigDecimal())
                .replaceAll(CLEAR_UP_REGEX, EMPTY_SYMBOL);
        if (money.isNegative()) {
            value = MINUS_SYMBOL + value;
        }
        return value;
    }

    /**
     * Updates formatter locale.
     * @param locale which will be used.
     */
    @Override
    public void updateLocale(Locale locale) {
        currentLocale = locale;
        formatter = initializeFormatter(currentLocale);
    }

    /**
     * Returns current locale which uses formatter.
     * @return locale instance.
     */
    @Override
    public Locale getCurrentLocale() {
        return currentLocale;
    }

    /**
     * Initializes formatter for Money based on locale.
     * Current format: (SYMBOL)###,###.00
     * @param locale which will be used for formatting
     * @return localized formatter, which will be used for money formatting
     */
    private NumberFormat initializeFormatter(Locale locale) {
        // formatTemplate defines which formatting structure will be used
        // By default will be used australian dollars format - (CURRENCY_SYMBOL)(DIGITS)
        DecimalFormat formatTemplate = (DecimalFormat) NumberFormat.getCurrencyInstance(Country.AUSTRALIA.locale());
        String currencySymbol = getCurrencySymbol(locale);
        DecimalFormatSymbols formatSymbols = formatTemplate.getDecimalFormatSymbols();
        formatSymbols.setMonetaryDecimalSeparator(MONEY_DECIMAL_SEPARATOR);
        formatSymbols.setGroupingSeparator(MONEY_THOUSAND_SEPARATOR);
        formatSymbols.setCurrencySymbol(currencySymbol);
        formatTemplate.setDecimalFormatSymbols(formatSymbols);
        return formatTemplate;
    }

    /**
     * Returns currency symbol for money instance
     * @param locale , which will be used if country for locale doesn't exist.
     * @return currency symbol for money.
     */
    private String getCurrencySymbol(Locale locale) {
        String currencySymbol;
        Country country = Country.findCountryByLocale(locale);
        if (country != null) {
            currencySymbol = country.currencyShortSymbol();
        } else {
            currencySymbol = Currency.getInstance(locale).getSymbol();
        }
        return currencySymbol;
    }
}
