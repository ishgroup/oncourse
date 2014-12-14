/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

package ish.math;

import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;

/**
 * Money is a class which handles all the common features needed for handling currency.
 * 
 * Most importantly, this class properly handles rounding at all times.
 * 
 * @PublicApi
 */
public class Money extends Number implements Comparable<Money> {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(Money.class);

	public static final RoundingMode DEFAULT_ROUND = RoundingMode.HALF_UP;
	public static final int DEFAULT_SCALE = 2;
	private static final int EXPANDED_SCALE = DEFAULT_SCALE * 4;
	public static final int CENTS_IN_DOLLAR = 10 ^ DEFAULT_SCALE;

	public static final Money ZERO = new Money(BigDecimal.ZERO);
	public static final Money ONE = new Money(BigDecimal.ONE);

	private BigDecimal decimalValue;

	/**
	 * Construct a new money instance. If null is passed, then a Money.ZERO amount will be created.
	 * 
	 * @param val value to create
	 */
	public Money(BigDecimal val) {
		if (val == null) {
			setValue(BigDecimal.ZERO);
		} else {
			setValue(val);
		}
	}

	/**
	 * Construct a new money instance. If null or empty string is passed, then a Money.ZERO amount will be created.
	 * 
	 * @param val value to create
	 * @PublicApi   
	 */
	public Money(String val) {
		if (val == null || val.length() == 0) {
			setValue(BigDecimal.ZERO);
		} else {
			setValue(new BigDecimal(val));
		}
	}

	/**
	 * Construct a new money instance, first argument is dollars, second is cents
	 * 
	 * @param dollars
	 * @param cents
	 * @PublicApi
	 */
	public Money(int dollars, int cents) {
		setValue(BigDecimal.valueOf(dollars).add(BigDecimal.valueOf(cents, DEFAULT_SCALE)));
	}

	/**
	 * returns sum
	 * 
	 * @param val to be added to this money object
	 * @return new money object
	 * @PublicApi
	 */
	public Money add(Money val) {
		if (val == null) {
			logger.info("Money.add() called with null argument, assuming zero value");
			return new Money(this.decimalValue);
		}
		return new Money(this.decimalValue.add(val.toBigDecimal()));
	}

	/**
	 * returns sum
	 * 
	 * @param val to be added to this money object
	 * @return new money object
	 * @PublicApi
	 */
	public Money add(BigDecimal val) {
		if (val == null) {
			logger.info("Money.add() called with null argument, assuming zero value");
			return new Money(this.decimalValue);
		}
		return new Money(this.decimalValue.add(val));
	}

	/**
	 * returns quotient, where object is dividend and the param is divisor
	 * 
	 * @param val divisor
	 * @return new money object
	 * @PublicApi
	 */
	public Money divide(Money val) {
		if (val == null) {
			logger.info("Money.divide() called with null argument, assuming value of 1");
			return new Money(this.decimalValue);
		}
		return divide(val.toBigDecimal());
	}

	/**
	 * returns quotient, where object is dividend and the param is divisor
	 * 
	 * @param val divisor
	 * @return new money object
	 * @PublicApi
	 */
	public Money divide(BigDecimal val) {
		if (val == null) {
			logger.info("Money.divide() called with null argument, assuming value of 1");
			return new Money(this.decimalValue);
		}
		return new Money(this.decimalValue.setScale(EXPANDED_SCALE).divide(val, DEFAULT_SCALE, DEFAULT_ROUND));
	}

	/**
	 * returns quotient, where object is dividend and the param is divisor
	 * Used for rounding any value with cents to a greater integer value
	 *
	 * @param val divisor
	 * @param roundingUp rounded up to the nearest dollar
	 * @return new money object
	 * @PublicApi
	 */
	public Money divide(BigDecimal val, boolean roundingUp) {
		if (val == null) {
			logger.info("Money.divide() called with null argument, assuming value of 1");
			return new Money(this.decimalValue);
		}
		if (roundingUp) {
			return new Money(this.decimalValue.setScale(EXPANDED_SCALE).divide(val, 0, RoundingMode.UP));
		} else {
			return divide(val);
		}
	}
	
	/**
	 * returns quotient, where object is dividend and the param is divisor
	 * Used for rounding any value with cents to a greater integer value
	 * 
	 * @param val divisor
	 * @param roundingUp rounded up to the nearest dollar
	 * @return new money object
	 * @PublicApi
	 */
	public Money divide(Money val, boolean roundingUp) {
		if (val == null) {
			logger.info("Money.divide() called with null argument, assuming value of 1");
			return new Money(this.decimalValue);
		}
		return divide(val.toBigDecimal(), roundingUp);
	}

	/**
	 * returns difference, where object is minuend and param is subtrahend
	 * 
	 * @param val subtrahend
	 * @return new money object
	 * @PublicApi
	 */
	public Money subtract(Money val) {
		if (val == null) {
			return new Money(this.decimalValue);
		}
		return new Money(this.decimalValue.subtract(val.toBigDecimal()));
	}

	/**
	 * returns product
	 * 
	 * @param val factor
	 * @return new money object
	 */
	public Money multiply(Money val) {
		if (val == null) {
			return new Money(this.decimalValue);
		}
		return multiply(val.toBigDecimal());
	}

