package ish.oncourse.portal.components;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Room;
import ish.oncourse.model.Session;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;

public class NextSession {
	
	private static final String DATE_FORMAT = "dd/MMM/E";

	/**
	 * Format for printing classes time.
	 */
	private static final String TIME_FORMAT = "h:mm a";
	
	@Parameter
	@Property
	private CourseClass courseClass;

	private SimpleDateFormat dateFormatter;

	private SimpleDateFormat timeFormatter;

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
		
		this.dateFormatter = new SimpleDateFormat(DATE_FORMAT);
		this.timeFormatter = new SimpleDateFormat(TIME_FORMAT);

		return true;
	}

	public String getDay() {
		return dateFormatter.format(nextSession.getStartDate()).split("/")[0];
	}

	public String getMonth() {
		return dateFormatter.format(nextSession.getStartDate()).split("/")[1];
	}

	public String getWeekDay() {
		return dateFormatter.format(nextSession.getStartDate()).split("/")[2];
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
		return String.format("%s - %s", timeFormatter.format(nextSession.getStartDate()),
				timeFormatter.format(nextSession.getEndDate())).toLowerCase();
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
		return dateFormatter.format(session.getStartDate()).split("/")[0];
	}

	public String getSessionMonth() {
		return dateFormatter.format(session.getStartDate()).split("/")[1];
	}

	public String getSessionWeekDay() {
		return dateFormatter.format(session.getStartDate()).split("/")[2];
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
		return String.format("%s - %s", timeFormatter.format(session.getStartDate()),
				timeFormatter.format(session.getEndDate())).toLowerCase();
	}
}
