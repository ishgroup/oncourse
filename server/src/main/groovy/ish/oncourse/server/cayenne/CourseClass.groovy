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


import ish.budget.ClassCostUtil
import ish.common.types.ClassCostFlowType
import ish.common.types.ClassFundingSource
import ish.common.types.CourseClassAttendanceType
import ish.common.types.DeliveryMode
import ish.common.types.SessionRepetitionType
import ish.math.Money
import ish.messaging.ICourseClass
import ish.messaging.IModule
import ish.oncourse.API
import ish.oncourse.cayenne.CourseClassUtil
import ish.oncourse.cayenne.QueueableEntity
import ish.oncourse.function.CalculateCourseClassNominalHours
import ish.oncourse.function.CalculateCourseClassReportableHours
import ish.oncourse.server.cayenne.glue._CourseClass
import ish.oncourse.server.cayenne.glue._Session
import ish.util.MoneyUtil
import ish.validation.ValidationFailure
import org.apache.cayenne.PersistenceState
import org.apache.cayenne.query.Ordering
import org.apache.cayenne.query.SortOrder
import org.apache.cayenne.validation.ValidationResult
import org.apache.commons.lang3.StringUtils
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

import javax.annotation.Nonnull
import javax.annotation.Nullable
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

/**
 * The CourseClass entity is what you see in the user interface called just "class". The term
 * 'CourseClass' is used because 'class' is a reserved word in Java and will cause ambiguity.
 *
 * A CourseClass is the object which can be enrolled in by students, has a price, timetable and represents
 * the instance of the Course which is sold and delivered.
 *
 */
//TODO docs
@API
@QueueableEntity
class CourseClass extends _CourseClass implements CourseClassTrait, ICourseClass, Queueable, NotableTrait, ExpandableTrait, AttachableTrait {


	public static final String EXPORT_DISCOUNT_RELATION = "discount"
	public static final String SUCCESS_AND_QUEUED_ENROLMENTS = "successAndQueuedEnrolments"

	private static Logger logger = LogManager.getLogger()
	public static final String DISPLAYABLE_LOCATION_PROPERTY = "displayableLocation"

	public static final String FEE_INC_GST_PROP = "feeIncGst"
	public static final String TUTORS_ABRIDGED_PROP = "tutorsAbridged"
	public static final String ENROLMENTS_COUNT_PROP = "validEnrolmentCount"
	public static final String PLACES_LEFT_PROP = "placesLeft"
	public static final String TIME_ZONE_ID = "clientTimeZoneId"
	public static final String HAS_ZERO_WAGES = "hasZeroWages"
	public static final String IS_TRAINEESHIP = "isTraineeship"

	/**
	 * @return
	 */
	@API
	@Override
	Object getValueForKey(@Nonnull String key) {
		switch (key) {
			case FEE_INC_GST_PROP:
				return getFeeGST()
			case SUCCESS_AND_QUEUED_ENROLMENTS:
				return getSuccessAndQueuedEnrolments()
		}
		return readProperty(key)
	}

	/**
	 * @return description for this class containing its course name and venue where it is held
	 */
	@Nonnull
	@API
	String getDescription() {
		StringBuffer result

		result = new StringBuffer(getCode())
		if (getCourse() != null && getCourse().getName() != null) {
			result.append(" ").append(getCourse().getName())
		}
		if (getRoom() != null && getRoom().getSite() != null && getRoom().getSite().getName() != null) {
			result.append(" ").append(getRoom().getSite().getName())
		}
		return result.toString()
	}

	/**
	 * @return a string representation of the site name, street, suburb, and postcode.
	 */
	@Nonnull
	@API
	String getDisplayableLocation() {
		StringBuilder buff = new StringBuilder()
		if (getRoom() != null && getRoom().getSite() != null && !StringUtils.isEmpty(getRoom().getSite().getName())) {
			buff.append(getRoom().getSite().getName()).append(": ")
			if (!StringUtils.isEmpty(getRoom().getSite().getStreet())) {
				buff.append(getRoom().getSite().getStreet()).append(' ')
			}
			if (!StringUtils.isEmpty(getRoom().getSite().getSuburb())) {
				buff.append(getRoom().getSite().getSuburb()).append(' ')
			}
			if (!StringUtils.isEmpty(getRoom().getSite().getPostcode())) {
				buff.append(getRoom().getSite().getPostcode())
			}
		}
		return buff.toString()
	}

