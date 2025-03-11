/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */
//file:noinspection ChangeToOperator

package ish.math

import groovy.transform.CompileStatic
import ish.math.context.MoneyContext
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

import java.math.RoundingMode

@CompileStatic
class MoneyTest {

    static Collection<Arguments> values() {
        List<List<BigDecimal>> data = [
                [10.00, 10.00, 10.00, 10.00],
                [10.01, 10.00, 10.00, 10.00],
                [10.04, 10.00, 10.00, 10.00],
                [10.05, 10.10, 10.00, 10.00],
                [10.06, 10.10, 10.00, 10.00],
                [10.24, 10.20, 10.00, 10.00],
                [10.25, 10.30, 10.50, 10.00],
                [10.26, 10.30, 10.50, 10.00],
                [10.49, 10.50, 10.50, 10.00],
                [10.50, 10.50, 10.50, 11.00],
                [10.51, 10.50, 10.50, 11.00],
                [10.74, 10.70, 10.50, 11.00],
                [10.75, 10.80, 11.00, 11.00],
                [10.76, 10.80, 11.00, 11.00],
                [10.99, 11.00, 11.00, 11.00]
        ]

        Collection<Arguments> resultData = []
        for (List<BigDecimal> test : data) {
            resultData.add(Arguments.of(Money.of(test[0]), Money.of(test[1]), Money.of(test[2]), Money.of(test[3])))
        }
        return resultData
    }

    @BeforeAll
    static void setupEnvironment() throws Exception {
        MoneyManager.updateSystemContext(MoneyContextFactory.create(Country.AUSTRALIA))
    }

    @ParameterizedTest
    @MethodSource("values")
    void testRound(Money original, Money round10, Money round50, Money round1d) {
        // no rounding
        Assertions.assertEquals(original, original.round(MoneyRounding.ROUNDING_NONE), "non rounding failed for ${original}")
        // 10c rounding
        Assertions.assertEquals(round10, original.round(MoneyRounding.ROUNDING_10C), "10c rounding failed for ${original}")
        // 50c rounding
        Assertions.assertEquals(round50, original.round(MoneyRounding.ROUNDING_50C), "50c rounding failed for ${original}")
        // 1$ rounding
        Assertions.assertEquals(round1d, original.round(MoneyRounding.ROUNDING_1D), "1d rounding failed for ${original}")

        // no rounding
        Assertions.assertEquals(original.negate(),
                Money.of(original.toBigDecimal()).negate().round(MoneyRounding.ROUNDING_NONE), "non rounding failed for -${original}")
        // 10c rounding
        Assertions.assertEquals(round10.negate(),
                Money.of(original.toBigDecimal()).negate().round(MoneyRounding.ROUNDING_10C), "10c rounding failed for -${original}")
        // 50c rounding
        Assertions.assertEquals(round50.negate(),
                Money.of(original.toBigDecimal()).negate().round(MoneyRounding.ROUNDING_50C), "50c rounding failed for -${original}")
        // 1$ rounding
        Assertions.assertEquals(round1d.negate(),
                Money.of(original.toBigDecimal()).negate().round(MoneyRounding.ROUNDING_1D), "1d rounding failed for -${original}")
    }

    @Test
    void testCents() {
        Assertions.assertEquals(99, Money.of(new BigDecimal("10.99")).getFractional())
        Assertions.assertEquals(1, Money.of(new BigDecimal("10.01")).getFractional())
        Assertions.assertEquals(-1, Money.of(new BigDecimal("-10.01")).getFractional())
        Assertions.assertEquals(-99, Money.of(new BigDecimal("-10.99")).getFractional())
    }

    @Test
    void testOneCentRoundingErrorWithAngelCode() {
        Money valueEnteredIncTax = Money.of(225, 0)
        BigDecimal taxRate = BigDecimal.valueOf(1, 1) // 0.1

        Money exTaxAmount = valueEnteredIncTax.divide(BigDecimal.ONE.add(taxRate))

        Money expectedResult = Money.of(204, 55)
        Assertions.assertEquals(expectedResult.toBigDecimal(), exTaxAmount.toBigDecimal(), "Ex tax rounding error")

        // multiplier = rate * 10 / 11
        // we MUST expand scale to get an accurate result.
        BigDecimal taxMultiplier = taxRate.movePointRight(1).setScale(6).divide(BigDecimal.valueOf(11), RoundingMode.HALF_UP)
        Money taxAmount = valueEnteredIncTax.multiply(taxMultiplier)
        BigDecimal expectedTaxAmount = BigDecimal.valueOf(2045, 2)
        Assertions.assertEquals(expectedTaxAmount, taxAmount.toBigDecimal(), "Tax rounding error:")

        Money newTotalIncTax = exTaxAmount.add(taxAmount)
        Assertions.assertEquals(valueEnteredIncTax.toBigDecimal(), newTotalIncTax.toBigDecimal(), "Ex tax rounding error")
    }

