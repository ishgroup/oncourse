package ish.math;

import org.junit.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.junit.Assert.*;

public class MoneyTest {
	Money[][] list = new Money[][] {
			{
					Money.valueOf(new BigDecimal("10.00")),
					Money.valueOf(new BigDecimal("10.00")),
					Money.valueOf(new BigDecimal("10.00")),
					Money.valueOf(new BigDecimal("10.00")),
					Money.valueOf(new BigDecimal("10.00")) },
			{
					Money.valueOf(new BigDecimal("10.01")),
					Money.valueOf(new BigDecimal("10.01")),
					Money.valueOf(new BigDecimal("10.00")),
					Money.valueOf(new BigDecimal("10.00")),
					Money.valueOf(new BigDecimal("10.00")) },
			{
					Money.valueOf(new BigDecimal("10.04")),
					Money.valueOf(new BigDecimal("10.04")),
					Money.valueOf(new BigDecimal("10.00")),
					Money.valueOf(new BigDecimal("10.00")),
					Money.valueOf(new BigDecimal("10.00")) },
			{
					Money.valueOf(new BigDecimal("10.05")),
					Money.valueOf(new BigDecimal("10.05")),
					Money.valueOf(new BigDecimal("10.10")),
					Money.valueOf(new BigDecimal("10.00")),
					Money.valueOf(new BigDecimal("10.00")) },
			{
					Money.valueOf(new BigDecimal("10.06")),
					Money.valueOf(new BigDecimal("10.06")),
					Money.valueOf(new BigDecimal("10.10")),
					Money.valueOf(new BigDecimal("10.00")),
					Money.valueOf(new BigDecimal("10.00")) },
			{
					Money.valueOf(new BigDecimal("10.24")),
					Money.valueOf(new BigDecimal("10.24")),
					Money.valueOf(new BigDecimal("10.20")),
					Money.valueOf(new BigDecimal("10.00")),
					Money.valueOf(new BigDecimal("10.00")) },
			{
					Money.valueOf(new BigDecimal("10.25")),
					Money.valueOf(new BigDecimal("10.25")),
					Money.valueOf(new BigDecimal("10.30")),
					Money.valueOf(new BigDecimal("10.50")),
					Money.valueOf(new BigDecimal("10.00")) },
			{
					Money.valueOf(new BigDecimal("10.26")),
					Money.valueOf(new BigDecimal("10.26")),
					Money.valueOf(new BigDecimal("10.30")),
					Money.valueOf(new BigDecimal("10.50")),
					Money.valueOf(new BigDecimal("10.00")) },
			{
					Money.valueOf(new BigDecimal("10.49")),
					Money.valueOf(new BigDecimal("10.49")),
					Money.valueOf(new BigDecimal("10.50")),
					Money.valueOf(new BigDecimal("10.50")),
					Money.valueOf(new BigDecimal("10.00")) },
			{
					Money.valueOf(new BigDecimal("10.50")),
					Money.valueOf(new BigDecimal("10.50")),
					Money.valueOf(new BigDecimal("10.50")),
					Money.valueOf(new BigDecimal("10.50")),
					Money.valueOf(new BigDecimal("11.00")) },
			{
					Money.valueOf(new BigDecimal("10.51")),
					Money.valueOf(new BigDecimal("10.51")),
					Money.valueOf(new BigDecimal("10.50")),
					Money.valueOf(new BigDecimal("10.50")),
					Money.valueOf(new BigDecimal("11.00")) },
			{
					Money.valueOf(new BigDecimal("10.74")),
					Money.valueOf(new BigDecimal("10.74")),
					Money.valueOf(new BigDecimal("10.70")),
					Money.valueOf(new BigDecimal("10.50")),
					Money.valueOf(new BigDecimal("11.00")) },
			{
					Money.valueOf(new BigDecimal("10.75")),
					Money.valueOf(new BigDecimal("10.75")),
					Money.valueOf(new BigDecimal("10.80")),
					Money.valueOf(new BigDecimal("11.00")),
					Money.valueOf(new BigDecimal("11.00")) },
			{
					Money.valueOf(new BigDecimal("10.76")),
					Money.valueOf(new BigDecimal("10.76")),
					Money.valueOf(new BigDecimal("10.80")),
					Money.valueOf(new BigDecimal("11.00")),
					Money.valueOf(new BigDecimal("11.00")) },
			{
					Money.valueOf(new BigDecimal("10.99")),
					Money.valueOf(new BigDecimal("10.99")),
					Money.valueOf(new BigDecimal("11.00")),
					Money.valueOf(new BigDecimal("11.00")),
					Money.valueOf(new BigDecimal("11.00")) } };