    /**
	 * @return class fee including GST
	 */
	@API
	@Override
	Money getFeeIncGst() {
		if (getTax() != null) {
			return MoneyUtil.getPriceIncTax(getFeeExGst(), getTax().getRate(), getTaxAdjustment())
		}
		return MoneyUtil.getPriceIncTax(getFeeExGst(), null, getTaxAdjustment())
	}

	/**
	 * @return /class/uniqueCode
	 */
	@Nonnull
	@API
	String getPublicRelativeURL() {
		return "/class/" + getUniqueCode()
	}

    /**
	 * @return a unique identifier in the form {@code courseCode-classCode}.
	 */
	@Nonnull
	@API
	@Override
	String getUniqueCode() {
		StringBuilder buff = new StringBuilder()
		if (getCourse() != null && getCourse().getCode() != null) {
			buff.append(getCourse().getCode())
		}
		buff.append("-")
		if (getCode() != null) {
			buff.append(getCode())
		}
		return buff.toString()
	}

	@Override
	void postAdd() {
		super.postAdd()

		if (getIsCancelled() == null) {
			setIsCancelled(Boolean.FALSE)
		}
		if (getIsDistantLearningCourse() == null) {
			setIsDistantLearningCourse(Boolean.FALSE)
		}
		if (getIsShownOnWeb() == null) {
			setIsShownOnWeb(Boolean.FALSE)
		}
		if (getExpectedHours() == null) {
			setExpectedHours(BigDecimal.ZERO)
		}
		if (getReportableHours() == null) {
			setReportableHours(CalculateCourseClassNominalHours.valueOf(this).calculate())
		}
	}

	/**
	 * @return the GST of the class fee
	 */
	@API
	Money getFeeGST() {
		if (getTax() != null) {
			return MoneyUtil.getPriceTax(getFeeExGst(), getTax().getRate(), getTaxAdjustment())
		}
		return MoneyUtil.getPriceTax(getFeeExGst(), null, getTaxAdjustment())
	}

	/**
	 * This is a faked flattened relationship via discountCourseClasses.discount.
	 *
	 * @return the discounts for this class
	 */
	@Nonnull
	@API
	List<Discount> getDiscounts() {
		List<Discount> discounts = new ArrayList<>()
		for (DiscountCourseClass dcc : getDiscountCourseClasses()) {
			if (dcc.getDiscount() != null && dcc.getPersistenceState() != PersistenceState.DELETED) {
				discounts.add(dcc.getDiscount())
			}
		}
		return discounts
	}

	/**
	 * @deprecated field - unable to drop from db.
	 * @return
	 */
	@Deprecated
	@Override
	String getMaterials() {
		return null
	}

	void updateSessionCount() {
		setSessionsCount(getSessions().size())
	}

	/**
	 * making the class room match the session room (first session listed chronologically) <br>
	 * (this code is a result of long going bug, it might be limiting to some customers by not allowing to choose the class room freely, but since there it is
	 * not displayed in gui at the moment and it caused problems me and Ari agreed on this, marcin 31 Jan 2011)
	 */
	void updateClassRoom() {
		if (!isDistantLearningCourse) {
			setRoom(getSessions().sort {it.startDatetime}.find {it.room != null}?.room)
		}
	}

	void updateStartEndDates() {
		List<Session> sess = getSessions()

		if (sess != null && sess.size() != 0) {
			Date start = null
			Date end = null
			long totalTimeInMilis = 0
			for (Session s : sess) {
				if (start == null || s.getStartDatetime() != null && start.after(s.getStartDatetime())) {
					start = s.getStartDatetime()
				}
				if (end == null || s.getEndDatetime() != null && end.before(s.getEndDatetime())) {
					end = s.getEndDatetime()
				}
				if (s.getStartDatetime() != null && s.getEndDatetime() != null) {
					totalTimeInMilis = totalTimeInMilis + s.getEndDatetime().getTime() - s.getStartDatetime().getTime()
				}
			}
			if (start == null || getStartDateTime() == null || start != getStartDateTime()) {
				setStartDateTime(start)
			}

			if (end == null || getEndDateTime() == null || end != getEndDateTime()) {
				setEndDateTime(end)
			}
			setSessionsCount(sess.size())
			setMinutesPerSession((int) (totalTimeInMilis / 1000 / 60 / sess.size()))
		} else {
			if (getSessionRepeatInterval() != null && getSessionsCount() != null && getStartDateTime() != null) {
				GregorianCalendar gc = new GregorianCalendar()
				gc.setTime(getStartDateTime())
				gc.add(GregorianCalendar.DATE, getSessionRepeatInterval() * getSessionsCount())
				setEndDateTime(gc.getTime())
			}
		}
	}

