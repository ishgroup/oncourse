/*
 * Copyright ish group pty ltd 2025.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.math;

import org.javamoney.moneta.spi.AbstractAmountFactory;

import javax.money.*;
import java.math.RoundingMode;

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


    @Override
    protected Money create(Number number, CurrencyUnit currency, MonetaryContext monetaryContext) {
        return Money.of(number, currency, MonetaryContext.from(monetaryContext, Money.class));
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
}
