package ish.oncourse.portal.components;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

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

	@SetupRender
	boolean setupRender() {
		if (courseClass == null) {
			return false;
		}
		Date now = new Date();
		Date closest = null;

		for (Session session : courseClass.getSessions()) {
			Date startDate = session.getStartDate();
			if (startDate.after(now)) {
				if (closest == null || startDate.before(closest)) {
					closest = startDate;
					nextSession = session;
				}
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
	
}
