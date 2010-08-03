package ish.oncourse.model;

import ish.oncourse.model.auto._CourseClass;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.Ordering;
import org.apache.cayenne.query.SortOrder;
import org.apache.commons.lang.time.DateUtils;

public class CourseClass extends _CourseClass {

	public Integer getId() {
		return (getObjectId() != null && !getObjectId().isTemporary()) ? ((Number) getObjectId()
				.getIdSnapshot().get(ID_PK_COLUMN)).intValue()
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
		List<Session> sessions = ExpressionFactory.matchExp(
				Session.DELETED_PROPERTY, null).orExp(
				ExpressionFactory.matchExp(Session.DELETED_PROPERTY, false))
				.filterObjects(getSessions());

		if (sessions.size() > 1) {
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

			for (int i = 0, count = sessions.size(); i < count; i++) {
				Session session = sessions.get(i);
				Date sessionStart = session.getStartTimestamp();
				Date sessionEnd = session.getEndTimestamp();

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
		if (getSessions().size() == 0) {
			return null;
		}

		List<Session> list = new ArrayList<Session>(getSessions());
		new Ordering(Session.START_TIMESTAMP_PROPERTY, SortOrder.ASCENDING)
				.orderList(list);
		return list.get(0);
	}

	public boolean isAvailableEnrolmentPlaces() {
		return availableEnrolmentPlaces() > 0;
	}

	public int availableEnrolmentPlaces() {
		int result = -1;
		if (getMaximumPlaces() != null && getMaximumPlaces() > 0) {
			int validEnrolment = validEnrolmentsCount();
			result = getMaximumPlaces() - validEnrolment;
		}
		return Math.max(0, result);
	}

	public int validEnrolmentsCount() {
		int result = ExpressionFactory.inExp(Enrolment.STATUS_PROPERTY,
				ISHPayment.STATUSES_LEGIT).filterObjects(getEnrolments())
				.size();
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

			if (session.hasStartAndEndTimestamps()) {
				return true;
			}
		}
		return false;
	}
	
	public TimeZone getClassTimeZone(){
		return TimeZone.getTimeZone(getTimeZone());
	}
	
}
