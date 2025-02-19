/*
 * Copyright ish group pty ltd 2025.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.math.context;


import ish.math.format.MoneyFormatter;

import javax.money.CurrencyUnit;
import java.math.RoundingMode;
import java.util.Locale;

/**
 * Defines configuration settings for handling {@code Money} instances,
 * including currency, locale, and formatting preferences.
 * <p>
 * This interface provides methods to retrieve locale-specific and currency-related settings,
 * ensuring consistency in monetary calculations, formatting, and display.
 * </p>
 */
public interface MoneyContext {

    /**
     * The default rounding mode applied to monetary calculations.
     */
    RoundingMode DEFAULT_ROUND = RoundingMode.HALF_UP;

    /**
     * Retrieves the locale associated with this money context.
     *
     * @return the {@code Locale} defining regional monetary conventions.
     */
    Locale getLocale();

    /**
     * Retrieves the currency unit associated with this money context.
     *
     * @return the {@code CurrencyUnit} representing the active currency.
     */
    CurrencyUnit getCurrency();

    /**
     * Retrieves the ISO 4217 currency code of the configured currency.
     *
     * @return a {@code String} representing the three-letter currency code (e.g., "USD", "EUR").
     */
    String getCurrencyCode();

    /**
     * Retrieves the short currency symbol associated with the configured currency.
     * <p>
     * Examples: "$" for USD, "€" for EUR.
     * </p>
     *
     * @return a {@code String} representing the short currency symbol.
     */
    String getShortCurrencySymbol();

    /**
     * Retrieves the money formatter associated with this context.
     * <p>
     * The formatter is responsible for converting {@code Money} instances into strings.
     * </p>
     *
     * @return a {@code MoneyFormatter} instance for formatting monetary values.
     */
    MoneyFormatter getFormatter();
}