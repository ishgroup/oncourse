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

import com.google.inject.Inject
import com.sun.istack.NotNull
import ish.common.types.AvetmissStudentDisabilityType
import ish.common.types.AvetmissStudentEnglishProficiency
import ish.common.types.AvetmissStudentIndigenousStatus
import ish.common.types.AvetmissStudentLabourStatus
import ish.common.types.AvetmissStudentPriorEducation
import ish.common.types.AvetmissStudentSchoolLevel
import ish.common.types.ClientIndustryEmploymentType
import ish.common.types.ClientOccupationIdentifierType
import ish.common.types.StudentCitizenship
import ish.common.types.UsiStatus
import ish.oncourse.API
import ish.oncourse.cayenne.QueueableEntity
import ish.oncourse.cayenne.Taggable
import ish.oncourse.server.cayenne.glue._Student
import ish.oncourse.server.services.IAutoIncrementService
import org.apache.cayenne.PersistenceState
import org.apache.cayenne.validation.BeanValidationFailure
import org.apache.cayenne.validation.ValidationResult
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

import javax.annotation.Nonnull
import javax.annotation.Nullable
/**
 * A student is a type of contact who is able to be enrolled. Every Student object will be linked to exactly one
 * Contact. Only student specific data is stored here: for all regular contact attributes and relationships, refer to the
 * Contact object.
 *
 * A contact can be both a student and tutor at the same time.
 *
 */
@API
@QueueableEntity
class Student extends _Student implements StudentTrait, Queueable, Taggable, AttachableTrait {

	public static final String CONTACT_KEY = "contact";
	public static final String OUTCOMES = "outcomes";

	public static final int MIN_SCHOOL_COMPLETION_YEAR = 1940;

	private static Logger logger = LogManager.getLogger()

	@Inject
	private transient IAutoIncrementService autoIncrementService

	@Override
	void postAdd() {
		super.postAdd()
		if (getLabourForceStatus() == null) {
			setLabourForceStatus(AvetmissStudentLabourStatus.DEFAULT_POPUP_OPTION)
		}
		if (getEnglishProficiency() == null) {
			setEnglishProficiency(AvetmissStudentEnglishProficiency.DEFAULT_POPUP_OPTION)
		}
		if (getDisabilityType() == null) {
			setDisabilityType(AvetmissStudentDisabilityType.DEFAULT_POPUP_OPTION)
		}
		if (getHighestSchoolLevel() == null) {
			setHighestSchoolLevel(AvetmissStudentSchoolLevel.DEFAULT_POPUP_OPTION)
		}
		if (getIndigenousStatus() == null) {
			setIndigenousStatus(AvetmissStudentIndigenousStatus.DEFAULT_POPUP_OPTION)
		}
		if (getIsOverseasClient() == null) {
			setIsOverseasClient(Boolean.FALSE)
		}
		if (getIsStillAtSchool() == null) {
			setIsStillAtSchool(Boolean.FALSE)
		}
		// if ( getLanguage() == null ) setLanguage( null ); // FIXME: (ari) do we want English perhaps?
		if (getPriorEducationCode() == null) {
			setPriorEducationCode(AvetmissStudentPriorEducation.DEFAULT_POPUP_OPTION)
		}
		if (getStudentNumber() == null) {
			setStudentNumber(autoIncrementService.getNextStudentNumber())
		}
		if (getUsiStatus() == null) {
			setUsiStatus(UsiStatus.DEFAULT_NOT_SUPPLIED)
		}
		if (getCitizenship() == null) {
			setCitizenship(StudentCitizenship.NO_INFORMATION)
		}
		if (getFeeHelpEligible() == null) {
			setFeeHelpEligible(false)
		}
		if (getClientIndustryEmployment() == null) {
			setClientIndustryEmployment(ClientIndustryEmploymentType.NOT_SET)
		}
		if (getClientOccupationIdentifier() == null) {
			setClientOccupationIdentifier(ClientOccupationIdentifierType.NOT_SET)
		}
	}