	void updateClassFee() {
		setFeeExGst(ClassCostUtil.calculateFee(getCosts()))
		setDeposit(ClassCostUtil.calculateDeposit(getCosts()))

		// this single line is a temporary solution to a problem where tax adjustment is not propagated from class cost
		// for now we have only one classcost-fee componenet, so this is safe. will be removed in 3.1
		for (ClassCost cc : getCosts()) {
			if (ClassCostFlowType.INCOME == cc.getFlowType() && Boolean.TRUE == cc.getInvoiceToStudent()) {
				setTaxAdjustment(cc.getTaxAdjustment())
			}
		}

	}

	@Override
	void preUpdate() {
		super.preUpdate()
		onCourseClassModified()
		updateOutcomesStartEndDates()
	}

	private void onCourseClassModified() {
		int sessionSize = getSessions().size()
		int costsSize = getCosts().size()

		updateStartEndDates()
		updateSessionCount()

		if (sessionSize > 0) {
			updateClassRoom()
		}

		if (costsSize > 0) {
			updateClassFee()
		}
	}

	private void updateOverriddenFields() {
		if (getReportableHours() == null) {
			setReportableHours(CalculateCourseClassReportableHours.valueOf(this).calculate());
		}
		if (getEnrolments() != null && getEnrolments().size() > 0) {

			if (getFundingSource() != null) {
				getEnrolments().findAll{ e -> !e.fundingSource}.each { e -> e.fundingSource = getFundingSource()}
			}
			if (getRelatedFundingSource() != null) {
				getEnrolments().findAll{ e -> !e.relatedFundingSource}.each { e -> e.relatedFundingSource = getRelatedFundingSource()}
			}
			if (getVetFundingSourceStateID() != null) {
				getEnrolments().findAll{ e -> !e.vetFundingSourceStateID}.each {e -> e.vetFundingSourceStateID = getFundingSource()}
			}
			if (getVetPurchasingContractID() != null) {
				getEnrolments().findAll{ e -> !e.vetPurchasingContractID}.each {e -> e.vetPurchasingContractID = getVetPurchasingContractID()}
			}

			updateOutcomesStartEndDates()
		}
	}

	private void updateOutcomesStartEndDates() {
		List<Outcome> outcomes = enrolments*.outcomes.flatten() as List<Outcome>

		outcomes.findAll { !it.startDateOverridden }
				.each { o ->
					o.setStartDate(o.getActualStartDate())
				}
		outcomes.findAll { !it.endDateOverridden }
				.each { o ->
					o.setEndDate(o.getActualEndDate())
				}
	}

	/**
	 * @see ish.oncourse.server.cayenne.glue.CayenneDataObject#prePersist()
	 */
	@Override
	protected void prePersist() {
		super.prePersist()
		onCourseClassModified()
		updateOverriddenFields()
	}

	@Override
	void addToAttachmentRelations(AttachmentRelation relation) {
		addToAttachmentRelations((CourseClassAttachmentRelation) relation)
	}

	@Override
	void removeFromAttachmentRelations(AttachmentRelation relation) {
		removeFromAttachmentRelations((CourseClassAttachmentRelation) relation)
	}

	@Override
	Class<? extends AttachmentRelation> getRelationClass() {
		return CourseClassAttachmentRelation.class
	}
	/**
	 * @return list of successful and in transaction enrolments made to this class
	 */
	@Nonnull
	@API
	@Override
	List<Enrolment> getSuccessAndQueuedEnrolments() {
		return (List<Enrolment>) CourseClassUtil.getSuccessAndQueuedEnrolments(getEnrolments())
	}

	/**
	 * @return sum of payable hours in all sessions of this courseClass
	 */
	@API
	@Override
	BigDecimal getPayableClassroomHours() {
		BigDecimal sum = BigDecimal.ZERO
		if (getSessions() != null && getSessions().size() > 0) {
			sum = getSessions()*.getPayableDurationInHours().sum() as BigDecimal
		} else if (getSessionsCount() != null && getMinutesPerSession() != null) {
			return getSessionsCount() * getMinutesPerSession() / 60L
		}
		return sum
	}

