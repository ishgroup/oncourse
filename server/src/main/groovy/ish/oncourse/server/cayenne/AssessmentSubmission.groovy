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
import ish.oncourse.cayenne.AssessmentSubmissionInterface
import ish.oncourse.cayenne.QueueableEntity
import ish.oncourse.server.cayenne.glue._AssessmentSubmission

import javax.annotation.Nonnull
import javax.annotation.Nullable
//TODO docs
@API
@QueueableEntity
class AssessmentSubmission extends _AssessmentSubmission implements Queueable, NotableTrait, AttachableTrait, AssessmentSubmissionInterface {

	public static final String STUDENT_NAME_PROPERTY = "studentName"
	public static final String CLASS_NAME_PROPERTY = "courseClassName"
	public static final String ASSESSMENT_NAME_PROPERTY = "assessmentName"
	public static final String MARKED_BY_ID_PROPERTY = "markedById"
	public static final String GRADE_KEY = "gradeDisplayValue"

	@Override
	void addToAttachmentRelations(AttachmentRelation relation) {
		addToAttachmentRelations((AssessmentSubmissionAttachmentRelation) relation)
	}

	@Override
	void removeFromAttachmentRelations(AttachmentRelation relation) {
		removeFromAttachmentRelations((AssessmentSubmissionAttachmentRelation) relation)
	}

	@Override
	Class<? extends AttachmentRelation> getRelationClass() {
		return AssessmentSubmissionAttachmentRelation.class
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

	/**
	 * @return the date and time this record was modified
	 */
	@Nonnull
	@API
	@Override
	Date getModifiedOn() {
		return super.getModifiedOn()
	}

	/**
	 * @return the assessment class: connection between a courseClass and the assessment submission
	 */
	@Nonnull
	@API
	@Override
	AssessmentClass getAssessmentClass() {
		return super.getAssessmentClass()
	}

	/**
	 * @return the enrolment which is related to the assessment submission
	 */
	@Nonnull
	@API
	@Override
	Enrolment getEnrolment() {
		return super.getEnrolment()
	}

	/**
	 * @return the id of the assessor who marked the assessment submission
	 */
	@Nullable
	@API
	@Override
	Contact getMarkedBy() {
		return super.getMarkedBy()
	}

	/**
	 * @return the full name of student
	 */
	@API
	String getStudentName() {
		return getEnrolment().getStudent().getContact().getFullName()
	}

	/**
	 * @return the courseClass name in next format: 'courseName courseCode-classCode'
	 */
	@API
	String getCourseClassName() {
		CourseClass courseClass = getAssessmentClass().getCourseClass()
		return "${courseClass.getCourse().getName()} ${courseClass.getCourse().getCode()}-${courseClass.getCode()}"
	}

	/**
	 * @return the assessment name
	 */
	@API
	String getAssessmentName() {
		return getAssessmentClass().getAssessment().getName()
	}

	/**
	 *
	 * @return itemName of related GradingItem or null if grade is incorrect
	 */
	String getGradeDisplayValue() {
		def type = assessmentClass.assessment.gradingType
		return type == null ? "" : type.getDisplayableNameOfGrade(grade)
	}

	@Override
	Date getSubmittedDate() {
		return super.getSubmittedOn()
	}
}