	@Override
	void setPersistenceState(int persistenceState) {
		super.setPersistenceState(persistenceState)
		if (persistenceState == PersistenceState.DELETED) {
			if (getContact() != null) {
				getContact().setIsStudent(false)
			}
		}
	}

	@Override
	void preUpdate() {
		super.preUpdate()
		if (getStudentNumber() == null) {
			setStudentNumber(autoIncrementService.getNextStudentNumber())
		}
	}

	@Override
	void validateForDelete(@Nonnull final ValidationResult result) {
		if (getEnrolments() != null && getEnrolments().size() > 0) {
			result.addFailure(new BeanValidationFailure(this, ENROLMENTS.getName(), "There are enrolments for this student."))
		}
		if (getPriorLearnings() != null && getPriorLearnings().size() > 0) {
			result.addFailure(new BeanValidationFailure(this, PRIOR_LEARNINGS.getName(), "There are prior learnings for this student."))
		}
		if (getWaitingLists() != null && getWaitingLists().size() > 0) {
			result.addFailure(new BeanValidationFailure(this, WAITING_LISTS.getName(), "This student is on a waiting list."))
		}
	}

	/**
	 *
	 * @return full name of the student, including the middle name if any.
	 */
	@Nullable
	@API
	String getFullName() {
		return getContact().getFullName()
	}

	/**
	 * @return list of invoices linked to this student
	 */
	@Nonnull
	@API
	List<Invoice> getInvoices() {
		return getContact().getInvoices()
	}

	@Override
	void addToAttachmentRelations(AttachmentRelation relation) {
		addToAttachmentRelations((StudentAttachmentRelation) relation)
	}

	@Override
	void removeFromAttachmentRelations(AttachmentRelation relation) {
		removeFromAttachmentRelations((StudentAttachmentRelation) relation)
	}

	@Override
	Class<? extends AttachmentRelation> getRelationClass() {
		return StudentAttachmentRelation.class
	}

	/**
	 * Commonwealth Higher Education Student Support Number
	 * @return CHESSN number for this student
	 */
	@API
	@Override
	String getChessn() {
		return super.getChessn()
	}