	@Test
	public void testRound() {
		for (Money[] aList : this.list) {
			Money value = aList[0];

// System.out.println("");
// System.out.println("----");
// System.out.println("org :\t" + list[i][0]);
// System.out.println("1");
// System.out.println("is:\t" + new Money(value.toBigDecimal()).round(Money.ROUNDING_NONE));
// System.out.println("should be:\t" + list[i][1]);
// System.out.println("2");
// System.out.println("is :\t" + new Money(value.toBigDecimal()).round(Money.ROUNDING_10C));
// System.out.println("should be:\t" + list[i][2]);
// System.out.println("3");
// System.out.println("is :\t" + new Money(value.toBigDecimal()).round(Money.ROUNDING_50C));
// System.out.println("should be:\t" + list[i][3]);
// System.out.println("4");
// System.out.println("is :\t" + new Money(value.toBigDecimal()).round(Money.ROUNDING_1D));
// System.out.println("should be:\t" + list[i][4]);
// System.out.println("----");

			// no rounding
			assertEquals("non rounding failed for value" + value, aList[1], new Money(value.toBigDecimal()).round(MoneyRounding.ROUNDING_NONE));
			// 10c rounding
			assertEquals("10c rounding failed for value" + value, aList[2], new Money(value.toBigDecimal()).round(MoneyRounding.ROUNDING_10C));
			// 50c rounding
			assertEquals("50c rounding failed for value" + value, aList[3], new Money(value.toBigDecimal()).round(MoneyRounding.ROUNDING_50C));
			// 1$ rounding
			assertEquals("1d rounding failed for value" + value, aList[4], new Money(value.toBigDecimal()).round(MoneyRounding.ROUNDING_1D));

			// no rounding
			assertEquals("non rounding failed for value" + value.negate(), aList[1].negate(),
					new Money(value.toBigDecimal()).negate().round(MoneyRounding.ROUNDING_NONE));
			// 10c rounding
			assertEquals("10c rounding failed for value" + value.negate(), aList[2].negate(),
					new Money(value.toBigDecimal()).negate().round(MoneyRounding.ROUNDING_10C));
			// 50c rounding
			assertEquals("50c rounding failed for value" + value.negate(), aList[3].negate(),
					new Money(value.toBigDecimal()).negate().round(MoneyRounding.ROUNDING_50C));
			// 1$ rounding
			assertEquals("1d rounding failed for value" + value.negate(), aList[4].negate(),
					new Money(value.toBigDecimal()).negate().round(MoneyRounding.ROUNDING_1D));
		}
	}

	@Test
	public void testCents() {
		assertEquals(99, Money.valueOf(new BigDecimal("10.99")).getCents());
		assertEquals(1, Money.valueOf(new BigDecimal("10.01")).getCents());
		assertEquals(-1, Money.valueOf(new BigDecimal("-10.01")).getCents());
		assertEquals(-99, Money.valueOf(new BigDecimal("-10.99")).getCents());
	}

	@Test
	public void testOneCentRoundingErrorWithAngelCode() {
		Money valueEnteredIncTax = new Money(225, 0);
		BigDecimal taxRate = BigDecimal.valueOf(1, 1); // 0.1

		Money exTaxAmount = valueEnteredIncTax.divide(BigDecimal.ONE.add(taxRate));

		Money expectedResult = new Money(204, 55);
		assertEquals("Ex tax rounding error", expectedResult.toBigDecimal(), exTaxAmount.toBigDecimal());

		// multiplier = rate * 10 / 11
		// we MUST expand scale to get an accurate result.
		BigDecimal taxMultiplier = taxRate.movePointRight(1).setScale(6).divide(BigDecimal.valueOf(11), RoundingMode.HALF_UP);
		Money taxAmount = valueEnteredIncTax.multiply(taxMultiplier);
		BigDecimal expectedTaxAmount = BigDecimal.valueOf(2045, 2);
		assertEquals("Tax rounding error:", expectedTaxAmount, taxAmount.toBigDecimal());

		Money newTotalIncTax = exTaxAmount.add(taxAmount);
		assertEquals("Ex tax rounding error", valueEnteredIncTax.toBigDecimal(), newTotalIncTax.toBigDecimal());
	}

