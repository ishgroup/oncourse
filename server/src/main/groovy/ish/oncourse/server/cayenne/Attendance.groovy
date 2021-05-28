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
import ish.messaging.IAttendance
import ish.oncourse.API
import ish.oncourse.cayenne.QueueableEntity
import ish.oncourse.server.cayenne.glue._Attendance
import ish.util.DurationFormatter

import javax.annotation.Nonnull
import javax.annotation.Nullable

/**
 * This entity represents the relationship between a tutor or student, and the sessions that comprise
 * a CourseClass. They can be used to track whether students show up for sessions (and for how long)
 * and also whether tutors should be paid for delivery.
 *
 */
@API
@QueueableEntity
class Attendance extends _Attendance implements IAttendance, Queueable {

	@Override
	void preUpdate() {
		List<Outcome> classOutcomes = session.courseClass.enrolments*.outcomes.flatten() as List<Outcome>
		classOutcomes.findAll { !it.startDateOverridden }.each {o ->
			o.startDate = o.actualStartDate
		}
		classOutcomes.findAll { !it.endDateOverridden }.each {o ->
			o.endDate = o.actualEndDate
		}
	}

	/**
	 * Attendance types represent the result of particular attendance marking.
	 * @return the attendance type
	 */
	@Nonnull
	@API
	@Override
	AttendanceType getAttendanceType() {
		return super.getAttendanceType()
	}

	/**
	 * If null, then assume the attendance started when the session started.
	 * @return the time that this attendance started
	 */
	@API
	@Override
	Date getAttendedFrom() {
		return super.getAttendedFrom()
	}

	/**
	 * If null, then assume the attendance ended when the session ended.
	 * @return the time this attendance ended
	 */
	@API
	@Override
	Date getAttendedUntil() {
		return super.getAttendedUntil()
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
	 * @return the duration in minutes of the attendance
	 */
	@API
	@Override
	Integer getDurationMinutes() {
		return super.getDurationMinutes()
	}

	BigDecimal getDurationInHours() {
		if (getAttendedFrom() == null || getAttendedUntil()) {
			return BigDecimal.ZERO
		}
		return DurationFormatter.parseDurationInHours(getAttendedUntil().getTime() - getAttendedFrom().getTime())
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
	 * @return any additional notes entered by the tutor
	 */
	@API
	@Override
	String getNote() {
		return super.getNote()
	}

	/**
	 * Tutors can login and mark student attendances for their classes in onCourse portal
	 *
	 * @return the ID of a tutor who marked student's attendance
	 */
	@Nonnull
	@API
	@Override
	Tutor getMarkedByTutor() {
		return super.getMarkedByTutor()
	}

	/**
	 * @return the session this attendance is linked to
	 */
	@Nonnull
	@API
	@Override
	Session getSession() {
		return super.getSession()
	}

	/**
	 * @return which student the attendance is for
	 */
	@Nonnull
	@API
	@Override
	Student getStudent() {
		return super.getStudent()
	}

	@Override
	boolean isAbsent() {
		return AttendanceType.STATUSES_ABSENCE.contains(this.attendanceType)
	}
/**
	 * @return date and time when attendance was marked by a tutor
	 */
	@Nullable
	@API
	@Override
	Date getMarkedByTutorDate() {
		return super.getMarkedByTutorDate()
	}
}
