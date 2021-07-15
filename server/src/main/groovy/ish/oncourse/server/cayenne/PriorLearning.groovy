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
import ish.oncourse.server.cayenne.glue._PriorLearning
import ish.validation.ValidationFailure
import org.apache.cayenne.validation.ValidationResult
import org.apache.commons.lang3.StringUtils

import javax.annotation.Nonnull
import javax.annotation.Nullable

/**
 * Student's prior learning record.
 */
@API
@QueueableEntity
class PriorLearning extends _PriorLearning implements Queueable, AttachableTrait {



	@Override
	void validateForSave(@Nonnull final ValidationResult result) {
		if (getStudent() == null) {
			result.addFailure(ValidationFailure.validationFailure(this, STUDENT.getName(), "You must enter a student for this prior learning."))
		}
		if (StringUtils.isBlank(getTitle())) {
			result.addFailure(ValidationFailure.validationFailure(this, TITLE.getName(), "You must enter a title for this prior learning."))
		}
	}

	@Override
	void addToAttachmentRelations(AttachmentRelation relation) {
		addToAttachmentRelations((PriorLearningAttachmentRelation) relation)
	}

	@Override
	void removeFromAttachmentRelations(AttachmentRelation relation) {
		removeFromAttachmentRelations((PriorLearningAttachmentRelation) relation)
	}

	@Override
	Class<? extends AttachmentRelation> getRelationClass() {
		return PriorLearningAttachmentRelation.class
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
	 * @return the date and time this record was modified
	 */
	@API
	@Override
	Date getModifiedOn() {
		return super.getModifiedOn()
	}

	/**
	 * @return notes linked to this prior learning record
	 */
	@API
	@Override
	String getNotes() {
		return super.getNotes()
	}

	/**
	 * @return qualification linked to this prior learning record
	 */
	@Nullable
	@API
	@Override
	Qualification getQualification() {
		return super.getQualification()
	}

	/**
	 * @return student linked to this prior learning record
	 */
	@Nonnull
	@API
	@Override
	Student getStudent() {
		return super.getStudent()
	}


	@Override
	String getSummaryDescription() {
		if (getStudent() == null || getStudent().getContact() == null) {
			return super.getSummaryDescription()
		}
		return getStudent().getContact().getName(false)
	}
}
