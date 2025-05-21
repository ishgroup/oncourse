/*
 * Copyright ish group pty ltd 2025.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.math;

import ish.math.context.MoneyContext;
import ish.math.format.PlainMoneyDecimalFormatter;
import ish.oncourse.API;
import org.apache.commons.lang3.StringUtils;
import org.javamoney.moneta.spi.MoneyUtils;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import javax.money.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * The {@code Money} class provides a convenient way to work with monetary values,
 * ensuring accuracy and consistency in financial calculations.
 * <p>
 * This class supports standard ISO 4217 currency codes and handles rounding rules appropriately,
 * making it suitable for various financial applications.
 * </p>
 *
 * <h2>Key Features</h2>
 * <ul>
 *     <li>Automatically sets up the monetary currency according to your settings in the Financial section.</li>
 *     <li>Ensures accurate rounding of monetary values.</li>
 *     <li>Provides easy-to-use arithmetic operations.</li>
 * </ul>
 *
 * <h2>Creating a Money Instance</h2>
 * <p>
 * To create a new monetary value, we recommend using one of the following methods:
 * <ul>
 *     <li>{@code of(Number num)}</li>
 *     <li>{@code of(String str)}</li>
 *     <li>{@code of(Money money)}</li>
 * </ul>
 * </p>
 * <p>
 * Example:
 * </p>
 * <pre>
 *     Money amount = Money.of(100.50);          		// $100.50
 *     Money amountFromString = Money.of("75.25"); 		// $75.25
 *     Money copy = Money.of(amount);              		// $100.50
 * </pre>
 *
 * <h2>Performing Calculations</h2>
 * <p>
 * You can perform common mathematical operations such as addition, subtraction,
 * multiplication, and division:
 * </p>
 * <pre>
 *     Money price = Money.of(50);
 *     Money tax = Money.of(5.75);
 *
 *     Money total = price.add(tax);          // $55.75
 *     Money discount = total.multiply(0.9);  // $50.18 - Apply 10% discount
 * </pre>
 *
 * <h2>Money Behavior</h2>
 * <p>
 * Note that the {@link Money} type is immutable - a new instance of {@link Money} type is created after every operation.
 * To compare values, use methods:
 * <ul>
 *     <li>{@code  isEqualTo({@link Money amount})}</li>
 *     <li>{@code isGreaterThan(@link Money amount)}}</li>
 *     <li>{@code isLessThan(@link Money amount)}</li>
 *     <li>{@code isGreaterThanOrEqualTo(@link Money amount)}</li>
 *     <li>{@code isLessThanOrEqualTo(@link Money amount)}</li>
 * </ul>
 * </p>
 * <p>
 * If you want to obtain boolean values commonly used in conditions, we recommend using predefined constants:
 * </p>
 * <pre>
 *     Money zero = Money.ZERO; // the same as Money.of(BigDecimal.ZERO);
 *     Monet one = Money.ONE;  // the same as  Money.of(BigDecimal.ONE);
 * </pre>
 * <p>
 *     {@link Money} instance currrentcy it is automatically synchronized with the Angel Financial section settings.
 * </p>
 *
 * <h2>Best Practices</h2>
 * <ul>
 *     <li>Use {@code Money} for all financial operations instead of raw {@code BigDecimal} to avoid rounding problems.</li>
 *     <li>Use predefined arithmetic methods ({@code add}, {@code subtract}, {@code multiply}, {@code divide}) for accurate calculations.</li>
 * </ul>
 */
@API
final public class Money implements MonetaryAmount, Comparable<MonetaryAmount>, Serializable {

	private static final long serialVersionUID = 1L;

	private final MoneyManager contextManager = new MoneyManager();
	private final BigDecimal number;

	public static Money ZERO = Money.of(BigDecimal.ZERO);
	public static Money ONE = Money.of(BigDecimal.ONE);

	/**
	 * Constructs a new {@code Money} instance. If {@code null} is passed as value,
	 * a {@code Money.ZERO} amount will be created.
	 *
	 * @param amount the value to create the {@code Money} instance.
	 *               If {@code null}, {@code Money.ZERO} will be used.
	 */
	@API
	public static Money of(@Nullable Number amount) {
		return new Money(amount);
	}

	public static Money of(@Nullable Number amount, MoneyContext customContext) {
		return new Money(amount, customContext);
	}

	/**
	 * Constructs a new {@code Money} instance. If {@code null} or an empty string is passed,
	 * a {@code Money.ZERO} amount will be created.
	 *
	 * @param amount the value to create the {@code Money} instance.
	 *               If {@code null} or empty, {@code Money.ZERO} will be used.
	 */
	@API
	public static Money of(@Nullable String amount) {
		return new Money(Converter.convert(amount));
	}

	public static Money of(@Nullable String amount, MoneyContext customContext) {
		return new Money(Converter.convert(amount), customContext);
	}