	/**
	 * @return class cacellation warning message
	 */
	@API
	String getCancelWarningMessage() {
		StringBuilder builder = new StringBuilder();

		successAndQueuedEnrolments.each { e ->
			String warning = e.cancelWarningMessage
			if (warning) {
				builder.append(warning)
				builder.append('\n')
			}
		}
		return builder.length() > 0 ? builder.toString() : null
	}

	/**
	 * @return number of successful and in transaction enrolments made to this class
	 */
	@API
	@Override
	int getValidEnrolmentCount() {
		List<Enrolment> list = getSuccessAndQueuedEnrolments()
		if (list == null) {
			return 0
		}

		return list.size()
	}

	/**
	 * @return
	 */
	@API
	int getSessionsCountForTutor(@Nullable Tutor t) {
		int sessionCount = 0
		if (t == null) {
			return sessionCount
		}

		for (CourseClassTutor tutorRole : getTutorRoles()) {
			if (tutorRole.getTutor() == t) {
				sessionCount = tutorRole.getSessionsTutors().size()
			}
		}

		return sessionCount
	}

	@Override
	void onEntityCreation() {
		super.onEntityCreation()
		if (getFeeExGst() == null) {
			setFeeExGst(Money.ZERO)
		}
		if (getDeliveryMode() == null) {
			setDeliveryMode(DeliveryMode.CLASSROOM)
		}
		if (getFundingSource() == null) {
			setFundingSource(ClassFundingSource.DOMESTIC_FULL_FEE)
		}
		if (getIsCancelled() == null) {
			setIsCancelled(Boolean.FALSE)
		}
		if (getIsDistantLearningCourse() == null) {
			setIsDistantLearningCourse(Boolean.FALSE)
		}
		if (getIsShownOnWeb() == null) {
			setIsShownOnWeb(Boolean.FALSE)
		}
		if (getIsActive() == null) {
			setIsActive(true)
		}
		if (getSessionsCount() == null) {
			setSessionsCount(0)
		}
		if (getIsClassFeeApplicationOnly() == null) {
			setIsClassFeeApplicationOnly(false)
		}
		if (getTaxAdjustment() == null) {
			setTaxAdjustment(Money.ZERO)
		}
		if (getSuppressAvetmissExport() == null) {
			setSuppressAvetmissExport(false)
		}
		if (getBudgetedPlaces() == null) {
			setBudgetedPlaces(0)
		}

	}

	/**
	 * Convenience method to get the very first (chronologically) session for a class
	 *
	 * @return first session (in chronological order)
	 */
	@Nullable
	@API
	Session getFirstSession() {
		if (getSessions() == null || getSessions().size() == 0) {
			return null
		}
		List<Session> list = new ArrayList<>(getSessions())
		new Ordering(_Session.START_DATETIME_PROPERTY, SortOrder.ASCENDING).orderList(list)
		return list.get(0)
	}

	/**
	 * Convenience method to get the percentage of delivered hours
	 *
	 * @param treshold - date to calculate the ratio
	 * @return percentage of delivered hours
	 */
	@API
	BigDecimal getPercentageOfDeliveredScheduledHoursBeforeDate(@Nullable Date treshold) {
		if (treshold == null) {
			throw new IllegalArgumentException("treshold date has to be specified")
		}
		if (getSessions() == null || getSessions().size() == 0) {
			return BigDecimal.ONE
		}
		List<Session> list = new ArrayList<>(getSessions())
		new Ordering(_Session.START_DATETIME_PROPERTY, SortOrder.ASCENDING).orderList(list)
		BigDecimal deliveredHours = BigDecimal.ZERO
		BigDecimal totalHours = BigDecimal.ZERO
		for (Session s : list) {
			BigDecimal sessionHours = s.getDurationInHours()
			if (treshold.after(s.getStartDatetime())) {
				deliveredHours = deliveredHours.add(sessionHours)
			}
			totalHours = totalHours.add(sessionHours)
		}

		if (totalHours == BigDecimal.ZERO) {
			return BigDecimal.ONE
		}
		return deliveredHours.divide(totalHours, 2, BigDecimal.ROUND_HALF_UP)
	}