    @Test
    void testMoneyConstructorDollarWithCents() {
        Money actualResult = Money.of(204, 55)
        BigDecimal expectedResult = BigDecimal.valueOf(20455, 2)
        Assertions.assertEquals(expectedResult, actualResult.toBigDecimal(), "Money construction error")
    }

    @Test
    void testMoneyConstructorWithNULLString() {
        Money actualResult = Money.of((String) null)
        BigDecimal expectedResult = BigDecimal.ZERO.setScale(2)
        Assertions.assertEquals(expectedResult, actualResult.toBigDecimal(), "Money construction error")
    }

    @Test
    void testMoneyConstructorWithNULLBigDecimal() {
        Money actualResult = Money.of((BigDecimal) null)
        BigDecimal expectedResult = BigDecimal.ZERO.setScale(2)
        Assertions.assertEquals(expectedResult, actualResult.toBigDecimal(), "Money construction error")
    }

    @Test
    void testMoneyMultiplyFractionAccuracy() {
        Money valueEnteredIncTax = Money.of(204, 55) // $204.55
        BigDecimal taxRate = BigDecimal.valueOf(1, 1) // 0.1

        Money taxAmount = valueEnteredIncTax.multiply(taxRate) // $20.455
        BigDecimal expectedResult = BigDecimal.valueOf(2046, 2) // $20.46
        Assertions.assertEquals(expectedResult, taxAmount.toBigDecimal(), "Money rounding error")

        Assertions.assertEquals(4, Money.of(0.69).multiply(16).getFractional())
        Assertions.assertEquals(40, Money.of(0.18).multiply(30).getFractional())
    }

    @Test
    void testOneCentRoundingErrorWithValidCode() {
        int scale = MoneyManager.getSystemContext().getCurrency().getDefaultFractionDigits()

        BigDecimal valueEnteredIncTax = BigDecimal.valueOf(22500, scale)

        BigDecimal taxDivisor = BigDecimal.valueOf(11)
        BigDecimal taxMultiplier = BigDecimal.valueOf(10)
        BigDecimal exTaxMultiplier = taxMultiplier.setScale(8).divide(taxDivisor, RoundingMode.HALF_UP)
        // System.out.println("exTaxMult:" + exTaxMultiplier);

        BigDecimal exTaxAmount = valueEnteredIncTax.multiply(exTaxMultiplier)
        // System.out.println("exTaxAmount:" + exTaxAmount);

        BigDecimal taxAmount = valueEnteredIncTax.setScale(scale).divide(taxDivisor, RoundingMode.HALF_UP)
        // System.out.println("exTaxAmount:" + taxAmount);

        Money moneyValueEntered = Money.of(valueEnteredIncTax)
        Money moneyExTaxAmount = moneyValueEntered.multiply(exTaxMultiplier)
        Money moneyTaxAmount = moneyValueEntered.divide(taxDivisor)

        exTaxAmount = exTaxAmount.setScale(scale, MoneyContext.DEFAULT_ROUND)
        taxAmount = taxAmount.setScale(scale, MoneyContext.DEFAULT_ROUND)

        Assertions.assertEquals(exTaxAmount.unscaledValue(), moneyExTaxAmount.toBigDecimal().unscaledValue(), "Ex tax rounding error")
        Assertions.assertEquals(taxAmount.unscaledValue(), moneyTaxAmount.toBigDecimal().unscaledValue(), "Tax rounding error")
    }

    @Test
    void testEquals() {
        Assertions.assertFalse(Money.of("20.44").equals(Money.of("30.55")), "Checking equals")
        Assertions.assertTrue(Money.of("20.44").equals(Money.of("20.44")), "Checking equals")
        Assertions.assertFalse(Money.ZERO.equals(Money.of("20.44")), "Checking equals")
        Assertions.assertFalse(Money.of("20.44").equals(Money.of("-20.44")), "Checking equals")
        Assertions.assertTrue(Money.of("40.66").equals(Money.of("30.55").add(Money.of("10.11"))), "Checking equals")
        Assertions.assertTrue(Money.of("40.66").equals(Money.of("30.55").add(new BigDecimal("10.11"))), "Checking equals")

        Assertions.assertTrue(Money.of("20.44").hashCode() == Money.of("20.44").hashCode(), "Checking equals")
        Assertions.assertFalse(Money.of("20.44").hashCode() == Money.of("20.45").hashCode(), "Checking equals")
    }

