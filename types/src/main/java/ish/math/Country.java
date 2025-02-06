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
 * Java provides lots of locales, mostly based on os time zone and keyboard type. We have produced subset of countries, to make things easier.
 *
 * @author marcin
 */
public enum Country {

	AUSTRALIA(new Locale("en", "AU"), "$", "AUD"),
	EUROPE(Locale.GERMANY, "\u20AC", "EUR"),
	ENGLAND(Locale.UK, "\u00A3", "GBP"),
	US(Locale.US, "$", "USD"),
	HONG_KONG(Locale.US, "$", "HKD"),
	SWITZERLAND(new Locale("de", "CH"), "SFr.", "CHF"),
	NORWAY(new Locale("no", "NO"), "kr", "NOK"),
	SOUTH_AFRICA(new Locale("en", "ZA"), "R", "ZAR");

	private final Locale locale;
	private final String shortSymbol;
	private final String symbol;
	private final CurrencyUnit currency;

	Country(Locale locale, String shortCurrencySymbol, String currencySymbol) {
		this.locale = locale;
		this.currency = Monetary.getCurrency(locale);
		this.shortSymbol = shortCurrencySymbol;
		this.symbol = currencySymbol;
	}

	/**
	 * @return short currency symbol, e.g. "$"
	 */
	public String currencyShortSymbol() {
		return this.shortSymbol;
	}

	/**
	 * @return currency symbol, eg. "AUD", "USD"
	 */
	public String currencySymbol() {
		return this.symbol;
	}

	/**
	 * @return combined currency symbol, eg. "AUD ($)", "USD ($)"
	 */
	public String currencyCombinedSymbol() {
		return this.symbol + (this.shortSymbol != null ? " (" + this.shortSymbol + ")" : "");
	}

	/**
	 * @return currency object associated with the country
	 */
	public CurrencyUnit currency() {
		return this.currency;
	}

	/**
	 * @return locale associated with the country
	 */
	public Locale locale() {
		return this.locale;
	}

	/**
	 * Returns enumeration key by passed integer value. Not ideal but number of keys is still small.
	 *
	 * @param val integer value taken from or stored to database.
	 * @return enumeration key.
	 */
	public static Country forCurrencySymbol(String val) {
		for (Country c : Country.values()) {
			if (c.currencySymbol().equals(val.trim()) || c.currencyShortSymbol() != null && c.currencyShortSymbol().equals(val.trim())) {
				return c;
			}
		}
		throw new IllegalArgumentException("Enumeration key doesn't exist for value:'" + val + "'");
	}

    /**
     * Finds Country instance based on locale
     * @param locale key
     * @return country with some locale or null, if country for locale doesn't exist
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
