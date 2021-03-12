
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

import ish.common.CalculateEndDate
import ish.common.CalculateStartDate
import ish.common.types.ClassFundingSource
import ish.common.types.DeliveryMode
import ish.common.types.OutcomeStatus
import ish.messaging.IOutcome
import ish.oncourse.API
import ish.oncourse.cayenne.QueueableEntity
import ish.oncourse.entity.delegator.OutcomeDelegator
import ish.oncourse.function.CalculateOutcomeReportableHours
import ish.oncourse.server.cayenne.glue._Outcome
import ish.util.LocalDateUtils
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

import javax.annotation.Nonnull
import javax.annotation.Nullable
import java.time.LocalDate

/**
 * Outcomes are a relationship between a student and a Module/Unit of Competency and represents
 * a student's progress through training, and the result of their assessment.
 * Typically an outcome is linked to a single enrolment, but it is possible for outcomes to be
 * created which aren't linked to an enrolment at all for recognition of prior learning.
 *
 */
@API
@QueueableEntity
class Outcome extends _Outcome implements IOutcome, Queueable, OutcomeTrait {

	private static final Logger logger = LogManager.getLogger()
	public static final String STUDENT_NAME = "studentName"
	public static final String STARTDATE = "startDate"
	public static final String ENDDATE = "endDate"
	public static final String TRAINING_PLAN_START_DATE_PROPERTY = "trainingPlanStartDate"
	public static final String TRAINING_PLAN_END_DATE_PROPERTY = "trainingPlanEndDate"

	@Override
	void postAdd() {
		super.postAdd()
		logger.debug("onEntityCreation")
	}

	@Override
	void preUpdate() {
		super.preUpdate()
		updateOverriddenStartEndDates()
	}

	@Override
	void prePersist() {
		super.prePersist()

		if (startDateOverridden == null) {
			startDateOverridden = false
		}
		if (endDateOverridden == null) {
			endDateOverridden = false
		}
		if (reportableHours == null) {
			reportableHours == CalculateOutcomeReportableHours.valueOf(this).calculate()
		}

		updateOverriddenFields()
		updateOverriddenStartEndDates()
	}

	private void updateOverriddenFields() {
		if (getFundingSource() == null && getEnrolment() != null) {
			setFundingSource(getEnrolment().getFundingSource());
		}

		if (getVetPurchasingContractID() == null && getEnrolment() != null) {
			setVetPurchasingContractID(getEnrolment().getVetPurchasingContractID());
		}

		if (getVetFundingSourceStateID() == null && getEnrolment() != null) {
			setVetFundingSourceStateID(getEnrolment().getVetFundingSourceStateID());
		}

		if (getVetPurchasingContractScheduleID() == null &&
				getEnrolment() != null && getEnrolment().getCourseClass() != null) {
			setVetPurchasingContractScheduleID(getEnrolment().getCourseClass().getVetPurchasingContractScheduleID());
		}

		if (getDeliveryMode() == null &&
				getEnrolment() != null && getEnrolment().getCourseClass() != null) {
			setDeliveryMode(getEnrolment().getCourseClass().getDeliveryMode());
		}

		if (getReportableHours() == null) {
			setReportableHours(CalculateOutcomeReportableHours.valueOf(this).calculate());
		}
	}

	private void updateOverriddenStartEndDates() {
		if (startDate == null && !startDateOverridden) {
			startDate = actualStartDate
		}

		if (endDate == null && !endDateOverridden) {
			endDate = actualEndDate
		}
	}

	/**
	 * @return
	 */
	@Override
	Object getValueForKey(String key) {
		if ((STATUS_PROPERTY + "_for_print") == key) {
			if (getModule() == null) {
				return getKeyForValue(getValueForKey(STATUS_PROPERTY), OutcomeStatus.STATUS_CHOICES_NON_VET)
			}
			return getKeyForValue(getValueForKey(STATUS_PROPERTY), OutcomeStatus.STATUS_CHOICES_ALL)
		}

		return super.getValueForKey(key)
	}

	ClassFundingSource getOutcomeFundingSource() {
		return super.getFundingSource()
	}

	/**
	 * An AVETMISS reporting requirement for the funding source of an outcome. If the funding source
	 * is not overridden in the outcome, this function will return the value from the enrolment.
	 *
	 * @return funding source
	 */
	@API
	@Override
	ClassFundingSource getFundingSource() {
		super.getFundingSource()
	}

	/**
	 * An AVETMISS reporting requirement for the funding source state ID of an outcome. If the funding source state ID
	 * is not overridden in the outcome, this function will return the value from the enrolment and if not set there from the class.
	 *
	 * @return funding source state ID
	 */
	@API
	@Override
	String getVetFundingSourceStateID() {
		String fundingSource = super.getVetFundingSourceStateID()
		if (getEnrolment() != null && fundingSource == null) {
			fundingSource = getEnrolment().getVetFundingSourceStateID()
		}
		return fundingSource
	}

	String getOutcomeVetFundingSourceStateID() {
		return super.getVetFundingSourceStateID()
	}

	/**
	 * An AVETMISS reporting requirement for the purchasing contract ID of an outcome. If the purchasing contract ID
	 * is not overridden in the outcome, this function will return the value from the enrolment and if not set there from the class.
	 *
	 * @return purchasing contract ID
	 */
	@API
	@Override
	String getVetPurchasingContractID() {
		String contractID = super.getVetPurchasingContractID()
		if (getEnrolment() != null && contractID == null) {
			contractID = getEnrolment().getVetPurchasingContractID()
		}
		return contractID
	}

