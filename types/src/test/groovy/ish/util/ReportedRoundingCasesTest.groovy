/*
 * Copyright ish group pty ltd 2026.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */
package ish.util

import groovy.transform.CompileStatic
import ish.common.payable.IInvoiceLineInterface
import ish.common.payable.InvoicePayableLineWrapper
import ish.common.payable.MockInvoiceLine
import ish.common.types.DiscountType
import ish.math.Country
import ish.math.Money
import ish.math.MoneyContextFactory
import ish.math.MoneyManager
import ish.math.MoneyRounding
import ish.oncourse.cayenne.DiscountCourseClassInterface
import ish.oncourse.cayenne.DiscountInterface
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

import static org.mockito.Matchers.any
import static org.mockito.Mockito.*

/**
 * Rounding cases reported by QA in Jira, with the numbers from the tickets. Assertion messages start with the ticket key.
 * The same cases are checked on client in client-html/src/tests/common/utils/ReportedRoundingCases.test.ts
 */
@CompileStatic
class ReportedRoundingCasesTest {

    private static final BigDecimal GST = new BigDecimal("0.1")

    @BeforeAll
    static void setupEnvironment() {
        MoneyManager.updateSystemContext(MoneyContextFactory.create(Country.AUSTRALIA))
    }

    @Test
    void testFeeEnteredIncGstIsKeptWithoutExtraCent() {
        ["38", "50", "72", "93", "380", "545", "1345"].each { String fee ->
            Money feeIncTax = Money.of(fee)
            Money feeExTax = MoneyUtil.calculatePriceExFromPriceInc(feeIncTax, GST)
            Money taxEach = InvoiceUtil.calculateTaxEachForInvoiceLine(feeExTax, Money.ZERO, GST)

            Assertions.assertEquals(feeIncTax, feeExTax.add(taxEach), "CS-107, CS-160: \$${fee} inc GST".toString())
        }
    }

    /**
     * Checkout (Quick Enrol, web) applies class discount with DiscountUtils
     */
    @Test
    void testCheckoutTotalWithClassDiscount() {
        assertCheckoutTotal("CS-244 7: \$1222.73 with 50% discount", "1222.73", GST, percent(0.5), "672.50")
        assertCheckoutTotal("CS-244 III, 8: \$1222.73 with \$318.18 Dollar discount", "1222.73", GST, dollar("318.18"), "995.00")
        assertCheckoutTotal("CS-493: \$268.18 with \$10 Dollar discount", "268.18", GST, dollar("10"), "284.00")
        assertCheckoutTotal("CS-424: \$268.18 with \$9.09 Dollar discount", "268.18", GST, dollar("9.09"), "285.00")
        assertCheckoutTotal("CS-455: \$408.18 with 9.5% discount to nearest 10 cents", "408.18", GST, percent(0.095, MoneyRounding.ROUNDING_10C), "406.30")
        assertCheckoutTotal("CS-546: \$100 without tax with 10% discount", "100.00", BigDecimal.ZERO, percent(0.1), "90.00")
        assertCheckoutTotal("CS-546: \$100 without tax with 50% discount", "100.00", BigDecimal.ZERO, percent(0.5), "50.00")
        assertCheckoutTotal("CS-107: \$544.55 with 3% discount", "544.55", GST, percent(0.03), "581.03")
        assertCheckoutTotal("CS-107, SST-136: \$10.00 with 3.5% discount", "10.00", GST, percent(0.035), "10.62")
        assertCheckoutTotal("OD-18286: \$244.55 with 100% discount", "244.55", GST, percent(1.0), "0.00")
        assertCheckoutTotal("class discount override replaces discount amount", "1222.73", GST, percent(0.5, MoneyRounding.ROUNDING_NONE, Money.of("100")), "1235.00")

        // fee inc GST -> price ex tax -> total after $9 Dollar discount rounded to nearest dollar
        ["18.18": "10.00", "45.45": "40.00", "90.91": "90.00", "268.18": "285.00", "345.45": "370.00", "1222.73": "1335.00"].each { String price, String total ->
            assertCheckoutTotal("CS-160: \$${price} with \$9 Dollar discount to nearest dollar".toString(), price, GST, dollar("9", MoneyRounding.ROUNDING_1D), total)
        }
    }

