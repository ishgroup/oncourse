/*
 * Copyright ish group pty ltd 2020.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.entity.mixins

import groovy.transform.CompileDynamic
import ish.math.Money
import ish.oncourse.API
import ish.oncourse.server.cayenne.Discount
import ish.oncourse.server.cayenne.DiscountCourseClass
import ish.oncourse.server.cayenne.Enrolment
import ish.oncourse.server.cayenne.InvoiceLine
import ish.util.DiscountUtils

@CompileDynamic
class DiscountCourseClassMixin {

    /**
     * Returns all the discounted enrolments in the related courseclasses created in the provided date ranged
     *
     * @param from start of date range
     * @param to end of date range
     * @return discount enrolments in the related courseClass
     */
    @API
    static getDiscountedEnrolments(DiscountCourseClass self, Date from, Date to) {
        return self.courseClass.getEnrolmentsWithinDateRange(from, to).findAll {
            Enrolment e -> !e.originalInvoiceLine.discountEachExTax.zero && e.originalInvoiceLine.invoiceLineDiscounts.find {
                ild -> ild.discount.equals(self.discount)
            }
        }
    }


    /**
     * Returns all the invoicelines for enrolments in the related courseclasses created in the provided date ranged
     *
     * @param from start of date range
     * @param to end of date range
     * @return invoicelines joined to discount enrolments in the related courseClass
     */
    @API
    static getDiscountedInvoiceLines(DiscountCourseClass self, Date from, Date to) {
        return getDiscountedEnrolments(self, from, to).collect { Enrolment e -> e.originalInvoiceLine }
    }

    /**
     * Returns the total amount discounted for enrolments created in a given date range
     *
     * @param from start of date range
     * @param to end of date range
     * @return total amount discounted
     */
    @API
    static getTotalDiscountedValue(DiscountCourseClass self, Date from, Date to) {
        Money totalFromToDiscountValue = Money.ZERO()
        List<Enrolment> enrolmentList = getDiscountedEnrolments(self, from ,to)


        for (Enrolment enrol : enrolmentList) {
            InvoiceLine invoiceLine = enrol.originalInvoiceLine
            if (invoiceLine.invoiceLineDiscounts.size() == 1) {
                totalFromToDiscountValue = totalFromToDiscountValue.add(invoiceLine.getDiscountEachExTax())
            } else {
                //need to get clear specification how we should calculate discountValue for each dicount applied to this enrolment
                BigDecimal rate = enrol.originalInvoiceLine.tax.rate
                Money feeExGst = enrol.courseClass.feeExGst

                List<Discount> discounts = enrol.originalInvoiceLine.discounts as List<Discount>
                Money currentTotalDiscountAmount = Money.ZERO()
                Money currentDiscountAmount = Money.ZERO()
                for (Discount discount : discounts) {

                    DiscountCourseClass classDiscount = self.classCost.discountCourseClass.find { dcc -> dcc.discount.equals(discount)}

                    Money discountValue = DiscountUtils.discountValue(classDiscount, feeExGst, rate)
                    currentTotalDiscountAmount = currentTotalDiscountAmount.add(discountValue)

                    if (discount.equals(self.getDiscount())) {
                        currentDiscountAmount = discountValue
                    }
                }

                if (currentTotalDiscountAmount.compareTo(Money.ZERO()) != 0) {
                    totalFromToDiscountValue = totalFromToDiscountValue.add(enrol.originalInvoiceLine.getDiscountTotalExTax()
                            .multiply(currentDiscountAmount.divide(currentTotalDiscountAmount)))
                }

            }
        }
        return totalFromToDiscountValue
    }
}
