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
import ish.common.types.*
import ish.math.Money
import ish.messaging.IEnrolment
import ish.oncourse.API
import ish.oncourse.cayenne.QueueableEntity
import ish.oncourse.entity.delegator.OutcomeDelegator
import ish.oncourse.function.CalculateOutcomeReportableHours
import ish.oncourse.server.cayenne.glue._Enrolment
import ish.util.LocalDateUtils
import ish.validation.EnrolmentStatusValidator
import org.apache.cayenne.PersistenceState
import org.apache.cayenne.query.ObjectSelect
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

import javax.annotation.Nonnull
import javax.annotation.Nullable

import static ish.common.types.EnrolmentStatus.NEW
import static java.lang.String.format


/**
 * An enrolment joins a student to a class. There can be only one enrolment per student per class.
 *
 * An enrolment doesn't directly carry any financial information about the cost of the enrolment, however it might be linked
 * to one or more invoice lines which you can interrogate to determine the cost, discounts and follow through to the invoice
 * and payments. The join between InvoiceLine and Enrolment is one of the few places in onCourse where financial data
 * touches educational data. This is a quite deliberate delineation between the two.
 *
 * An enrolment also doesn't carry information about attendance; there is an Attendance object which joins the Enrolment to
 * each Session in the CourseClass.
 *
 * If you are looking for Outcomes for each unit of competency (Module) undertaken by the Student, then of course follow
 * the relation to Outcomes for this Enrolment.
 *
 */
//TODO docs
@API
@QueueableEntity
class Enrolment extends _Enrolment implements EnrolmentTrait, IEnrolment, Queueable, NotableTrait, ExpandableTrait, AttachableTrait {

	public static final String IS_VET_PROPERTY = 'vet'
	static final String DISPLAY_STATUS_PROP = 'displayStatus'


	private static final Logger logger = LogManager.getLogger()

	boolean isVet() {
		for (Outcome outcome : getOutcomes()) {
			if (outcome.getModule() != null) {
				return true
			}
		}
		return false
	}

	@Override
	void postAdd() {
		super.postAdd()
		if (getStatus() == null) {
			setStatus(NEW)
		}

		if (getEligibilityExemptionIndicator() == null) {
			setEligibilityExemptionIndicator(false)
		}

		if (getVetIsFullTime() == null) {
			setVetIsFullTime(false)
		}

		if (getVetFeeIndicator() == null) {
			setVetFeeIndicator(false)
		}

		if (getStudyReason() == null) {
			setStudyReason(StudyReason.STUDY_REASON_NOT_STATED)
		}

		if (getVetFeeExemptionType() == null) {
			setVetFeeExemptionType(VETFeeExemptionType.UNSET)
		}

		if (getConfirmationStatus() == null) {
			setConfirmationStatus(ConfirmationStatus.NOT_SENT)
		}

		if (getFeeHelpAmount() == null) {
			setFeeHelpAmount(Money.ZERO)
		}

		if (getSuppressAvetmissExport() == null) {
			setSuppressAvetmissExport(false)
		}

		if (getAttendanceType() == null) {
			setAttendanceType(CourseClassAttendanceType.NO_INFORMATION)
		}
	}

	@Override
	void prePersist() {
		updateOverriddenFields()
	}

	/**
	 * @return class cacellation warning message
	 */
	@API
	String getCancelWarningMessage() {
		if(hasVoucherPayments()) {
			return "The enrolment for $student.contact.name was paid for using a voucher. Please note that the student will receive a credit note to the value of their enrolment fee."
		} else {
			return null
		}
	}

	boolean hasVoucherPayments() {
		return !ObjectSelect.query(PaymentIn.class)
				.where(PaymentIn.PAYMENT_METHOD.dot(PaymentMethod.TYPE).eq(PaymentType.VOUCHER))
				.and(PaymentIn.PAYMENT_IN_LINES.dot(PaymentInLine.INVOICE).dot(Invoice.INVOICE_LINES).dot(InvoiceLine.ENROLMENT).eq(this))
				.select(context).isEmpty()
	}


	@Override
	void preUpdate() {
		super.preUpdate()
	}

