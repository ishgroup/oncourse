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
import ish.oncourse.API
import ish.oncourse.server.cayenne.CourseClassTutor

@CompileDynamic
class CourseClassTutorMixin {

	/**
	 * A summary of the CourseClass sessions. Returns the  number of sessions and the sum of hours in all sessions
	 * The output for a class with 3 sessions and 5 hours per session would be:
	 * ```
	 * 3 (15)
	 * ```
	 * @return number of sessions and total hours
	 */
	@API
	static getSessionSummary(CourseClassTutor self) {
		def sessions = self.courseClass.sessions.findAll {
			s -> s.tutors.find { true }?.equals(self.tutor)
		}.inject([number: 0, hours: 0.0]) {
			acc, s -> [number: acc.number + 1, hours: acc.hours + s.payableDurationInHours.doubleValue()]
		}

		return sessions.number > 0 ? "${sessions.number} (${sessions.hours})" : "0 (0)"
	}
}