    @Test
    void testRoundingUpDivide() {
        Money money = Money.of("5.00")

        //division without rounding, 5/13 == 0.38
        Money money1 = money.divide(new BigDecimal(13))
        Assertions.assertEquals(Money.of("0.38"), money1, "division failed for value " + money1)

        //Rounded up to the nearest dollar, 5/13 == 0.38 (less then 0.50) should be rounded to the 1.00
        Money money2 = money.divide(new BigDecimal(13), true)
        Assertions.assertEquals(Money.ONE, money2, "1d rounding failed for value " + money2)

        //should be rounded to the 2.00
        Money money3 = money.divide(new BigDecimal(4), true)
        Assertions.assertEquals(Money.of("2.00"), money3, "2d rounding failed for value " + money3)

        // do nothing if value is already integer, 5/5 == 1
        Money money4 = money.divide(new BigDecimal(5), true)
        Assertions.assertEquals(Money.ONE, money4, "1d rounding failed for value " + money4)

        money = Money.of("-5.00")

        //the same test for negative value
        Money money5 = money.divide(new BigDecimal(13), true)
        Assertions.assertEquals(Money.of("-1.00"), money5, "1d rounding failed for value " + money3)

        Money money6 = Money.of(11.04)
        Assertions.assertEquals(Money.of("-1.00"), money5, "1d rounding failed for value " + money3)

    }

    @Test
    void testNumberFormatterUsingAustraliaLocale() {
        Money money = Money.of("5.00")
        Assertions.assertEquals("5.00", money.toPlainString())

        money = Money.of("-5.00")
        Assertions.assertEquals("-5.00", money.toPlainString())

        money = Money.of("-005.55")
        Assertions.assertEquals("-5.55", money.toPlainString())

        money = Money.of(-34.5555)
        Assertions.assertEquals("-34.56", money.toPlainString())

        money = Money.of(1892.5555)
        Assertions.assertEquals("1892.56", money.toPlainString())

        money = Money.ZERO
        Assertions.assertEquals("0.00", money.toPlainString())
    }

    @Test
    void testNumberFormatterInCustomLocale() {
        Money money = Money.of("5.00", MoneyContextFactory.create(Country.AUSTRALIA))
        Assertions.assertEquals("5.00", money.toPlainString())

        money = Money.of("-5.00", MoneyContextFactory.create(Country.US))
        Assertions.assertEquals("-5.00", money.toPlainString())

        money = Money.of("-005.55", MoneyContextFactory.create(Country.NORWAY))
        Assertions.assertEquals("-5.55", money.toPlainString())

        money = Money.of(-34.5555,  MoneyContextFactory.create(Country.EUROPE))
        Assertions.assertEquals("-34.56", money.toPlainString())

        money = Money.of(1892.5555,  MoneyContextFactory.create(Country.SOUTH_AFRICA))
        Assertions.assertEquals("1892.56", money.toPlainString())

        money = Money.ZERO
        Assertions.assertEquals("0.00", money.toPlainString())
    }

    @Test
    void testMoneyInCustomContext() {
        MoneyContext usContext = MoneyContextFactory.create(Country.US)
        Money usMoney = Money.of(1, usContext)

        Assertions.assertNotEquals(usMoney, Money.of(1))

        Assertions.assertEquals(usMoney, Money.of(1.0, usContext))
        Assertions.assertEquals(usMoney, Money.of('1.0', usContext))
        Assertions.assertEquals(usMoney, Money.of(1, usContext))

        Assertions.assertNotEquals(usMoney, Money.of(4.0, usContext))
        Assertions.assertNotEquals(usMoney, Money.of('4.0', usContext))
        Assertions.assertNotEquals(usMoney, Money.of(4, usContext))

        MoneyContext hongKongContext = MoneyContextFactory.create(Country.HONG_KONG)
        Assertions.assertNotEquals(usMoney, Money.of(1.0, hongKongContext))
        Assertions.assertNotEquals(usMoney, Money.of('1.0', hongKongContext))
        Assertions.assertNotEquals(usMoney, Money.of(1, hongKongContext))

        MoneyManager.updateSystemContext(usContext)

        Assertions.assertEquals(usMoney, Money.of(1))

        Assertions.assertEquals(usMoney, Money.of(1.0, usContext))
        Assertions.assertEquals(usMoney, Money.of('1.0', usContext))
        Assertions.assertEquals(usMoney, Money.of(1, usContext))

    }
}
