package ish.oncourse.model;

import ish.common.types.EnrolmentStatus;
import ish.math.Money;
import ish.oncourse.model.auto._CourseClass;
import ish.oncourse.utils.DateUtils;
import ish.oncourse.utils.DiscountUtils;
import ish.oncourse.utils.QueueableObjectUtils;
import ish.oncourse.utils.TimestampUtilities;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.EJBQLQuery;
import org.apache.cayenne.query.Ordering;
import org.apache.cayenne.query.SelectQuery;
import org.apache.cayenne.query.SortOrder;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

public class CourseClass extends _CourseClass implements Queueable {
	
	private static final long serialVersionUID = 3351739058505297154L;

	/**
	 * parameter for add/remove cookies actions
	 */
	public static final String COURSE_CLASS_ID_PARAMETER = "courseClassId";

	/**
	 * ordered classes cookie name
	 */
	public static final String SHORTLIST_COOKIE_KEY = "shortlist";

	public static final int EARLIEST_END_FOR_EVENING = 18;
	public static final int LATEST_START_FOR_DAYTIME = 18;
	private Set<String> daysOfWeek;

	public Long getId() {
		return QueueableObjectUtils.getId(this);
	}

	/**
	 * @return total number of minutes from all sessions that have start and end
	 *         dates defined.
	 */
	public Integer getTotalDurationMinutes() {
		int sum = 0;
		for (Session s : getSessions()) {
			sum += (s.getDurationMinutes() != null) ? s.getDurationMinutes() : 0;
		}
		return sum;
	}

	/**
	 * @return total number of hours from all sessions that have start and end
	 *         dates defined.
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
	 *         end times
	 */
	public boolean isSessionsHaveDifferentTimes() {

		if (getSessions().size() > 1) {
			TimeZone timezone = null;
			{
				String timezoneName = "Australia/Sydney";
				if (getTimeZone() != null && !"".equals(getTimeZone().trim())) {
					timezoneName = getTimeZone();
				}
				timezone = TimeZone.getTimeZone(timezoneName);
			}

			Calendar startCalendar = null;
			Calendar endCalendar = null;

			for (int i = 0, count = getSessions().size(); i < count; i++) {
				Session session = getSessions().get(i);
				Date sessionStart = session.getStartDate();
				Date sessionEnd = session.getEndDate();

				if (i == 0) {
					if (sessionStart != null) {
						startCalendar = Calendar.getInstance(timezone);
						startCalendar.setTimeInMillis(sessionStart.getTime());
					}
					if (sessionEnd != null) {
						endCalendar = Calendar.getInstance(timezone);
						endCalendar.setTimeInMillis(sessionEnd.getTime());
					}
				} else if (sessionStart == null && startCalendar != null || sessionStart != null
						&& startCalendar == null || sessionEnd == null && endCalendar != null || sessionEnd != null
						&& endCalendar == null) {
					return false;
				} else {
					Calendar start = Calendar.getInstance(timezone);
					start.setTimeInMillis(sessionStart.getTime());
					
					if (!DateUtils.isTheSameTime(start, startCalendar)) {
						return true;
					} else {
						Calendar end = Calendar.getInstance(timezone);
						end.setTimeInMillis(sessionEnd.getTime());
						if (!DateUtils.isTheSameTime(end, endCalendar)) {
							return true;
						}
					}
				}
			}
		}

		return false;
	}

	public boolean isHasSessions() {
		return getSessions().size() > 0;
	}

	public boolean isHasManySessions() {
		return getSessions().size() > 1;
	}

	public Session getFirstSession() {
		if (getSessions().isEmpty()) {
			return null;
		}

		List<Session> list = new ArrayList<Session>(getSessions());
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
		EJBQLQuery query = new EJBQLQuery(
				"Select count(e) from Enrolment e where e.courseClass=?1 and e.status in (?2)");
		query.setParameter(1, this);
		
		List<Integer> statusesValues = new ArrayList<Integer>();
		// use values, because the EJBQL in cayenne 3.0 doesn't convert enums in
		// this expression
		for (EnrolmentStatus es : Enrolment.VALID_ENROLMENTS) {
			statusesValues.add(es.getDatabaseValue());
		}
		query.setParameter(2, statusesValues);
		return ((Number) getObjectContext().performQuery(query).get(0)).intValue();
	}

	/**
	 * Gets all the "valid" enrolments of this class {@see
	 * EnrolmentStatus#VALID_ENROLMENTS}.
	 * 
	 * @return list of valid enrolments.
	 */
	@SuppressWarnings("all")
	public List<Enrolment> getValidEnrolments() {
		SelectQuery query = new SelectQuery(Enrolment.class, ExpressionFactory.matchExp(
				Enrolment.COURSE_CLASS_PROPERTY, this).andExp(
				ExpressionFactory.inExp(Enrolment.STATUS_PROPERTY, Arrays.asList(Enrolment.VALID_ENROLMENTS))));
		return getObjectContext().performQuery(query);
	}

