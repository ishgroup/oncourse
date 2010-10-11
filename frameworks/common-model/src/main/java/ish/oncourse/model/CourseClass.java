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

	/**
	 * parameter for add/remove cookies actions
	 */
	public static final String COURSE_CLASS_ID_PARAMETER = "courseClassId";

	/**
	 * ordered classes cookey name
	 */
	public static final String SHORTLIST_COOKEY_KEY = "shortlist";

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
			if (earliest==null || sessionStartHour < earliest) {
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
			if (latest==null || sessionEndHour > latest) {
				latest = sessionEndHour;
			}
		}
		return latest;
	}

	private Integer getLatestSessionStartHour() {
		Integer latest = null;
		for (Session session : getSessions()) {
			Calendar end = Calendar.getInstance();
			end.setTime(session.getStartDate());
			Integer sessionStartHour = end.get(Calendar.HOUR_OF_DAY);
			if (latest==null || sessionStartHour > latest) {
				latest = sessionStartHour;
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
	
	public float focusMatchForDays(String searchDay) {
		float result = 0.0f;
		if (getDaysOfWeek() != null) {
			List<String> uniqueDays = new ArrayList<String>();
			uniqueDays.addAll(getDaysOfWeek());
			for (String day : uniqueDays) {
				String lowerDay = day.toLowerCase();
				if (TimestampUtilities.DaysOfWeekNamesLowerCase
						.contains(lowerDay)
						&& lowerDay.equalsIgnoreCase(day)) {
					result = 1.0f;
					break;
				}
				if (TimestampUtilities.DaysOfWorkingWeekNamesLowerCase
						.contains(lowerDay)
						&& "weekday".equalsIgnoreCase(day)) {
					result = 1.0f;
					break;
				}
				if (TimestampUtilities.DaysOfWeekendNamesLowerCase
						.contains(lowerDay)
						&& "weekend".equalsIgnoreCase(day)) {
					result = 1.0f;
					break;
				}
			}
		}

		return result;
	}

	public float focusMatchForTime(String time) {
		float result = 0.0f;

		Integer latestHour = getLatestSessionStartHour();
		Integer earliestHour = getEarliestSessionStartHour();
		// much discussion about what day and evening mean...
		// current definitions is that any session that starts before 5 pm is
		// daytime, any that starts after 5pm is evening
		boolean isEvening = latestHour != null && latestHour >= 17;
		boolean isDaytime = earliestHour != null && earliestHour < 17;
		if (isEvening && isDaytime || isDaytime
				&& "daytime".equalsIgnoreCase(time) || isEvening
				&& "evening".equalsIgnoreCase(time)) {
			result = 1.0f;
		}
		return result;

	}
	
	
	public float focusMatchForPrice(Float price) {
		float result = 0.0f;
		float maxPrice = price;
		if (hasFeeIncTax()) {
			if (getFeeIncGst().floatValue() > maxPrice) {
				result = 0.75f - (getFeeIncGst().floatValue() - maxPrice)
						/ maxPrice * 0.25f;
				if (result < 0.25f) {
					result = 0.25f;
				}
			}
		}

		return result;
	}

	/**
	 * Haversine formula: R = earthÕs radius (mean radius = 6,371km) dLat = lat2
	 * - lat1 dLon = lon2 - lon1 a = (sin(dLat/2))^2 +
	 * cos(lat1)*cos(lat2)*(sin(dLat/2))^2 c = 2*atan2(sqrt(a), sqrt(1-a)) d =
	 * R*c
	 * 
	 * @param nearLatitude
	 * @param nearLongitude
	 * @return
	 */
	public float focusMatchForNear(Double nearLatitude, Double nearLongitude) {
		float result = 0.0f;

		if (nearLatitude != null && nearLongitude != null && getRoom() != null
				&& getRoom().getSite() != null
				&& getRoom().getSite().isHasCoordinates()) {
			Site site = getRoom().getSite();

			double earthRadius = 6371; // km

			double lat1 = Math.toRadians(site.getLatitude().doubleValue());
			double lon1 = Math.toRadians(site.getLongitude().doubleValue());
			double lat2 = Math.toRadians(nearLatitude);
			double lon2 = Math.toRadians(nearLongitude);

			double dLat = lat2 - lat1;
			double dLon = lon2 - lon1;
			double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(lat1)
					* Math.cos(lat2) * Math.sin(dLon / 2) * Math.sin(dLon / 2);
			double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

			double distance = earthRadius * c;
			float searchKilometers = 10.0f;
			if (distance >= 5d * searchKilometers) {
				result = 0f;
			} else if (distance <= searchKilometers) {
				result = 1f;
			} else {
				result = 1 - ((float) distance - searchKilometers)
						/ (4f * searchKilometers);
			}

		}
		return result;

	}
}
