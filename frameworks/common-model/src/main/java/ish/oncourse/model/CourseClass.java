package ish.oncourse.model;

import ish.oncourse.math.Money;
import ish.oncourse.model.auto._CourseClass;
import ish.oncourse.utils.TimestampUtilities;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;

import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.Ordering;
import org.apache.cayenne.query.SortOrder;
import org.apache.commons.lang.time.DateUtils;


public class CourseClass extends _CourseClass {

	public static final int EARLIEST_END_FOR_EVENING = 18;
	public static final int LATEST_START_FOR_DAYTIME = 18;
	private Set<String> daysOfWeek;

	public Integer getId() {
		return (getObjectId() != null && !getObjectId().isTemporary()) ? ((Number) getObjectId().getIdSnapshot().get(ID_PK_COLUMN)).intValue()
				: null;
	}

	/**
	 * @return total number of minutes from all sessions that have start and end
	 *         dates defined.
	 */
	public Integer getTotalDurationMinutes() {
		int sum = 0;
		for (Session s : getSessions()) {
			sum += (s.getDurationMinutes() != null) ? s.getDurationMinutes()
					: 0;
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
			result = BigDecimal.valueOf(totalMinutes.longValue()).setScale(2).divide(BigDecimal.valueOf(60), RoundingMode.HALF_UP);
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
				} else if (sessionStart == null && startCalendar != null
						|| sessionStart != null && startCalendar == null
						|| sessionEnd == null && endCalendar != null
						|| sessionEnd != null && endCalendar == null) {
					return false;
				} else {
					Calendar start = Calendar.getInstance(timezone);
					start.setTimeInMillis(sessionStart.getTime());

					if (!DateUtils.isSameLocalTime(start, startCalendar)) {
						return true;
					} else {
						Calendar end = Calendar.getInstance(timezone);
						end.setTimeInMillis(sessionEnd.getTime());
						if (!DateUtils.isSameLocalTime(end, endCalendar)) {
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

	public int validEnrolmentsCount() {
		int result = ExpressionFactory.inExp(Enrolment.STATUS_PROPERTY,
				ISHPayment.STATUSES_LEGIT).filterObjects(getEnrolments()).size();
		return Math.max(0, result);
	}

	public Long getRecordId() {
		return (Long) readProperty(ID_PK_COLUMN);
	}

	public String getUniqueIdentifier() {
		return getCourse() + "-" + getCode();
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
		return TimeZone.getTimeZone(getTimeZone());
	}

	public boolean hasFeeIncTax() {
		BigDecimal fee = getFeeIncGst();
		return fee != null && Money.ZERO.toBigDecimal().compareTo(fee) < 0;
	}

	public BigDecimal getFeeIncGst() {
		BigDecimal feeGst = getFeeGst();
		BigDecimal feeExGst = getFeeExGst();
		if (feeGst == null || feeExGst == null) {
			return feeExGst;
		}
		return feeExGst.add(feeGst);
	}

	public BigDecimal getTaxMultiplier() {
		if (getFeeGst() == null || Money.ZERO.compareTo(getFeeGst()) >= 0) {
			return Money.ONE;
		}

		// TODO grab correct rate from onCourse later
		return new BigDecimal("1.10");
	}

	public boolean isGstExempt() {
		BigDecimal feeGst = getFeeGst();
		return feeGst == null || Money.ZERO.compareTo(feeGst) == 0;
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
					days.add(TimestampUtilities.dayOfWeek(
							s.getStartDate(),
							true,
							TimeZone.getTimeZone(s.getTimeZone())));
				}
				daysOfWeek = TimestampUtilities.uniqueDaysInOrder(days);
			} else {
				// no sessions recorded, so guess from class start / finish time
				daysOfWeek = new HashSet<String>();
				if (getStartDate() != null) {
					daysOfWeek.add(TimestampUtilities.dayOfWeek(
							getStartDate(), true, TimeZone.getTimeZone(getTimeZone())));

				}
				if (getEndDate() != null) {
					daysOfWeek.add(TimestampUtilities.dayOfWeek(
							getEndDate(), true, TimeZone.getTimeZone(getTimeZone())));

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
			Calendar start = Calendar.getInstance();
			start.setTime(session.getStartDate());
			Integer sessionStartHour = start.get(Calendar.HOUR_OF_DAY);
			if (earliest!=null && sessionStartHour < earliest) {
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
			Calendar end = Calendar.getInstance();
			end.setTime(session.getEndDate());
			Integer sessionEndHour = end.get(Calendar.HOUR_OF_DAY);
			if (latest!=null && sessionEndHour > latest) {
				latest = sessionEndHour;
			}
		}
		return latest;
	}

	/**
	 * @return all sessions that satisfy hasStartAndEndTimestamps
	 * @see Session#hasStartAndEndTimestamps()
	 */
	public List<Session> getTimelineableSessions() {
		List<Session> classSessions = getSessions();
		List<Session> validSessions = new ArrayList<Session>();
		for (Session session : classSessions) {
			if ((session.getStartDate() != null) && (session.getEndDate() != null)) {
				validSessions.add(session);
			}
		}
		return validSessions;
	}
	
	public boolean isHasRoom() {
		return getRoom() != null;
	}
}
