package ish.oncourse.willow.checkout.functions

import ish.math.Money
import ish.oncourse.model.CourseClass
import ish.oncourse.model.DiscountCourseClass
import ish.oncourse.model.Student
import ish.oncourse.services.discount.GetAppliedDiscounts
import ish.oncourse.services.discount.GetPossibleDiscounts
import ish.oncourse.services.discount.WebDiscountUtils
import ish.oncourse.util.FormatUtils
import ish.oncourse.willow.model.web.CourseClassPrice
import ish.oncourse.willow.model.web.Discount
import ish.util.DiscountUtils


class BuildClassPrice {


    CourseClass c
    boolean allowByApplication = false
    Money overridenFee = null
    List<ish.oncourse.model.Discount> promotions

    BuildClassPrice(CourseClass c, boolean allowByApplication, Money overridenFee, List<ish.oncourse.model.Discount> promotions) {
        this.c = c
        this.allowByApplication = allowByApplication
        this.overridenFee = overridenFee
        this.promotions = promotions
    }
    
    CourseClassPrice build() {

        new CourseClassPrice().with { ccp ->
            ccp.fee = c.feeIncGst.doubleValue()
            ccp.hasTax = !c.gstExempt
            ccp.feeOverriden = overridenFee ? (c.gstExempt ? overridenFee.doubleValue() : overridenFee.multiply(BigDecimal.ONE.add(c.taxRate)).doubleValue()) : (Double) null
            if (!feeOverriden) {
                DiscountCourseClass bestDiscount = (DiscountCourseClass) DiscountUtils.chooseDiscountForApply(GetAppliedDiscounts.valueOf(c, promotions).get(), c.feeExGst, c.taxRate)

                if (bestDiscount) {
                    ccp.appliedDiscount = new Discount().with { d ->
                        Money value = c.getDiscountedFeeIncTax(bestDiscount)
                        d.discountedFee = value.doubleValue()
                        d.discountValue = c.feeIncGst.subtract(value).doubleValue()
                        d.title = ((ish.oncourse.model.Discount) bestDiscount.discount).name
                        Date discountExpiryDate = WebDiscountUtils.expiryDate((ish.oncourse.model.Discount) bestDiscount.discount, c.startDate)
                        if (discountExpiryDate) {
                            d.title = d.title + " expires ${FormatUtils.getShortDateFormat(c.college.timeZone).format(discountExpiryDate)}"
                        }
                        d
                    }
                }
                
                WebDiscountUtils.sortByDiscountValue(GetPossibleDiscounts.valueOf(c).get(), c.feeExGst, c.taxRate).each { d ->
                    ccp.possibleDiscounts << new Discount(discountedFee: d.feeIncTax.doubleValue(), title: d.title)
                }
            }
            ccp
        }
    }
    
}