	/**
	 * Constructs a new {@code Money} instance as a copy of the provided {@code MonetaryAmount}.
	 * <p>
	 *  <code>MonetaryAmount</code> represent the family of Money types.
	 *  However, for most users, the {@code Money} type will suffice for all financial operations.
	 * </p>
	 * <p>
	 * If the provided {@code MonetaryAmount} is {@code null}, a {@code Money.of(0.00)} will be created.
	 * This method ensures that any monetary value is accurately represented as a {@code Money} instance.
	 * </p>
	 *
	 * @param amount the {@code MonetaryAmount} to create new {@code Money} instance.
	 *               If {@code null}, a {@code  Money.of(0.00)} instance will be returned.
	 * @return a new {@code Money} instance that representing the specified {@code MonetaryAmount},
	 *         or {@code  Money.of(0.00)} if the provided amount is {@code null}.
	 */
	@API
	public static Money of(@Nullable MonetaryAmount amount) {
		return new Money(Converter.convert(amount));
	}


	public static Money of(@Nullable MonetaryAmount amount, MoneyContext customContext) {
		return new Money(Converter.convert(amount), customContext);
	}

	/**
	 * Constructs a new {@code Money} instance using the specified major and fractional
	 * amount units. The first argument represents the whole amount (like dollar), while the second
	 * represents the fractional part(like cent).
	 * <p>
	 *     Example:
	 *     <pre>
	 *         Money value = Money.of(5, 25) <=> $5.25
	 *         Money value = Money.of(55, 120) <=> $56.20
	 *         Money value = Money.of(2, -50) <=> $1.50
	 *         Money value = Money.of(2, -250) <=> -$1.50
	 *     </pre>
	 * </p>
	 *
	 * @param majorAmountUnit the whole number value of the monetary amount.
	 * @param fractionalAmountUnit the fractional value of the monetary amount.
	 *                                If it exceeds 99, the excess will be converted and added into the major amount unit.
	 *                             Negative values are also supported and will be substracted from the major amount unit.
	 */
	@API
	public static Money of(@NonNull Integer majorAmountUnit, @NonNull Integer fractionalAmountUnit) {
		return new Money(Converter.convert(majorAmountUnit, fractionalAmountUnit, MoneyManager.getSystemContext().getCurrency()));
	}

	public static Money of(@NonNull Integer majorAmountUnit, @NonNull Integer fractionalAmountUnit, MoneyContext customContext) {
		return new Money(Converter.convert(majorAmountUnit, fractionalAmountUnit, customContext.getCurrency()), customContext);
	}

	/**
	 * Creates a new {@code Money} instance from the given {@code Number} amount.
	 * <p>
	 * If the provided amount is {@code null}, this method returns {@code null}.
	 * Otherwise, it delegates to {@link Money#of(Number)} to create the {@code Money} instance.
	 * </p>
	 *
	 * <p>Example usage:</p>
	 * <pre>
	 *     Money value1 = Money.exactOf(10.75); // $10.75
	 *     Money value2 = Money.exactOf(null);  // null
	 * </pre>
	 *
	 * @param amount the numeric amount to convert into a {@code Money} instance.
	 *               Can be {@code null}, in which case {@code null} is returned.
	 * @return a new {@code Money} instance representing the given amount, or {@code null} if the input is {@code null}.
	 */
	public static Money exactOf(@Nullable Number amount) {
		return amount == null ? null : Money.of(amount);
	}

	private Money(Number amount, MoneyContext customContext) {
		contextManager.setUpCustomContext(customContext);
		this.number = MoneyUtils.getBigDecimal(Objects.requireNonNullElse(amount, BigDecimal.ZERO), getContext());
	}

	/**
	 * Constructs a new {@code Money} instance. If {@code null} is passed as value,
	 * a {@code Money.ZERO} amount will be created.
	 *
	 * @param amount the value to create the {@code Money} instance.
	 *               If {@code null}, {@code Money.ZERO} will be used.
	 *
	 * @deprecated This constructor is deprecated. Use {@link #of(Number)} instead.
	 */
	@API
	@Deprecated
	public Money(Number amount) {
		this(amount, null);
	}


	/**
	 * Constructs a new {@code Money} instance. If {@code null} or an empty string is passed,
	 * a {@code Money.ZERO} amount will be created.
	 *
	 * @param amount the value to create the {@code Money} instance.
	 *               If {@code null} or empty, {@code Money.ZERO} will be used.
	 *
	 * @deprecated This constructor is deprecated. Use {@link #of(String)} instead.
	 */
	@API
	@Deprecated
	public Money(String amount) {
		this(Converter.convert(amount));
	}