	/**
	 * @return time zone of the class venue or default server time zone if not specified
	 */
	@API
	@Override
	TimeZone getTimeZone() {
		if (getRoom() == null || getRoom().getSite() == null ||
				getRoom().getSite().getIsVirtual() || getRoom().getSite().getLocalTimezone() == null) {
			return TimeZone.getDefault()
		}
		return TimeZone.getTimeZone(getRoom().getSite().getLocalTimezone())
	}

	/**
	 * @return null if class has no location specified. Converted to browser time zone
	 */
	String getClientTimeZoneId() {
		if (getRoom() == null || getRoom().getSite() == null ||
				getRoom().getSite().getIsVirtual() || getRoom().getSite().getLocalTimezone() == null) {
			return null
		}
		return getRoom().getSite().getLocalTimezone()
	}

	@Override
	void validateForSave(@Nonnull ValidationResult validationResult) {
		super.validateForSave(validationResult)

		if (getCourse() != null && !getCourse().isNewRecord() &&
				detectDuplicate([COURSE_PROPERTY, CODE_PROPERTY ] as String[], null, true) != null) {
			validationResult.addFailure(ValidationFailure.validationFailure(this, CODE_PROPERTY, "You must enter a unique course code."))
		}
	}

	/**
	 * @return AttendanceType for AVETMISS reporting in Victoria only
	 */
	@Nonnull
	@API
	@Override
	CourseClassAttendanceType getAttendanceType() {
		return super.getAttendanceType()
	}

	/**
	 * @return number of budgeted places
	 */
	@Nonnull
	@API
	@Override
	Integer getBudgetedPlaces() {
		return super.getBudgetedPlaces()
	}

	/**
	 * @return the census date for NCVER reporting
	 */
	@API
	@Override
	LocalDate getCensusDate() {
		return super.getCensusDate()
	}

