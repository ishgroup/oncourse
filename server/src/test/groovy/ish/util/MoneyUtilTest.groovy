/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.util

import ish.IshTestCase
import ish.math.Money
import org.junit.Test

import static org.junit.Assert.assertEquals

/**
 */
class MoneyUtilTest  {
	private static HashMap<Money, Money> listOne = new HashMap<>()
    private static HashMap<Money, Money> listTwo = new HashMap<>()

    static {

        listOne.put(new Money("10"), new Money("11"))
        listOne.put(new Money("15"), new Money("16.5"))
        listOne.put(new Money("1"), new Money("1.1"))
        listOne.put(new Money("0.1"), new Money("0.11"))
        listOne.put(new Money("0.01"), new Money("0.01"))

        listTwo.put(new Money("0.1"), Money.ZERO)
        listTwo.put(new Money("1"), Money.ZERO)
        listTwo.put(new Money("2"), Money.ZERO)
        listTwo.put(new Money("3"), Money.ZERO)
        listTwo.put(new Money("4"), Money.ZERO)
        listTwo.put(new Money("5"), new Money("-0.01"))
        listTwo.put(new Money("6"), Money.ZERO)
        listTwo.put(new Money("7"), Money.ZERO)
        listTwo.put(new Money("8"), Money.ZERO)
        listTwo.put(new Money("9"), Money.ZERO)
        listTwo.put(new Money("10"), Money.ZERO)
        listTwo.put(new Money("60"), new Money("-0.01"))
        listTwo.put(new Money("61"), Money.ZERO)
        listTwo.put(new Money("62"), Money.ZERO)
        listTwo.put(new Money("63"), Money.ZERO)
        listTwo.put(new Money("64"), Money.ZERO)
        listTwo.put(new Money("65"), Money.ZERO)
        listTwo.put(new Money("66"), Money.ZERO)
        listTwo.put(new Money("67"), Money.ZERO)
        listTwo.put(new Money("68"), Money.ZERO)
        listTwo.put(new Money("69"), Money.ZERO)
        listTwo.put(new Money("70"), Money.ZERO)
        listTwo.put(new Money("71"), new Money("-0.01"))
        listTwo.put(new Money("72"), Money.ZERO)
        listTwo.put(new Money("73"), Money.ZERO)
        listTwo.put(new Money("74"), Money.ZERO)
        listTwo.put(new Money("75"), Money.ZERO)
        listTwo.put(new Money("76"), Money.ZERO)
        listTwo.put(new Money("77"), Money.ZERO)
        listTwo.put(new Money("78"), Money.ZERO)
        listTwo.put(new Money("79"), Money.ZERO)
        listTwo.put(new Money("80"), Money.ZERO)
        listTwo.put(new Money("160"), Money.ZERO)
        listTwo.put(new Money("161"), Money.ZERO)
        listTwo.put(new Money("162"), Money.ZERO)
        listTwo.put(new Money("163"), Money.ZERO)
        listTwo.put(new Money("164"), Money.ZERO)
        listTwo.put(new Money("165"), Money.ZERO)
        listTwo.put(new Money("166"), Money.ZERO)
        listTwo.put(new Money("167"), Money.ZERO)
        listTwo.put(new Money("168"), Money.ZERO)
        listTwo.put(new Money("169"), Money.ZERO)
        listTwo.put(new Money("170"), new Money("-0.01"))
        listTwo.put(new Money("171"), Money.ZERO)
        listTwo.put(new Money("172"), Money.ZERO)
        listTwo.put(new Money("173"), Money.ZERO)
        listTwo.put(new Money("174"), Money.ZERO)
        listTwo.put(new Money("175"), Money.ZERO)
        listTwo.put(new Money("176"), Money.ZERO)
        listTwo.put(new Money("177"), Money.ZERO)
        listTwo.put(new Money("178"), Money.ZERO)
        listTwo.put(new Money("179"), Money.ZERO)
        listTwo.put(new Money("180"), Money.ZERO)

        listTwo.put(new Money("111.11"), Money.ZERO)
        listTwo.put(new Money("77.77"), Money.ZERO)
        listTwo.put(new Money("66.66"), Money.ZERO)
        listTwo.put(new Money("154.00"), Money.ZERO)
        listTwo.put(new Money("124.00"), Money.ZERO)
        listTwo.put(new Money("155.00"), Money.ZERO)
        listTwo.put(new Money("156.00"), Money.ZERO)

        listTwo.put(new Money("54.00"), Money.ZERO)
        listTwo.put(new Money("55.00"), Money.ZERO)
        listTwo.put(new Money("56.00"), Money.ZERO)

        listTwo.put(new Money("95.94"), Money.ZERO)
        listTwo.put(new Money("95.95"), Money.ZERO)
        listTwo.put(new Money("95.96"), Money.ZERO)
        listTwo.put(new Money("95.97"), new Money("-0.01"))
        listTwo.put(new Money("95.98"), Money.ZERO)
    }

	@Test
    void testGetPriceIncGst() {
		System.out.println("test getPriceIncGst")

        for (Money input : listOne.keySet()) {
			// System.out.println("testFormatValue input: " + input);
			Money output = MoneyUtil.getPriceIncTax(input, new BigDecimal(1), Money.ZERO)
            // System.out.println("testFormatValue output: " + output);
			assertEquals(listOne.get(input).doubleValue(), output.doubleValue(), 0.001)
        }
	}

	@Test
    void testCalculateTaxAdjustment() {
		System.out.println("test calculateTaxAdjustment")

        for (Money inc : listTwo.keySet()) {
			Money taxajd = listTwo.get(inc)
            BigDecimal taxRate = new BigDecimal("1.1")
            Money ex = inc.divide(taxRate)
            // System.out.println("testFormatValue input: " + inc);
			Money output = MoneyUtil.calculateTaxAdjustment(inc, ex, new BigDecimal(1))
            // System.out.println("testFormatValue input: " + inc + " ... " + output);

			assertEquals(taxajd.doubleValue(), output.doubleValue(), 0.001)
        }
	}

}
