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
import ish.math.LocaleUtil;
import ish.math.context.MoneyContext;
import ish.math.context.MoneyContextUpdater;
import ish.math.format.DefaultMoneyFormatter;
import ish.math.format.MoneyFormatter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.money.CurrencyUnit;
import javax.money.Monetary;
import java.util.Currency;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicReference;

@Singleton
public class MoneyContextProvider implements MoneyContext, MoneyContextUpdater {

    private static final Logger LOGGER = LogManager.getLogger();

    private final AtomicReference<Locale> locale = new AtomicReference<>();

    private final AtomicReference<CurrencyUnit> currency = new AtomicReference<>();
    private final AtomicReference<String> currencyCode = new AtomicReference<>();
    private final AtomicReference<String> currencySymbol = new AtomicReference<>();

    private final AtomicReference<MoneyFormatter> formatter = new AtomicReference<>();

    public MoneyContextProvider() {
        Country defaultCountry = Country.fromLocale(LocaleUtil.getDefaultLocale());
        if (defaultCountry != null) {
            updateCountry(defaultCountry);
        } else {
            this.locale.set(LocaleUtil.getDefaultLocale());
            this.currency.set(Monetary.getCurrency(this.locale.get()));
            this.currencyCode.set(this.currency.get().getCurrencyCode());
            this.currencySymbol.set(Currency.getInstance(currency.get().getCurrencyCode()).getSymbol());
            this.formatter.set(new DefaultMoneyFormatter(this.locale.get(), this.currencySymbol.get()));
        }
    }

    @Override
    public void updateCountry(Country country) {
        this.locale.set(country.locale());
        this.currency.set(country.currency());
        this.currencyCode.set(country.currencyCode());
        this.currencySymbol.set(country.currencySymbol());
        this.formatter.set(new DefaultMoneyFormatter(this.locale.get(), this.currencySymbol.get()));
    }

    @Override
    public Locale getLocale() {
        return locale.get();
    }

    @Override
    public CurrencyUnit getCurrency() {
        return currency.get();
    }

    @Override
    public String getCurrencyCode() {
        return currencyCode.get();
    }

    @Override
    public String getCurrencySymbol() {
        return currencySymbol.get();
    }

    @Override
    public MoneyFormatter getFormatter() {
        return formatter.get();
    }

}
