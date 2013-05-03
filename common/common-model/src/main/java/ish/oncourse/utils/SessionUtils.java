package ish.oncourse.utils;

import ish.oncourse.model.Session;

import java.util.*;

public class SessionUtils {


	public static boolean isSameTime(List<Session> sessions)
	{
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

	static StartEndTime valueOf(Session session)
	{
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

	public static final class StartEndTime implements Comparable<StartEndTime>
	{
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
