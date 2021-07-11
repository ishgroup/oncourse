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

import ish.messaging.ICourse
import ish.messaging.ICourseModule
import ish.oncourse.API
import ish.oncourse.cayenne.QueueableEntity
import ish.oncourse.server.cayenne.glue._CourseModule

import javax.annotation.Nonnull

/**
 * Object representing relation between course and module.
 */
@API
@QueueableEntity
class CourseModule extends _CourseModule implements Queueable, ICourseModule {



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
	 * @return linked course record
	 */
	@Nonnull
	@API
	@Override
	Course getCourse() {
		return super.getCourse()
	}

	/**
	 * @return linked module record
	 */
	@Nonnull
	@API
	@Override
	Module getModule() {
		return super.getModule()
	}

	@Override
	void setCourse(ICourse course) {
		super.setCourse((Course) course)
	}

	@Override
	void setModule(Module module) {
		super.setModule((Module) module)

	}
}
