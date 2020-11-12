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
import ish.oncourse.API
import ish.oncourse.server.cayenne.Attendance
import ish.oncourse.server.cayenne.Enrolment


class EnrolmentMixin {

	@Deprecated
	static getInvoiceLine(Enrolment self) {
		getOriginalInvoiceLine(self)
	}

	/**
	 * @return original invoice line which was created during enrolment process.
	 * Such invoice line store original enrolment price, discount amount and other properties which usually
	 * uses by customers in reports, exports and other components.
	 */
	@API
	static getOriginalInvoiceLine(Enrolment self) {
		self.invoiceLines.sort { a,b -> a.invoice.invoiceNumber <=> b.invoice.invoiceNumber }.first()
	}

	/**
	 * @param self
	 * @return true if related attendances exist for this enrolment
	 */
	@API
	static boolean hasAttendance(Enrolment self) {
		self.courseClass.sessions*.attendance.flatten().find { at -> self.student.equalsIgnoreContext(((Attendance) at).student) } != null
	}

	/**
	 * @param self
	 * @return true if this enrolment will be printed
	 */
	@API
	static boolean allowedToPrint(Enrolment self) {
		boolean hasNoDuplicateSuccessfulEnrolment = self.student.enrolments
				.findAll { e -> e.courseClass.equalsIgnoreContext(self.courseClass) }
				.findAll { e -> EnrolmentStatus.STATUSES_LEGIT.contains(e.status) }
				.findAll { e -> !e.equalsIgnoreContext(self) }
				.empty


				self.student.enrolments.find { e ->
			!e.equalsIgnoreContext(self) && e.courseClass.equalsIgnoreContext(self.courseClass) && EnrolmentStatus.STATUSES_LEGIT.contains(e.status)
		} != null

		boolean isLastFailedEnrolment = self.student.enrolments
				.findAll { e -> e.courseClass.equalsIgnoreContext(self.courseClass) }
				.findAll { e -> !EnrolmentStatus.STATUSES_LEGIT.contains(e.status) }
				.findAll { e -> !e.equalsIgnoreContext(self) }
				.findAll { e -> e.createdOn.after(self.createdOn) }
				.empty

		return EnrolmentStatus.SUCCESS == self.status ||
				EnrolmentStatus.QUEUED == self.status ||
				(hasAttendance(self) && hasNoDuplicateSuccessfulEnrolment && isLastFailedEnrolment)
	}


}
