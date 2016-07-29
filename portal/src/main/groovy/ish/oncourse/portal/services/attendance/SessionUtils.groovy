package ish.oncourse.portal.services.attendance

import ish.oncourse.model.Room
import ish.oncourse.model.Session
import ish.oncourse.utils.DateUtils
import org.apache.commons.lang3.StringUtils

class SessionUtils {
	
	static Map<Date, List<Session>> groupByDay(List<Session> sessions) {
		sessions.groupBy { s -> DateUtils.startOfDay(s.startDate, s.timeZone) }
	}


	static Map<String, Map<Date, List<Session>>> groupByMonthDay(List<Session> sessions) {
		groupByDay(sessions).groupBy { e -> DateUtils.startOfMonth(e.key).format('MMMM yyyy') }
	}
	
	static String getVenue(Session session) {
		Room room  = session.getRoom();

		if (room == null) {
			room =  session.getCourseClass().getRoom();
		}

		if (room != null)
			return String.format("%s, %s", room.getName(), room.getSite().getName());
		else
			return StringUtils.EMPTY;
	}
}