	/**
	 * Constructs a new {@code Money} instance using the specified major and fractional
	 * amount units. The first argument represents the whole amount (like dollar), while the second
	 * represents the fractional part(like cent).
	 * <p>
	 *     Example:
	 *     <pre>
	 *         Money value = Money.of(5, 25) <=> $5.25
	 *         Money value = Money.of(55, 120) <=> $56.20
	 *         Money value = Money.of(2, -50) <=> $1.50
	 *         Money value = Money.of(2, -250) <=> -$1.50
	 *     </pre>
	 * </p>
	 *
	 * @param majorAmountUnit the whole number value of the monetary amount.
	 * @param fractionalAmountUnit the fractional value of the monetary amount.
	 *                                If it exceeds 99, the excess will be converted and added into the major amount unit.
	 *                             Negative values are also supported and will be substracted from the major amount unit.
	 *
     *  @deprecated This constructor is deprecated. Use {@link #of(Integer, Integer)} instead.
	 */
	@API
	@Deprecated
	public Money(Integer majorAmountUnit, Integer fractionalAmountUnit) {
		this(Converter.convert(majorAmountUnit, fractionalAmountUnit, MoneyManager.getSystemContext().getCurrency()));
	}

	@Override
	public MonetaryContext getContext() {
		return MonetaryContextBuilder.of()
				.setMaxScale(Math.min(256, getCurrency().getDefaultFractionDigits() * 2))
				.set(MoneyContext.DEFAULT_ROUND)
				.build();
	}

	public MoneyContext getCurrencyContext() {
		return contextManager.getContext();
	}

	@Override
	public CurrencyUnit getCurrency() {
		return contextManager.getContext().getCurrency();
	}

	@Override
	public MonetaryAmountFactory<Money> getFactory() {
		return new MoneyFactory().setMoneyContext(contextManager.getContext()).setAmount(this);
	}

	/**
	 * Adds the specified amount and returning a new {@code Money} object.
	 * <p>
	 *  <code>MonetaryAmount</code> represent the family of Money types.
	 *  However, for most users, the {@code Money} type will suffice for all financial operations.
	 *  <p> Example: </p>
	 *
	 *     <pre>
	 *         Money sum = Money.ONE.add(Money.of(4));	             // $5.00
	 *         Money sum = Money.ONE.add(Money.of(2.442));	   // $3.44
	 *         Money sum = Money.ONE.add(Money.of(2.445));	   // $3.45
	 *     </pre>
	 * </p>
	 *
	 * <p>If the provided amount is {@code null}, this method will return the current instance.</p>
	 *
	 * @param amount the value to be added to this {@code Money} object. If {@code null}, the current instance will be returned.
	 * @return a new {@code Money} object representing the sum.
	 */
	@API
	@Override
	public Money add(@Nullable MonetaryAmount amount) {
		return Objects.isNull(amount) ? this : toInstance(toMoneta().add(amount));
	}

	/**
	 * Adds the specified amount and returning a new {@code Money} object.
	 *
	 * <p>If the provided amount is {@code null}, this method will return the current instance.</p>
	 *
	 *  <p> Example: </p>
	 *
	 *     <pre>
	 *         Money sum = Money.ONE.add(4);	       // $5.00
	 *         Money sum = Money.ONE.add(2.442);    // $3.44
	 *         Money sum = Money.ONE.add(2.445);    // $3.45
	 *     </pre>
	 * </p>
	 *
	 * @param amount the value to be added to this {@code Money} object. If {@code null}, the current instance will be returned.
	 * @return a new {@code Money} object representing the sum.
	 */
	@API
	public Money add(@Nullable Number amount) {
		return Objects.isNull(amount) ? this : add(toInstance(amount));
	}

	/**
	 * Subtract the specified amount and returning a new {@code Money} object.
	 * <p>
	 *  <code>MonetaryAmount</code> represent the family of Money types.
	 *  However, for most users, the {@code Money} type will suffice for all financial operations.
	 *  <p> Example: </p>
	 *
	 *     <pre>
	 *         Money sum = Money.of(4).subtract(Money.ONE);           // $3.00
	 *         Money sum = Money.of(4).subtract(Money.of("1.44"));    // $2.56
	 *     </pre>
	 * </p>
	 *
	 * <p>If the provided amount is {@code null}, this method will return the current instance.</p>
	 *
	 * @param amount the value to be subtracted from this {@code Money} object. If {@code null}, the current instance will be returned.
	 * @return a new {@code Money} object representing subtraction result.
	 */
	@Override
	@API
	public Money subtract(@Nullable MonetaryAmount amount) {
		return Objects.isNull(amount) ? this : toInstance(toMoneta().subtract(amount));
	}

	/**
	 * Subtract the specified amount and returning a new {@code Money} object.
	 *
	 * <p>If the provided amount is {@code null}, this method will return the current instance.</p>
	 *
	 *  <p> Example: </p>
	 *
	 *     <pre>
	 *         Money result = Money.of(4).subtract(1);           // $3.00
	 *         Money result = Money.of(4).subtract(1.44);        // $2.56
	 *     </pre>
	 * </p>
	 *
	 * @param amount the value to be subtracted from this {@code Money} object. If {@code null}, the current instance will be returned.
	 * @return a new {@code Money} object representing subtraction result.
	 */
	@API
	public Money subtract(@Nullable Number amount) {
		return Objects.isNull(amount) ? this : subtract(toInstance(amount));
	}

