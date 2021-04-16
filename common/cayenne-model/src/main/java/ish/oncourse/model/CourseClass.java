package ish.oncourse.model;

import ish.common.types.EnrolmentStatus;
import ish.math.Money;
import ish.oncourse.cayenne.CourseClassInterface;
import ish.oncourse.common.field.FieldProperty;
import ish.oncourse.common.field.Property;
import ish.oncourse.common.field.PropertyGetSetFactory;
import ish.oncourse.model.auto._CourseClass;
import ish.oncourse.model.auto._TutorRole;
import ish.oncourse.utils.DateUtils;
import ish.oncourse.utils.QueueableObjectUtils;
import ish.oncourse.utils.SessionUtils;
import ish.util.DiscountUtils;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.cayenne.query.Ordering;
import org.apache.cayenne.query.QueryCacheStrategy;
import org.apache.cayenne.query.SortOrder;
import org.apache.commons.lang.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class CourseClass extends _CourseClass implements Queueable, CourseClassInterface {

	private static final long serialVersionUID = 3351739058505297154L;

	/**
	 * parameter for add/remove cookies actions
	 */
	public static final String COURSE_CLASS_ID_PARAMETER = "courseClassId";

	/**
	 * ordered classes cookie name
	 */
	public static final String SHORTLIST_COOKIE_KEY = "shortlist";

	public static final int MORNING_START = 6;
	public static final int EVENING_START = 17;
	private Set<String> daysOfWeek;

	public Long getId() {
		return QueueableObjectUtils.getId(this);
	}

	/**
	 * @return total number of minutes from all sessions that have start and end
	 * dates defined.
	 */
	public Integer getTotalDurationMinutes() {
		int sum = 0;
		for (Session s : getTimelineableSessions()) {
			sum += (s.getDurationMinutes() != null) ? s.getDurationMinutes() : 0;
		}
		return sum;
	}

	/**
	 * @return total number of hours from all sessions that have start and end
	 * dates defined.
	 */
	public BigDecimal getTotalDurationHours() {
		BigDecimal result = BigDecimal.ZERO;

		Integer totalMinutes = getTotalDurationMinutes();
		if (totalMinutes != null) {
			result = BigDecimal.valueOf(totalMinutes.longValue()).setScale(2)
					.divide(BigDecimal.valueOf(60), RoundingMode.HALF_UP);
		}
		return result;
	}

	/**
	 * @return true if there are any sessions that have different start times or
	 * end times
	 */
	public boolean isSessionsHaveDifferentTimes() {
		return !SessionUtils.isSameTime(getTimelineableSessions());
	}

	public boolean isHasSessions() {
		return getTimelineableSessions().size() > 0;
	}

	public boolean isHasManySessions() {
		return getTimelineableSessions().size() > 1;
	}

	public Session getFirstSession() {
		List<Session> sessions = getTimelineableSessions();
		if (sessions.isEmpty())
			return null;
		List<Session> list = new ArrayList<>(sessions);
		new Ordering(Session.START_DATE_PROPERTY, SortOrder.ASCENDING).orderList(list);
		return list.get(0);
	}

	public boolean isHasAvailableEnrolmentPlaces() {
		return getAvailableEnrolmentPlaces() > 0;
	}

	public int getAvailableEnrolmentPlaces() {
		int result = -1;
		if (getMaximumPlaces() != null && getMaximumPlaces() > 0) {
			int validEnrolment = validEnrolmentsCount();
			result = getMaximumPlaces() - validEnrolment;
		}
		return Math.max(0, result);
	}

	/**
	 * Calculates the count of current valid enrolments of this class.
	 *
	 * @return count of enrolments.
	 */
	public int validEnrolmentsCount() {
		return ObjectSelect.query(Enrolment.class).count().and(Enrolment.COURSE_CLASS.eq(this)).and(Enrolment.STATUS.in(EnrolmentStatus.SUCCESS, EnrolmentStatus.IN_TRANSACTION))
				.cacheStrategy(QueryCacheStrategy.LOCAL_CACHE, Enrolment.class.getSimpleName())
				.selectOne(objectContext).intValue();

	}

	/**
	 * Gets all the "valid" enrolments of this class {@see
	 * EnrolmentStatus#VALID_ENROLMENTS}.
	 *
	 * @return list of valid enrolments.
	 */
	public List<Enrolment> getValidEnrolments() {
		return ObjectSelect.query(Enrolment.class).
				where(Enrolment.COURSE_CLASS.eq(this)).
				and(Enrolment.STATUS.in(Enrolment.VALID_ENROLMENTS)).
				select(getObjectContext());
	}

	public String getUniqueIdentifier() {
		return getCourse().getCode() + "-" + getCode();
	}

	public boolean isHasAnyTimelineableSessions() {
		return getTimelineableSessions().size() > 0;
	}

	public TimeZone getClassTimeZone() {
		if (getTimeZone() == null) {
			return null;
		}
		return TimeZone.getTimeZone(getTimeZone());
	}

	/**
	 * @deprecated looks like we don't use this method anymore
	 */
	@Deprecated
	public String getIsoStartDate() {
		return LocalDateTime.ofInstant(getStartDate().toInstant(), ZoneId.of(getTimeZone())).format(DateTimeFormatter.BASIC_ISO_DATE);
	}

	/**
	 * @deprecated looks like we don't use this method anymore
	 */
	@Deprecated
	public String getIsoEndDate() {
		return LocalDateTime.ofInstant(getEndDate().toInstant(), ZoneId.of(getTimeZone())).format(DateTimeFormatter.BASIC_ISO_DATE);
	}

	public boolean hasFeeIncTax(BigDecimal overriddenTaxRate) {
		Money fee = getFeeIncGst(overriddenTaxRate);
		return fee != null && !fee.isZero();
	}

	public Money getFeeIncGst(BigDecimal overriddenTaxRate) {
		Money feeGst;
		Money feeExGst = getFeeExGst();
		if (feeExGst == null) {
			return null;
		}

		if (overriddenTaxRate == null) {
			feeGst = getFeeGst();
		} else {
			feeGst = feeExGst.multiply(overriddenTaxRate);
		}

		return feeExGst.add(feeGst);
	}

	public boolean isGstExempt() {
		Money feeGst = getFeeGst();
		return feeGst == null || feeGst.isZero();
	}

	/**
	 * @return an array of all the days of the week on which sessions occur for
	 * this class
	 */
	public Set<String> getDaysOfWeek() {
		if (daysOfWeek == null) {
			if (getTimelineableSessions().size() > 0) {
				ArrayList<String> days = new ArrayList<>();
				for (Session s : getTimelineableSessions()) {
					days.add(DateUtils.dayOfWeek(s.getStartDate(), true, TimeZone.getTimeZone(s.getTimeZone())));
				}
				daysOfWeek = DateUtils.uniqueDaysInOrder(days);
			} else {
				// no sessions recorded, so guess from class start / finish time
				daysOfWeek = new HashSet<>();
				if (getStartDate() != null) {
					daysOfWeek.add(DateUtils.dayOfWeek(getStartDate(), true,
							TimeZone.getTimeZone(getTimeZone())));

				}
				if (getEndDate() != null) {
					daysOfWeek
							.add(DateUtils.dayOfWeek(getEndDate(), true, TimeZone.getTimeZone(getTimeZone())));

				}

			}
		}
		return daysOfWeek;
	}

	public Integer getEarliestSessionStartHour() {
		Integer earliest = null;
		for (Session session : getTimelineableSessions()) {
			Calendar start = Calendar.getInstance(TimeZone.getTimeZone(session.getTimeZone()));
			start.setTime(session.getStartDate());
			Integer sessionStartHour = start.get(Calendar.HOUR_OF_DAY);
			if (earliest == null || sessionStartHour < earliest) {
				earliest = sessionStartHour;
			}
		}
		return earliest;
	}

	public Integer getLatestSessionEndHour() {
		Integer latest = null;
		for (Session session : getTimelineableSessions()) {
			Calendar end = Calendar.getInstance(TimeZone.getTimeZone(getTimeZone()));
			end.setTime(session.getEndDate());
			Integer sessionEndHour = end.get(Calendar.HOUR_OF_DAY);
			if (latest == null || sessionEndHour > latest) {
				latest = sessionEndHour;
			}
		}
		return latest;
	}

	private boolean isDayTime(int earliest) {
		return earliest < EVENING_START && earliest > MORNING_START;
	}

	/**
	 * Returns true if the course class does not have sessions or if 6:00 < start time < 17:00
	 */
	public boolean isDaytime() {
		Integer earliest = getEarliestSessionStartHour();
		return earliest == null || isDayTime(earliest);
	}

	/**
	 * Returns true if the course class does not have sessions or if 17:00 <= start time <= 6:00
	 */
	public boolean isEvening() {
		Integer earliest = getEarliestSessionStartHour();
		return earliest == null || !isDayTime(earliest);
	}

	/**
	 * @return all sessions that satisfy hasStartAndEndTimestamps
	 */
	@SuppressWarnings("unchecked")
	public List<Session> getTimelineableSessions() {
		return getPersistentTimelineableSessions();
	}

	public List<Session> getPersistentTimelineableSessions() {
		ObjectSelect<Session> select = ObjectSelect.query(Session.class)
				.where(Session.COURSE_CLASS.eq(this))
				.and(Session.START_DATE.isNotNull())
				.and(Session.END_DATE.isNotNull())
				.prefetch(Session.ROOM.joint())
				.prefetch(Session.ROOM.dot(Room.SITE).joint())
				.cacheStrategy(QueryCacheStrategy.LOCAL_CACHE)
				.cacheGroup(Session.class.getSimpleName());
		return new LinkedList<>(select.select(getObjectContext()));
	}

	public boolean isHasRoom() {
		return getRoom() != null && getRoom().getSite() != null && getRoom().getSite().getIsWebVisible();
	}

	public boolean hasEnded() {
		Date end = getEndDate();
		return end != null && new Date().after(end);
	}

	public boolean hasStarted() {
		Date start = getStartDate();
		return start != null && new Date().after(start);
	}

	public boolean isRunning() {
		return hasStarted() && !hasEnded();
	}

	/**
	 * DiscountCourseClasses with the valid date ranges that are bound to this courseClass
	 * via {@link #getDiscountCourseClasses()} relationship.
	 *
	 * @return the discounts for this class
	 */
	@SuppressWarnings("unchecked")
	public List<DiscountCourseClass> getAvalibleDiscountCourseClasses() {
		return ObjectSelect.query(DiscountCourseClass.class).
				where(DiscountCourseClass.DISCOUNT.dot(Discount.IS_AVAILABLE_ON_WEB).isTrue()).
				and(DiscountCourseClass.COURSE_CLASS.eq(this)).
				and(Discount.getCurrentDateFilterForDiscountCourseClass(getStartDate())).
				select(getObjectContext());
	}

	/**
	 * All discounts bound to the given courseClass that require concessions
	 * instead of discount codes.
	 *
	 * @return
	 */
	public List<Discount> getConcessionDiscounts() {

		List<DiscountCourseClass> availableDiscountsWithoutCode = (DiscountCourseClass.DISCOUNT.dot(Discount.CODE).isNull())
				.orExp(DiscountCourseClass.DISCOUNT.dot(Discount.CODE).eq(StringUtils.EMPTY)).filterObjects(getAvalibleDiscountCourseClasses());

		List<Discount> discounts = new ArrayList<>(availableDiscountsWithoutCode.size());
		for (DiscountCourseClass discountCourseClass : availableDiscountsWithoutCode) {
			Discount discount = discountCourseClass.getDiscount();
			if (discount.getDiscountConcessionTypes() != null && !discount.getDiscountConcessionTypes().isEmpty()) {
				discounts.add(discount);
			}
		}
		return discounts;
	}

	/**
	 * The value of discount without tax if the discount is applied to
	 * the courseClass price.
	 *
	 * @param discount
	 * @return
	 */
	public Money getDiscountAmountExTax(Discount discount, BigDecimal overriddenTaxRate) {
		if (discount == null) {
			return Money.ZERO;
		} else {
			return DiscountUtils.discountValue(getDiscountCourseClassBy(discount), getFeeExGst(), overriddenTaxRate != null ? overriddenTaxRate : getTaxRate());
		}
	}

	/**
	 * The value of discount with tax if the discount is applied to
	 * the courseClass price.
	 *
	 * @param discountCourseClass
	 * @return
	 */
	public Money getDiscountAmountIncTax(DiscountCourseClass discountCourseClass, BigDecimal overriddenTaxRate) {
		return getFeeIncGst(overriddenTaxRate).subtract(getDiscountedFeeIncTax(discountCourseClass, overriddenTaxRate));
	}

	/**
	 * The value of discounted tax if the discount is applied to the
	 * courseClass price.
	 *
	 * @param discount
	 * @return
	 */
	public Money getDiscountedTax(Discount discount, BigDecimal overriddenTaxRate) {
		return getDiscountedFeeIncTax(getDiscountCourseClassBy(discount), overriddenTaxRate).subtract(getDiscountedFee(discount, overriddenTaxRate));
	}

	/**
	 * The value of discounted fee with tax if the selectedDiscounts are applied
	 * to the courseClass price.
	 *
	 * @param discountCourseClass
	 * @return
	 */
	public Money getDiscountedFeeIncTax(DiscountCourseClass discountCourseClass, BigDecimal overriddenTaxRate) {
		if (discountCourseClass == null) {
			return getFeeIncGst(overriddenTaxRate);
		} else {
			return DiscountUtils.getDiscountedFee(discountCourseClass, getFeeExGst(), overriddenTaxRate != null ? overriddenTaxRate : getTaxRate());
		}
	}

	/**
	 * The value of discounted fee without tax if the discount is
	 * applied to the courseClass price.
	 *
	 * @param discount
	 * @return
	 */
	public Money getDiscountedFee(Discount discount, BigDecimal overriddenTaxRate) {
		return getFeeExGst().subtract(getDiscountAmountExTax(discount, overriddenTaxRate));
	}

	public DiscountCourseClass getDiscountCourseClassBy(Discount discount) {
		for (DiscountCourseClass discountCourseClass : getDiscountCourseClasses()) {
			if (discountCourseClass.getDiscount().equals(discount)) {
				return discountCourseClass;
			}
		}
		return null;
	}

	/**
	 * The tax rate for the class: tax/fee.
	 *
	 * @return
	 */
	public BigDecimal getTaxRate() {
		if (getFeeExGst().isZero()) {
			return BigDecimal.ZERO;
		}
		return getFeeGst().divide(getFeeExGst()).toBigDecimal();
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see ish.oncourse.model.auto._CourseClass#getFeeExGst()
	 */
	@Override
	public Money getFeeExGst() {
		Money fee = super.getFeeExGst();
		if (fee == null) {
			setFeeExGst(Money.ZERO);
		}
		return super.getFeeExGst();
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see ish.oncourse.model.auto._CourseClass#getFeeGst()
	 */
	@Override
	public Money getFeeGst() {
		Money feeGst = super.getFeeGst();
		if (feeGst == null) {
			setFeeGst(Money.ZERO);
		}
		return super.getFeeGst();
	}

	public boolean isVirtualSiteUsed() {
		Session session = getFirstSession();
		return session != null && session.isVirtualSiteUsed();
	}

	@Override
	public String getTimeZone() {
		String value = null;
		Session session = getFirstSession();
		if (session != null) {
			value = getFirstSession().getTimeZone();
		} else {
			value = getCollege().getTimeZone();
		}
		return value;
	}

	@Override
	public boolean isAsyncReplicationAllowed() {
		return true;
	}

	@Override
	public Date getStartDateTime() {
		return super.getStartDate();
	}

	@Override
	public Date getEndDateTime() {
		return super.getEndDate();
	}

	@Override
	@Property(value = FieldProperty.CUSTOM_FIELD_COURSE_CLASS, type = PropertyGetSetFactory.SET, params = {String.class, String.class})
	public void setCustomFieldValue(String key, String value) {
		setCustomFieldValue(key, value, CourseCustomField.class);
	}

	public Set<Tutor> getTutors() {
		return getTutorRoles().stream()
				.filter(_TutorRole::getInPublicity)
				.map(TutorRole::getTutor)
				.filter(tutor -> tutor.getFinishDate() == null || tutor.getFinishDate().after(new Date()))
				.collect(Collectors.toSet());
	}
	
}
