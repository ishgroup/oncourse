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

import ish.common.types.ModuleType
import ish.messaging.IModule
import ish.oncourse.API
import ish.oncourse.cayenne.QueueableEntity
import ish.oncourse.server.cayenne.glue._Module
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

import javax.annotation.Nonnull
import java.math.BigDecimal
import java.util.Date
import java.util.List

/**
 * The Module class contains records from both modules (state based) and units of competetency (national)
 * from the training.gov.au website. You cannot create or delete records in this table since they are
 * automatically kept in sync with data from the official website.
 *
 */
@API
@QueueableEntity
class Module extends _Module implements Queueable, IModule {


	private static final Logger logger = LogManager.getLogger()

	@Override
	void postAdd() {
		super.postAdd()
		if (getIsOffered() == null) {
			setIsOffered(false)
		}
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
	 * @return the NCVER 'Subject field of education' identifer code
	 */
	@API
	@Override
	String getFieldOfEducation() {
		return super.getFieldOfEducation()
	}


	/**
	 * @return the ModuleType of this outcome
	 */
	@Nonnull
	@API
	@Override
	ModuleType getType() {
		return super.getType()
	}

	/**
	 * @return true if this module is offered by the college
	 */
	@Nonnull
	@API
	@Override
	Boolean getIsOffered() {
		return super.getIsOffered()
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
	 * @return national code
	 */
	@API
	@Override
	String getNationalCode() {
		return super.getNationalCode()
	}

	/**
	 * @return nominal hours
	 */
	@API
	@Override
	BigDecimal getNominalHours() {
		return super.getNominalHours()
	}

	/**
	 * @return title
	 */
	@API
	@Override
	String getTitle() {
		return super.getTitle()
	}



	/**
	 * @return a list of courses linked to this module
	 */
	@Nonnull
	@API
	@Override
	List<Course> getCourses() {
		return super.getCourses()
	}

	/**
	 * @return
	 */
	@Nonnull
	@Override
	Qualification getDefaultQualification() {
		return super.getDefaultQualification()
	}

	/**
	 * @return
	 */
	@Nonnull
	@Override
	List<Outcome> getOutcomes() {
		return super.getOutcomes()
	}

	/**
	 * @return
	 */
	@Nonnull
	@Override
	TrainingPackage getTrainingPackage() {
		return super.getTrainingPackage()
	}

	/**
	 * Setting this field reduces the choices available to users in the UI of onCourse to
	 * only those modules which are offered.
	 *
	 * @param isOffered true if this college offers the module
	 */
	@API
	void setIsOffered(Boolean isOffered) {
	    writeProperty("isOffered", isOffered)
	}

	@Override
	boolean isAsyncReplicationAllowed() {
		return Boolean.TRUE == getIsCustom()
	}

	@Override
	String getSummaryDescription() {
		return getTitle()
	}
}
