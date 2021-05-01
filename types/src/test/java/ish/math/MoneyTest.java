package ish.math;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

import java.math.BigDecimal;
import java.math.RoundingMode;




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
			Assertions.assertEquals(aList[1], new Money(value.toBigDecimal()).round(MoneyRounding.ROUNDING_NONE), "non rounding failed for value" + value);
			// 10c rounding
			Assertions.assertEquals(aList[2], new Money(value.toBigDecimal()).round(MoneyRounding.ROUNDING_10C), "10c rounding failed for value" + value);
			// 50c rounding
			Assertions.assertEquals(aList[3], new Money(value.toBigDecimal()).round(MoneyRounding.ROUNDING_50C), "50c rounding failed for value" + value);
			// 1$ rounding
			Assertions.assertEquals(aList[4], new Money(value.toBigDecimal()).round(MoneyRounding.ROUNDING_1D), "1d rounding failed for value" + value);

			// no rounding
			Assertions.assertEquals(aList[1].negate(),
					new Money(value.toBigDecimal()).negate().round(MoneyRounding.ROUNDING_NONE), "non rounding failed for value" + value.negate());
			// 10c rounding
			Assertions.assertEquals(aList[2].negate(),
					new Money(value.toBigDecimal()).negate().round(MoneyRounding.ROUNDING_10C), "10c rounding failed for value" + value.negate());
			// 50c rounding
			Assertions.assertEquals(aList[3].negate(),
					new Money(value.toBigDecimal()).negate().round(MoneyRounding.ROUNDING_50C), "50c rounding failed for value" + value.negate());
			// 1$ rounding
			Assertions.assertEquals(aList[4].negate(),
					new Money(value.toBigDecimal()).negate().round(MoneyRounding.ROUNDING_1D), "1d rounding failed for value" + value.negate());
		}
	}

	@Test
	public void testCents() {
		Assertions.assertEquals(99, Money.valueOf(new BigDecimal("10.99")).getCents());
		Assertions.assertEquals(1, Money.valueOf(new BigDecimal("10.01")).getCents());
		Assertions.assertEquals(-1, Money.valueOf(new BigDecimal("-10.01")).getCents());
		Assertions.assertEquals(-99, Money.valueOf(new BigDecimal("-10.99")).getCents());
	}

	@Test
	public void testOneCentRoundingErrorWithAngelCode() {
		Money valueEnteredIncTax = new Money(225, 0);
		BigDecimal taxRate = BigDecimal.valueOf(1, 1); // 0.1

		Money exTaxAmount = valueEnteredIncTax.divide(BigDecimal.ONE.add(taxRate));

		Money expectedResult = new Money(204, 55);
		Assertions.assertEquals(expectedResult.toBigDecimal(), exTaxAmount.toBigDecimal(), "Ex tax rounding error");

		// multiplier = rate * 10 / 11
		// we MUST expand scale to get an accurate result.
		BigDecimal taxMultiplier = taxRate.movePointRight(1).setScale(6).divide(BigDecimal.valueOf(11), RoundingMode.HALF_UP);
		Money taxAmount = valueEnteredIncTax.multiply(taxMultiplier);
		BigDecimal expectedTaxAmount = BigDecimal.valueOf(2045, 2);
		Assertions.assertEquals(expectedTaxAmount, taxAmount.toBigDecimal(), "Tax rounding error:");

		Money newTotalIncTax = exTaxAmount.add(taxAmount);
		Assertions.assertEquals(valueEnteredIncTax.toBigDecimal(), newTotalIncTax.toBigDecimal(), "Ex tax rounding error");
	}

	@Test
	public void testMoneyConstructorDollarWithCents() {
		Money actualResult = new Money(204, 55);
		BigDecimal expectedResult = BigDecimal.valueOf(20455, 2);
		Assertions.assertEquals(expectedResult, actualResult.toBigDecimal(), "Money construction error");
	}

	@Test
	public void testMoneyConstructorWithNULLString() {
		Money actualResult = new Money((String) null);
		BigDecimal expectedResult = BigDecimal.ZERO.setScale(2);
		Assertions.assertEquals(expectedResult, actualResult.toBigDecimal(), "Money construction error");
	}

	@Test
	public void testMoneyConstructorWithNULLBigDecimal() {
		Money actualResult = new Money((BigDecimal) null);
		BigDecimal expectedResult = BigDecimal.ZERO.setScale(2);
		Assertions.assertEquals(expectedResult, actualResult.toBigDecimal(), "Money construction error");
	}

	@Test
	public void testMoneyMultiplyFractionAccuracy() {
		Money valueEnteredIncTax = new Money(204, 55); // $204.55
		BigDecimal taxRate = BigDecimal.valueOf(1, 1); // 0.1

		Money taxAmount = valueEnteredIncTax.multiply(taxRate); // $20.455
		BigDecimal expectedResult = BigDecimal.valueOf(2046, 2); // $20.46
		Assertions.assertEquals(expectedResult, taxAmount.toBigDecimal(), "Money rounding error");
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

		Assertions.assertEquals(exTaxAmount.unscaledValue(), moneyExTaxAmount.toBigDecimal().unscaledValue(), "Ex tax rounding error");
		Assertions.assertEquals(taxAmount.unscaledValue(), moneyTaxAmount.toBigDecimal().unscaledValue(), "Tax rounding error");
	}

	@Test
	public void testEquals() {
		Assertions.assertFalse(new Money("20.44").equals(new Money("30.55")), "Checking equals");
		Assertions.assertTrue(new Money("20.44").equals(new Money("20.44")), "Checking equals");
		Assertions.assertFalse(Money.ZERO.equals(new Money("20.44")), "Checking equals");
		Assertions.assertFalse(new Money("20.44").equals(new Money("-20.44")), "Checking equals");
		Assertions.assertTrue(new Money("40.66").equals(new Money("30.55").add(new Money("10.11"))), "Checking equals");
		Assertions.assertTrue(new Money("40.66").equals(new Money("30.55").add(new BigDecimal("10.11"))), "Checking equals");

		Assertions.assertTrue(new Money("20.44").hashCode() == new Money("20.44").hashCode(), "Checking equals");
		Assertions.assertFalse(new Money("20.44").hashCode() == new Money("20.45").hashCode(), "Checking equals");
	}

	@Test
	public void testRoundingUpDivide() {
		Money money = new Money("5.00");
		
		//division without rounding, 5/13 == 0.38
		Money money1 = money.divide(new BigDecimal(13));
		Assertions.assertEquals(new Money("0.38"), money1, "division failed for value " + money1);
		
		//Rounded up to the nearest dollar, 5/13 == 0.38 (less then 0.50) should be rounded to the 1.00
		Money money2 = money.divide(new BigDecimal(13), true);
		Assertions.assertEquals(Money.ONE, money2, "1d rounding failed for value " + money2);

		//should be rounded to the 2.00
		Money money3 = money.divide(new BigDecimal(4), true);
		Assertions.assertEquals(new Money("2.00"), money3, "2d rounding failed for value " + money3);

		// do nothing if value is already integer, 5/5 == 1
		Money money4 = money.divide(new BigDecimal(5), true);
		Assertions.assertEquals(Money.ONE, money4, "1d rounding failed for value " + money4);
		
		money = new Money("-5.00");

		//the same test for negative value 
		Money money5 = money.divide(new BigDecimal(13), true);
		Assertions.assertEquals(new Money("-1.00"), money5, "1d rounding failed for value " + money3);
		
	}

	@Test
	public void testNumberFormatter() {
		Money money = new Money("5.00");
		Assertions.assertEquals("5.00", money.toPlainString());
		
		money = new Money("-5.00");
		Assertions.assertEquals("-5.00", money.toPlainString());

		money = new Money("-005.55");
		Assertions.assertEquals("-5.55", money.toPlainString());
		
		money = Money.valueOf(new BigDecimal(-34.5555));
		Assertions.assertEquals("-34.56", money.toPlainString());

		money = Money.valueOf(new BigDecimal(1892.5555));
		Assertions.assertEquals("1892.56", money.toPlainString());

		money = Money.ZERO;
		Assertions.assertEquals("0.00", money.toPlainString());
	}
}