	@Test
	public void testMoneyConstructorDollarWithCents() {
		Money actualResult = new Money(204, 55);
		BigDecimal expectedResult = BigDecimal.valueOf(20455, 2);
		assertEquals("Money construction error", expectedResult, actualResult.toBigDecimal());
	}

	@Test
	public void testMoneyConstructorWithNULLString() {
		Money actualResult = new Money((String) null);
		BigDecimal expectedResult = BigDecimal.ZERO.setScale(2);
		assertEquals("Money construction error", expectedResult, actualResult.toBigDecimal());
	}

	@Test
	public void testMoneyConstructorWithNULLBigDecimal() {
		Money actualResult = new Money((BigDecimal) null);
		BigDecimal expectedResult = BigDecimal.ZERO.setScale(2);
		assertEquals("Money construction error", expectedResult, actualResult.toBigDecimal());
	}

	@Test
	public void testMoneyMultiplyFractionAccuracy() {
		Money valueEnteredIncTax = new Money(204, 55); // $204.55
		BigDecimal taxRate = BigDecimal.valueOf(1, 1); // 0.1

		Money taxAmount = valueEnteredIncTax.multiply(taxRate); // $20.455
		BigDecimal expectedResult = BigDecimal.valueOf(2046, 2); // $20.46
		assertEquals("Money rounding error", expectedResult, taxAmount.toBigDecimal());
	}

	@Test
	public void testOneCentRoundingErrorWithValidCode() {
		BigDecimal valueEnteredIncTax = BigDecimal.valueOf(22500, 2);

		BigDecimal taxDivisor = BigDecimal.valueOf(11);
		BigDecimal taxMultiplier = BigDecimal.valueOf(10);
		BigDecimal exTaxMultiplier = taxMultiplier.setScale(6).divide(taxDivisor, RoundingMode.HALF_UP);
		// System.out.println("exTaxMult:" + exTaxMultiplier);

		BigDecimal exTaxAmount = valueEnteredIncTax.multiply(exTaxMultiplier);
		// System.out.println("exTaxAmount:" + exTaxAmount);

		BigDecimal taxAmount = valueEnteredIncTax.setScale(2).divide(taxDivisor, RoundingMode.HALF_UP);
		// System.out.println("exTaxAmount:" + taxAmount);

		Money moneyValueEntered = Money.valueOf(valueEnteredIncTax);
		Money moneyExTaxAmount = moneyValueEntered.multiply(exTaxMultiplier);
		// System.out.println("exTaxAmount:" + moneyExTaxAmount);

		Money moneyTaxAmount = moneyValueEntered.divide(taxDivisor);
		// System.out.println("exTaxAmount:" + moneyTaxAmount);

		exTaxAmount = exTaxAmount.setScale(2, Money.DEFAULT_ROUND);
		taxAmount = taxAmount.setScale(2, Money.DEFAULT_ROUND);

		assertEquals("Ex tax rounding error", exTaxAmount.unscaledValue(), moneyExTaxAmount.toBigDecimal().unscaledValue());
		assertEquals("Tax rounding error", taxAmount.unscaledValue(), moneyTaxAmount.toBigDecimal().unscaledValue());
	}

	@Test
	public void testEquals() {
		assertFalse("Checking equals", new Money("20.44").equals(new Money("30.55")));
		assertTrue("Checking equals", new Money("20.44").equals(new Money("20.44")));
		assertFalse("Checking equals", Money.ZERO.equals(new Money("20.44")));
		assertFalse("Checking equals", new Money("20.44").equals(new Money("-20.44")));
		assertTrue("Checking equals", new Money("40.66").equals(new Money("30.55").add(new Money("10.11"))));
		assertTrue("Checking equals", new Money("40.66").equals(new Money("30.55").add(new BigDecimal("10.11"))));

		assertTrue("Checking equals", new Money("20.44").hashCode() == new Money("20.44").hashCode());
		assertFalse("Checking equals", new Money("20.44").hashCode() == new Money("20.45").hashCode());
	}
}