	private void updateOverriddenFields() {
		if (getFundingSource() == null && getCourseClass() != null) {
			setFundingSource(getCourseClass().getFundingSource());
		}
		if (getVetPurchasingContractID() == null && getCourseClass() != null) {
			setVetPurchasingContractID(getCourseClass().getVetPurchasingContractID());
		}
		if (getVetFundingSourceStateID() == null && getCourseClass() != null) {
			setVetFundingSourceStateID(getCourseClass().getVetFundingSourceStateID());
		}
		if (getOutcomes() != null && getOutcomes().size() > 0) {
			outcomes.findAll {o -> !o.startDateOverridden }
					.each {o -> o.setStartDate(LocalDateUtils.dateToValue(new CalculateStartDate(OutcomeDelegator.valueOf(o), Boolean.TRUE).calculate())) }
			outcomes.findAll {o -> !o.endDateOverridden }
					.each {o -> o.setEndDate(LocalDateUtils.dateToValue(new CalculateEndDate(OutcomeDelegator.valueOf(o), Boolean.TRUE).calculate())) }

			outcomes.findAll{ o -> !o.fundingSource}.each { o -> o.setFundingSource(getFundingSource())}
			outcomes.findAll{ o -> !o.vetPurchasingContractID}.each { o -> o.setVetPurchasingContractID(getVetPurchasingContractID())}
			outcomes.findAll{ o -> !o.vetFundingSourceStateID}.each { o -> o.setVetFundingSourceStateID(getVetFundingSourceStateID())}
			if (getCourseClass() != null) {
				if (getCourseClass().getVetPurchasingContractScheduleID() != null) {
					outcomes.findAll { o -> !o.vetPurchasingContractScheduleID }.each { o -> o.setVetPurchasingContractScheduleID(getCourseClass().getVetPurchasingContractScheduleID()) }
				}
				if (getCourseClass().getDeliveryMode() != null) {
					outcomes.findAll{ o -> !o.deliveryMode}.each { o -> o.setDeliveryMode(getCourseClass().getDeliveryMode())}
				}
			}
		}
	}

	/**
	 * Convenience method getting an outcome for a given module
	 *
	 * @param module  module linked to the outcome, can be null for non-vet outcome
	 * @param create  whether an outcome should be created in case there are no exisitng ones.
	 * @return Outcome
	 */
	@Nullable
	Outcome getOutcomeForModuleAndEnrolment(@Nullable Module module, boolean create) {
		// only add outcome if it has not been created yet
		for (Outcome o : getOutcomes()) {
			Module outcomeModule = o.getModule()
			if (outcomeModule == null && module == null || outcomeModule != null && outcomeModule == module) {
				return o
			}
		}

		if (create) {
			Outcome outcome = getObjectContext().newObject(Outcome.class)
			outcome.setModule(module)
			outcome.setEnrolment(this)
			outcome.setStatus(OutcomeStatus.STATUS_NOT_SET)
			outcome.setReportableHours(CalculateOutcomeReportableHours.valueOf(outcome).calculate())
			return outcome
		}
		return null
	}

	/**
	 * Convenience method getting an attendance for a given session and student
	 *
	 * @param session - session linked to the attendance, cannot be null
	 * @param student - student linked to the attendance, cannot be null
	 * @return Attendance
	 */
	// TODO: this method signature makes no sense. It should either be static or not require a student
	@Nullable
	Attendance getAttendanceForSessionAndStudent(@Nullable Session session, @Nullable Student student) {
		if (student == null || session == null) {
			return null
		}

		List<Attendance> attendances = session.getAttendance()

		for (Attendance at : attendances) {
			if (at.getStudent().equalsIgnoreContext(student)) {
				return at
			}
		}

		return null
	}

	@Override
	void setPersistenceState(final int persistenceState) {
		logger.debug("changing persistence state from: {} to: {}",
				PersistenceState.persistenceStateName(getPersistenceState()),
				PersistenceState.persistenceStateName(persistenceState))
		super.setPersistenceState(persistenceState)
	}

	boolean statusOkForWillowProcessing() {
		return getStatus() == null || EnrolmentStatus.QUEUED == getStatus()
	}

