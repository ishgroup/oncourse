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
import ish.oncourse.API
import ish.oncourse.cayenne.ModuleInterface
import ish.oncourse.cayenne.QueueableEntity
import ish.oncourse.server.api.dao.EntityRelationDao
import ish.oncourse.server.cayenne.glue._Module
import org.apache.cayenne.query.SelectById
import javax.annotation.Nonnull
import javax.annotation.Nullable

/**
 * The Module class contains records from both modules (state based) and units of competetency (national)
 * from the training.gov.au website. You cannot create or delete records in this table since they are
 * automatically kept in sync with data from the official website.
 *
 */
@API
@QueueableEntity
class Module extends _Module implements Queueable, ModuleInterface {

	public static final String NATIONAL_CODE_KEY = "nationalCode";

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
	@Nonnull @API @Override
	Date getCreatedOn() {
		return super.getCreatedOn()
	}

	/**
	 * @return the NCVER 'Subject field of education' identifer code
	 */
	@Nullable @API @Override
	String getFieldOfEducation() {
		return super.getFieldOfEducation()
	}


	/**
	 * @return the educational accreditation type of module
	 */
	@Nonnull @API @Override
	ModuleType getType() {
		return super.getType()
	}

	/**
	 * @return true if this module is offered by the college
	 */
	@Nonnull @API @Override
	Boolean getIsOffered() {
		return super.getIsOffered()
	}

	/**
	 * @return the date and time this record was modified
	 */
	@Nonnull @API @Override
	Date getModifiedOn() {
		return super.getModifiedOn()
	}

	/**
	 * @return a unique code which could be issued by an educational governing body or created by the college
	 */
	@API @Override
	String getNationalCode() {
		return super.getNationalCode()
	}

	/**
	 * A number of hours in which this module is expected to be delivered.
	 *
	 * @return nominal hours
	 */
	@API @Override
	BigDecimal getNominalHours() {
		return super.getNominalHours()
	}

	/**
	 * @return title
	 */
	@API @Override
	String getTitle() {
		return super.getTitle()
	}

	/**
	 * @return a list of courses linked to this module
	 */
	@Nonnull @API @Override
	List<Course> getCourses() {
		return super.getCourses()
	}

	@Override
	boolean isAsyncReplicationAllowed() {
		return Boolean.TRUE == getIsCustom()
	}

	@Nonnull @Override
	String getSummaryDescription() {
		return getTitle()
	}

	/**
	 * Credit points may be issued for the award of this module.
	 *
	 * @return number of credit points as a decimal
	 */
	@Nullable @Override @API
	BigDecimal getCreditPoints() {
		return super.getCreditPoints()
	}

	/**
	 * Some modules are valid only for a certain period of time before they need to be renewed.
	 *
	 * @return Number of days after award before expiry
	 */
	@Nullable @Override @API
	Integer getExpiryDays() {
		return super.getExpiryDays()
	}


	@Nullable @Override @API
	String getSpecialization() {
		return super.getSpecialization()
	}
/**
	 * @return courses related to this module
	 */
	@Nonnull @API
	List<Course> getRelatedCourses() {
		List<Course> courses = new ArrayList<>()
		EntityRelationDao.getRelatedFrom(context, Module.simpleName, id)
				.findAll { it -> Course.simpleName == it.fromEntityIdentifier}
				.each { it -> courses.add(SelectById.query(Course, it.fromRecordId).selectOne(context)) }
		EntityRelationDao.getRelatedTo(context, Module.simpleName, id)
				.findAll { it -> Course.simpleName == it.toEntityIdentifier}
				.each { it -> courses.add(SelectById.query(Course, it.toRecordId).selectOne(context)) }
		return courses
	}
}
