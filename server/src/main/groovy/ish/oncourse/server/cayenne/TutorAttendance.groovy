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

package ish.oncourse.server.cayenne

import ish.common.types.AttendanceType
import ish.oncourse.API
import ish.oncourse.cayenne.QueueableEntity
import ish.oncourse.server.cayenne.glue._TutorAttendance
import ish.util.DurationFormatter

import javax.annotation.Nonnull
import java.math.RoundingMode
import java.time.Duration

/**
 * This entity represents the relationship between a tutor and the sessions that comprise
 * a CourseClass. They can be used to track whether tutor was present at a session (and for how long)
 * and also whether they should be paid for delivery.
 */
@API
@QueueableEntity
class TutorAttendance extends _TutorAttendance implements TutorAttendanceTrait, Queueable, ExpandableTrait {


	@Override
	void prePersist() {
		if (!startDatetime) {
			startDatetime = session.startDatetime
		}
		if (!endDatetime) {
			endDatetime = session.endDatetime
		}
		if (actualPayableDurationMinutes == null) {
			actualPayableDurationMinutes = DurationFormatter.durationInMinutesBetween(startDatetime, endDatetime)
		}
	}


	/**
	 * @return type of attendance
	 */
	@Nonnull
	@API
	@Override
	AttendanceType getAttendanceType() {
		return super.getAttendanceType()
	}

	/**
	 * @return the date and time this record was created
	 */
	@API
	@Override
	Date getCreatedOn() {
		return super.getCreatedOn()
	}

	/**
	 * @return actual payable duration of the attendance
	 */
	@API
	@Override
	Integer getActualPayableDurationMinutes() {
		return super.getActualPayableDurationMinutes()
	}

	/**
	 * @return the date and time this record was modified
	 */
	@API
	@Override
	Date getModifiedOn() {
		return super.getModifiedOn()
	}

	/**
	 * @return private notes about attendance, to be seen only by the college staff
	 */
	@API
	@Override
	String getNote() {
		return super.getNote()
	}



	/**
	 * @return user who approved this attendance
	 */
	@Nonnull
	@API
	@Override
	SystemUser getApprovedByUser() {
		return super.getApprovedByUser()
	}

	/**
	 * @return class-tutor relation object
	 */
	@Nonnull
	@API
	@Override
	CourseClassTutor getCourseClassTutor() {
		return super.getCourseClassTutor()
	}

	/**
	 * @return user who marked this attendance
	 */
	@Nonnull
	@API
	@Override
	SystemUser getMarkedByUser() {
		return super.getMarkedByUser()
	}

	/**
	 * @return session linked to this attendance
	 */
	@Nonnull
	@API
	@Override
	Session getSession() {
		return super.getSession()
	}

	/**
	 * @return tutor attendance start date time (roster start date time)
	 */
	@Nonnull
	@API
	@Override
	Date getStartDatetime() {
		return super.getStartDatetime()
	}

	/**
	 * @return tutor attendance end date time (roster end date time)
	 */
	@Nonnull
	@API
	@Override
	Date getEndDatetime() {
		return super.getEndDatetime()
	}

	/**
	 * @return actual payable duration in hours
	 */
	BigDecimal getActualPayableDurationHours() {
		return DurationFormatter.durationInHoursFromMinutes(actualPayableDurationMinutes)
	}

	/**
	 * @return  budgeted payable duration in hours, actually tutor roster duration
	 */
	BigDecimal getBudgetedPayableDurationHours() {
		return DurationFormatter.durationInHoursBetween(startDatetime, endDatetime)
	}

	/**
	 * @return time zone of session that was got from site of room
	 */
	@API
	TimeZone getTimeZone() {
		this.session.getTimeZone()
	}

	@Override
	Class<? extends CustomField> getCustomFieldClass() {
		return TutorAttendanceCustomField
	}
}
