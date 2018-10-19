package ish.oncourse.willow.checkout.functions

import ish.math.Money
import ish.oncourse.model.CourseClass
import ish.oncourse.model.DiscountCourseClass
import ish.oncourse.model.InvoiceLine
import ish.oncourse.model.Product
import ish.oncourse.model.Tax
import ish.util.DiscountUtils
import ish.util.InvoiceUtil

class CalculatePrice {
    
    Money priceEachEx
    Money discountEachEx
    
    BigDecimal taxRate
    Money taxAdjustment

    private InvoiceLine invoiceLine

    private CalculatePrice (Money priceEachEx, Money discountEachEx) {
        this.priceEachEx = priceEachEx
        this.discountEachEx = discountEachEx
        this.invoiceLine = new InvoiceLine()
        this.invoiceLine.quantity = BigDecimal.ONE
    }

    CalculatePrice(Money priceEachEx, Money discountEachEx, BigDecimal taxRate, Money taxAdjustment) {
        this(priceEachEx, discountEachEx)
        this.taxRate = taxRate
        this.taxAdjustment = taxAdjustment
    }



   CalculatePrice(Money priceEachEx, Money discountEachEx, Tax taxOverride, CourseClass courseClass) {
        this(priceEachEx, discountEachEx)
       
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

   CalculatePrice(Money priceEachEx, Money discountEachEx, Tax taxOverride, Product product) {
       
        this(priceEachEx, discountEachEx)

        if (taxOverride != null && taxOverride.rate != null) {
            this.taxRate = taxOverride.rate
        } else {
            product.taxRate
        }

        if (taxOverride == null) {
            this.taxAdjustment = product.taxAdjustment
        } else {
            this.taxAdjustment = Money.ZERO
        }

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