	String getOutcomeVetPurchasingContractID() {
		return super.getVetPurchasingContractID()
	}

	/**
	 * An AVETMISS reporting requirement for the purchasing contract schedule ID of an outcome. If the purchasing contract schedule ID
	 * is not overridden in the outcome, this function will return the value from the class.
	 *
	 * @return VET purchasing contract schedule ID
	 */
	@API
	@Override
	String getVetPurchasingContractScheduleID() {
		String contractID = super.getVetPurchasingContractScheduleID()
		if (getEnrolment() != null && contractID == null) {
			contractID = getEnrolment().getCourseClass().getVetPurchasingContractScheduleID()
		}
		return contractID
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
	 * An AVETMISS reporting requirement for the delivery mode of an outcome.
	 * TODO: review why this isn't picking up the status from the class when this value is null
	 * @return
	 */
	@API
	@Override
	DeliveryMode getDeliveryMode() {
	    super.getDeliveryMode()
	}

	/**
	 * @return
	 */
	@API
	@Override
	Integer getHoursAttended() {
		return super.getHoursAttended()
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
	BigDecimal getReportableHours() {
		return super.getReportableHours()
	}

	/**
	 * @return specific program identifier used for NCVER reporting
	 */
	@API
	@Override
	String getSpecificProgramIdentifier() {
		return super.getSpecificProgramIdentifier()
	}

	/**
	 * An AVETMISS reporting requirement for the outcome status.
	 * If this payline was created from a classCost record, then this is Nonnull
	 * @return outcome status
	 *
	 */
	@Nonnull
	@API
	@Override
	OutcomeStatus getStatus() {
		return super.getStatus()
	}



	/**
	 * @return relational object between this outcome and the related certificate
	 */
	@Nonnull
	@Override
	List<CertificateOutcome> getCertificateOutcomes() {
		return super.getCertificateOutcomes()
	}

	/**
	 * All the certificates this outcome has ever been linked to. Be careful since this might return a large number of records.
	 * You might be better performing a query on the certificate entity to get just the results you need.
	 *
	 * @return a list of certificates
	 */
	@Nonnull
	@API
	List<Certificate> getCertificate() {
		List<Certificate> result = new ArrayList<>()
		for (CertificateOutcome co: getCertificateOutcomes()) {
			result.add(co.getCertificate())
		}
		return result
	}

	/**
	 * @return onCourse enrolment linked to the outcome
	 */
	@Nullable
	@API
	@Override
	Enrolment getEnrolment() {
		return super.getEnrolment()
	}

	/**
	 * @return unit of competency linked to the outcome
	 */
	@Nullable
	@API
	@Override
	Module getModule() {
		return super.getModule()
	}

	/**
	 * PriorLearning is an outcome that was imported into onCourse from outside sources (e.g. different college) or was added manually
	 * without a link to existing enrolment
	 *
	 * @return PriorLearning ID
	 */
	@Nullable
	@API
	@Override
	PriorLearning getPriorLearning() {
		return super.getPriorLearning()
	}

	/**
	 * Tutors can login and mark outcomes for their students in onCourse portal
	 *
	 * @return the ID of a tutor who marked the outcome
	 */
	@Nullable
	@API
	@Override
	Tutor getMarkedByTutor() {
		return super.getMarkedByTutor()
	}

	/**
	 * @return date and time when outcome was marked by a tutor
	 */
	@Nullable
	@API
	@Override
	Date getMarkedByTutorDate() {
		return super.getMarkedByTutorDate()
	}

	@Override
	String getSummaryDescription() {
		Contact contact = null
		if (getEnrolment() != null) {
			contact = getEnrolment().getStudent().getContact()
		} else if(getPriorLearning() != null) {
			contact = getPriorLearning().getStudent().getContact()
		}
		if(contact == null) {
			return super.getSummaryDescription()
		}
		return contact.getName(false)
	}


	/**
	 * @return start date of training plan
	 */
	@API
	LocalDate getTrainingPlanStartDate() {
		return LocalDateUtils.dateToValue(calculateStartDate(Boolean.FALSE))
	}

	/**
	 * @return end date of training plan
	 */
	@API
	LocalDate getTrainingPlanEndDate() {
		return LocalDateUtils.dateToValue(calculateEndDate(Boolean.FALSE))
	}

	/**
	 * @return actual start date of outcome: depends on attendances and assessment submissions
	 */
	@API
	LocalDate getActualStartDate() {
		return LocalDateUtils.dateToValue(calculateStartDate(Boolean.TRUE))
	}

	/**
	 * @return actual end date of outcome: depends on attendances and assessment submissions
	 */
	@API
	LocalDate getActualEndDate() {
		return LocalDateUtils.dateToValue(calculateEndDate(Boolean.TRUE))
	}

	Date calculateStartDate(Boolean attendanceTakenIntoAccount) {
		return new CalculateStartDate(OutcomeDelegator.valueOf(this), attendanceTakenIntoAccount).calculate()
	}

	Date calculateEndDate(Boolean attendanceTakenIntoAccount) {
		return new CalculateEndDate(OutcomeDelegator.valueOf(this), attendanceTakenIntoAccount).calculate()
	}
}
