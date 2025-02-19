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

import javax.money.CurrencyUnit;
import javax.money.Monetary;
import java.util.Locale;

/**
 * Defines a set of supported countries with their associated locales and currency.
 * <p>
 * Java provides many locales, primarily based on the OS time zone and keyboard type.
 * To simplify currency-related operations, we have selected a specific subset of countries.
 * Each country is associated with a {@link Locale}, a currency symbol, and a {@link CurrencyUnit}.
 * </p>
 */
public enum Country {

	AUSTRALIA(new Locale("en", "AU"), "$"),
	EUROPE(Locale.GERMANY, "\u20AC"),
	ENGLAND(Locale.UK, "\u00A3"),
	US(Locale.US, "$"),
	HONG_KONG(new Locale("zh", "HK"), "$"),
	SWITZERLAND(new Locale("de", "CH"), "SFr."),
	NORWAY(new Locale("no", "NO"), "kr"),
	SOUTH_AFRICA(new Locale("en", "ZA"), "R");

	private final Locale locale;
	private final CurrencyUnit currency;
	private final String currencyCode;
	private final String currencySymbol;

	Country(Locale locale, String shortCurrencySymbol) {
		this.locale = locale;
		this.currency = Monetary.getCurrency(locale);
		this.currencyCode = Monetary.getCurrency(locale).getCurrencyCode();
		this.currencySymbol = shortCurrencySymbol;
	}

	/**
	 * @return locale associated with the country
	 */
	public Locale locale() {
		return this.locale;
	}

	/**
	 * Gets the standard currency code symbol for this country.
	 * <p>
	 * Example: "AUD" for Australia, "USD" for the United States.
	 * </p>
	 *
	 * @return the currency symbol (ISO 4217 code).
	 */
	public String currencyCode() {
		return currencyCode;
	}

	/**
	 * Gets the currency symbol for this country.
	 * <p>
	 * Example: "$" for Australia, "€" for Germany.
	 * </p>
	 *
	 * @return the short currency symbol.
	 */
	public String currencySymbol() {
		return currencySymbol;
	}

	/**
	 * Gets the {@link CurrencyUnit} associated with this Country locale.
	 *
	 * @return the currency unit.
	 */
	public CurrencyUnit currency() {
		return this.currency;
	}

	/**
	 * Finds a {@link Country} based on a given currency symbol.
	 * <p>
	 * This method searches for a country by either standard currency code (e.g., "USD")
	 * or short symbol (e.g., "$").
	 * </p>
	 *
	 * @param val the currency symbol or code to match.
	 * @return the corresponding {@link Country} enum.
	 * @throws IllegalArgumentException if no matching country is found.
	 */
	public static Country forCurrencySymbol(String val) {
		for (Country c : Country.values()) {
			if (c.currencyCode().equals(val.trim()) || c.currencySymbol() != null && c.currencySymbol().equals(val.trim())) {
				return c;
			}
		}
		throw new IllegalArgumentException("Enumeration key doesn't exist for value:'" + val + "'");
	}

	/**
	 * Finds a {@link Country} based on a given {@link Locale}.
	 * <p>
	 * This method searches for a country that matches the provided locale.
	 * If no match is found, it returns {@code null}.
	 * </p>
	 *
	 * @param locale the locale to search for.
	 * @return the corresponding {@link Country} or {@code null} if no match is found.
	 */
	public static Country findCountryByLocale(Locale locale) {
	    if (locale != null) {
            for (Country country : Country.values()) {
                if (country.locale().equals(locale)) {
                    return country;
                }
            }
        }
        return null;
    }
}
