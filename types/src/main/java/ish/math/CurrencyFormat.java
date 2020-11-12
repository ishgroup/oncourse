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

import java.util.Locale;

public final class CurrencyFormat {

    private static CurrencyFormatter formatter = new DefaultCurrencyFormatter();

    private CurrencyFormat() {}

    public static void registerNewFormatter(CurrencyFormatter currencyFormatter) {
        formatter = currencyFormatter;
    }

    public static void updateLocale(Locale locale) {
        formatter.updateLocale(locale);
    }

    /**
     * Get current money currency
     * @return current currency
     */
    public static Locale getCurrentLocale() {
        return formatter.getCurrentLocale();
    }

    public static String formatMoney(Money money) {
        return formatter.format(money);
    }
}