	/**
	 * @return class code
	 */
	@Nonnull
	@API
	@Override
	String getCode() {
		return super.getCode()
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
	 * @return  standard deliveryMode value for AVETMISS reporting
	 */
	@Nonnull
	@API
	@Override
	DeliveryMode getDeliveryMode() {
		return super.getDeliveryMode()
	}

	/**
	 * @return
	 */
	@API
	@Override
	Money getDeposit() {
		return super.getDeposit()
	}

	/**
	 * DET Booking ID are used for NSW ePayments AVETMISS reporting.
	 * @return standard DET Booking ID string
	 */
	@API
	@Override
	String getDetBookingId() {
		return super.getDetBookingId()
	}

	/**
	 * @return end date and time of the last session of the class
	 */
	@API
	@Override
	Date getEndDateTime() {
		return super.getEndDateTime()
	}

	/**
	 * @return
	 */
	@API
	@Override
	BigDecimal getExpectedHours() {
		return super.getExpectedHours()
	}

	/**
	 * @return the class free exclusing GST
	 */
	@Nonnull
	@API
	@Override
	Money getFeeExGst() {
		return super.getFeeExGst()
	}

	/**
	 * @return
	 */
	@API
	@Override
	String getFinalDETexport() {
		return super.getFinalDETexport()
	}
	

	/**
	 * Returns a value mapped to funding source. Used for AVETMISS reporting.
	 * To return the funding source entity, call 'relatedFundingSource'
	 *
	 * @return mapped funding source value used for reporting
	 */
	@Nonnull
	@Override
	ClassFundingSource getFundingSource() {
		return super.getFundingSource()
	}


	/**
	 * @return
	 */
	@API
	@Override
	String getInitialDETexport() {
		return super.getInitialDETexport()
	}

	/**
	 * @return true if class is active and can accept enrolments
	 */
	@Nonnull
	@API
	@Override
	Boolean getIsActive() {
		if (getIsCancelled()) {
			return false
		}
		return super.getIsActive()
	}

	/**
	 * @return true if class has been cancelled
	 */
	@Nonnull
	@API
	@Override
	Boolean getIsCancelled() {
		return super.getIsCancelled()
	}

	/**
	 * @return
	 */
	@Nonnull
	@API
	@Override
	Boolean getIsClassFeeApplicationOnly() {
		return super.getIsClassFeeApplicationOnly()
	}

	/**
	 * @return
	 */
	@Nonnull
	@API
	@Override
	Boolean getIsDistantLearningCourse() {
		return super.getIsDistantLearningCourse()
	}

	/**
	 * @return true if class is visible and searchable on college website
	 */
	@Nonnull
	@API
	@Override
	Boolean getIsShownOnWeb() {
		return super.getIsShownOnWeb()
	}

	/**
	 * @return the maximum age of students allowed to enrol in this courseClass
	 */
	@API
	@Override
	Integer getMaxStudentAge() {
		return super.getMaxStudentAge()
	}

	/**
	 * @return
	 */
	@API
	@Override
	Integer getMaximumDays() {
		return super.getMaximumDays()
	}

	/**
	 * @return maximum number of enrolments class can accept
	 */
	@Nonnull
	@API
	@Override
	Integer getMaximumPlaces() {
		return super.getMaximumPlaces()
	}

	/**
	 * @return
	 */
	@API
	@Override
	String getMessage() {
		return super.getMessage()
	}

	/**
	 * @return
	 */
	@API
	@Override
	String getMidwayDETexport() {
		return super.getMidwayDETexport()
	}

	/**
	 * @return  the minimum age of students allowed to enrol in this courseClass
	 */
	@API
	@Override
	Integer getMinStudentAge() {
		return super.getMinStudentAge()
	}

	/**
	 * @return minimim number of enrolments required for class to proceed
	 */
	@Nonnull
	@API
	@Override
	Integer getMinimumPlaces() {
		return super.getMinimumPlaces()
	}

	/**
	 * @return duration of the session in minutes
	 */
	@API
	@Override
	Integer getMinutesPerSession() {
		return super.getMinutesPerSession()
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
	 * @return
	 */
	@API
	@Override
	Integer getSessionRepeatInterval() {
		return super.getSessionRepeatInterval()
	}

	/**
	 * @return
	 */
	@Override
	SessionRepetitionType getSessionRepeatType() {
		return super.getSessionRepeatType()
	}

	/**
	 * @return number of sessions class has
	 */
	@Nonnull
	@API
	@Override
	Integer getSessionsCount() {
		return super.getSessionsCount()
	}

	/**
	 * @return
	 */
	@API
	@Override
	Boolean getSessionsSkipWeekends() {
		return super.getSessionsSkipWeekends()
	}

	/**
	 * @return start date and time of the first session of the class
	 */
	@API
	@Override
	Date getStartDateTime() {
		return super.getStartDateTime()
	}

	/**
	 * @return true if this class is suppressed from AVETMISS export
	 */
	@Nonnull
	@API
	@Override
	Boolean getSuppressAvetmissExport() {
		return super.getSuppressAvetmissExport()
	}

	/**
	 * @return
	 */
	@Nonnull
	@API
	@Override
	Money getTaxAdjustment() {
		return super.getTaxAdjustment()
	}

	/**
	 * @return
	 */
	@API
	@Override
	Integer getVetCourseSiteID() {
		return super.getVetCourseSiteID()
	}

	/**
	 * @return
	 */
	@API
	@Override
	String getVetFundingSourceStateID() {
		return super.getVetFundingSourceStateID()
	}

	/**
	 * @return
	 */
	@API
	@Override
	String getVetPurchasingContractID() {
		return super.getVetPurchasingContractID()
	}

	/**
	 * @return
	 */
	@API
	@Override
	String getVetPurchasingContractScheduleID() {
		return super.getVetPurchasingContractScheduleID()
	}

	/**
	 * @return web description field that is rendered on courseClass webpage
	 */
	@API
	@Override
	String getWebDescription() {
		return super.getWebDescription()
	}

	/**
	 * Returns all CorperatePasses that can be used with this CourseClass
	 *
	 * @return object relating class and CorporatePass
	 */
	@Nonnull
	@API
	@Override
	List<CorporatePassCourseClass> getCorporatePassCourseClass() {
		return super.getCorporatePassCourseClass()
	}

	/**
	 * @return all ClassCosts linked to this CourseClass
	 */
	@Nonnull
	@API
	@Override
	List<ClassCost> getCosts() {
		return super.getCosts()
	}

	/**
	 * @return course record linked to this class
	 */
	@Nonnull
	@API
	@Override
	Course getCourse() {
		return super.getCourse()
	}

	/**
	 * Returns all Discount that can be used with this CourseClass
	 *
	 * @return object relating class and Discounts
	 */
	@Nonnull
	@API
	@Override
	List<DiscountCourseClass> getDiscountCourseClasses() {
		return super.getDiscountCourseClasses()
	}

	@Override
	void addModuleToAllSessions(IModule module) {
		CourseClassUtil.addModuleToAllSessions(this, module, SessionModule.class)
	}

	/**
	 * @return complete list of enrolments (successful, failed or cancelled) ever made to this class
	 */
	@Nonnull
	@API
	@Override
	List<Enrolment> getEnrolments() {
		return super.getEnrolments()
	}

	/**
	 * @return
	 */
	@Nonnull
	@API
	@Override
	Account getIncomeAccount() {
		return super.getIncomeAccount()
	}

	/**
	 * @return
	 */
	@Nonnull
	@API
	@Override
	List<InvoiceLine> getInvoiceLines() {
		return super.getInvoiceLines()
	}

	/**
	 * @return main room associated with this class
	 */
	@Nonnull
	@API
	@Override
	Room getRoom() {
		return super.getRoom()
	}

	/**
	 * @return list of sessions linked to this class
	 */
	@Nonnull
	@API
	@Override
	List<Session> getSessions() {
		return super.getSessions()
	}

	/**
	 * Returns the a list of TutorRole objects that is used to relate CourseClasses and Tutors
	 *
	 * @return relation TutorRole object of each Tutor assigned to this class
	 */
	@Nonnull
	@API
	@Override
	List<CourseClassTutor> getTutorRoles() {
		return super.getTutorRoles()
	}


	/**
	 * Returns the funding source for this CourseClass.
	 *
	 * @return funding source used for this class
	 */
	@API
	@Nullable
	@Override
	FundingSource getRelatedFundingSource() {
		return super.getRelatedFundingSource()
	}

	/**
	 * @return the list of related AssessmentClasses
	 */
	@Nonnull
	@API
	@Override
	List<AssessmentClass> getAssessmentClasses() { return super.getAssessmentClasses() }

	/**
	 * @return The list of tags assigned to course class
	 */
	@Nonnull
	@API
	List<Tag> getTags() {
		List<Tag> tagList = new ArrayList<>(getTaggingRelations().size())
		for (CourseClassTagRelation relation : getTaggingRelations()) {
			tagList.add(relation.getTag())
		}
		return tagList
	}


	@API
	String getTutorsAbridged() {
		List<Tutor> tutors = new ArrayList<>()
		List<CourseClassTutor> courseClassTutors = getTutorRoles()
		for(CourseClassTutor classTutor : courseClassTutors) {
			if(classTutor.getTutor() != null) {
				tutors.add(classTutor.getTutor())
			}
		}
		if (tutors == null || tutors.size() == 0) {
			return "not set"
		} else if(tutors.size() == 1) {
			return tutors.get(0).getContact().getName()
		}

		return tutors.get(0).getContact().getName() + " et al"
	}

	@API
	int getPlacesLeft() {
		return getMaximumPlaces() == null ? 0 : getMaximumPlaces() - getValidEnrolmentCount()
	}

	@API
	boolean getHasZeroWages() {
		return getCosts()
				.stream()
				.filter{classCost -> ClassCostFlowType.WAGES == classCost.getFlowType() }
				.anyMatch{
					ClassCostUtil.getPerUnitAmountExTax(it) == null ||
							ClassCostUtil.getPerUnitAmountExTax(it).isZero()
				}
	}

	@API
	boolean getIsTraineeship() {
		return course.isTraineeship
	}

	/**
	 * @return JSR-310 compatible start date of the class
	 */
	LocalDate getStart() {
		return Instant.ofEpochMilli(getStartDateTime().getTime()).atZone(ZoneId.of(getTimeZone().getID())).toLocalDate()
	}

	/**
	 * @return JSR-310 compatible end of the class
	 */
	LocalDate getEnd() {
		return Instant.ofEpochMilli(getEndDateTime().getTime()).atZone(ZoneId.of(getTimeZone().getID())).toLocalDate()
	}

	@Override
	String getSummaryDescription() {
		if (getCourse() == null) {
			return super.getSummaryDescription()
		}
		return getCourse().getName() + " " + getCourse().getCode() + "-" + getCode()
	}

	@Override
	Class<? extends CustomField> getCustomFieldClass() {
		return CourseClassCustomField
	}
}