	@Override
	public Money multiply(long l) {
		return toInstance(toMoneta().multiply(l));
	}

	public Money multiply(int i) {
		return toInstance(toMoneta().multiply(i));
	}

	@Override
	public Money multiply(double v) {
		return toInstance(toMoneta().multiply(v));
	}

	/**
	 * Multiplies this {@code Money} instance by the specified amount and returning a new {@code Money} object.
	 *
	 * <p>If the provided amount is {@code null}, this method will return the current instance.</p>
	 * <p>To specify the amount as a particular type, you could use type qualifiers:</p>
	 * <ul>
	 *     <li>For long values, use {@code l} - {@code 1l}</li>
	 *     <li>For double values, use {@code d} - {@code 1.0d}</li>
	 * </ul>
	 *
	 * <p> Example: </p>
	 *     <pre>
	 *         Money result = Money.of(5).multiply(2l);  		   // $10.00
	 *         Money result = Money.of(2.445).multiply(Money.of("1"))         // $2.45
	 *     </pre>
	 * </p>
	 * @param amount the value to multiply with this {@code Money} object.
	 *               If {@code null}, the current instance will be returned.
	 * @return a new {@code Money} object representing multiplication result.
	 */
	@Override
	@API
	public Money multiply(@Nullable Number amount) {
		return Objects.isNull(amount) ? this : toInstance(toMoneta().multiply(amount));
	}

	/**
	 * Multiplies this {@code Money} instance by the specified amount and returning a new {@code Money} object.
	 * <p>
	 *  <code>MonetaryAmount</code> represent the family of Money types.
	 *  However, for most users, the {@code Money} type will suffice for all financial operations.
	 * <p> Example: </p>
	 *     <pre>
	 *         Money result = Money.of(5).multiply(2l);  		   // $10.00
	 *         Money result = Money.of(2, 445).multiply(Money.of("1"))        // $2.45
	 *     </pre>
	 * </p>
	 * </p>
	 *
	 * @param amount the value to multiply with this {@code Money} object.
	 *               If {@code null}, the current instance will be returned.
	 * @return a new {@code Money} object representing multiplication result.
	 */
	@API
	public Money multiply(@Nullable MonetaryAmount amount) {
		return Objects.isNull(amount) ? this : multiply(amount.getNumber());
	}

	@Override
	public Money divide(long l) {
		return divide(l, false);
	}

	public Money divide(int i) {
		return divide(i, false);
	}

	@Override
	public Money divide(double v) {
		return divide(v, false);
	}

	/**
	 * Divides this {@code Money} instance by the specified amount and returning a new {@code Money} object.
	 * <p>
	 *  <code>MonetaryAmount</code> represent the family of Money types.
	 *  However, for most users, the {@code Money} type will suffice for all financial operations.
	 *  <p> Example: </p>
	 *
	 *     <pre>
	 *         Money result = Money.of(5).divide(Money.of("2.4"));    // $2.08
	 *         Money result = Money.of(1.456).divide(Money.of(2))     // $0.73
	 *     </pre>
	 * </p>
	 *
	 * @param amount the value to multiply with this {@code Money} object.
	 *               If {@code null}, the current instance will be returned.
	 * @return a new {@code Money} object representing division result.
	 */
	@API
	public Money divide(@Nullable MonetaryAmount amount) {
		return divide(amount, false);
	}

	/**
	 * Divides this {@code Money} instance by the specified amount and returning a new {@code Money} object.
	 * <p>To specify the amount as a particular type, you could use type qualifiers:</p>
	 * <ul>
	 *     <li>For long values, use {@code l} - {@code 1l}</li>
	 *     <li>For double values, use {@code d} - {@code 1.0d}</li>
	 * </ul>
	 *
	 * @param amount the value to multiply with this {@code Money} object.
	 *               If {@code null}, the current instance will be returned.
	 * @return a new {@code Money} object representing division result.
	 */
	@Override
	@API
	public Money divide(@Nullable Number amount) {
		return divide(amount, false);
	}

	/**
	 * Divides this {@code Money} instance by the specified amount and returning a new {@code Money} object.
	 *  <p>
	 *      This method provides an option to round up the result if needed.
	 *      It is used for rounding any value with cents to a greater integer value.
	 *  </p>
	 *  <p>
	 *  <code>MonetaryAmount</code> represent the family of Money types.
	 *  However, for most users, the {@code Money} type will suffice for all financial operations.
	 *  <p> Example: </p>
	 *
	 *     <pre>
	 *         Money sum = Money.of(5).divide(Money.of("2.4"), true);    // $3.00
	 *         Money sum = Money.of(5).divide(Money.of("2.4"), false);    // $2.08
	 *     </pre>
	 * </p>
	 *
	 * @param amount     the divisor used to divide this {@code Money} object.
	 *                   If {@code null}, the current instance will be returned.
	 * @param roundingUp if {@code true}, the result will be rounded up.
	 * @return a new {@code Money} object representing division result.
	 * @throws ArithmeticException if the specified amount is zero.
	 */
	@API
	public Money divide(@Nullable MonetaryAmount amount, boolean roundingUp) {
		return Objects.isNull(amount) ? this : divide(amount.getNumber(), roundingUp);
	}

