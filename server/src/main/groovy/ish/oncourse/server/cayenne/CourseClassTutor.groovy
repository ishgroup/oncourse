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
import ish.oncourse.server.cayenne.glue._CourseClassTutor
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

import javax.annotation.Nonnull

/**
 * A CourseClass record may have one or more Tutors attached to it. The same Tutor may even
 * be attached several times with different roles. For example, as a lecturer and an assessor.
 * This then feeds the tutor attendance and payroll functionality.
 *
 *
 */
@QueueableEntity
@API
class CourseClassTutor extends _CourseClassTutor implements Queueable {


	private static Logger logger = LogManager.getLogger()

	private void checkInPublicity() {
		if (getInPublicity() == null) {
			setInPublicity(true)
		}
	}

	@Override
	protected void postAdd() {
		super.postAdd()
		checkInPublicity()
	}

	@Override
	protected void prePersist() {
		super.prePersist()
		checkInPublicity()
	}

	@Override
	protected void preUpdate() {
		super.preUpdate()
		checkInPublicity()
	}

	/**
	 * @return the date this tutor has been confirmed on
	 */
	@Override
	@API
	Date getConfirmedOn() {
		return super.getConfirmedOn()
	}

	/**
	 * @return the date and time this record was created
	 */
	@Override
	@API
	Date getCreatedOn() {
		return super.getCreatedOn()
	}


	/**
	 * @return returns true if this tutor is publically shown in class information
	 */
	@Nonnull
	@Override
	@API
	Boolean getInPublicity() {
		return super.getInPublicity()
	}

	/**
	 * @return the date and time this record was modified
	 */
	@Override
	@API
	Date getModifiedOn() {
		return super.getModifiedOn()
	}

	/**
	 * @return the linked CourseClass
	 */
	@Nonnull
	@Override
	@API
	CourseClass getCourseClass() {
		return super.getCourseClass()
	}

	/**
	 * @return the role with which the Tutor has been attached to the CourseClass
	 */
	@Nonnull
	@Override
	@API
	DefinedTutorRole getDefinedTutorRole() {
		return super.getDefinedTutorRole()
	}

	/**
	 * @return the attendance records related to this tutor and their role in the courseClass
	 */
	@Nonnull
	@Override
	@API
	List<TutorAttendance> getSessionsTutors() {
		return super.getSessionsTutors()
	}

	/**
	 * @return the linked Tutor
	 */
	@Nonnull
	@Override
	@API
	Tutor getTutor() {
		return super.getTutor()
	}

	@Override
	String getSummaryDescription() {
		if (getTutor() != null) {
			return getTutor().getContact().getName()
		}
		return super.getSummaryDescription()
	}
}
