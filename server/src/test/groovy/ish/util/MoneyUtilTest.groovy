/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.util

import groovy.transform.CompileStatic
import ish.math.Country
import ish.math.Money
import ish.math.MoneyContextFactory
import ish.math.MoneyManager
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test


@CompileStatic
class MoneyUtilTest {

    @BeforeAll
    static void setupEnvironment() {
        MoneyManager.updateSystemContext(MoneyContextFactory.create(Country.AUSTRALIA))
    }
    private static HashMap<Money, Money> listOne = new HashMap<>()
    private static HashMap<Money, Money> listTwo = new HashMap<>()

    static {

        listOne.put(Money.of("10"), Money.of("11"))
        listOne.put(Money.of("15"), Money.of("16.5"))
        listOne.put(Money.of("1"), Money.of("1.1"))
        listOne.put(Money.of("0.1"), Money.of("0.11"))
        listOne.put(Money.of("0.01"), Money.of("0.01"))

        listTwo.put(Money.of("0.1"), Money.ZERO)
        listTwo.put(Money.of("1"), Money.ZERO)
        listTwo.put(Money.of("2"), Money.ZERO)
        listTwo.put(Money.of("3"), Money.ZERO)
        listTwo.put(Money.of("4"), Money.ZERO)
        listTwo.put(Money.of("5"), Money.ZERO)
        listTwo.put(Money.of("6"), Money.ZERO)
        listTwo.put(Money.of("7"), Money.ZERO)
        listTwo.put(Money.of("8"), Money.ZERO)
        listTwo.put(Money.of("9"), Money.ZERO)
        listTwo.put(Money.of("10"), Money.ZERO)
        listTwo.put(Money.of("60"), Money.ZERO)
        listTwo.put(Money.of("61"), Money.ZERO)
        listTwo.put(Money.of("62"), Money.ZERO)
        listTwo.put(Money.of("63"), Money.ZERO)
        listTwo.put(Money.of("64"), Money.ZERO)
        listTwo.put(Money.of("65"), Money.ZERO)
        listTwo.put(Money.of("66"), Money.ZERO)
        listTwo.put(Money.of("67"), Money.ZERO)
        listTwo.put(Money.of("68"), Money.ZERO)
        listTwo.put(Money.of("69"), Money.ZERO)
        listTwo.put(Money.of("70"), Money.ZERO)
        listTwo.put(Money.of("71"), Money.ZERO)
        listTwo.put(Money.of("72"), Money.ZERO)
        listTwo.put(Money.of("73"), Money.ZERO)
        listTwo.put(Money.of("74"), Money.ZERO)
        listTwo.put(Money.of("75"), Money.ZERO)
        listTwo.put(Money.of("76"), Money.ZERO)
        listTwo.put(Money.of("77"), Money.ZERO)
        listTwo.put(Money.of("78"), Money.ZERO)
        listTwo.put(Money.of("79"), Money.ZERO)
        listTwo.put(Money.of("80"), Money.ZERO)
        listTwo.put(Money.of("160"), Money.ZERO)
        listTwo.put(Money.of("161"), Money.ZERO)
        listTwo.put(Money.of("162"), Money.ZERO)
        listTwo.put(Money.of("163"), Money.ZERO)
        listTwo.put(Money.of("164"), Money.ZERO)
        listTwo.put(Money.of("165"), Money.ZERO)
        listTwo.put(Money.of("166"), Money.ZERO)
        listTwo.put(Money.of("167"), Money.ZERO)
        listTwo.put(Money.of("168"), Money.ZERO)
        listTwo.put(Money.of("169"), Money.ZERO)
        listTwo.put(Money.of("170"), Money.ZERO)
        listTwo.put(Money.of("171"), Money.ZERO)
        listTwo.put(Money.of("172"), Money.ZERO)
        listTwo.put(Money.of("173"), Money.ZERO)
        listTwo.put(Money.of("174"), Money.ZERO)
        listTwo.put(Money.of("175"), Money.ZERO)
        listTwo.put(Money.of("176"), Money.ZERO)
        listTwo.put(Money.of("177"), Money.ZERO)
        listTwo.put(Money.of("178"), Money.ZERO)
        listTwo.put(Money.of("179"), Money.ZERO)
        listTwo.put(Money.of("180"), Money.ZERO)

        listTwo.put(Money.of("111.11"), Money.ZERO)
        listTwo.put(Money.of("77.77"), Money.ZERO)
        listTwo.put(Money.of("66.66"), Money.ZERO)
        listTwo.put(Money.of("154.00"), Money.ZERO)
        listTwo.put(Money.of("124.00"), Money.ZERO)
        listTwo.put(Money.of("155.00"), Money.ZERO)
        listTwo.put(Money.of("156.00"), Money.ZERO)

        listTwo.put(Money.of("54.00"), Money.ZERO)
        listTwo.put(Money.of("55.00"), Money.ZERO)
        listTwo.put(Money.of("56.00"), Money.ZERO)

        listTwo.put(Money.of("95.94"), Money.ZERO)
        listTwo.put(Money.of("95.95"), Money.ZERO)
        listTwo.put(Money.of("95.96"), Money.ZERO)
        listTwo.put(Money.of("95.97"), Money.ZERO)
        listTwo.put(Money.of("95.98"), Money.ZERO)
    }

