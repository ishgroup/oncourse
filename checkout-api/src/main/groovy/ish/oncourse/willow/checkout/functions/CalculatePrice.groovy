package ish.oncourse.willow.checkout.functions

import ish.math.Money
import ish.oncourse.model.CourseClass
import ish.oncourse.model.DiscountCourseClass
import ish.oncourse.model.InvoiceLine
import ish.util.DiscountUtils
import ish.util.InvoiceUtil

class CalculatePrice {
    
    Money priceEachEx
    Money discountEachEx
    BigDecimal taxRate
    Money taxAdjustment
    
    private InvoiceLine invoiceLine

    CalculatePrice(Money priceEachEx, Money discountEachEx, BigDecimal taxRate, Money taxAdjustment) {
        this.priceEachEx = priceEachEx
        this.discountEachEx = discountEachEx
        this.taxRate = taxRate
        this.taxAdjustment = taxAdjustment
        this.invoiceLine = new InvoiceLine()
        this.invoiceLine.quantity = BigDecimal.ONE
    }

    CalculatePrice calculate() {
        InvoiceUtil.fillInvoiceLine(invoiceLine, priceEachEx, Money.ZERO, taxRate, taxAdjustment)
        this
    }
    
    CalculatePrice applyDiscount(DiscountCourseClass chosenDiscount) {
        DiscountUtils.applyDiscounts(chosenDiscount, invoiceLine, taxRate, taxAdjustment)
        this
    }
    
    Money getFinalPriceToPayIncTax() {
        invoiceLine.finalPriceToPayIncTax
    }

    static Money calculateTaxAdjustment(CourseClass courseClass) {
        return courseClass.getFeeIncGst(null).subtract(courseClass.feeExGst.multiply(courseClass.taxRate.add(BigDecimal.ONE)))
    }
}
