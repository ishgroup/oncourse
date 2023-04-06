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
import ish.common.types.AttendanceType
import ish.oncourse.API
import ish.oncourse.entity.services.StudentConcessionService
import ish.oncourse.entity.services.StudentService
import ish.oncourse.server.cayenne.Student
import static ish.oncourse.server.entity.mixins.MixinHelper.getService
import ish.oncourse.server.cayenne.StudentConcession

import javax.annotation.Nonnull

@CompileDynamic
class StudentMixin {

    // TODO presentation login doesn't really belong here
	static getConcessionsDescription(Student self) {
		if (self.getConcessions() == null || self.getConcessions().size() == 0) {
			return "None"
		}
		StringBuilder result = new StringBuilder()
		for (StudentConcession sc : self.getConcessions()) {
			if (getService(StudentConcessionService).isNotExpired(sc)) {
				if (result.length() > 0) {
					result.append(", ")
				}
				result.append(sc.getConcessionType().getName())
				if (sc.getConcessionNumber() != null) {
					result.append(":")
					result.append(sc.getConcessionNumber())
				}
			}
		}
		if (result.length() == 0) {
			return "No current concessions"
		}
		return result.toString()
	}

    //TODO this should not mask the real join
	static @Nonnull getOutcomes(Student self) {
		return getService(StudentService).getOutcomes(self, false)
	}

	/**
	 * @return the culmative attendance percentage for all sessions for all classes this student has been enrolled in
	 */
	@API
	static getAttendancePercent(Student self) {
		getAttendancePercent(self, null, new Date())
	}

	/**
	 * @return the culmative attendance for all sessions in a given date period
	 */
	@API
	static Integer getAttendancePercent(Student self, Date start, Date end) {
		double minutesPassed = 0d
		double minutesPresent = 0d

		self.attendances.findAll { a ->
			(start == null ?: a.session.startDatetime.after(start)) && (end == null ?: a.session.endDatetime.before(end))
		}.each { a ->
			if (a.attendanceType != null && !AttendanceType.UNMARKED.equals(a.attendanceType)) {
				double sessionDuration = a.getSession().getDurationInMinutes().doubleValue()
				minutesPassed += sessionDuration
				if (AttendanceType.ATTENDED.equals(a.attendanceType) || AttendanceType.DID_NOT_ATTEND_WITH_REASON.equals(a.attendanceType)) {
					minutesPresent += sessionDuration
				} else if (AttendanceType.PARTIAL.equals(a.attendanceType)) {
					Integer partialDuration = a.durationMinutes
					if (partialDuration != null) {
						minutesPresent += Math.min(sessionDuration, partialDuration.doubleValue())
					}
				}
			}
		}

		minutesPassed > 0d ? (int) (100 * minutesPresent / minutesPassed) : null
	}
}
