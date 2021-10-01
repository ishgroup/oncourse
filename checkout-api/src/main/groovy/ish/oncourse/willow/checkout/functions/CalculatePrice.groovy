package ish.oncourse.willow.checkout.functions

import groovy.transform.CompileStatic
import ish.math.Money
import ish.oncourse.model.CourseClass
import ish.oncourse.model.DiscountCourseClass
import ish.oncourse.model.InvoiceLine
import ish.oncourse.model.Product
import ish.oncourse.model.Tax
import ish.util.DiscountUtils
import ish.util.InvoiceUtil

@CompileStatic
class CalculatePrice {
    
    Money priceEachEx
    Money discountEachEx
    
    BigDecimal taxRate
    Money taxAdjustment
    BigDecimal quantity

    private InvoiceLine invoiceLine

    private CalculatePrice (Money priceEachEx, Money discountEachEx, BigDecimal quantity) {
        this.priceEachEx = priceEachEx
        this.discountEachEx = discountEachEx
        this.invoiceLine = new InvoiceLine()
        this.quantity = quantity
    }

    CalculatePrice(Money priceEachEx, Money discountEachEx, BigDecimal taxRate, Money taxAdjustment, BigDecimal quantity) {
        this(priceEachEx, discountEachEx, quantity)
        this.taxRate = taxRate
        this.taxAdjustment = taxAdjustment
    }

   CalculatePrice(Money priceEachEx, Money discountEachEx, Tax taxOverride, CourseClass courseClass, BigDecimal quantity) {
        this(priceEachEx, discountEachEx, quantity)
       
        if (taxOverride != null && taxOverride.rate != null) {
            this.taxRate = taxOverride.rate
        } else {
            this.taxRate = courseClass.taxRate
        }
        
        if (taxOverride == null) {
            this.taxAdjustment = calculateTaxAdjustment(courseClass)
        } else {
            this.taxAdjustment = Money.ZERO
        }

    }

   CalculatePrice(Money priceEachEx, Money discountEachEx, Tax taxOverride, Product product, BigDecimal quantity) {
       
        this(priceEachEx, discountEachEx, quantity)

        if (taxOverride != null && taxOverride.rate != null) {
            this.taxRate = taxOverride.rate
        } else {
            this.taxRate = product.taxRate
        }

        if (taxOverride == null) {
            this.taxAdjustment = product.taxAdjustment
        } else {
            this.taxAdjustment = Money.ZERO
        }

    }

    CalculatePrice calculate() {
        InvoiceUtil.fillInvoiceLine(invoiceLine, priceEachEx, Money.ZERO, taxRate, taxAdjustment, quantity)
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
