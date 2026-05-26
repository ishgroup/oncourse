/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.cayenne

import java.math.RoundingMode

trait DiscountCourseClassTrait {
    
    abstract CourseClass getCourseClass()
    abstract Discount getDiscount()
    
    BigDecimal getActualUsePercent() {
        
        List<Enrolment> enrolments = courseClass.successAndQueuedEnrolments
        BigDecimal totalCount = enrolments.size().toBigDecimal()
        if (totalCount > 0) {
            BigDecimal discountedCount =  (enrolments*.invoiceLines.flatten() as List<InvoiceLine>)
                    .findAll {il -> discount.id in il.invoiceLineDiscounts*.discount*.id}
                    .size().toBigDecimal()
            return (discountedCount*100/totalCount).setScale(2, RoundingMode.HALF_EVEN)
        } else {
            return BigDecimal.ZERO
        }
    }
}