	@Override
	void addToAttachmentRelations(AttachmentRelation relation) {
		addToAttachmentRelations((EnrolmentAttachmentRelation) relation)
	}

	@Override
	void removeFromAttachmentRelations(AttachmentRelation relation) {
		removeFromAttachmentRelations((EnrolmentAttachmentRelation) relation)
	}

	@Override
	Class<? extends AttachmentRelation> getRelationClass() {
		return EnrolmentAttachmentRelation.class
	}

	/**
	 * Overriding the entity setter to make sure illegal changes aren't allowed.<br>
	 * <br>
	 * List of allowed status changes: <br>
	 * <ul>
	 * <li>null -> anything</li>
	 * <li>NEW -> anything but null</li>
	 * <li>QUEUED -> anything but null/NEW</li>
	 * <li>IN_TRANSACTION -> anything but null/NEW/QUEUED</li>
	 * <li>SUCCESS -> only CANCELLED/REFUNDED allowed</li>
	 * <li>FAILED/FAILED_CARD_DECLINED/FAILED_NO_PLACES -> no further status change allowed</li>
	 * <li>CANCELLED/REFUNDED -> no further status change allowed</li>
	 * </ul>
	 *
	 * @param status new enrolment status to set
	 */
	@Override
	void setStatus(EnrolmentStatus status) {
		if (getStatus() != null) {
			validateStatus(status)
		}
		super.setStatus(status)
	}

	private void validateStatus(EnrolmentStatus status) {
		if (status == null) {
			throw new IllegalArgumentException(format("Enrolment: objectId: %s, willowId: %d: Can't change status to null!", getObjectId(), getWillowId()))
		}

		if (status == getStatus()) {
			return
		}

		boolean error = !EnrolmentStatusValidator.valueOf(getStatus(), status).validate()
		if (error) {
			throw new IllegalArgumentException(format(
					"Enrolment: objectId: %s, willowId:%d : Can't set the %s status for enrolment with %s status!",
					getObjectId(), getWillowId(), status, getStatus()))
		}
	}

	/**
	 * Check if async replication is allowed on this object.
	 *
	 * @return isAsyncReplicationAllowed
	 */
	@Override
	boolean isAsyncReplicationAllowed() {
		// first of all we need to check that this enrollment isn't a web-enrollment with in transaction status
		// because replicate them back from angel to willow after create of Attendancies and Outcomes
		// may lead to status overriding from final to in transaction like in case #14882.
		if (PaymentSource.SOURCE_WEB == getSource() && EnrolmentStatus.IN_TRANSACTION == getStatus()) {
			return false
		}


		// need to get rid of this loop as soon as we remove old QE
		for (InvoiceLine il : getInvoiceLines()) {
			if (il != null && !il.getInvoice().getPaymentInLines().isEmpty()) {
				for (PaymentInLine line : il.getInvoice().getPaymentInLines()) {
					PaymentIn paymentIn = line.getPaymentIn()
					if ((paymentIn.getStatus() != PaymentStatus.IN_TRANSACTION && paymentIn.getStatus() != PaymentStatus.CARD_DETAILS_REQUIRED) || paymentIn.gatewayReference != null) {
						return true
					}
				}
				return false
			}
		}

		return getStatus() != null && EnrolmentStatus.QUEUED != getStatus() && EnrolmentStatus.IN_TRANSACTION != getStatus()
	}

	/**
	 * @see ish.common.payable.EnrolmentInterface#isInFinalStatus()
	 */
	@Override
	boolean isInFinalStatus() {
		return EnrolmentStatus.STATUSES_FINAL.contains(getStatus())
	}

	/**
	 * An AVETMISS reporting requirement for the VET Funding source state ID of an enrolment. If the VET Funding source state ID
	 * is not overridden in the enrolment, this function will return the value from the class.
	 *
	 * @return
	 */
	@API
	@Override
	String getVetFundingSourceStateID() {
		super.getVetFundingSourceStateID()
	}

	/**
	 * An AVETMISS reporting requirement for the purchasing contract ID of an enrolment. If the purchasing contract ID
	 * is not overridden in the enrolment, this function will return the value from the class.
	 *
	 * @return
	 */
	@API
	@Override
	String getVetPurchasingContractID() {
		super.getVetPurchasingContractID()
	}

