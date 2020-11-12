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

import ish.oncourse.API
import ish.oncourse.entity.services.SessionService
import ish.oncourse.server.cayenne.SessionModule
import static ish.oncourse.server.entity.mixins.MixinHelper.getService

class SessionModuleMixin {

	/**
	 * @return the date of the first sessions that this module is attached to
	 */
	@API
	static getModuleStartDate(SessionModule self) {
		self.session.courseClass.sessions.collectMany { s -> s.sessionModules }.findAll {
			SessionModule sm -> sm.module.equals(self.module)
		}.collect { SessionModule sm -> sm.session }.toSorted {
			s1, s2 -> s1.startDatetime <=> s2.startDatetime
		}.find { true }?.startDatetime
	}

	/**
	 * @return the date of the last sessions that this module is attached to
	 */
	@API
	static getModuleEndDate(SessionModule self) {
		self.session.courseClass.sessions.collectMany { s -> s.sessionModules }.findAll {
			SessionModule sm -> sm.module.equals(self.module)
		}.collect { SessionModule sm -> sm.session }.toSorted {
			s1, s2 -> s2.endDatetime <=> s1.endDatetime
		}.find { true }?.endDatetime
	}

	/**
	 * @return a list of all tutors delivering this session
	 */
	@API
	static getTutorNames(SessionModule self) {
		def tutorNames = self.session.tutorRoles.findAll {
			role -> role && getService(SessionService).getTutorAttendanceForRole(self.session, role)
		}.collect { role -> role.tutor.contact.getName(false) }

		return tutorNames.empty ? "Delivery scheduled, no tutor assigned" : tutorNames.join("; ")
	}
}
