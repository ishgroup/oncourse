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

import ish.common.types.EnrolmentStatus
import ish.math.Money
import ish.oncourse.API
import ish.oncourse.server.cayenne.Discount
import ish.oncourse.server.cayenne.Enrolment
import ish.persistence.CommonExpressionFactory

import javax.annotation.Nonnull
import javax.annotation.Nullable

class DiscountMixin {

    /**
     * Get all the active enrolments which used this discount and the amount of the discount isn't $0
     *
     * @param self
     * @return a list of enrolments
     */
	@API
	static @Nonnull List<Enrolment> getDiscountedEnrolments(Discount self) {
		return self.invoiceLineDiscounts.collect { ild -> ild.invoiceLine.enrolment }.findAll {
			Enrolment e -> EnrolmentStatus.STATUSES_LEGIT.contains(e.status) && !e.originalInvoiceLine.discountEachExTax.zero
		}
	}

    /**
     * Get all the active enrolments which used this discount and the amount of the discount isn't $0
     * Filter that list of enrolments to a date range. If you pass a null date in either option, then that part of the
     * filter is not applied. The date range applies to the invoice creation date, not the enrolment date.
     *
     * @param self
     * @param from the starting date of the range. This date will automatically be extended to the previous midnight
     * @param to the ending date of the range. This date will automatically be extended to the next midnight
     * @return a list of enrolments
     */
	@API
	static @Nonnull List<Enrolment> getDiscountedEnrolments(Discount self, @Nullable Date from, @Nullable Date to) {
		return getDiscountedEnrolments(self).findAll {
			e -> from == null || e.originalInvoiceLine.createdOn >= CommonExpressionFactory.previousMidnight(from)
		}.findAll {
			e -> to == null || e.originalInvoiceLine.createdOn <= CommonExpressionFactory.nextMidnight(to)
		}
	}

    /**
     * Get the total amount of discount (ex tax) applied to all invoices created in the date range. If you pass a
     * null date in either option, then that part of the filter is not applied.
     *
     * @param self
     * @param from the starting date of the range. This date will automatically be extended to the previous midnight
     * @param to the ending date of the range. This date will automatically be extended to the next midnight
     * @return total amount of discount ex tax
     */
	@API
	static @Nonnull getTotalDiscountExTax(Discount self, @Nullable Date from, @Nullable Date to) {
		return getDiscountedEnrolments(self, from, to).inject(Money.ZERO()) {
			Money total, e -> total.add(e.originalInvoiceLine.discountTotalExTax)
		}
	}

    /**
     * Get the total amount of discount (including tax) applied to all invoices created in the date range. If you pass a
     * null date in either option, then that part of the filter is not applied.
     *
     * @param self
     * @param from the starting date of the range. This date will automatically be extended to the previous midnight
     * @param to the ending date of the range. This date will automatically be extended to the next midnight
     * @return total amount of discount inc tax
     */
	@API
	static @Nonnull Money getTotalDiscountIncTax(Discount self, @Nullable Date from, @Nullable Date to) {
		return getDiscountedEnrolments(self, from, to).inject(Money.ZERO()) {
			Money total, e -> total.add(e.originalInvoiceLine.discountTotalIncTax)
		}
	}
}
