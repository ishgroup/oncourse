package ish.oncourse.services.discount

import groovy.transform.CompileStatic
import ish.math.Money
import ish.oncourse.model.Discount
import ish.oncourse.model.DiscountCourseClass
import ish.util.DateTimeUtil
import ish.util.DiscountUtils

class WebDiscountUtils {

    static List<DiscountItem> sortByDiscountValue(List<DiscountCourseClass> discounts, Money classFee, BigDecimal tax) {

        ArrayList<DiscountItem> discountItems = new ArrayList<>()

        if (!discounts.empty) {
            discounts.groupBy { DiscountCourseClass d -> DiscountUtils.getDiscountedFee(d, classFee, tax) }
                    .sort { Map.Entry it -> it.key }
                    .each { Money fee, List<DiscountCourseClass> classDiscounts ->
                discountItems << new DiscountItem(discounts: classDiscounts*.discount, feeIncTax: fee).init()
            }
        }
        return discountItems
    }

    /**
     * Retrieves expiry date of discount to apply.
     *
     * @param discount
     * @return
     */
    static Date expiryDate(Discount discount, Date classStart) {
        Date result = null

        if (discount.validTo) {
            Calendar expiryDate = Calendar.getInstance()
            expiryDate.setTime(discount.validTo)
            // the whole last day discount should be available
            expiryDate.set(Calendar.HOUR_OF_DAY, 23)
            expiryDate.set(Calendar.MINUTE, 59)
            expiryDate.set(Calendar.SECOND, 59)
            result = expiryDate.getTime()

        } else if (discount.validToOffset && classStart) {
            Calendar expiryDate = Calendar.getInstance()
            expiryDate.setTime(classStart)
            // the whole last day discount should be available
            expiryDate.set(Calendar.HOUR_OF_DAY, 23)
            expiryDate.set(Calendar.MINUTE, 59)
            expiryDate.set(Calendar.SECOND, 59)
            expiryDate.add(Calendar.DATE, discount.validToOffset)
            result = expiryDate.getTime()
        }

        return result
    }

    static List<DiscountCourseClass> filterValidDateRange(List<DiscountCourseClass> discounts, Date classStartDate) {
        Date now = new Date()

        if (classStartDate != null) {
            int startClassOffsetInDays = DateTimeUtil.getDaysLeapYearDaylightSafe(classStartDate, now)

            discounts.findAll {
                ((!it.discount.validTo && !it.discount.validToOffset) ||
                        (it.discount.validTo && it.discount.validTo.after(now)) ||
                        (it.discount.validToOffset && it.discount.validToOffset >= startClassOffsetInDays)) &&
                        ((!it.discount.validFrom && !it.discount.validFromOffset) ||
                                (it.discount.validFrom && it.discount.validFrom.before(now)) ||
                                (it.discount.validFromOffset && it.discount.validFromOffset <= startClassOffsetInDays))
            }

        } else {
            discounts.findAll {
                (!it.discount.validTo && !it.discount.validToOffset) || (it.discount.validTo && it.discount.validTo.after(now)) &&
                        (!it.discount.validFrom && !it.discount.validFromOffset) || (it.discount.validTo && it.discount.validTo.after(now))
            }
        }
    }
}