	/**
	 * Divides this {@code Money} instance by the specified amount and returning a new {@code Money} object.
	 *  <p>
	 *      This method provides an option to round up the result if needed.
	 *      It is used for rounding any value with cents to a greater integer value.
	 *  </p>
	 *  <p>
	 * <p>To specify the amount as a particular type, you could use type qualifiers:</p>
	 * <ul>
	 *     <li>For long values, use {@code l} - {@code 1l}</li>
	 *     <li>For double values, use {@code d} - {@code 1.0d}</li>
	 * </ul>
	 *  <p> Example: </p>
	 *
	 *     <pre>
	 *         Money sum = Money.of(5).divide(2.4d, true);    // $3.00
	 *         Money sum = Money.of(5).divide(2.4d, false);    // $2.08
	 *     </pre>
	 * </p>
	 *
	 * @param amount     the divisor used to divide this {@code Money} object.
	 *                   If {@code null}, the current instance will be returned.
	 * @param roundingUp if {@code true}, the result will be rounded up.
	 * @return a new {@code Money} object representing division result.
	 * @throws ArithmeticException if the specified amount is zero.
	 */
	@API
	public Money divide(@Nullable Number amount, boolean roundingUp) {
		if (Objects.isNull(amount)) return this;
		org.javamoney.moneta.Money result = org.javamoney.moneta.Money
				.of(number, getCurrency(),getContext().toBuilder().build())
				.divide(amount);

		if (roundingUp) {
			result = result.with(Monetary.getRounding(RoundingQueryBuilder.of().setScale(0).set(RoundingMode.UP).build()));
		}
		return toInstance(result);
	}

	/**
	 * Checks if this {@code Money} instance is greater than the specified {@code MonetaryAmount}.
	 *
	 * <p>
	 *     <code>MonetaryAmount</code> represent the family of Money types.
	 *     However, for most users, the {@code Money} type will suffice for all financial operations.
	 * </p>
	 *
	 * <p>If the provided amount is {@code null}, this method will return {@code true}.</p>
	 *
	 * @param amount the {@code MonetaryAmount} to compare with.
	 * @return {@code true} if this instance is greater than the specified amount, {@code false} otherwise.
	 */
	@API
	@Override
	public boolean isGreaterThan(@Nullable MonetaryAmount amount) {
		return Objects.isNull(amount) || compareTo(amount) > 0;
	}

	/**
	 * Checks if this {@code Money} instance is greater or equal to the specified {@code MonetaryAmount}.
	 *
	 * <p>
	 *     <code>MonetaryAmount</code> represent the family of Money types.
	 *     However, for most users, the {@code Money} type will suffice for all financial operations.
	 * </p>
	 *
	 * <p>If the provided amount is {@code null}, this method will return {@code true}.</p>
	 *
	 * @param amount the {@code MonetaryAmount} to compare with.
	 * @return {@code true} if this instance is greater or equal to the specified amount, {@code false} otherwise.
	 */
	@API
	@Override
	public boolean isGreaterThanOrEqualTo(@Nullable MonetaryAmount amount) {
		return Objects.isNull(amount) || compareTo(amount) >= 0;
	}

	/**
	 * Checks if this {@code Money} instance is less than the specified {@code MonetaryAmount}.
	 *
	 * <p>
	 *     <code>MonetaryAmount</code> represent the family of Money types.
	 *     However, for most users, the {@code Money} type will suffice for all financial operations.
	 * </p>
	 *
	 * <p>If the provided amount is {@code null}, this method will return {@code false}.</p>
	 *
	 * @param amount the {@code MonetaryAmount} to compare with.
	 * @return {@code true} if this instance is less than the specified amount, {@code false} otherwise.
	 */
	@API
	@Override
	public boolean isLessThan(@Nullable MonetaryAmount amount) {
		return !Objects.isNull(amount) && compareTo(amount) < 0;
	}

	/**
	 * Checks if this {@code Money} instance is less or equal to the specified {@code MonetaryAmount}.
	 *
	 * <p>
	 *     <code>MonetaryAmount</code> represent the family of Money types.
	 *     However, for most users, the {@code Money} type will suffice for all financial operations.
	 * </p>
	 *
	 * <p>If the provided amount is {@code null}, this method will return {@code false}.</p>
	 *
	 * @param amount the {@code MonetaryAmount} to compare with.
	 * @return {@code true} if this instance is less or equal to the specified amount, {@code false} otherwise.
	 */
	@API
	@Override
	public boolean isLessThanOrEqualTo(@Nullable MonetaryAmount amount) {
		return !Objects.isNull(amount) && compareTo(amount) <= 0;
	}

