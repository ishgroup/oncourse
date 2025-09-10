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

import ish.common.util.DisplayableExtendedEnumeration;

import javax.money.CurrencyUnit;
import javax.money.Monetary;
import java.util.Arrays;
import java.util.Locale;

/**
 * Defines a set of supported countries with their associated locales and currency.
 * <p>
 * Java provides many locales, primarily based on the OS time zone and keyboard type.
 * To simplify currency-related operations, Angel has specific subset of countries.
 * Each country is associated with a {@link Locale}, a currency symbol, and a {@link CurrencyUnit}.
 * </p>
 */
public enum Country implements DisplayableExtendedEnumeration<Integer> {

	AUSTRALIA(1, "Australia", new Locale("en", "AU"), "$"),
	EUROPE(2, "Europe", Locale.GERMANY, "\u20AC"),
	ENGLAND(3, "England", Locale.UK, "\u00A3"),
	US(4, "USA", Locale.US, "$"),
	HONG_KONG(5, "Hong Kong", new Locale("zh", "HK"), "$"),
	SWITZERLAND(6, "Switizerland", new Locale("de", "CH"), "SFr."),
	NORWAY(7, "Norway", new Locale("no", "NO"), "kr"),
	SOUTH_AFRICA(8, "South Africa", new Locale("en", "ZA"), "R");

	private final Integer value;
	private final String displayName;

	private final Locale locale;
	private final CurrencyUnit currency;
	private final String currencyCode;
	private final String currencySymbol;

	Country(Integer value, String displayName, Locale locale, String shortCurrencySymbol) {
		this.value = value;
		this.displayName = displayName;
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
	 * Gets standard currency code symbol for this country.
	 * <p>
	 * Example: "AUD" for Australia, "USD" for the United States.
	 * </p>
	 *
	 * @return currency code according to ISO 4217.
	 */
	public String currencyCode() {
		return currencyCode;
	}

	/**
	 * Gets predefined currency symbol for this country.
	 * <p>
	 * Example: "$" for Australia, "€" for Germany.
	 * </p>
	 *
	 * @return currency symbol
	 */
	public String currencySymbol() {
		return currencySymbol;
	}

	/**
	 * Gets the {@link CurrencyUnit} associated with this Country locale.
	 *
	 * @return currency unit.
	 */
	public CurrencyUnit currency() {
		return this.currency;
	}

	/**
	 * Finds a {@link Country} based on a given database value.
	 * <p>
	 * This method searches for a country that matches the provided database value.
	 * If no match is found, it returns {@code null}.
	 * </p>
	 *
	 * @param value - integer value presentation in database
	 * @return corresponding {@link Country} or {@code null} if no match is found.
	 */
	public static Country fromDatabaseValue(Integer value) {
		return Arrays.stream(values())
				.filter( it -> it.getDatabaseValue().equals(value))
				.findFirst()
				.orElse(null);
	}

	/**
	 * Finds a {@link Country} based on a given database value.
	 * <p>
	 * This method searches for a country that matches the provided database value.
	 * If no match is found, it returns {@code null}.
	 * </p>
	 *
	 * @param value - integer value presentation in database but in String format
	 * @return corresponding {@link Country} or {@code null} if no match is found.
	 */
	public static Country fromDatabaseValue(String value) {
		return fromDatabaseValue(Integer.parseInt(value));
	}

	/**
	 * Finds a {@link Country} based on a given {@link Locale}.
	 * <p>
	 * This method searches for a country that matches the provided locale.
	 * If no match is found, it returns {@code null}.
	 * </p>
	 *
	 * @param locale locale to search for.
	 * @return corresponding {@link Country} or {@code null} if no match is found.
	 */
	public static Country fromLocale(Locale locale) {
		return Arrays.stream(values())
				.filter( it -> it.locale().equals(locale))
				.findFirst()
				.orElse(null);
    }

	@Override
	public String getDisplayName() {
		return displayName;
	}

	@Override
	public Integer getDatabaseValue() {
		return value;
	}
}