	/**
	 * returns product
	 * 
	 * @param val factor
	 * @return new money object
	 * @PublicApi
	 */
	public Money multiply(long val) {
		return multiply(BigDecimal.valueOf(val));
	}

	/**
	 * returns product
	 * 
	 * @param val factor
	 * @return new money object
	 */
	public Money multiply(double val) {
		return multiply(BigDecimal.valueOf(val));
	}

	/**
	 * returns product
	 * 
	 * @param val factor
	 * @return new money object
	 * @PublicApi
	 */
	public Money multiply(BigDecimal val) {
		if (val == null) {
			return new Money(this.decimalValue);
		}
		// expand for fraction multiplication for better precision
		return new Money(this.decimalValue.setScale(EXPANDED_SCALE).multiply(val));
	}

	public boolean isGreaterThan(Money val) {
		if (val == null) {
			return true;
		}
		return compareTo(val) > 0;
	}

	public boolean isLessThan(Money val) {
		if (val == null) {
			return false;
		}
		return compareTo(val) < 0;
	}

	public boolean isZero() {
		return compareTo(Money.ZERO) == 0;
	}

	public static boolean isZeroOrEmpty(Money value) {
		return value == null || value.isZero();
	}

	public Money negate() {
		return new Money(this.decimalValue.negate());
	}

	/**
	 * Set the backing BigDecimal value for this Money. This is private since for the moment, Money is immutable.
	 * 
	 * @param newValue
	 */
	private void setValue(BigDecimal newValue) {
		this.decimalValue = newValue.setScale(DEFAULT_SCALE, DEFAULT_ROUND);
	}

	public Money min(Money val) {
		return isLessThan(val) ? this : val;
	}

	public Money max(Money val) {
		return isLessThan(val) ? val : this;
	}

	public Money abs() {
		return this.decimalValue.signum() < 0 ? negate() : this;
	}

	/**
	 * 
	 * @return a big decimal representation
	 * @PublicApi
	 */
	public BigDecimal toBigDecimal() {
		return this.decimalValue;
	}

	public int getCents() {
		// ( 10.99 - 10 ) * 100
		return this.decimalValue.subtract(new BigDecimal(this.decimalValue.toBigInteger())).multiply(BigDecimal.valueOf(100)).intValue();
	}

	public Money round(MoneyRounding roundType) {

		if (MoneyRounding.ROUNDING_10C.equals(roundType)) {
			return new Money(this.decimalValue.setScale(1, DEFAULT_ROUND));
		} else if (MoneyRounding.ROUNDING_50C.equals(roundType)) {

			int cents = getCents();

			if (cents == 0 || cents == 50 || cents == -50) {
				return this;
			} else if (cents > -25 && cents < 25) {
				return new Money(this.decimalValue.setScale(0, BigDecimal.ROUND_DOWN));
			} else if (cents >= 25 && cents < 75) {
				BigDecimal result = this.decimalValue.setScale(0, BigDecimal.ROUND_DOWN);
				return new Money(result.add(new BigDecimal("0.5")));
			} else if (cents <= -25 && cents > -75) {
				BigDecimal result = this.decimalValue.setScale(0, BigDecimal.ROUND_DOWN);
				return new Money(result.subtract(new BigDecimal("0.5")));
			} else if (cents >= 75 || cents <= -75) {
				return new Money(this.decimalValue.setScale(0, BigDecimal.ROUND_UP));
			}
		} else if (MoneyRounding.ROUNDING_1D.equals(roundType)) {
			return new Money(this.decimalValue.setScale(0, DEFAULT_ROUND));
		}
		return this;
	}

	public static Money valueOf(BigDecimal value) {
		return new Money(value);
	}

	@Override
	public String toString() {
		try {
			Class<?> formatterClass = Class.forName("ish.util.MoneyFormatter");
			Object sharedInstance = formatterClass.getMethod("getSharedInstance", (Class<?>[]) null).invoke(null, (Object[]) null);

			if (sharedInstance != null) {
				return (String) formatterClass.getMethod("valueToString", Object.class).invoke(sharedInstance, this);
			}
		} catch (Exception e) {}

		return NumberFormat.getCurrencyInstance().format(doubleValue());
	}

	//
	// Implement required Number methods
	//
	/**
	 * @see java.lang.Number#doubleValue()
	 */
	@Override
	public double doubleValue() {
		return this.decimalValue.doubleValue();
	}

	/**
	 * @see java.lang.Number#floatValue()
	 */
	@Override
	public float floatValue() {
		return this.decimalValue.floatValue();
	}

	/**
	 * @see java.lang.Number#intValue()
	 */
	@Override
	public int intValue() {
		return this.decimalValue.intValue();
	}

	/**
	 * @see java.lang.Number#longValue()
	 */
	@Override
	public long longValue() {
		return this.decimalValue.longValue();
	}

	/**
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Money m) {
		if (m == null) {
			throw new IllegalArgumentException("cannot compare money to null object");
		}
		return toBigDecimal().compareTo(m.toBigDecimal());
	}

	@Override
	public int hashCode() {
		return this.decimalValue.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Money)) {
			return false;
		}
		return this.decimalValue.equals(((Money) obj).decimalValue);
	}

}