	/**
	 * Checks if this {@code Money} instance is equal to the specified {@code MonetaryAmount}.
	 *
	 * <p>
	 *     <code>MonetaryAmount</code> represent the family of Money types.
	 *     However, for most users, the {@code Money} type will suffice for all financial operations.
	 * </p>
	 *
	 * <p>If the provided amount is {@code null}, this method will return {@code false}.</p>
	 *
	 * @param amount the {@code MonetaryAmount} to compare with.
	 * @return {@code true} if this instance is equal to the specified amount, {@code false} otherwise.
	 */
	@API
	@Override
	public boolean isEqualTo(@Nullable MonetaryAmount amount) {
		return !Objects.isNull(amount) && compareTo(amount) == 0;
	}

	@Override
	public int signum() {
		return toMoneta().signum();
	}

	@Override
	public Money remainder(long l) {
		return toInstance(toMoneta().remainder(l));
	}

	@Override
	public Money remainder(double v) {
		return toInstance(toMoneta().remainder(v));
	}

	@Override
	public Money remainder(@Nullable Number amount) {
		return Objects.isNull(amount) ? this : toInstance(toMoneta().remainder(amount));
	}

	@Override
	public Money[] divideAndRemainder(long l) {
		// not use lambda and method reference because DSL compile everything as Groovy and Groovy doesn't support Java lambda and '::' syntax
		org.javamoney.moneta.Money[] divideAndRemainderData = toMoneta().divideAndRemainder(l);
		Money[] result = new Money[divideAndRemainderData.length];
		for (int i = 0; i < divideAndRemainderData.length; i++) {
			result[i] = toInstance(divideAndRemainderData[i]);
		}
		return result;
	}

	@Override
	public Money[] divideAndRemainder(double v) {
		// not use lambda and method reference because DSL compile everything as Groovy and Groovy doesn't support Java lambda and '::' syntax
		org.javamoney.moneta.Money[] divideAndRemainderData = toMoneta().divideAndRemainder(v);
		Money[] result = new Money[divideAndRemainderData.length];
		for (int i = 0; i < divideAndRemainderData.length; i++) {
			result[i] = toInstance(divideAndRemainderData[i]);
		}
		return result;
	}

	@Override
	public Money[] divideAndRemainder(@Nullable Number amount) {
		// not use lambda and method reference because DSL compile everything as Groovy and Groovy doesn't support Java lambda and '::' syntax
		org.javamoney.moneta.Money[] divideAndRemainderData = toMoneta().divideAndRemainder(amount);
		Money[] result = new Money[divideAndRemainderData.length];
		for (int i = 0; i < divideAndRemainderData.length; i++) {
			result[i] = toInstance(divideAndRemainderData[i]);
		}
		return result;
	}

	@Override
	public Money divideToIntegralValue(long l) {
		return toInstance(toMoneta().divideToIntegralValue(l));
	}

	@Override
	public Money divideToIntegralValue(double v) {
		return toInstance(toMoneta().divideToIntegralValue(v));
	}

	@Override
	public Money divideToIntegralValue(@Nullable Number amount) {
		return Objects.isNull(amount) ? this : toInstance(toMoneta().divideToIntegralValue(amount));
	}

	@Override
	public Money scaleByPowerOfTen(int i) {
		return toInstance(toMoneta().scaleByPowerOfTen(i));
	}

	/**
	 * Converts any negative monetary amount to its positive equivalent.
	 * If the current instance is positive or zero, it will be returned as is.</p>
	 *
	 * @return a new {@code Money} instance representing the absolute value of this instance.
	 */
	@API
	@Override
	public Money abs() {
		return toInstance(toMoneta().abs());
	}

	/**
	 * Returns the minimum of this {@code Money} instance and the specified {@code MonetaryAmount}.
	 * <p>
	 *     <code>MonetaryAmount</code> represent the family of Money types.
	 *     However, for most users, the {@code Money} type will suffice for all financial operations.
	 * </p>
	 *
	 * <p>If the provided amount is {@code null} or less than or equal to this instance,
	 * the current instance will be returned. Otherwise, the specified amount.</p>
	 *
	 * @return the maximum {@code Money} instance between this instance and the specified amount.
	 */
	@API
	public Money min(@Nullable MonetaryAmount amount) {
		return Objects.isNull(amount) || isLessThan(amount) ? this : toInstance(amount);
	}

	/**
	 * Returns the maximum of this {@code Money} instance and the specified {@code MonetaryAmount}.
	 * <p>
	 *     <code>MonetaryAmount</code> represent the family of Money types.
	 *     However, for most users, the {@code Money} type will suffice for all financial operations.
	 * </p>
	 *
	 * <p>If the provided amount is {@code null} or greater than or equal to this instance,
	 * the current instance will be returned. Otherwise, the specified amount.</p>
	 *
	 * @return the maximum {@code Money} instance between this instance and the specified amount.
	 */
	@API
	public Money max(@Nullable MonetaryAmount amount) {
		return Objects.isNull(amount) || !isLessThan(amount) ? this : toInstance(amount);
	}

