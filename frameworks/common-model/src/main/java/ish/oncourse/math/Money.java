/**
 * 
 */
package ish.oncourse.math;

import java.math.BigDecimal;
import java.math.BigInteger;
import org.apache.log4j.Logger;


/**
 * @author ldeck
 */
public class Money extends BigDecimal {

	public final static int DEFAULT_ROUND = ROUND_HALF_UP;
	public final static int DEFAULT_SCALE = 2;
	private static final Logger LOG = Logger.getLogger(Money.class);
	public final static Money ONE = new Money(valueOf(100L, DEFAULT_SCALE));
	public static final Integer ROUNDING_10C = Integer.valueOf(1);
	public static final Integer ROUNDING_1D = Integer.valueOf(3);
	public static final Integer ROUNDING_50C = Integer.valueOf(2);
	public static final Integer ROUNDING_NONE = Integer.valueOf(0);
	private final static BigDecimal RoundResultA = BigDecimal.valueOf(10L, 1);
	private final static BigDecimal RoundResultB = BigDecimal.valueOf(5L, 1);
	private final static BigDecimal RoundTestA = BigDecimal.valueOf(75L, 2);
	private final static BigDecimal RoundTestB = BigDecimal.valueOf(25L, 2);
	/**
	 * {@value}
	 */
	public final static Money ZERO = new Money(BigInteger.ZERO, DEFAULT_SCALE);


	public Money(BigDecimal val) {
		super(val.setScale(DEFAULT_SCALE, DEFAULT_ROUND).unscaledValue(), DEFAULT_SCALE);
	}

	protected Money(BigInteger unscaledValue, int scale) {
		this(valueOf(unscaledValue.longValue(), scale));
	}


	/**
	 * @param aNum
	 *            - the number to round
	 * @param scale
	 *            - the resulting scale. A positive integer.
	 * @param roundingScale
	 *            - scale point to round at. Can be negative.
	 * @return a BigDecimal with a scale of <code>scale</code>
	 */
	public static BigDecimal roundWithScale(BigDecimal aNum, int scale, int roundingScale, int roundingMode) {
		int offset, offsetScale, offsetRoundingScale;
		BigDecimal roundedBigDecimal, numberPart, fractionPart;

		if (aNum == null) {
			return null;
		} else if (scale < 0) {
			throw new IllegalArgumentException("Argument scale cannot be negative.");
		}

		BigDecimal abs = aNum.abs();

		// make sure we have some fraction component to round
		offset = scale <= roundingScale ? 0 : scale - roundingScale - 1; // e.g., if scale == 2, roundingScale == 1, result = 0.
		offsetScale = scale + offset;
		offsetRoundingScale = roundingScale + offset;
		roundedBigDecimal = offset != 0 ? abs.movePointLeft(offset) : abs;

		// perform initial rounding to scale
		roundedBigDecimal = roundedBigDecimal.setScale(offsetScale, roundingMode);
		LOG.debug("initial round with offset:" + roundedBigDecimal);

		// nothing else to do if rounding digit is greater or equal to scale
		if (offsetRoundingScale >= offsetScale) {
			return roundedBigDecimal;
		}

		// grab number and fraction parts
		numberPart = BigDecimal.valueOf(roundedBigDecimal.longValue()).setScale(offsetScale);
		fractionPart = roundedBigDecimal.subtract(numberPart);

		switch (roundingMode) {
			case BigDecimal.ROUND_CEILING:
				fractionPart = RoundResultA;
				break;
			case BigDecimal.ROUND_FLOOR:
				fractionPart = ZERO;
				break;
			case BigDecimal.ROUND_UP: {
				if (fractionPart.compareTo(RoundResultB) > 0) {
					fractionPart = RoundResultA; // move up
				} else if (fractionPart.compareTo(ZERO) > 0) {
					fractionPart = RoundResultB; // move to half
				} else {
					fractionPart = ZERO; // move down
				}
				break;
			}
			case BigDecimal.ROUND_DOWN: {
				if (fractionPart.compareTo(RoundResultB) < 0) {
					fractionPart = ZERO; // move down
				} else if (fractionPart.compareTo(RoundResultA) < 0) {
					fractionPart = RoundResultB; // move down half
				} else {
					fractionPart = RoundResultA; // leave up
				}
				break;
			}
			case BigDecimal.ROUND_HALF_UP:
			case BigDecimal.ROUND_HALF_DOWN:
			case BigDecimal.ROUND_HALF_EVEN: {
				boolean roundHalfDown =
						roundingMode == BigDecimal.ROUND_HALF_DOWN || roundingMode == BigDecimal.ROUND_HALF_EVEN && numberPart.intValue() % 2 == 1;
				if (fractionPart.compareTo(RoundTestA) > 0) {
					fractionPart = RoundResultA; // move up
				} else if (fractionPart.compareTo(RoundTestA) == 0) {
					fractionPart = roundHalfDown ? RoundResultB : RoundResultA;
				} else if (fractionPart.compareTo(RoundTestB) > 0) {
					fractionPart = RoundResultB; // move to half
				} else if (fractionPart.compareTo(RoundTestB) == 0) {
					fractionPart = roundHalfDown ? ZERO : RoundResultB;
				} else {
					fractionPart = ZERO; // move down
				}
				break;
			}
			default:
				break;
		}

		// add the fraction back in
		roundedBigDecimal = numberPart.add(fractionPart);
		LOG.debug("rounded prior to offset reset:" + roundedBigDecimal);

		// if there was an offset, readjust point offset
		if (offset != 0) {
			roundedBigDecimal = roundedBigDecimal.movePointRight(offset);
		}
		if (BigDecimal.ZERO.compareTo(aNum) > 0) {
			roundedBigDecimal = roundedBigDecimal.multiply(BigDecimal.valueOf(-1));
		}
		return roundedBigDecimal;
	}

