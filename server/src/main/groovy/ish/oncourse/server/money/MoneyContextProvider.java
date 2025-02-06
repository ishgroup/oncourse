/*
 * Copyright ish group pty ltd 2025.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.money;

import groovy.lang.Singleton;
import ish.math.Country;
import ish.math.MoneyContext;
import ish.math.format.DefaultMoneyFormatter;
import ish.math.format.MoneyFormatter;

import javax.money.CurrencyUnit;
import javax.money.Monetary;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicReference;

@Singleton
public class MoneyContextProvider implements MoneyContext {

    private final AtomicReference<CurrencyUnit> currency = new AtomicReference<>();
    private final AtomicReference<Locale> locale = new AtomicReference<>();
    private final AtomicReference<MoneyFormatter> formatter = new AtomicReference<>();

    public MoneyContextProvider() {
        currency.set(Monetary.getCurrency(Locale.getDefault()));
        locale.set(Locale.getDefault());
    }

    public void updateCountry(Country country) {
        this.currency.set(country.currency());
        this.locale.set(country.locale());
        this.formatter.set(new DefaultMoneyFormatter(country));
    }

    public CurrencyUnit getCurrency() {
        return currency.get();
    }

    public Locale getLocale() {
        return locale.get();
    }

    @Override
    public MoneyFormatter getFormatter() {
        return formatter.get();
    }

}