	@Override
	public Money negate() {
		return toInstance(toMoneta().negate());
	}

	@Override
	public Money plus() {
		return toInstance(toMoneta().plus());
	}

	// Support for Groovy.sum() method
	public Money plus(Money money) {
		return this.add(money);
	}

	@Override
	public Money stripTrailingZeros() {
		return toInstance(toMoneta().stripTrailingZeros());
	}

	public Money round(MoneyRounding roundType) {
		switch (roundType) {
			case ROUNDING_10C:
				return toInstance(toMoneta().with(Monetary.getRounding(
						RoundingQueryBuilder.of().setScale(1).set(RoundingMode.HALF_UP).build()))
				);
			case ROUNDING_50C:
				double cents = toMoneta().remainder(1).getNumber().doubleValue(); // get minor part (coins, cents, etc.)
				MonetaryAmount roundedAmount = toMoneta().with(Monetary.getRounding(
						RoundingQueryBuilder.of().setScale(0).set(RoundingMode.DOWN).build())
				);

				if (cents == 0.0 || cents == 0.50 || cents == -0.50) {
					return toInstance(toMoneta());
				}
				if (cents > -0.25 && cents < 0.25) {
					return toInstance(roundedAmount); // Rounding down
				}
				if (cents >= 0.25 && cents < 0.75) {
					MonetaryAmount half = toMoneta().getFactory().setNumber(0.5)
							.setContext(toMoneta().getContext()).create();

					return toInstance(roundedAmount.add(half)); // Add 0.5
				}
				if (cents <= -0.25 && cents > -0.75) {
					MonetaryAmount half = toMoneta().getFactory().setNumber(0.5)
							.setContext(toMoneta().getContext()).create();

					return toInstance(roundedAmount.subtract(half)); // Subtract 0.5
				}
				// cents >= 0.75 || cents <= -0.75
				return toInstance(toMoneta().with(Monetary
						.getRounding(RoundingQueryBuilder.of().setScale(0).set(RoundingMode.UP).build()))
				);

			case ROUNDING_1D:
				// 5.4999999 -> 5.50 -> 6.00
				return toInstance(of(number.intValue(), getFractional()).with(Monetary
						.getRounding(RoundingQueryBuilder.of().setScale(0).set(RoundingMode.HALF_UP).build()))
				);
			default:
				return toInstance(toMoneta());
		}
	}

	@Override
	public NumberValue getNumber() {
		return toMoneta().getNumber();
	}

	/**
	 * Returns the monetary value as a {@link BigDecimal}.
	 */
	@API
	public BigDecimal toBigDecimal() {
		return round(number);
	}

	/**
	 * Returns the monetary value as a {@link Double}.
	 *
	 * @deprecated use {@link #toDouble()}
	 */
	@API
	@Deprecated
	public double doubleValue() {
		return getNumber().doubleValue();
	}

	/**
	 * Returns the monetary value as a {@link Double}.
	 */
	@API
	public Double toDouble() {
		return getNumber().doubleValue();
	}

	/**
	 * Returns the monetary value as a {@link Float}.
	 *
	 * @deprecated use {@link #toFloat()}
	 */
	@API
	@Deprecated
	public float floatValue() {
		return getNumber().floatValue();
	}

	/**
	 * Returns the monetary value as a {@link Float}.
	 */
	@API
	public float toFloat() {
		return getNumber().floatValue();
	}

	/**
	 * Returns the monetary value as a {@link Integer}.
	 *
	 * @deprecated use {@link #toInteger()}
	 */
	@API
	@Deprecated
	public int intValue() {
		return getNumber().intValue();
	}

	/**
	 * Returns the monetary value as a {@link Integer}.
	 */
	@API
	public Integer toInteger() {
		return getNumber().intValue();
	}

	/**
	 * Returns the monetary value as a {@link Long}.
	 *
	 * @deprecated use {@link #toLong()}
	 */
	@API
	@Deprecated
	public long longValue() {
		return getNumber().longValue();
	}

	/**
	 * Returns the monetary value as a {@link Long}.
	 */
	@API
	public long toLong() {
		return getNumber().longValue();
	}

	/**
	 * Retrieves the fractional part(cents, coins) of the monetary value.
	 *
	 * @return the fractional part of this {@code Money} instance as an integer.
	 *         For example, if the value is $10.99, this method will return 99.
	 */
	public Integer getFractional() {
		// round - for cases like 4.49999
		return round(number).remainder(BigDecimal.ONE).movePointRight(getCurrency().getDefaultFractionDigits()).intValue();
	}

	@Override
	public String toString() {
		return contextManager.getContext().getFormatter().format(this);
	}

	/**
	 * String representation of the monetary value, omitting the currency sign.
	 * <p>
	 * For example, a monetary amount of {@code $25.00} will be represented as {@code "25.00"}.
	 * </p>
	 * <p>
	 * This method is useful for scenarios where the currency sign is not needed,
	 * and only the numerical value is required. (reports for example)
	 * </p>
	 */
	@API
	public String toPlainString() {
		return new PlainMoneyDecimalFormatter(contextManager.getContext().getLocale()).format(this);
	}

