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

import ish.oncourse.API
import ish.oncourse.cayenne.QueueableEntity
import ish.oncourse.server.cayenne.glue._CorporatePassCourseClass

import javax.annotation.Nonnull
/**
 * Object representing relation between CorporatePass and class.
 * CorporatePass can only be used for enrolling into classes which are linked to it.
 */
@API
@QueueableEntity
class CorporatePassCourseClass extends _CorporatePassCourseClass implements Queueable {



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
	 * @return CorporatePass record
	 */
	@Nonnull
	@API
	@Override
	CorporatePass getCorporatePass() {
		return super.getCorporatePass()
	}

	/**
	 * @return class record
	 */
	@Nonnull
	@API
	@Override
	CourseClass getCourseClass() {
		return super.getCourseClass()
	}
}
