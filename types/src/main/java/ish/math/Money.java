/*
 * Copyright ish group pty ltd 2025.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.math;

import ish.math.format.PlainMoneyDecimalFormatter;
import ish.oncourse.API;
import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import javax.money.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Objects;

/**
 * Money is a class which handles all the common features needed for handling currency.
 * <p>
 * Most importantly, this class properly handles rounding at all times.
 *
 */
// деление на 0 не обрабывается
// получение остатка от 0 и 1 не учитвается
@API
public class Money implements MonetaryAmount, Comparable<MonetaryAmount>, Serializable {

	private static final long serialVersionUID = 1L;

	private static volatile MoneyContext context;
	private final MonetaryAmount money;

	@API
	@Deprecated
	public Money(String amount) {
		this.money = of(amount).money;
	}

	@API
	@Deprecated
	public Money(Integer majorAmountUnit, Integer fractionalAmountUnit) {
		BigDecimal amount = BigDecimal.valueOf(majorAmountUnit)
				.add(BigDecimal.valueOf(fractionalAmountUnit, context.getCurrency().getDefaultFractionDigits()));
		this.money = of(amount).money;
	}

	@API
	@Deprecated
	public Money(Number amount) {
		this.money = org.javamoney.moneta.Money.of(
				Objects.requireNonNullElse(amount, 0),
				context.getCurrency(),
				MonetaryContextBuilder.of()
						.setMaxScale(context.getCurrency().getDefaultFractionDigits())
						.set(context.DEFAULT_ROUND)
						.build()
		);
	}

	private Money(Number amount, CurrencyUnit currency, MonetaryContext context) {
		this.money = org.javamoney.moneta.Money.of(
				Objects.requireNonNullElse(amount, 0),
				currency,
				context
		);
	}

	/**
	 * Private constructor to enforce immutability.
	 * Use only if the provided amount is already immutable.
	 */
	private Money(MonetaryAmount amount) {
		this.money = Objects.requireNonNullElse(amount, new Money(0));
	}

	@API
	public static Money of(@Nullable MonetaryAmount amount) {
		return new Money(Objects.requireNonNullElse(amount, Money.ZERO()));
	}

	@API
	public static Money of(@Nullable Number amount) {
		return new Money(Objects.requireNonNullElse(amount, 0));
	}

	@API
	public static Money of(@Nullable String amount) {
		return new Money(StringUtils.isBlank(amount) ? BigDecimal.ZERO : new BigDecimal(amount));
	}

	@API
	public static Money of(@NonNull Integer majorAmountUnit, @NonNull Integer fractionalAmountUnit) {
		return new Money(majorAmountUnit, fractionalAmountUnit);
	}

	@API
	protected static Money of(@NonNull Number amount, @NonNull CurrencyUnit currency, @NonNull MonetaryContext context) {
		return new Money(amount, currency, context);
	}

	@Override
	public MonetaryContext getContext() {
		return money.getContext();
	}

	@Override
	public MonetaryAmountFactory<Money> getFactory() {
		return new MoneyAmountFactory().setAmount(this);
	}

	@Override
	@API
	public Money add(@Nullable MonetaryAmount amount) {
		return Objects.isNull(amount) ? of(this) : of(money.add(amount));
	}

	@API
	public Money add(@Nullable Number amount) {
		return Objects.isNull(amount) ? of(this) : add(of(amount));
	}

	@Override
	@API
	public Money subtract(@Nullable MonetaryAmount amount) {
		return Objects.isNull(amount) ? of(this) : of(money.subtract(amount));
	}

	@API
	public Money subtract(@Nullable Number amount) {
		return Objects.isNull(amount) ? of(this) : subtract(of(amount));
	}

	@Override
	@API
	public Money multiply(long l) {
		return of(money.multiply(l));
	}

	@Override
	@API
	public Money multiply(double v) {
		return of(money.multiply(v));
	}

	@Override
	@API
	public Money multiply(@Nullable Number amount) {
		return Objects.isNull(amount) ? of(this) : of(money.multiply(amount));
	}

