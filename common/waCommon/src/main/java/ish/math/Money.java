/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

package ish.math;

import ish.oncourse.API;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;

/**
 * Money is a class which handles all the common features needed for handling currency.
 * 
 * Most importantly, this class properly handles rounding at all times.
 * 
 */
@API
public class Money extends BigDecimal {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger();

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
		super(val == null ? BigDecimal.ZERO.toString() : val.toString());
		setValue(val == null ? BigDecimal.ZERO : val);
	}

	/**
	 * Construct a new money instance. If null or empty string is passed, then a Money.ZERO amount will be created.
	 * 
	 * @param val value to create
	 */
	@API
	public Money(String val) {
		super(StringUtils.isBlank(val) ? BigDecimal.ZERO.toString() : val);
		setValue(StringUtils.isBlank(val) ? BigDecimal.ZERO : new BigDecimal(val));
	}

	/**
	 * Construct a new money instance, first argument is dollars, second is cents
	 * 
	 * @param dollars dollars value
	 * @param cents cents value
	 */
	@API
	public Money(int dollars, int cents) {
		super(BigDecimal.valueOf(dollars).add(BigDecimal.valueOf(cents, DEFAULT_SCALE)).toString());
		setValue(BigDecimal.valueOf(dollars).add(BigDecimal.valueOf(cents, DEFAULT_SCALE)));
	}

	/**
	 * returns sum
	 * 
	 * @param val to be added to this money object
	 * @return new money object
	 */
	@API
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
	 */
	@API
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
	 */
	@API
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
	 */
	@API
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
	 */
	@API
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
	 */
	@API
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
	 */
	@API
	public Money subtract(Money val) {
		if (val == null) {
			return new Money(this.decimalValue);
		}
		return new Money(this.decimalValue.subtract(val.toBigDecimal()));
	}

	/**
	 * returns difference, where object is minuend and param is subtrahend
	 *
	 * @param val subtrahend
	 * @return new money object
	 */
	@API
	public Money subtract(BigDecimal val) {
		if (val == null) {
			return new Money(decimalValue);
		}
		return new Money(decimalValue.subtract(val));
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
	 */
	@API
	public Money multiply(long val) {
		return multiply(BigDecimal.valueOf(val));
	}

	/**
	 * returns product
	 * 
	 * @param val factor
	 * @return new money object
	 */
	public Money multiply(Double val) {
		return multiply(BigDecimal.valueOf(val));
	}

	/**
	 * returns product
	 * 
	 * @param val factor
	 * @return new money object
	 */
	@API
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

	public boolean isNegative() {
		return isLessThan(Money.ZERO);
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
	 */
	@API
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
		return NumberFormat.getCurrencyInstance().format(decimalValue);
	}
	
	/**
	 *
	 * @return a string representation skipping currency sign
	 * $25 -> "25.00"
	 */
	@API
	public String toPlainString() {
		return new MoneyDecimalFormatter().valueToString(this, DEFAULT_SCALE);
	}
	
	@Override
	public double doubleValue() {
		return decimalValue.doubleValue();
	}

	@Override
	public float floatValue() {
		return decimalValue.floatValue();
	}
	
	@Override
	public int intValue() {
		return decimalValue.intValue();
	}
	
	@Override
	public long longValue() {
		return decimalValue.longValue();
	}
	
	@Override
	public int compareTo(BigDecimal value) {
		if (value == null) {
			throw new IllegalArgumentException("cannot compare money to null object");
		}
		
		BigDecimal val = value;
		if (value instanceof Money) {
			val = ((Money) value).decimalValue;
		}
		
		return toBigDecimal().compareTo(val);
	}

	@Override
	public int hashCode() {
		return this.decimalValue.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof BigDecimal)) {
			return false;
		}
		
		BigDecimal val = (BigDecimal) obj;
		if (obj instanceof Money) {
			val = ((Money) obj).decimalValue;
		}
		
		return decimalValue.equals(val);
	}

}
