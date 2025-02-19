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
import org.javamoney.moneta.spi.AbstractAmountFactory;

import javax.money.*;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Currency;
import java.util.Locale;

public class MoneyAmountFactory extends AbstractAmountFactory<Money> {

    static final MonetaryContext DEFAULT_CONTEXT = MonetaryContextBuilder.of(Money.class)
            .set(64)
            .setMaxScale(63)
            .set(RoundingMode.HALF_UP)
            .build();

    static final MonetaryContext MAX_CONTEXT = MonetaryContextBuilder.of(Money.class)
            .setPrecision(0)
            .setMaxScale(-1)
            .set(RoundingMode.HALF_UP)
            .build();

    static final MoneyContext DEFAULT_MONEY_CONTEXT = new MoneyContext() {
        @Override
        public CurrencyUnit getCurrency() {
            return Monetary.getCurrency(Locale.getDefault());
        }

        @Override
        public String getCurrencyCode() {
            return getCurrency().getCurrencyCode();
        }

        @Override
        public String getCurrencySymbol() {
            return Currency.getInstance(getCurrencyCode()).getSymbol();
        }

        @Override
        public Locale getLocale() {
            return Locale.getDefault();
        }

        @Override
        public MoneyFormatter getFormatter() {
            return new DefaultMoneyFormatter(getLocale(), getCurrencySymbol());
        }
    };

    private MoneyContext moneyContext = DEFAULT_MONEY_CONTEXT;

    @Override
    protected Money create(Number number, CurrencyUnit currency, MonetaryContext monetaryContext) {
        return Money.of(number, moneyContext);
    }

    public MonetaryAmountFactory<Money> setMoneyContext(MoneyContext moneyContext) {
        this.moneyContext = moneyContext;
        return this;
    }

    @Override
    public MonetaryAmountFactory<Money> setCurrency(CurrencyUnit currency) {
        var country = Arrays.stream(Country.values()).filter(c -> c.currency().equals(currency)).findFirst();
        if (country.isEmpty()) {
            throw new IllegalArgumentException("There aren't system Country that supports currency: " + currency);
        }
        setMoneyContext(buildMoneyContext(country.get()));
        super.setCurrency(country.get().currency());
        return this;
    }

    @Override
    public MonetaryAmountFactory<Money> setCurrency(String currencyCode) {
        var country = Arrays.stream(Country.values()).filter(c -> c.currency().getCurrencyCode().equals(currencyCode)).findFirst();
        if (country.isEmpty()) {
            throw new IllegalArgumentException("There aren't country that supports currency code: " + currencyCode);
        }
        setMoneyContext(buildMoneyContext(country.get()));
        super.setCurrency(country.get().currency());
        return this;
    }

    @Override
    public NumberValue getMaxNumber() {
        return null;
    }

    @Override
    public NumberValue getMinNumber() {
        return null;
    }

    @Override
    public Class<? extends MonetaryAmount> getAmountType() {
        return Money.class;
    }

    @Override
    protected MonetaryContext loadDefaultMonetaryContext() {
        return DEFAULT_CONTEXT;
    }

    @Override
    protected MonetaryContext loadMaxMonetaryContext() {
        return MAX_CONTEXT;
    }

    public static MoneyContext buildMoneyContext(Country country) {
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
}