    /**
     * Manual invoice line is saved with price, discount and tax calculated on client (InvoiceApiService.updateInvoiceLines),
     * client tax is kept when it is within a cent (InvoiceUtil.calculateTaxEachForManualInvoiceLine), so the total is the total client showed
     */
    @Test
    void testManualInvoiceLineTotalWithAmountsFromClient() {
        assertSavedLineTotal("CS-244 III: \$1222.73 x 3 with \$318.18 discount", "1222.73", "318.18", "90.45", 3, GST, "2985.00")
        assertSavedLineTotal("CS-244 5: \$1000 x 3 with \$500 discount", "1000.00", "500.00", "50.00", 3, GST, "1650.00")
        assertSavedLineTotal("CS-244 7: \$1222.73 with 50% discount", "1222.73", "611.37", "61.14", 1, GST, "672.50")
        assertSavedLineTotal("CS-493: \$268.18 with \$10 Dollar discount", "268.18", "10.00", "25.82", 1, GST, "284.00")
        assertSavedLineTotal("CS-455: \$408.18 with 9.5% discount to nearest 10 cents", "408.18", "38.82", "36.94", 1, GST, "406.30")
        assertSavedLineTotal("CS-107: \$544.55 with 3% discount", "544.55", "16.34", "52.82", 1, GST, "581.03")
        assertSavedLineTotal("CS-107, SST-136: \$10.00 with 3.5% discount", "10.00", "0.35", "0.97", 1, GST, "10.62")

        // the rounding cent goes to tax, as checkout does: (11.11 - 1.54) * 1.15 = 11.0055, the discount rounds the total to $11.00
        assertSavedLineTotal("CS-634: \$11.11 with 15% discount to nearest dollar at 15% tax", "11.11", "1.54", "1.43", 1, new BigDecimal("0.15"), "11.00")
        // $380 class fee with $34.55 Dollar discount is $342.00, while (345.45 - 34.55) * 1.1 = 341.99
        assertSavedLineTotal("CS-634: \$345.45 with \$34.55 discount and \$342.00 total", "345.45", "34.55", "31.10", 1, GST, "342.00")
        // totals are compared, not taxes: 3.45 * 1.1 = 3.795 rounds to 3.80, but its tax 0.345 rounds to 0.34
        assertSavedLineTotal("CS-634: \$3.45 with \$3.81 total typed in", "3.45", "0.00", "0.36", 1, GST, "3.81")
    }

    @Test
    void testTaxEachSentToClientAddsUpToSavedTotal() {
        // CS-107: $38 fee inc GST is 34.55 ex tax, stored tax 3.455 and saved total 38.005 -> $38.00, rounding the tax alone gives 3.46
        Money price = Money.of("34.55")
        Money storedTax = InvoiceUtil.calculateTaxEachForInvoiceLine(price, Money.ZERO, GST)
        Money taxSent = InvoiceUtil.getTaxEachToTotal(price, Money.ZERO, storedTax)

        Assertions.assertEquals(new BigDecimal("3.46"), storedTax.toBigDecimal())
        Assertions.assertEquals(new BigDecimal("3.45"), taxSent.toBigDecimal())

        // the line is opened and saved again without changes: the tax sent back is kept and the total stays $38.00
        Money taxSavedAgain = InvoiceUtil.calculateTaxEachForManualInvoiceLine(price, Money.ZERO, GST, taxSent)
        Assertions.assertEquals(new BigDecimal("3.45"), taxSavedAgain.toBigDecimal())
        Assertions.assertEquals(Money.of("38.00"), price.add(taxSavedAgain))

        // tax already in cents is sent as it is
        Assertions.assertEquals(new BigDecimal("31.10"), InvoiceUtil.getTaxEachToTotal(Money.of("345.45"), Money.of("34.55"), Money.of("31.10")).toBigDecimal())
        Assertions.assertEquals(new BigDecimal("19.00"), InvoiceUtil.getTaxEachToTotal(Money.of("200.00"), Money.of("10.00"), Money.of("19.00")).toBigDecimal())
    }

    @Test
    void testManualInvoiceLineTaxIsCalculatedWhenClientTaxIsOff() {
        Money price = Money.of("200.00")
        Money discount = Money.of("10.00")

        // tax sent for price without discount is not kept (the same case as in api-test invoice post.feature)
        Assertions.assertEquals(Money.of("19.00"), InvoiceUtil.calculateTaxEachForManualInvoiceLine(price, discount, GST, Money.of("20.00")))
        Assertions.assertEquals(Money.of("19.00"), InvoiceUtil.calculateTaxEachForManualInvoiceLine(price, discount, GST, Money.of("19.02")))
        Assertions.assertEquals(Money.of("19.01"), InvoiceUtil.calculateTaxEachForManualInvoiceLine(price, discount, GST, Money.of("19.01")))
        Assertions.assertEquals(Money.of("19.00"), InvoiceUtil.calculateTaxEachForManualInvoiceLine(price, discount, GST, null))
    }

