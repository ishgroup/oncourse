/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.math;

import java.util.Currency;
import java.util.Locale;

/**
 * Java provides lots of locales, mostly based on os time zone and keyboard type. We have produced subset of countries, to make things easier.
 * 
 * @author marcin
 */
public enum Country {

	AUSTRALIA(new Locale("en", "AU"), "$", "AUD"),
	EUROPE(Locale.FRANCE, "\u20AC", "EUR"),
	ENGLAND(Locale.UK, "\u00A3", "GBP"),
	US(Locale.US, "$", "USD"),
	HONG_KONG(new Locale("en", "HK"), "$", "HKD"),
	SWITZERLAND(new Locale("de", "CH"), null, "CHF"),
	NORWAY(new Locale("no", "NO"), "kr", "NOK"),
	SOUTH_AFRICA(new Locale("en", "ZA"), "R", "ZAR");

	private String symbol;
	private String shortSymbol;
	private Currency currency;
	private Locale locale;

	private Country(Locale locale, String shortCurrencySymbol, String currencySymbol) {
		this.locale = locale;
		this.currency = Currency.getInstance(locale);
		this.shortSymbol = shortCurrencySymbol;
		this.symbol = currencySymbol;

	}

	/**
	 * @return short currency symbol, eg. "$"
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
	public java.util.Currency currency() {
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
}
