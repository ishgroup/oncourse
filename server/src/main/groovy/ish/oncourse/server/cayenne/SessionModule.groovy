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

import ish.messaging.IOutcome
import ish.oncourse.API
import ish.oncourse.cayenne.AttendanceInterface
import ish.oncourse.cayenne.OutcomeInterface
import ish.oncourse.cayenne.QueueableEntity
import ish.oncourse.cayenne.SessionModuleInterface
import ish.oncourse.entity.delegator.OutcomeDelegator
import ish.oncourse.server.cayenne.Module
import ish.oncourse.server.cayenne.glue._SessionModule

import javax.annotation.Nonnull

/**
 * Object representing relation between session and module.
 */
@API
@QueueableEntity
class SessionModule extends _SessionModule implements SessionModuleInterface, Queueable {

	public static final String SESSION_KEY = "session";
	public static final String MODULE_KEY = "module";

	/**
	 * @return the date and time this record was created
	 */
	@API
	@Override
	Date getCreatedOn() {
		return super.getCreatedOn()
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
	 * @return linked module
	 */
	@Nonnull
	@API
	@Override
	Module getModule() {
		return super.getModule()
	}

	/**
	 * @return linked session
	 */
	@Nonnull
	@API
	@Override
	Session getSession() {
		return super.getSession()
	}

	@Override
	AttendanceInterface getAttendanceForOutcome(OutcomeInterface outcomeInterface) {
		IOutcome outcome = ((OutcomeDelegator) outcomeInterface).getOutcome()
		Student student = (Student) outcome.getEnrolment().getStudent()
		this.session.attendance.find{it.student.id == student.id}
	}

	@Override
	void setSession(Session session) {
		super.setSession((Session) session)
	}

	@Override
	void setModule(Module module) {
		super.setModule((Module) module)
	}
}
