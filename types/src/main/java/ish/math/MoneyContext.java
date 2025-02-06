/*
 * Copyright ish group pty ltd 2025.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.math;

import ish.math.format.MoneyFormatter;

import javax.money.CurrencyUnit;
import java.math.RoundingMode;
import java.util.Locale;

public interface MoneyContext {

    RoundingMode DEFAULT_ROUND = RoundingMode.HALF_UP;

    void updateCountry(Country country);

    CurrencyUnit getCurrency();
    Locale getLocale();
    MoneyFormatter getFormatter();

}