    @Test
    void testGetPriceIncGst() {
        for (Money input : listOne.keySet()) {
            Money output = MoneyUtil.getPriceIncTax(input, new BigDecimal("0.1"), Money.ZERO)
            Assertions.assertEquals(listOne.get(input), output)
        }
    }

    @Test
    void testCalculateTaxAdjustment() {
        for (Money inc : listTwo.keySet()) {
            Money taxajd = listTwo.get(inc)
            BigDecimal taxRate = new BigDecimal("1.1")
            Money ex = inc.divide(taxRate)
            Money output = MoneyUtil.calculateTaxAdjustment(inc, ex, new BigDecimal("0.1"))

            Assertions.assertEquals(taxajd, output)
        }
    }

    @Test
    void testCalculatePriceExFromPriceInc() {
        // 110 inc 10% tax -> 100 ex
        Money priceInc = Money.of(110)
        BigDecimal taxRate = new BigDecimal("0.10")
        Money result = MoneyUtil.calculatePriceExFromPriceInc(priceInc, taxRate)
        Assertions.assertEquals(Money.of(100), result, "110 / 1.10 should equal 100.00")
    }

    @Test
    void testRoundingInTaxCalculation() {
        // 11 / 1.10 = 10.00 exactly
        Money priceInc = Money.of(11)
        BigDecimal taxRate = new BigDecimal("0.10")
        Money result = MoneyUtil.calculatePriceExFromPriceInc(priceInc, taxRate)
        Assertions.assertNotNull(result)
        Assertions.assertEquals(Money.of(10), result, "11 / 1.10 should equal 10.00")
    }

    @Test
    void testCalculatePriceExWithNullPrice() {
        Money result = MoneyUtil.calculatePriceExFromPriceInc(null, new BigDecimal("0.10"))
        Assertions.assertEquals(Money.ZERO, result, "null price should return Money.ZERO")
    }

    @Test
    void testCalculatePriceExWithNullTaxRate() {
        Money priceInc = Money.of(110)
        Money result = MoneyUtil.calculatePriceExFromPriceInc(priceInc, null)
        Assertions.assertEquals(priceInc, result, "null taxRate should return the price unchanged")
    }

    @Test
    void testCalculatePriceExWithZeroTaxRate() {
        // 0% tax: ex-tax == inc-tax
        Money priceInc = Money.of(50)
        BigDecimal taxRate = BigDecimal.ZERO
        Money result = MoneyUtil.calculatePriceExFromPriceInc(priceInc, taxRate)
        Assertions.assertEquals(priceInc, result, "0% tax: ex-tax price must equal inc-tax price")
    }

}