	/**
	 * @return the a value represneting the citzenship of this student for AVETMISS reporting
	 */
	@Nonnull
	@API
	@Override
	StudentCitizenship getCitizenship() {
		return super.getCitizenship()
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
	 * @return a value representing the students disability status for AVETMISS reporting
	 */
	@Nonnull
	@API
	@Override
	AvetmissStudentDisabilityType getDisabilityType() {
		return super.getDisabilityType()
	}

	/**
	 * @return a value representing the students english proficiency for AVETMISS reporting
	 */
	@Nonnull
	@API
	@Override
	AvetmissStudentEnglishProficiency getEnglishProficiency() {
		return super.getEnglishProficiency()
	}

	/**
	 * @return true if the student is eligible for fee help
	 */
	@Nonnull
	@API
	@Override
	Boolean getFeeHelpEligible() {
		return super.getFeeHelpEligible()
	}

	/**
	 * @return a value representing the students highest level of school completed for AVETMISS reporting
	 */
	@Nonnull
	@API
	@Override
	AvetmissStudentSchoolLevel getHighestSchoolLevel() {
		return super.getHighestSchoolLevel()
	}

	/**
	 * @return a value representing the students indigenous status for AVETMISS reporting
	 */
	@Nonnull
	@API
	@Override
	AvetmissStudentIndigenousStatus getIndigenousStatus() {
		return super.getIndigenousStatus()
	}

	/**
	 * @return true if this is an overseas client
	 */
	@Nonnull
	@API
	@Override
	Boolean getIsOverseasClient() {
		return super.getIsOverseasClient()
	}

	/**
	 * @return true if this student is still at school
	 */
	@API
	@Override
	Boolean getIsStillAtSchool() {
		return super.getIsStillAtSchool()
	}

	/**
	 * @return a value representing the students labour/work status for AVETMISS reporting
	 */
	@Nonnull
	@API
	@Override
	AvetmissStudentLabourStatus getLabourForceStatus() {
		return super.getLabourForceStatus()
	}

	/**
	 * @return
	 */
	@API
	@Override
	String getMedicalInsurance() {
		return super.getMedicalInsurance()
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
	 * @return passport number for this student
	 */
	@API
	@Override
	String getPassportNumber() {
		return super.getPassportNumber()
	}

	/**
	 * @return a value representing the students highest level of prior education for AVETMISS reporting
	 */
	@Nonnull
	@API
	@Override
	AvetmissStudentPriorEducation getPriorEducationCode() {
		return super.getPriorEducationCode()
	}

	/**
	 * @return special needs of this student
	 */
	@API
	@Override
	String getSpecialNeeds() {
		return super.getSpecialNeeds()
	}

	/**
	 * @return true if the student needs assistance
	 */
	@API
	@Override
	Boolean getSpecialNeedsAssistance() {
		return super.getSpecialNeedsAssistance()
	}

	/**
	 * @return unique student number
	 */
	@Nonnull
	@API
	@Override
	Long getStudentNumber() {
		return super.getStudentNumber()
	}

	/**
	 * @return where this student was born
	 */
	@API
	@Override
	String getTownOfBirth() {
		return super.getTownOfBirth()
	}

	/**
	 * @return
	 */
	@API
	@Override
	String getUniqueLearnerIndentifier() {
		return super.getUniqueLearnerIndentifier()
	}

	/**
	 * @return the USI for this student
	 */
	@API
	@Override
	String getUsi() {
		return super.getUsi()
	}

	/**
	 * A verified USI can be validated against the government verification service.
	 * @return the verification status fof the students USI
	 */
	@Nonnull
	@API
	@Override
	UsiStatus getUsiStatus() {
		return super.getUsiStatus()
	}

	/**
	 * @return the date this students visa expires
	 */
	@API
	@Override
	Date getVisaExpiryDate() {
		return super.getVisaExpiryDate()
	}

	/**
	 * @return the visa number of this student
	 */
	@API
	@Override
	String getVisaNumber() {
		return super.getVisaNumber()
	}

	/**
	 * @return the visa type of this student
	 */
	@API
	@Override
	String getVisaType() {
		return super.getVisaType()
	}



	/**
	 * @return the year this student left/completed schooling
	 */
	@API
	@Override
	Integer getYearSchoolCompleted() {
		return super.getYearSchoolCompleted()
	}

	/**
	 * @return applications made by this student
	 */
	@Nonnull
	@API
	@Override
	List<Application> getApplications() {
		return super.getApplications()
	}

	/**
	 * @return list of all attendance records for this student
	 */
	@Nonnull
	@API
	@Override
	List<Attendance> getAttendances() {
		return super.getAttendances()
	}

	/**
	 * @return list of all certificates awarded to this student
	 */
	@Nonnull
	@API
	@Override
	List<Certificate> getCertificates() {
		return super.getCertificates()
	}

	/**
	 * @return list of all concessions linked to this student
	 */
	@Nonnull
	@API
	@Override
	List<StudentConcession> getConcessions() {
		return super.getConcessions()
	}

	/**
	 * @return contact record linked to this student
	 */
	@API
	@Override
	@NotNull
	Contact getContact() {
		return super.getContact()
	}

	/**
	 * @return country where student was born
	 */
	@API
	@Override
	Country getCountryOfBirth() {
		return super.getCountryOfBirth()
	}

	/**
	 * @return student's country of residency
	 */
	@API
	@Override
	Country getCountryOfResidency() {
		return super.getCountryOfResidency()
	}

	/**
	 * @return list of all enrolments (either successful, failed or cancelled) linked to this student
	 */
	@Nonnull
	@API
	@Override
	List<Enrolment> getEnrolments() {
		return super.getEnrolments()
	}

	/**
	 * @return the language spoken by this student
	 */
	@API
	@Override
	Language getLanguage() {
		return super.getLanguage()
	}

	/**
	 * @return a list of all the waiting lists for this student
	 */
	@Nonnull
	@API
	@Override
	List<WaitingList> getWaitingLists() {
		return super.getWaitingLists()
	}

}
