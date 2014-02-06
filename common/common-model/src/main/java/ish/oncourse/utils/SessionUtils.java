package ish.oncourse.utils;

import ish.oncourse.model.Session;
import ish.util.DateTimeUtil;
import org.apache.cayenne.query.Ordering;
import org.apache.cayenne.query.SortOrder;

import java.util.*;

public class SessionUtils {

	public static List<SessionDay> getSessionDays(List<Session> sessions) {
		new Ordering(Session.START_DATE_PROPERTY, SortOrder.ASCENDING).orderList(sessions);
		List<SessionDay> days = new ArrayList<>();
		for (Session session : sessions) {
			SessionDay day = getSameDaySession(days, session);
			if (day == null) {
				day = new SessionDay();
				day.setDayStartTime(session.getStartDate());
				day.setDayEndTime(session.getEndDate());
				days.add(day);
			} else {
				day.setDayEndTime(session.getEndDate());
			}
		}
		Collections.sort(days);
		return days;
	}

	private static SessionDay getSameDaySession(List<SessionDay> days, Session session) {
		if (days.isEmpty()) {
			return null;
		} else {
			SessionDay day = days.get(days.size() - 1);
			Calendar start = Calendar.getInstance(), sessionStart = Calendar.getInstance();
			start.setTime(DateTimeUtil.trancateToMidnight(day.getDayStartTime()));
			sessionStart.setTime(DateTimeUtil.trancateToMidnight(session.getStartDate()));
			return org.apache.commons.lang.time.DateUtils.isSameDay(start, sessionStart) ? day : null;
		}
	}


	public static boolean isSameTime(List<Session> sessions) {
		if (sessions.size() < 2)
			return true;
		ArrayList<StartEndTime> times = new ArrayList<StartEndTime>();
		for (Session session : sessions) {
			times.add(valueOf(session));
		}
		Collections.sort(times);

		StartEndTime first = times.get(0);
		StartEndTime last = times.get(times.size() -1);

		return first.equals(last);
	}

	static StartEndTime valueOf(Session session) {
		StartEndTime t = new StartEndTime();
		t.setStartTime(session.getStartDate() != null? adjustDate2Time(session.getStartDate()):0);
		t.setEndTime(session.getEndDate() != null ? adjustDate2Time(session.getEndDate()): 0);
		return t;

	}

	static long adjustDate2Time(Date date) {
		Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		calendar.setTime(date);
		calendar.set(Calendar.YEAR, 0);
		calendar.set(Calendar.DAY_OF_YEAR, 0);
		calendar.set(Calendar.MONTH, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.set(Calendar.SECOND, 0);
		return calendar.getTime().getTime();
	}

	public static final class SessionDay implements Comparable<SessionDay>{
		private Date dayStartTime;
		private Date dayEndTime;

		public Date getDayStartTime() {
			return dayStartTime;
		}

		public void setDayStartTime(Date dayStartTime) {
			this.dayStartTime = dayStartTime;
		}

		public Date getDayEndTime() {
			return dayEndTime;
		}

		public void setDayEndTime(Date dayEndTime) {
			this.dayEndTime = dayEndTime;
		}

		@Override
		public boolean equals(Object object) {
			return object instanceof SessionDay
				&& (this == object ||
					(this.dayStartTime == ((SessionDay) object).dayStartTime
						&& this.dayEndTime == ((SessionDay) object).dayEndTime));
		}

		@Override
		public int compareTo(SessionDay object) {
			int result = dayStartTime.compareTo(object.dayStartTime);
			if (result == 0) {
				result = dayEndTime.compareTo(object.dayEndTime);
			}
			return result;
		}
	}

	public static final class StartEndTime implements Comparable<StartEndTime> {
		private long startTime;
		private long endTime;

		@Override
		public boolean equals(Object obj) {
			return obj instanceof StartEndTime &&
					(obj == this ||
							(this.startTime == ((StartEndTime) obj).startTime && this.endTime == ((StartEndTime) obj).endTime));

		}

		public void setStartTime(long startTime) {
			this.startTime = startTime;
		}

		public void setEndTime(long endTime) {
			this.endTime = endTime;
		}

		public long getStartTime() {
			return startTime;
		}

		public long getEndTime() {
			return endTime;
		}

		@Override
		public int compareTo(StartEndTime o) {
			int result = new Long(startTime).compareTo(o.startTime);
			if (result == 0)
				result = new Long(endTime).compareTo(o.endTime);

			return result;
		}
	}
}
