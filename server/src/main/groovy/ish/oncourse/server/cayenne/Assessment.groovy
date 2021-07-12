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
import ish.oncourse.server.cayenne.glue._Assessment
import ish.validation.AngelAssessmentValidator
import ish.validation.ValidationFailure
import org.apache.cayenne.validation.ValidationResult

import javax.annotation.Nonnull
import javax.annotation.Nullable

//TODO docs
@API
@QueueableEntity
class Assessment extends _Assessment  implements Queueable, NotableTrait, AttachableTrait {


	public static final String NAME_KEY = "name";
	public static final String CODE_KEY = "code";
	public static final String ACTIVE_KEY = "active";

	@Override
	void addToAttachmentRelations(AttachmentRelation relation) {
		addToAttachmentRelations((AssessmentAttachmentRelation) relation)
	}

	@Override
	void removeFromAttachmentRelations(AttachmentRelation relation) {
		removeFromAttachmentRelations((AssessmentAttachmentRelation) relation)
	}

	@Override
	Class<? extends AttachmentRelation> getRelationClass() {
		return AssessmentAttachmentRelation.class
	}

	@Override
	void validateForSave(ValidationResult validationResult) {
		super.validateForSave(validationResult)

		AngelAssessmentValidator.valueOf(validationResult, this).validate()

		if (detectDuplicate([ CODE.getName() ] as String[] , null, true) != null) {
			validationResult.addFailure(ValidationFailure.validationFailure(this, CODE.getName(), "You must enter a unique assessment code."))
		}
	}

	/**
	 * @return true if active
	 */
	@Nonnull
	@API
	@Override
	Boolean getActive() {
		return super.getActive()
	}

	@Nonnull
	@API
	@Override
	String getCode() {
		return super.getCode()
	}

	/**
	 * @return the date and time this record was created
	 */
	@Nonnull
	@API
	@Override
	Date getCreatedOn() {
		return super.getCreatedOn()
	}

	@Nullable
	@API
	@Override
	String getDescription() {
		return super.getDescription()
	}


	/**
	 * @return the date and time this record was modified
	 */
	@Nonnull
	@API
	@Override
	Date getModifiedOn() {
		return super.getModifiedOn()
	}

	@Nonnull
	@API
	@Override
	String getName() {
		return super.getName()
	}

	/**
	 * AssesmentClasses is the relational object between an Assesment and a CourseClass
	 * @return all assesmentclasses this assesment is linked to
	 */
	@Nonnull
	@API
	@Override
	List<AssessmentClass> getAssessmentClasses() {
		return super.getAssessmentClasses()
	}

	@Override
	String getSummaryDescription() {
		return getName() + ' ' + getCode()
	}
}



