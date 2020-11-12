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
import ish.oncourse.cayenne.StudentConcessionInterface
import ish.oncourse.server.cayenne.glue._StudentConcession
import ish.validation.AngelStudentConcessionValidator
import org.apache.cayenne.validation.ValidationResult

import javax.annotation.Nonnull
import java.util.Date

/**
 * StudentConcessions are attributes of Students describing the properties of a particular ConcessionType held
 * by that student.
 *
 * For example, a student may hold a Senior's Card with no expiry date (they aren't getting younger), or a Centrelink
 * unemployment benefit which expires a year after creation.
 *
 * The main purpose of these concessions is to control the application of discounts.
 */
@API
@QueueableEntity
class StudentConcession extends _StudentConcession implements StudentConcessionInterface, Queueable {



	/**
	 * @return
	 * TODO: what is this for?
	 */
	@Override
	Date getAuthorisationExpiresOn() {
		return super.getAuthorisationExpiresOn()
	}

	/**
	 * @return
	 * TODO: what is this for?
	 */
	@Override
	Date getAuthorisedOn() {
		return super.getAuthorisedOn()
	}

	/**
	 * Concession numbers are not validated by onCourse.
	 *
	 * @return an identifier for the concession
	 */
	@API
	@Override
	String getConcessionNumber() {
		return super.getConcessionNumber()
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
	 * @return the date after which the conncession will not longer be valid
	 */
	@API
	@Override
	Date getExpiresOn() {
		return super.getExpiresOn()
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
	 * @return user who created concession
	 */
	@API
	@Override
	Contact getAuthorisedBy() {
		return super.getAuthorisedBy()
	}

	/**
	 * @return linked concession type
	 */
	@Nonnull
	@API
	@Override
	ConcessionType getConcessionType() {
		return super.getConcessionType()
	}

	/**
	 * @return student to whom this concession belongs
	 */
	@Nonnull
	@API
	@Override
	Student getStudent() {
		return super.getStudent()
	}

	@Override
	void validateForSave(ValidationResult validationResult) {
		super.validateForSave(validationResult)
		AngelStudentConcessionValidator.valueOf(this, validationResult).validate()
	}

	@Override
	String getSummaryDescription() {
		if (getConcessionType() != null) {
			return getConcessionType().getName()
		}
		return super.getSummaryDescription()
	}
}