	/**
	 * @return all modules belongs to the course this enrolment belongs to
	 */
	@API
	final List<Module> getCourseModules() {
		final List<Module> result = new ArrayList<>()
		if (getCourseClass() != null && getCourseClass().getCourse() != null && getCourseClass().getCourse().getModules() != null) {
			result.addAll(getCourseClass().getCourse().getModules())
		}
		return Collections.unmodifiableList(result)
	}

	/**
	 * @return standard fundingSource values for AVETMISS reporting
	 */
	@Override
	ClassFundingSource getFundingSource() {
		super.getFundingSource()
	}

	/**
	 * Returns the funding source for this enrolment.
	 *
	 * @return funding source used for this enrolment
	 */
	@API
	@Nullable
	@Override
	FundingSource getRelatedFundingSource() {
		super.getRelatedFundingSource()
	}

	/**
	 * @return enrolment confirmation email status: not sent, sent or suppressed from sending
	 */
	@Nonnull
	@API
	@Override
	ConfirmationStatus getConfirmationStatus() {
		return super.getConfirmationStatus()
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
	 * @return
	 */
	@API
	@Override
	String getCreditFOEId() {
		return super.getCreditFOEId()
	}

	/**
	 * @return highest level of prior VET study
	 */
	@API
	@Override
	CreditLevel getCreditLevel() {
		return super.getCreditLevel()
	}

	/**
	 * @return
	 */
	@API
	@Override
	String getCreditOfferedValue() {
		return super.getCreditOfferedValue()
	}

	/**
	 * @return
	 */
	@API
	@Override
	String getCreditProvider() {
		return super.getCreditProvider()
	}

	/**
	 * @return
	 */
	@API
	@Override
	CreditProviderType getCreditProviderType() {
		return super.getCreditProviderType()
	}

	/**
	 * @return
	 */
	@API
	@Override
	RecognitionOfPriorLearningIndicator getCreditTotal() {
		return super.getCreditTotal()
	}

	/**
	 * @return details of prior study for which credit/RPL was offered
	 */
	@API
	@Override
	CreditType getCreditType() {
		return super.getCreditType()
	}

	/**
	 * @return
	 */
	@API
	@Override
	String getCreditUsedValue() {
		return super.getCreditUsedValue()
	}

	/**
	 * @return
	 */
	@Nonnull
	@API
	@Override
	Boolean getEligibilityExemptionIndicator() {
		return super.getEligibilityExemptionIndicator()
	}

	/**
	 * @return
	 */
	@Nonnull
	@API
	@Override
	Money getFeeHelpAmount() {
		return super.getFeeHelpAmount()
	}

	/**
	 * @return the VET Fee help status for this enrolment
	 */
	@API
	@Override
	EnrolmentVETFeeHelpStatus getFeeHelpStatus() {
		return super.getFeeHelpStatus()
	}

	/**
	 * @return code indicating student status
	 */
	@API
	@Override
	StudentStatusForUnitOfStudy getFeeStatus() {
		return super.getFeeStatus()
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
	 * Payment can be made in onCourse (office) or from the onCourse website (web).
	 *
	 * @return where the payment for this enrolment was made
	 */
	@Nonnull
	@API
	@Override
	PaymentSource getSource() {
		return super.getSource()
	}

	/**
	 * @return origin of this enrolment: website or onCourse
	 */
	@API
	@Override
	EnrolmentStatus getStatus() {
		return super.getStatus()
	}

	/**
	 * @return ANZSIC standard code for industrial classification
	 */
	@API
	@Override
	Integer getStudentIndustryANZSICCode() {
		return super.getStudentIndustryANZSICCode()
	}

	/**
	 * @return standard set of values for AVETMISS reporting
	 */
	@Nonnull
	@API
	@Override
	StudyReason getStudyReason() {
		return super.getStudyReason()
	}

	/**
	 * @return
	 */
	@API
	@Override
	Boolean getTrainingPlanDeveloped() {
		return super.getTrainingPlanDeveloped()
	}

	/**
	 * @return
	 */
	@API
	@Override
	String getVetClientID() {
		return super.getVetClientID()
	}

	/**
	 * @return value for AVETMISS reporting
	 */
	@API
	@Override
	VETFeeExemptionType getVetFeeExemptionType() {
		return super.getVetFeeExemptionType()
	}

	/**
	 * @return
	 */
	@Nonnull
	@API
	@Override
	Boolean getVetFeeIndicator() {
		return super.getVetFeeIndicator()
	}

	/**
	 * @return returns true if this is a full time enrolment
	 */
	@Nonnull
	@API
	@Override
	Boolean getVetIsFullTime() {
		return super.getVetIsFullTime()
	}

	/**
	 * @return
	 */
	@API
	@Override
	String getVetTrainingContractID() {
		return super.getVetTrainingContractID()
	}

	/**
	 * @return linked class record
	 */
	@Nonnull
	@API
	@Override
	CourseClass getCourseClass() {
		return super.getCourseClass()
	}

	/**
	 * @return
	 */
	@Nonnull
	@Override
	List<InvoiceLine> getInvoiceLines() {
		return super.getInvoiceLines()
	}

	/**
	 * Non-VET classes will still have a single outcome for each enrolment, even though that outcome isn't linked to a
	 * Module.
	 *
	 * VET classes may have one or many outcomes. These outcomes are initially attached to the enrolment by creating one
	 * Outcome for each Module attached to the Course. However a user may remove and attach other Modules by creating
	 * or deleting Outcomes for this specific Enrolment.
	 *
	 * @return a list of outcomes
	 */
	@Nonnull
	@API
	@Override
	List<Outcome> getOutcomes() {
		return super.getOutcomes()
	}

	/**
	 * @return the student who enrolled
	 */
	@Nonnull
	@API
	@Override
	Student getStudent() {
		return super.getStudent()
	}

	/**
	 * @return all survey results for this enrolment
	 */
	@Nonnull
	@API
	@Override
	List<Survey> getSurveys() {
		return super.getSurveys()
	}

	/**
	* @return standard AttendanceTypes used for AVETMISS reporting in Victoria only
	*/
	@Nonnull
	@API
	@Override
	CourseClassAttendanceType getAttendanceType() {
		return super.getAttendanceType()
	}

	/**
	 * @return true if enrolment is suppressed from appearing in AVETMISS export
	 */
	@API
	@Override
	Boolean getSuppressAvetmissExport() {
		return super.getSuppressAvetmissExport()
	}

	/**
	 * @return
	 */
	@Nullable
	@API
	@Override
	String getCricosConfirmation() {
		return super.getCricosConfirmation()
	}

	/**
	 * Since enrolment has to many relationship 'invoiceLines' starting from angel 8.11
	 * need to use Enrolment#getOriginalInvoiceLine() method
	 * to get original invoice line which was created during enrolment process.
	 * Keep this method to support customs scripts/email templates/reports/exports and other.
	 * Will be removed in future.
	 */
	@Deprecated
	InvoiceLine getInvoiceLine() {
		return getOriginalInvoiceLine()
	}

	/**
	 * @return original invoice line which was created during enrolment process.
	 * Such invoice line store original enrolment price, discount amount and other properties which usually
	 * uses by customers in reports, exports and other components.
	 */
	@API
    @Override
	InvoiceLine getOriginalInvoiceLine() {
		List<InvoiceLine> lines = new LinkedList<>(getInvoiceLines())

		Comparator comparator = new Comparator<InvoiceLine>() {
			@Override
			int compare(InvoiceLine o1, InvoiceLine o2) {
				return o1.getInvoice().getInvoiceNumber() <=> o2.getInvoice().getInvoiceNumber()
			}
		}

		lines.sort(comparator)
		return lines.get(0)
	}


	@Override
	String getSummaryDescription() {
		if (getStudent() == null || getStudent().getContact() == null) {
			return super.getSummaryDescription()
		}
		return getStudent().getContact().getName(false)
	}

	@Override
	Class<? extends CustomField> getCustomFieldClass() {
		return EnrolmentCustomField
	}
}