	@API
	public Money multiply(@Nullable MonetaryAmount amount) {
		return Objects.isNull(amount) ? of(this) : multiply(amount.getNumber());
	}

	@Override
	@API
	public Money divide(long l) {
		return of(money.divide(l));
	}

	@Override
	@API
	public Money divide(double v) {
		return of(money.divide(v));
	}

	@Override
	@API
	public Money divide(@Nullable Number amount) {
		return divide(amount, false);
	}

	@API
	public Money divide(@Nullable Number amount, boolean roundingUp) {
		if (roundingUp) {
			MonetaryContext monetaryContext = MonetaryContextBuilder.of(money.getContext())
					.setMaxScale(Math.min(256, context.getCurrency().getDefaultFractionDigits() * 4))
					.build();

			return of(money.getFactory()
					.setCurrency(context.getCurrency())
					.setContext(monetaryContext)
					.setNumber(money.getNumber())
					.create()
					.divide(amount)
					.with(Monetary.getRounding(RoundingQueryBuilder.of().setScale(0).set(RoundingMode.UP).build()))
			);
		}
		return of(money.divide(amount));
	}

	@API
	public Money divide(@Nullable MonetaryAmount amount) {
		return divide(amount, false);
	}

	@API
	public Money divide(@Nullable MonetaryAmount amount, boolean roundingUp) {
		return Objects.isNull(amount) ? of(this) : divide(amount.getNumber(), roundingUp);
	}

	@Override
	public boolean isGreaterThan(@Nullable MonetaryAmount amount) {
		return Objects.isNull(amount) || money.isGreaterThan(amount);
	}

	@Override
	public boolean isGreaterThanOrEqualTo(@Nullable MonetaryAmount amount) {
		return Objects.isNull(amount) || money.isGreaterThanOrEqualTo(amount);
	}

	@Override
	public boolean isLessThan(@Nullable MonetaryAmount amount) {
		return !Objects.isNull(amount) && money.isLessThan(amount);
	}

	@Override
	public boolean isLessThanOrEqualTo(@Nullable MonetaryAmount amount) {
		return !Objects.isNull(amount) && money.isLessThanOrEqualTo(amount);
	}

	@Override
	public boolean isEqualTo(@Nullable MonetaryAmount amount) {
		return !Objects.isNull(amount) && money.isEqualTo(amount);
	}

	@Override
	public int signum() {
		return money.signum();
	}

	@Override
	public Money remainder(long l) {
		return of(money.remainder(l));
	}

	@Override
	public Money remainder(double v) {
		return of(money.remainder(v));
	}

	@Override
	public Money remainder(@Nullable Number amount) {
		return Objects.isNull(amount) ? of(this) : of(money.remainder(amount));
	}

	@Override
	public Money[] divideAndRemainder(long l) {
		return Arrays.stream(money.divideAndRemainder(l)).map(Money::of).toArray(Money[]::new);
	}

	@Override
	public Money[] divideAndRemainder(double v) {
		return Arrays.stream(money.divideAndRemainder(v)).map(Money::of).toArray(Money[]::new);
	}

	@Override
	public Money[] divideAndRemainder(@Nullable Number amount) {
		return Objects.isNull(amount) ?
				new Money[]{ this } :
				Arrays.stream(money.divideAndRemainder(amount)).map(Money::of).toArray(Money[]::new);
	}

	@Override
	public Money divideToIntegralValue(long l) {
		return of(money.divideToIntegralValue(l));
	}

	@Override
	public Money divideToIntegralValue(double v) {
		return of(money.divideToIntegralValue(v));
	}

	@Override
	public Money divideToIntegralValue(@Nullable Number amount) {
		return Objects.isNull(amount) ? of(this) : of(money.divideToIntegralValue(amount));
	}

	@Override
	public Money scaleByPowerOfTen(int i) {
		return of(money.scaleByPowerOfTen(i));
	}

	@Override
	public Money abs() {
		return of(money.abs());
	}

	@API
	public Money min(@Nullable MonetaryAmount amount) {
		return Objects.isNull(amount) || isLessThan(amount) ? of(this) : of(amount);
	}