	public static Money valueOf(BigDecimal aValue) {
		return new Money(aValue.setScale(DEFAULT_SCALE, DEFAULT_ROUND));
	}

	public static BigDecimal valueOf(long unscaledValue, int scale) {
		return valueOf(BigDecimal.valueOf(unscaledValue, scale));
	}

	@Override
	public BigDecimal add(BigDecimal val) {
		return setDefaultScale(super.add(val));
	}

	@Override
	public BigDecimal divide(BigDecimal val) {
		return setDefaultScale(super.divide(val, DEFAULT_ROUND));
	}

	public boolean isGreaterThan(BigDecimal val) {
		return compareTo(val) > 0;
	}

	public boolean isLessThan(BigDecimal val) {
		return compareTo(val) < 0;
	}

	@Override
	public BigDecimal max(BigDecimal val) {
		return setDefaultScale(super.max(val));
	}

	@Override
	public BigDecimal min(BigDecimal val) {
		return setDefaultScale(super.min(val));
	}

	@Override
	public BigDecimal multiply(BigDecimal val) {
		return setDefaultScale(super.multiply(val));
	}

	@Override
	public BigDecimal negate() {
		return setDefaultScale(super.negate());
	}

	public Money round(Number roundType) {
		if (ROUNDING_10C.equals(roundType)) {
			return setDefaultScale(setScale(1, DEFAULT_ROUND));
		} else if (ROUNDING_50C.equals(roundType)) {
			return Money.valueOf(roundWithScale(this, DEFAULT_SCALE, 1, DEFAULT_ROUND));
		} else if (ROUNDING_1D.equals(roundType)) {
			return setDefaultScale(setScale(0, DEFAULT_ROUND));
		}
		return this;
	}

	public Money setDefaultScale(BigDecimal val) {
		return valueOf(val);
	}

	@Override
	public BigDecimal subtract(BigDecimal val) {
		return setDefaultScale(super.subtract(val));
	}

	public BigDecimal toBigDecimal() {
		return new BigDecimal(unscaledValue(), scale());
	}
	
}