    private void assertCheckoutTotal(String message, String priceExTax, BigDecimal taxRate, DiscountCourseClassInterface classDiscount, String expectedTotal) {
        Money price = Money.of(priceExTax)
        Money expected = Money.of(expectedTotal)

        Assertions.assertEquals(expected, DiscountUtils.getDiscountedFee(classDiscount, price, taxRate), message)

        IInvoiceLineInterface invoiceLine = invoiceLine(price)
        DiscountUtils.applyDiscounts(classDiscount, invoiceLine, taxRate, Money.ZERO)
        Assertions.assertEquals(expected, invoiceLine.getPriceEachExTax().subtract(invoiceLine.getDiscountEachExTax()).add(invoiceLine.getTaxEach()), message)
    }

    private static void assertSavedLineTotal(String message, String priceExTax, String discountExTax, String clientTaxEach, int quantity,
                                             BigDecimal taxRate, String expectedTotal) {
        Money price = Money.of(priceExTax)
        Money discount = Money.of(discountExTax)
        MockInvoiceLine line = new MockInvoiceLine(price, discount, new BigDecimal(quantity), taxRate)
        line.setTaxEach(InvoiceUtil.calculateTaxEachForManualInvoiceLine(price, discount, taxRate, Money.of(clientTaxEach)))

        Assertions.assertEquals(Money.of(clientTaxEach), line.getTaxEach(), "${message}: client tax is kept".toString())
        // the total comes from the wrapper the same way as for AbstractInvoiceLine
        Assertions.assertEquals(Money.of(expectedTotal), new InvoicePayableLineWrapper(line).getFinalPriceToPayIncTax(), message)
    }

    private static DiscountCourseClassInterface percent(BigDecimal discountPercent, MoneyRounding rounding = MoneyRounding.ROUNDING_NONE, Money discountOverride = null) {
        DiscountInterface discount = discount(DiscountType.PERCENT, rounding)
        when(discount.getDiscountPercent()).thenReturn(discountPercent)
        return classDiscount(discount, discountOverride)
    }

    private static DiscountCourseClassInterface dollar(String discountDollar, MoneyRounding rounding = MoneyRounding.ROUNDING_NONE) {
        DiscountInterface discount = discount(DiscountType.DOLLAR, rounding)
        when(discount.getDiscountDollar()).thenReturn(Money.of(discountDollar))
        return classDiscount(discount, null)
    }

    private static DiscountInterface discount(DiscountType type, MoneyRounding rounding) {
        DiscountInterface discount = mock(DiscountInterface.class)
        when(discount.getDiscountMax()).thenReturn(Money.ZERO)
        when(discount.getDiscountMin()).thenReturn(Money.ZERO)
        when(discount.getRounding()).thenReturn(rounding)
        when(discount.getDiscountType()).thenReturn(type)
        return discount
    }

    private static DiscountCourseClassInterface classDiscount(DiscountInterface discount, Money discountOverride) {
        DiscountCourseClassInterface classDiscount = mock(DiscountCourseClassInterface.class)
        when(classDiscount.getDiscount()).thenReturn(discount)
        when(classDiscount.getDiscountDollar()).thenReturn(discountOverride)
        return classDiscount
    }

    private IInvoiceLineInterface invoiceLine(Money priceExTax) {
        IInvoiceLineInterface invoiceLine = mock(InvoiceLine.class)
        when(invoiceLine.getPriceEachExTax()).thenReturn(priceExTax)
        when(invoiceLine.getDiscountEachExTax()).thenCallRealMethod()
        when(invoiceLine.getTaxEach()).thenCallRealMethod()
        doCallRealMethod().when(invoiceLine).setDiscountEachExTax(any(Money.class))
        doCallRealMethod().when(invoiceLine).setTaxEach(any(Money.class))
        return invoiceLine
    }

    abstract class InvoiceLine implements IInvoiceLineInterface {
        private Money discountEachExTax
        private Money taxEach

        @Override
        Money getDiscountEachExTax() {
            return discountEachExTax
        }

        @Override
        void setDiscountEachExTax(Money discountEachExTax) {
            this.discountEachExTax = discountEachExTax
        }

        @Override
        Money getTaxEach() {
            return taxEach
        }

        @Override
        void setTaxEach(Money taxEach) {
            this.taxEach = taxEach
        }
    }
}
