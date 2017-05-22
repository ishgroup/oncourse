package ish.oncourse.willow.checkout.functions

import ish.math.Money
import ish.oncourse.model.CourseClass
import ish.oncourse.model.InvoiceLine
import ish.util.InvoiceUtil

class CalculatePrice {
    
    Money priceEachEx
    Money discountEachEx
    BigDecimal taxRate
    Money taxAdjustment


    CalculatePrice(Money priceEachEx, Money discountEachEx, BigDecimal taxRate, Money taxAdjustment) {
        this.priceEachEx = priceEachEx
        this.discountEachEx = discountEachEx
        this.taxRate = taxRate
        this.taxAdjustment = taxAdjustment
    }

    Money calculate() {
        InvoiceLine invoiceLine = new InvoiceLine()
        InvoiceUtil.fillInvoiceLine(invoiceLine, priceEachEx, Money.ZERO, taxRate, taxAdjustment)
        invoiceLine.priceEachIncTax
    }

    static Money calculateTaxAdjustment(CourseClass courseClass) {
        return courseClass.feeIncGst.subtract(courseClass.feeExGst.multiply(courseClass.taxRate.add(BigDecimal.ONE)))
    }
}
