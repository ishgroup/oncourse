/*
 * Copyright ish group pty ltd 2025.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.math;

import ish.math.context.MoneyContext;
import ish.math.format.DefaultMoneyFormatter;
import ish.math.format.MoneyFormatter;
import org.springframework.lang.Nullable;

import javax.money.CurrencyUnit;
import javax.money.Monetary;
import java.util.Currency;
import java.util.Locale;
import java.util.Objects;

/**
 * Factory to creating instances of {@link MoneyContext}.
 */
public class MoneyContextFactory {

    public static final MoneyContext DEFAULT_MONEY_CONTEXT = new DefaultMoneyContext();

    public static MoneyContext create(Country country) {
        return new MoneyContext() {
            @Override
            public CurrencyUnit getCurrency() {
                return country.currency();
            }

            @Override
            public String getCurrencyCode() {
                return country.currencyCode();
            }

            @Override
            public String getCurrencySymbol() {
                return country.currencySymbol();
            }

            @Override
            public Locale getLocale() {
                return country.locale();
            }

            @Override
            public MoneyFormatter getFormatter() {
                return new DefaultMoneyFormatter(getLocale(), getCurrencySymbol());
            }
        };
    }

    private static class DefaultMoneyContext implements MoneyContext {

        @Nullable
        private Country country;

        public DefaultMoneyContext() {
            this.country = Country.fromLocale(LocaleUtil.getDefaultLocale());
        }

        @Override
        public CurrencyUnit getCurrency() {
            return !Objects.isNull(country) ? country.currency() : Monetary.getCurrency(getLocale());
        }

        @Override
        public String getCurrencyCode() {
            return !Objects.isNull(country) ? country.currencyCode() : getCurrency().getCurrencyCode();
        }

        @Override
        public String getCurrencySymbol() {
            return !Objects.isNull(country) ? country.currencySymbol() : Currency.getInstance(getCurrencyCode()).getSymbol();
        }

        @Override
        public Locale getLocale() {
            return !Objects.isNull(country) ? country.locale() : LocaleUtil.getDefaultLocale();
        }

        @Override
        public MoneyFormatter getFormatter() {
            return new DefaultMoneyFormatter(getLocale(), getCurrencySymbol());
        }
    }
}
