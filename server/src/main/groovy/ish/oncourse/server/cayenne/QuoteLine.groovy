/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.cayenne

import ish.oncourse.server.cayenne.glue._QuoteLine

import javax.annotation.Nonnull

class QuoteLine extends _QuoteLine {

    @Override
    Quote getInvoice() {
        return super.getQuote()
    }

    @Override
    void setInvoice(AbstractInvoice abstractInvoice) {
        super.setQuote((Quote) abstractInvoice)
    }

    @Override
    Enrolment getEnrolment() {
        return null
    }

    @Override
    void setEnrolment(Enrolment enrolment) {
        super.setEnrolment(enrolment)
    }

    @Override
    CourseClass getCourseClass() {
        return super.getCourseClass()
    }

    /**
     * @return list of discounts linked to this quote line
     */
    @Nonnull
    @Override
    List<InvoiceLineDiscount> getInvoiceLineDiscounts() {
        return super.getQuoteLineDiscounts()
    }
}
