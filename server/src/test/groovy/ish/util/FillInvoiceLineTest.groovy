/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.util

import groovy.transform.CompileStatic
import ish.math.Money
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

@CompileStatic
class FillInvoiceLineTest {


    @Test
    void testFillInvoiceLine() {
        BigDecimal taxRate = new BigDecimal("0.1")

        // test from $0.00 to $100.01
        for (int i = 0; i < 10001; i++) {
            Money priceInc = Money.of("" + (i / 100d))
            Money priceEx = MoneyUtil.calculatePriceExFromPriceInc(priceInc, taxRate)
            Money taxAdjustment = MoneyUtil.calculateTaxAdjustment(priceInc, priceEx, taxRate)
            Money taxEach = InvoiceUtil.calculateTaxEachForInvoiceLine(priceEx, Money.ZERO, taxRate, taxAdjustment)
            // assume that the price inc equals price summed from the invoice line
            Assertions.assertEquals(priceInc, priceEx.subtract(Money.ZERO).add(taxEach))
        }
    }

    @Test
    void testFillInvoiceLine2() {
        BigDecimal taxRate = new BigDecimal("0.1")

        // test from $93.00 to $95.01
        for (int i = 9300; i < 9501; i++) {
            Money priceInc = Money.of("" + (i / 100d))
            Money priceEx = MoneyUtil.calculatePriceExFromPriceInc(priceInc, taxRate)
            Money taxAdjustment = MoneyUtil.calculateTaxAdjustment(priceInc, priceEx, taxRate)

            // test discount value from $0 to $2 with $0.01 step
            for (int j = 0; j <= 200; j++) {
                Money discountExTax = Money.of("" + (j / 100d))
                Money discountIncTax = MoneyUtil.getPriceIncTax(discountExTax, taxRate, Money.ZERO)
                Money taxEach = InvoiceUtil.calculateTaxEachForInvoiceLine(priceEx, discountExTax, taxRate, taxAdjustment)
                // assume that the price inc - discount inc equals price summed from the invoice line
                Assertions.assertEquals(priceInc.subtract(discountIncTax), priceEx.subtract(discountExTax).add(taxEach),
                        "$priceInc = $priceEx - $discountExTax + $taxEach")
            }

        }
    }
}
