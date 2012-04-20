package ish.oncourse.portal.components;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Room;
import ish.oncourse.model.Session;
import ish.oncourse.portal.services.PortalUtils;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;

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
		Date now = new Date();
		Date closest = null;
		nextSessions = new ArrayList<Session>();

		for (Session session : courseClass.getSessions()) {
			Date startDate = session.getStartDate();
			if (startDate.after(now)) {
				if (closest == null || startDate.before(closest)) {
					closest = startDate;
					nextSession = session;
				}
				nextSessions.add(session);
			}
		}
		
		if(nextSession != null){
			if(nextSessions.contains(nextSession)){
				nextSessions.remove(nextSession);
			}
		}
		

		return true;
	}

	public String getDay() {
		return PortalUtils.DATE_FORMATTER_dd_MMM_E.format(nextSession.getStartDate()).split("/")[0];
	}

	public String getMonth() {
		return PortalUtils.DATE_FORMATTER_dd_MMM_E.format(nextSession.getStartDate()).split("/")[1];
	}

	public String getWeekDay() {
		return PortalUtils.DATE_FORMATTER_dd_MMM_E.format(nextSession.getStartDate()).split("/")[2];
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
		return String.format("%s - %s", PortalUtils.TIME_FORMATTER_h_mm_a.format(nextSession.getStartDate()),
                PortalUtils.TIME_FORMATTER_h_mm_a.format(nextSession.getEndDate())).toLowerCase();
	}
	
	public Room getRoom(){
		return nextSession.getRoom();
	}
	
	public String getTimetablePageName() {
		return "timetable";
	}
	
	public boolean isMoreThenOneSessions() {
		return nextSessions != null && nextSessions.size() > 0;
	}
	
	public String getSessionDay() {
		return PortalUtils.DATE_FORMATTER_dd_MMM_E.format(session.getStartDate()).split("/")[0];
	}

	public String getSessionMonth() {
		return PortalUtils.DATE_FORMATTER_dd_MMM_E.format(session.getStartDate()).split("/")[1];
	}

	public String getSessionWeekDay() {
		return PortalUtils.DATE_FORMATTER_dd_MMM_E.format(session.getStartDate()).split("/")[2];
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
		return String.format("%s - %s", PortalUtils.TIME_FORMATTER_h_mm_a.format(session.getStartDate()),
                PortalUtils.TIME_FORMATTER_h_mm_a.format(session.getEndDate())).toLowerCase();
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
