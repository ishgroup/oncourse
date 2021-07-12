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
package ish.oncourse.entity.services;

import ish.oncourse.server.cayenne.CourseClassTutor;
import ish.oncourse.server.cayenne.Session;
import ish.oncourse.server.cayenne.TutorAttendance;
import ish.util.DurationFormatter;

import java.math.BigDecimal;

/**
 */
public class SessionService {

	public Long getDuration(Session session) {
		if (session.getEndDatetime() == null || session.getStartDatetime() == null) {
			return 0L;
		}
		return session.getEndDatetime().getTime() - session.getStartDatetime().getTime();
	}

	/**
	 * @return session duration in hours
	 */
	public BigDecimal getDurationInHours(Session session) {
		if (session.getEndDatetime() == null || session.getStartDatetime() == null) {
			return new BigDecimal("0");
		}
		return DurationFormatter.parseDurationInHours(session.getEndDatetime().getTime() - session.getStartDatetime().getTime());
	}

	/**
	 * @param tutorRole
	 * @return the TutorAttendance (used to be called SessionCourseClassTutor) for this tutor role (CourseClassTutor) for this session
	 */
	public TutorAttendance getTutorAttendanceForRole(Session session, CourseClassTutor tutorRole) {
		if (tutorRole == null) {
			return null;
		}
		for (TutorAttendance attendance : session.getSessionTutors()) {
			if (tutorRole.equals(attendance.getCourseClassTutor())) {
				return attendance;
			}
		}
		return null;
	}

	/**
	 * @return session payable duration in minutes
	 */
	public Integer getPayableDurationInMinutes(Session session) {
		Integer adjustment = session.getPayAdjustment();
		if (adjustment == null) {
			return session.getDurationInMinutes();
		}
		return session.getDurationInMinutes() - adjustment;
	}
}