	public Long getRecordId() {
		return (Long) readProperty(ID_PK_COLUMN);
	}

	public String getUniqueIdentifier() {
		return getCourse().getCode() + "-" + getCode();
	}

	public boolean isHasAnyTimelineableSessions() {
		for (Session session : getSessions()) {
			if ((session.getStartDate() != null) && (session.getEndDate() != null)) {
				return true;
			}
		}
		return false;
	}

	public TimeZone getClassTimeZone() {
		if (getTimeZone() == null) {
			return null;
		}
		return TimeZone.getTimeZone(getTimeZone());
	}

	public String getIsoStartDate() {
		DateTime dt = new DateTime(getStartDate());
		return dt.toString(ISODateTimeFormat.basicDate());
	}

	public String getIsoEndDate() {
		DateTime dt = new DateTime(getEndDate());
		return dt.toString(ISODateTimeFormat.basicDate());
	}

	public boolean hasFeeIncTax() {
		Money fee = getFeeIncGst();
		return fee != null && !fee.isZero();
	}

	public Money getFeeIncGst() {
		Money feeGst = getFeeGst();
		Money feeExGst = getFeeExGst();
		if (feeGst == null || feeExGst == null) {
			return feeExGst;
		}
		return feeExGst.add(feeGst);
	}

	public boolean isGstExempt() {
		Money feeGst = getFeeGst();
		return feeGst == null || feeGst.isZero();
	}

	/**
	 * @return an array of all the days of the week on which sessions occur for
	 *         this class
	 */
	public Set<String> getDaysOfWeek() {
		if (daysOfWeek == null) {
			if (getSessions().size() > 0) {
				ArrayList<String> days = new ArrayList<String>();
				for (Session s : getSessions()) {
					days.add(TimestampUtilities.dayOfWeek(s.getStartDate(), true, TimeZone.getTimeZone(s.getTimeZone())));
				}
				daysOfWeek = TimestampUtilities.uniqueDaysInOrder(days);
			} else {
				// no sessions recorded, so guess from class start / finish time
				daysOfWeek = new HashSet<String>();
				if (getStartDate() != null) {
					daysOfWeek.add(TimestampUtilities.dayOfWeek(getStartDate(), true,
							TimeZone.getTimeZone(getTimeZone())));

				}
				if (getEndDate() != null) {
					daysOfWeek
							.add(TimestampUtilities.dayOfWeek(getEndDate(), true, TimeZone.getTimeZone(getTimeZone())));

				}

			}
		}
		return daysOfWeek;
	}

	public boolean isDaytime() {
		Integer earliest = getEarliestSessionStartHour();
		// current definition is that any session that starts before 6 pm is
		// daytime
		if (earliest == null) {
			Calendar t = Calendar.getInstance(TimeZone.getTimeZone(getTimeZone()));
			// no sessions, so guess from start and end dates
			if (getStartDate() != null) {
				t.setTime(getStartDate());
				if (t.get(Calendar.HOUR_OF_DAY) < EARLIEST_END_FOR_EVENING) {
					return true;
				}
			}
			if (getEndDate() != null) {
				t.setTime(getEndDate());
				if (t.get(Calendar.HOUR_OF_DAY) < EARLIEST_END_FOR_EVENING) {
					return true;
				}
			}
			return false;
		}
		return earliest != null && earliest < LATEST_START_FOR_DAYTIME;
	}

	public Integer getEarliestSessionStartHour() {
		Integer earliest = null;
		for (Session session : getSessions()) {
			Calendar start = Calendar.getInstance(TimeZone.getTimeZone(session.getTimeZone()));
			start.setTime(session.getStartDate());
			Integer sessionStartHour = start.get(Calendar.HOUR_OF_DAY);
			if (earliest == null || sessionStartHour < earliest) {
				earliest = sessionStartHour;
			}
		}
		return earliest;
	}

	public boolean isEvening() {
		Integer latest = getLatestSessionEndHour();
		// current definition is that any session that ends on or after 6 pm is
		// evening
		if (latest == null) {
			Calendar t = Calendar.getInstance(TimeZone.getTimeZone(getTimeZone()));
			// no sessions, so guess from start and end dates
			if (getStartDate() != null) {
				t.setTime(getStartDate());
				if (t.get(Calendar.HOUR_OF_DAY) >= EARLIEST_END_FOR_EVENING) {
					return true;
				}
			}
			if (getEndDate() != null) {
				t.setTime(getEndDate());
				if (t.get(Calendar.HOUR_OF_DAY) >= EARLIEST_END_FOR_EVENING) {
					return true;
				}
			}
			return false;
		}
		return latest >= EARLIEST_END_FOR_EVENING;
	}

	public Integer getLatestSessionEndHour() {
		Integer latest = null;
		for (Session session : getSessions()) {
			Calendar end = Calendar.getInstance(TimeZone.getTimeZone(getTimeZone()));
			end.setTime(session.getEndDate());
			Integer sessionEndHour = end.get(Calendar.HOUR_OF_DAY);
			if (latest == null || sessionEndHour > latest) {
				latest = sessionEndHour;
			}
		}
		return latest;
	}

