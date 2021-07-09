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

import ish.common.types.QualificationType
import ish.oncourse.API
import ish.oncourse.cayenne.QueueableEntity
import ish.oncourse.server.api.dao.EntityRelationDao
import ish.oncourse.server.cayenne.glue._Qualification
import org.apache.cayenne.query.SelectById
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

import javax.annotation.Nonnull
import java.time.LocalDate

/**
 * The Qualification class contains records from both accredited courses (state based) and qualifications (national)
 * from the training.gov.au website. You cannot create or delete records in this table since they are
 * automatically kept in sync with data from the official website.
 *
 */
@API
@QueueableEntity
class Qualification extends _Qualification implements Queueable {


	private static final Logger logger = LogManager.getLogger()

	/**
	 * @return ANZSCO code
	 */
	@API
	@Override
	String getAnzsco() {
		return super.getAnzsco()
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
	 * @return true if this module is offered by the college
	 */
	@Nonnull
	@API
	@Override
	Boolean getIsOffered() {
		return super.getIsOffered()
	}

	/**
	 * @return the level as a string
	 */
	@API
	@Override
	String getLevel() {
		return super.getLevel()
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
	 * @return the national code. This value is unique.
	 */
	@API
	@Override
	String getNationalCode() {
		return super.getNationalCode()
	}

	/**
	 * @return
	 */
	@Override
	String getNewApprenticeship() {
		return super.getNewApprenticeship()
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
	 * @return
	 */
	@Override
	LocalDate getReviewDate() {
		return super.getReviewDate()
	}

	/**
	 * @return the qualification title
	 */
	@API
	@Override
	String getTitle() {
		return super.getTitle()
	}

	/**
	 * @return the type of qualification (eg. accredited course, qualification, skill set)
	 */
	@Nonnull
	@API
	@Override
	QualificationType getType() {
		return super.getType()
	}

	/**
	 * @return all courses linked to this qualification
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
	List<Module> getDefaultModules() {
		return super.getDefaultModules()
	}

	/**
	 * @return
	 */
	@Nonnull
	@Override
	List<PriorLearning> getPriorLearnings() {
		return super.getPriorLearnings()
	}

	/**
	 * @return
	 */
	@Nonnull
	@Override
	TrainingPackage getTrainingPackage() {
		return super.getTrainingPackage()
	}

	@Override
	boolean isAsyncReplicationAllowed() {
		return Boolean.TRUE == getIsCustom()
	}

	@Override
	String getSummaryDescription() {
		return getTitle()
	}

	/**
	 * @return courses related to this qualification
	 */
	@Nonnull
	@API
	List<Course> getRelatedCourses() {
		List<Course> courses = new ArrayList<>()
		EntityRelationDao.getRelatedFrom(context, Qualification.simpleName, id)
				.findAll { it -> Course.simpleName == it.fromEntityIdentifier}
				.each { it -> courses.add(SelectById.query(Course, it.fromRecordId).selectOne(context)) }
		EntityRelationDao.getRelatedTo(context, Qualification.simpleName, id)
				.findAll { it -> Course.simpleName == it.toEntityIdentifier}
				.each { it -> courses.add(SelectById.query(Course, it.toRecordId).selectOne(context)) }
		return courses
	}
}
