package ish.oncourse.portal.components;

import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Room;
import ish.oncourse.model.Session;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;

import java.text.DateFormat;
import java.util.*;

import static ish.oncourse.util.FormatUtils.*;

public class NextSession {

	@Parameter
	@Property
	private CourseClass courseClass;

	@Property
	private Session nextSession;

	@Property
	private List<Session> nextSessions;

	@Property
	private Session session;

	@SetupRender
	boolean setupRender() {
		if (courseClass == null) {
			return false;
		}

		initNextSessions();

		//nextSessions list contains only sessions which have end date greater or equals then current time.
		nextSession = nextSessions.size() > 0 ? nextSessions.get(0) : null;
		nextSessions.remove(nextSession);

		return true;
	}

	private void initNextSessions() {
		Date now = new Date();
		nextSessions = ExpressionFactory.greaterOrEqualExp(Session.END_DATE_PROPERTY, now).filterObjects(courseClass.getSessions());
		Collections.sort(nextSessions, new Comparator<Session>() {
			@Override
			public int compare(Session o1, Session o2) {
				if (o1 != null && o2 != null && o1.getStartDate() != null && o2.getStartDate() != null) {
					return o1.getStartDate().compareTo(o2.getStartDate());
				}
				return 0;
			}

		});
	}

	public String getDay() {
		return getDateFormat_dd_MMM_E_yyyy(nextSession.getTimeZone()).format(nextSession.getStartDate()).split("/")[0];
	}

	public String getMonth() {
		return getDateFormat_dd_MMM_E_yyyy(nextSession.getTimeZone()).format(nextSession.getStartDate()).split("/")[1];
	}

	public String getWeekDay() {
		return getDateFormat_dd_MMM_E_yyyy(nextSession.getTimeZone()).format(nextSession.getStartDate()).split("/")[2];
	}

	public String getYear() {
		return getDateFormat_dd_MMM_E_yyyy(nextSession.getTimeZone()).format(nextSession.getStartDate()).split("/")[3];
	}

	private String getSessionTime(Session session) {
		DateFormat dateFormat = getTimeFormat_h_mm_a(session.getTimeZone());
		return String.format("%s - %s", dateFormat.format(session.getStartDate()),
				dateFormat.format(session.getEndDate())).toLowerCase();
	}

	public boolean isToday() {
		Calendar date = Calendar.getInstance();
		int year = date.get(Calendar.YEAR);
		int month = date.get(Calendar.MONTH);
		int day = date.get(Calendar.DATE);
		date.setTime(nextSession.getStartDate());
		return (year - date.get(Calendar.YEAR) == 0) && (month - date.get(Calendar.MONTH) == 0)
				&& (day - date.get(Calendar.DATE) == 0);
	}

	public String getTime() {
		return getSessionTime(nextSession);
	}

	public Room getRoom() {
		return nextSession.getRoom();
	}

	public String getTimetablePageName() {
		return "timetable";
	}

	public boolean isMoreThenOneSessions() {
		return nextSessions != null && nextSessions.size() > 0;
	}

	public String getSessionDay() {
		return getDateFormat_dd_MMM_E(nextSession.getTimeZone()).format(session.getStartDate()).split("/")[0];
	}

	public String getSessionMonth() {
		return getDateFormat_dd_MMM_E(nextSession.getTimeZone()).format(session.getStartDate()).split("/")[1];
	}

	public String getSessionWeekDay() {
		return getDateFormat_dd_MMM_E(nextSession.getTimeZone()).format(session.getStartDate()).split("/")[2];
	}

	public boolean isSessionToday() {
		Calendar date = Calendar.getInstance();
		int year = date.get(Calendar.YEAR);
		int month = date.get(Calendar.MONTH);
		int day = date.get(Calendar.DATE);
		date.setTime(session.getStartDate());
		return (year - date.get(Calendar.YEAR) == 0) && (month - date.get(Calendar.MONTH) == 0)
				&& (day - date.get(Calendar.DATE) == 0);
	}

	public String getSessionTime() {
		return getSessionTime(session);
	}

	public String getRoomInformation() {
		StringBuilder result = new StringBuilder();
		if (session.getRoom() != null) {
			result.append(session.getRoom().getName());
			if (session.getRoom().getSite() != null) {
				result.append(", ");
				result.append(session.getRoom().getSite().getName());
			}
		}

		return result.toString();
	}
}