	/**
	 * Compares this {@code Money} instance with another {@code MonetaryAmount}.
	 * This method ensures that values are rounded before comparison to avoid precision issues.
	 *
	 * <p>Example usage:</p>
	 * <pre>
	 *     Money value1 = Money.of(10, 50); // $10.50
	 *     Money value2 = Money.of(10, 75); // $10.75
	 *
	 *     int result = value1.compareTo(value2); // result < 0 (10.50 < 10.75)
	 *
	 *     Money value1 = Money.of(10.499); // $10.50
	 *     Money value2 = Money.of(10. 50); // $10.50
	 *
	 *     int result = value1.compareTo(value2); // result = 0
	 * </pre>
	 *
	 * @param o the {@code MonetaryAmount} to compare with.
	 * @return a negative integer, zero, or a positive integer as this amount
	 *         is less than, equal to, or greater than the specified amount.
	 * @throws NullPointerException if the given argument is null.
	 */
	@Override
	@API
	public int compareTo(@NonNull MonetaryAmount o) {
		return round(number).compareTo(round(o.getNumber().numberValue(BigDecimal.class)));
	}


	/**
	 * <p>
	 *    Compares this {@code Money} instance with a {@code Number}.
	 * </p>
	 *
	 * <p>Example usage:</p>
	 * <pre>
	 *     Money value = Money.of(10, 50); // $10.50
	 *     int result = value.compareTo(10.75); // result < 0 (10.50 < 10.75)
	 * </pre>
	 *
	 * @deprecated Use {@link #compareTo(MonetaryAmount)} instead.
	 *
	 * @param o the {@code Number} to compare with.
	 * @return a negative integer, zero, or a positive integer as this amount
	 *         is less than, equal to, or greater than the specified number.
	 */
	@Deprecated // need for scripts
	public int compareTo(@NonNull Number o) {
		if (number == null) return -1;
		return round(number).compareTo(round(BigDecimal.valueOf(o.doubleValue())));
	}

	@Override
	public int hashCode() {
		return Objects.hash(getCurrency(), number.stripTrailingZeros());
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		} else if (!(obj instanceof Money)) {
			return false;
		} else {
			Money other = (Money) obj;
			return getCurrency().equals(other.getCurrency()) && compareTo(other) == 0;
		}
	}

	private org.javamoney.moneta.Money toMoneta() {
		return org.javamoney.moneta.Money.of(number, getCurrency(), getContext());
	}

	private Money toInstance(MonetaryAmount money) {
		return contextManager.isCustomized() ? new Money(money.getNumber(), contextManager.getContext()) : new Money(money.getNumber());
	}

	private Money toInstance(Number number) {
		return contextManager.isCustomized() ? Money.of(number, contextManager.getContext()) : new Money(number);
	}

	private BigDecimal round(BigDecimal value) {
		RoundingMode mode = RoundingMode.HALF_UP;
		int scale = getCurrency().getDefaultFractionDigits();
		return value.setScale(scale, mode);
	}

	/**
	 * Utility class for operations with {@code Money} type.
	 */
	private static class Converter {

		/**
		 * Converts a {@code String} value to a {@code Number}.
		 * If the provided value is blank, it returns {@link BigDecimal#ZERO}.
		 *
		 * @param value the {@code String} representation of the monetary value.
		 * @return the corresponding {@code Number} representation of the monetary value.
		 */
		static Number convert(String value) {
			return StringUtils.isBlank(value) ? BigDecimal.ZERO : new BigDecimal(value);
		}

		/**
		 * Converts a {@code MonetaryAmount} to a {@code Number}.
		 * If the provided {@code MonetaryAmount} is {@code null}, it returns
		 * {@link BigDecimal#ZERO}.
		 *
		 * @param value the {@code MonetaryAmount} to convert.
		 * @return the corresponding {@code Number} representation of the monetary amount.
		 */
		static Number convert(MonetaryAmount value) {
			return Objects.isNull(value) ? BigDecimal.ZERO : value.getNumber();
		}

		/**
		 * Converts specified major and fractional amount units along with the currency
		 * to {@code Number}. The major amount unit represents the whole part of the
		 * monetary value, while the fractional unit represents the smaller units (e.g.,
		 * cents, pence).
		 *
		 * @param majorAmountUnit the whole number part of the monetary value.
		 * @param fractionalAmountUnit the fractional part of the monetary value, which
		 *                             should be within the range defined by the currency.
		 * @param currency the {@code CurrencyUnit} defining the monetary context.
		 * @return a {@code Number} representation of the total monetary value.
		 */
		static Number convert(Integer majorAmountUnit, Integer fractionalAmountUnit, CurrencyUnit currency) {
			return BigDecimal
					.valueOf(majorAmountUnit)
					.add(BigDecimal.valueOf(fractionalAmountUnit, currency.getDefaultFractionDigits()));
		}
	}

}
