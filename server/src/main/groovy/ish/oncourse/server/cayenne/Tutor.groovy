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

import ish.common.types.PayslipPayType
import ish.oncourse.API
import ish.oncourse.cayenne.QueueableEntity
import ish.oncourse.cayenne.Taggable
import ish.oncourse.types.WorkingWithChildrenStatus
import ish.oncourse.server.cayenne.glue._Tutor
import ish.validation.ValidationFailure
import org.apache.cayenne.PersistenceState
import org.apache.cayenne.exp.Expression
import org.apache.cayenne.exp.ExpressionFactory
import org.apache.cayenne.validation.ValidationResult
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

import javax.annotation.Nonnull
import javax.annotation.Nullable
/**
 * A tutor is a type of contact who is able to deliver training. Every Tutor object will be linked to exactly one
 * Contact. Only tutor specific data is stored here: for all regular contact attributes and relationships, refer to the
 * Contact object.
 *
 * A contact can be both a student and tutor at the same time.
 *
 */
@API
@QueueableEntity
class Tutor extends _Tutor implements Queueable, Taggable, AttachableTrait {

	String CONTACT_KEY = "contact";
	private static Logger logger = LogManager.getLogger()

	@Override
	void onEntityCreation() {
		super.onEntityCreation()

		if (getWwChildrenStatus() == null) {
			setWwChildrenStatus(WorkingWithChildrenStatus.NOT_CHECKED)
		}
	}

	/**
	 * Simulating flattened relationship.
	 *
	 * @param obj - the tutor to add
	 */
	void addToCourseClasses(@Nullable CourseClass obj) {
		if (obj != null) {
			List<CourseClassTutor> tutorRoles = getCourseClassRoles()
			Expression tutorRoleExists = ExpressionFactory.matchExp(CourseClassTutor.COURSE_CLASS_PROPERTY, obj)
			List<CourseClassTutor> existingRoles = tutorRoleExists.filterObjects(tutorRoles)
			if (existingRoles.size() > 0) {
				if (existingRoles.size() > 1) {
					logger.error("There cannot be more than one tutorRole for CourseClass/Tutor", new Exception())
				}
			} else {
				CourseClassTutor newRole = getObjectContext().newObject(CourseClassTutor.class)
				newRole.setTutor(this)
				newRole.setCourseClass(obj)
			}
		}
	}

	@Override
	void setPersistenceState(int persistenceState) {
		super.setPersistenceState(persistenceState)
		if (persistenceState == PersistenceState.DELETED) {
			if (getContact() != null) {
				getContact().setIsTutor(false)
			}
		}
	}

	/**
	 * Simulating flattened relationship.
	 *
	 * @param obj - the tutor to remove
	 */
	void removeFromCourseClasses(@Nullable CourseClass obj) {
		if (obj != null) {
			List<CourseClassTutor> tutorRoles = getCourseClassRoles()
			if (tutorRoles != null) {
				for (CourseClassTutor aRole : tutorRoles) {
					if (aRole.getCourseClass() == obj) {
						getObjectContext().deleteObjects(aRole)
						break
					}
				}
			}
		}
	}

	/**
	 * @return all courseClasses linked via CourseClassTutor join
	 */
	@Nonnull
	@API
	List<CourseClass> getCourseClasses() {
		List<CourseClassTutor> tutorRoles = getCourseClassRoles()
		ArrayList<CourseClass> classes = new ArrayList<>()
		if (tutorRoles != null) {
			for (CourseClassTutor aRole : tutorRoles) {
				if (aRole.getCourseClass() != null) {
					classes.add(aRole.getCourseClass())
				}
			}
		}
		return classes
	}

	/**
	 * @return
	 * @deprecated Use getContact().getFullName() instead
	 */
	@Nullable
	@Deprecated
	String getFullName() {
		return getContact().getFullName()
	}

	@Override
	void validateForSave(ValidationResult result) {
		logger.debug("validateForSave")
		super.validateForSave(result)
	}

	/**
	 *
	 * @param result
	 */
	void validateForDeleteFlag(@Nonnull ValidationResult result) {
		if (getCourseClassRoles().size() > 0) {
			result.addFailure(ValidationFailure.validationFailure(this, COURSE_CLASS_ROLES_PROPERTY,
					"The tutor cannot be deleted, there are classses for this tutor."))
		}
	}

	@Override
	void addToAttachmentRelations(AttachmentRelation relation) {
		addToAttachmentRelations((TutorAttachmentRelation) relation)
	}

	@Override
	void removeFromAttachmentRelations(AttachmentRelation relation) {
		removeFromAttachmentRelations((TutorAttachmentRelation) relation)
	}

	@Override
	Class<? extends AttachmentRelation> getRelationClass() {
		return TutorAttachmentRelation.class
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
	 * @return the termination date for this tutor
	 */
	@API
	@Override
	Date getDateFinished() {
		return super.getDateFinished()
	}

	/**
	 * @return the date this tutor was first hired
	 */
	@API
	@Override
	Date getDateStarted() {
		return super.getDateStarted()
	}

	/**
	 * A tutor can have a different legal name to their regular publicised name. Use this one for official reports, like payroll.
	 *
	 * @return legal last (family) name
	 */
	@API
	@Override
	String getFamilyNameLegal() {
		return super.getFamilyNameLegal()
	}

	/**
	 * A tutor can have a different legal name to their regular publicised name. Use this one for official reports, like payroll.
	 *
	 * @return legal first name
	 */
	@API
	@Override
	String getGivenNameLegal() {
		return super.getGivenNameLegal()
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
	 * @return
	 */
	@API
	@Override
	String getPayrollRef() {
		return super.getPayrollRef()
	}

	/**
	 * @return this tutor's resume
	 */
	@API
	@Override
	String getResume() {
		return super.getResume()
	}

	/**
	 * @return the contact object linked to this tutor
	 */
	@Nonnull
	@API
	@Override
	Contact getContact() {
		return super.getContact()
	}

	/**
	 * Working with children check was verified
	 * @return date of the check
	 */
	@API @Nullable @Override
	Date getWwChildrenCheckedOn() {
		return super.getWwChildrenCheckedOn()
	}

	/**
	 * Working with children check was verified
	 * @return date that the verification expires and should be checked again
	 */
	@API @Nullable @Override
	Date getWwChildrenExpiry() {
		return super.getWwChildrenExpiry()
	}

	/**
	 * Working with children check was verified
	 * @return reference code from the WWC authority
	 */
	@API @Nullable @Override
	String getWwChildrenRef() {
		return super.getWwChildrenRef()
	}

	/**
	 * Working with children check was verified
	 * @return current status of the check
	 */
	@API @Nonnull @Override
	WorkingWithChildrenStatus getWwChildrenStatus() {
		return super.getWwChildrenStatus()
	}

	/**
	 * @return list of class roles for this tutor
	 */
	@Nonnull
	@API
	@Override
	List<CourseClassTutor> getCourseClassRoles() {
		return super.getCourseClassRoles()
	}

	/**
	 * This returns a list of every single session this tutor is teaching, past or present. For tutors with a lot of
	 * history, this could be slow, so perform a query instead for the specific data you need.
	 *
	 * @return all sessions linked to this tutor
	 */
	@Nonnull
	@API
	@Override
	List<Session> getSessions() {
		return super.getSessions()
	}

	/**
	 * By default, what type of Payslip to create for tutor pay associated with this tutor.
	 * If NULL, then don't create tutor pay at all.
	 * @return
	 */
	@API @Nullable @Override
	PayslipPayType getPayType() {
		return super.getPayType()
	}
}