	@API
	public Money max(@Nullable MonetaryAmount amount) {
		return Objects.isNull(amount) || !isLessThan(amount) ? of(this) : of(amount);
	}

	@Override
	public Money negate() {
		return of(money.negate());
	}

	@Override
	public Money plus() {
		return of(money.plus());
	}

	@Override
	public Money stripTrailingZeros() {
		return of(money.stripTrailingZeros());
	}

	public Money round(MoneyRounding roundType) {
		switch (roundType) {
			case ROUNDING_10C:
				return of(money.with(Monetary.getRounding(
						RoundingQueryBuilder.of()
								.setScale(1)
								.set(RoundingMode.HALF_UP)
								.build()))
				);
			case ROUNDING_50C:
				double cents = money.remainder(1).getNumber().doubleValue(); // Получаем дробную часть (центы)
				MonetaryAmount roundedAmount = money.with(Monetary.getRounding(
						RoundingQueryBuilder.of()
								.setScale(0)
								.set(RoundingMode.DOWN)
								.build())
				);

				if (cents == 0.0 || cents == 0.50 || cents == -0.50) {
					return of(money);
				}
				if (cents > -0.25 && cents < 0.25) {
					return of(roundedAmount); // Округление вниз
				}
				if (cents >= 0.25 && cents < 0.75) {
					MonetaryAmount half = money.getFactory().setNumber(0.5)
							.setContext(money.getContext())
							.setCurrency(money.getCurrency()).create();

					return of(roundedAmount.add(half)); // Добавляем 0.5
				}
				if (cents <= -0.25 && cents > -0.75) {
					MonetaryAmount half = money.getFactory().setNumber(0.5)
							.setContext(money.getContext())
							.setCurrency(money.getCurrency()).create();

					return of(roundedAmount.subtract(half)); // Вычитаем 0.5
				}
				// cents >= 0.75 || cents <= -0.75
				return of(money.with(Monetary.getRounding(
						RoundingQueryBuilder.of()
								.setScale(0)
								.set(RoundingMode.UP)
								.build()))
				);

			case ROUNDING_1D:
				return of(money.with(Monetary.getRounding(
						RoundingQueryBuilder.of()
								.setScale(0)
								.set(RoundingMode.HALF_UP)
								.build()))
				);
			default:
				return of(money);
		}
	}


	@Override
	public CurrencyUnit getCurrency() {
		return money.getCurrency();
	}

	@Override
	public NumberValue getNumber() {
		return money.getNumber();
	}

	@API
	public BigDecimal toBigDecimal() {
		return getNumber().numberValue(BigDecimal.class);
	}

	public Double toDoubleValue() {
		return getNumber().doubleValue();
	}

	public Float toFloatValue() {
		return getNumber().floatValue();
	}

	public Integer toIntegerValue() {
		return getNumber().intValue();
	}

	public Long toLongValue() {
		return getNumber().longValue();
	}

	public Integer getFractional() {
		MonetaryAmount minorUnit = money.remainder(1); // 10.99 -> 0.99
		return minorUnit.multiply(Math.pow(10, minorUnit.getNumber().getScale())).getNumber().intValue(); // 0.99 -> 99.0
	}

	@Override
	public String toString() {
		return context.getFormatter().format(this);
	}

	/**
	 * @return a string representation skipping currency sign
	 * $25 -> "25.00"
	 */
	@API
	public String toPlainString() {
		return new PlainMoneyDecimalFormatter(context.getLocale()).format(this);
	}

	@Override
	public int compareTo(@NonNull MonetaryAmount o) {
		return money.compareTo(o);
	}

	@Override
	public int hashCode() {
		return money.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		} else if (!(obj instanceof Money)) {
			return false;
		} else {
			Money other = (Money) obj;
			return this.getCurrency().equals(other.getCurrency()) && this.money.compareTo(other.money) == 0;
		}
	}

	public static Money ZERO() {
		return of(BigDecimal.ZERO);
	}

	public static Money ONE() {
		return of(BigDecimal.ONE);
	}

	public synchronized static void setContext(MoneyContext context) {
		Money.context = context;
	}
}