	/**
	 * @return all sessions that satisfy hasStartAndEndTimestamps
	 */
	@SuppressWarnings("unchecked")
	public List<Session> getTimelineableSessions() {
		List<Session> classSessions;
		
		if (getObjectId().isTemporary()) {
			classSessions = getSessions();
		}
		else {
			Expression expr = ExpressionFactory.matchExp(Session.COURSE_CLASS_PROPERTY, this);
			SelectQuery q = new SelectQuery(Session.class, expr);
			q.addPrefetch(Session.ROOM_PROPERTY);
			q.addPrefetch(Session.ROOM_PROPERTY + "." + Room.SITE_PROPERTY);
			classSessions = getObjectContext().performQuery(q);
		}
		
		List<Session> validSessions = new ArrayList<Session>();
		for (Session session : classSessions) {
			if ((session.getStartDate() != null) && (session.getEndDate() != null)) {
				validSessions.add(session);
			}
		}
		return validSessions;
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
	 * Discounts with the valid date ranges that are bound to this courseClass
	 * via {@link #getDiscountCourseClasses()} relationship.
	 * 
	 * @return the discounts for this class
	 */
	@SuppressWarnings("unchecked")
	public List<Discount> getDiscounts() {
		SelectQuery query = new SelectQuery(Discount.class, ExpressionFactory.matchExp(
				Discount.DISCOUNT_COURSE_CLASSES_PROPERTY + "." + DiscountCourseClass.COURSE_CLASS_PROPERTY, this)
				.andExp(ExpressionFactory.matchExp(Discount.IS_AVAILABLE_ON_WEB_PROPERTY, true))
				.andExp(Discount.getCurrentDateFilter()));

		return getObjectContext().performQuery(query);
	}

	/**
	 * All discounts bound to the given courseClass that require concessions
	 * instead of discount codes.
	 * 
	 * @return
	 */
	public List<Discount> getConcessionDiscounts() {

		List<Discount> availableDiscountsWithoutCode = (ExpressionFactory.matchExp(Discount.CODE_PROPERTY, null))
				.orExp(ExpressionFactory.matchExp(Discount.CODE_PROPERTY, StringUtils.EMPTY)).filterObjects(getDiscounts());

		List<Discount> discounts = new ArrayList<Discount>(availableDiscountsWithoutCode.size());
		for (Discount discount : availableDiscountsWithoutCode) {
			if (discount.getDiscountConcessionTypes() != null && !discount.getDiscountConcessionTypes().isEmpty()) {
				discounts.add(discount);
			}
		}
		return discounts;
	}
	
	/**
	 * Retrieves the discounts that should be applied to this class prices by
	 * the given policy.
	 * 
	 * @param policy
	 * @return
	 */
	public List<Discount> getDiscountsToApply(DiscountPolicy policy) {
		return policy.filterDiscounts(getDiscounts(), getFeeExGst());
	}

	/**
	 * The value of discount without tax if the selectedDiscounts are applied to
	 * the courseClass price.
	 * 
	 * @param selectedDiscounts
	 * @return
	 */
	public Money getDiscountAmountExTax(List<Discount> selectedDiscounts) {
		return DiscountUtils.discountValueForList(selectedDiscounts, getFeeExGst());
	}

	/**
	 * The value of discount with tax if the selectedDiscounts are applied to
	 * the courseClass price.
	 * 
	 * @param selectedDiscounts
	 * @return
	 */
	public Money getDiscountAmountIncTax(List<Discount> selectedDiscounts) {
		Money discountAmountForFee = getDiscountAmountExTax(selectedDiscounts);
		return discountAmountForFee.add(discountAmountForFee.multiply(getTaxRate()));
	}

	/**
	 * The value of discounted tax if the selectedDiscounts are applied to the
	 * courseClass price.
	 * 
	 * @param selectedDiscounts
	 * @return
	 */
	public Money getDiscountedTax(List<Discount> selectedDiscounts) {
		return getDiscountedFee(selectedDiscounts).multiply(getTaxRate());
	}

	/**
	 * The value of discounted fee with tax if the selectedDiscounts are applied
	 * to the courseClass price.
	 * 
	 * @param selectedDiscounts
	 * @return
	 */
	public Money getDiscountedFeeIncTax(List<Discount> selectedDiscounts) {
		Money discountedFee = getDiscountedFee(selectedDiscounts);
		return discountedFee.add(discountedFee.multiply(getTaxRate()));
	}

	/**
	 * The value of discounted fee without tax if the selectedDiscounts are
	 * applied to the courseClass price.
	 * 
	 * @param selectedDiscounts
	 * @return
	 */
	public Money getDiscountedFee(List<Discount> selectedDiscounts) {
		return getFeeExGst().subtract(getDiscountAmountExTax(selectedDiscounts));
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

}
