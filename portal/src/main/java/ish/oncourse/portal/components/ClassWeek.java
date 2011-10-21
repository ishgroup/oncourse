package ish.oncourse.portal.components;

import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Session;
import ish.oncourse.portal.pages.Classes;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;

public class ClassWeek {

	@Parameter
	private CourseClass courseClass;
	
	@InjectPage
	private Classes classes;
	
	@Parameter
	@Property
	private String day;

	private List<String> daysOfWeek;
	
	public List<String> getDaysOfWeek() {
		return daysOfWeek;
	}

	private Map<String, List<Session>> daysOfWeekMap;
	
	@Parameter
	@Property
	private Session session;
	
	
	@SetupRender
	void beforeRender() {
		
		daysOfWeekMap = new HashMap<String, List<Session>>();

		daysOfWeek = new ArrayList<String>(Arrays.asList("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"));
		
		List<Session> sessionsOnMonday = new ArrayList<Session>();
		List<Session> sessionsOnTuesday = new ArrayList<Session>();
		List<Session> sessionsOnWednesday = new ArrayList<Session>();
		List<Session> sessionsOnThursday = new ArrayList<Session>();
		List<Session> sessionsOnFriday = new ArrayList<Session>();
		List<Session> sessionsOnSaturday = new ArrayList<Session>();
		List<Session> sessionsOnSunday = new ArrayList<Session>();
		
		for(Session session: courseClass.getSessions()){
			Calendar cal = Calendar.getInstance();
		    cal.setTime(session.getStartDate());
		    int dayofweek = cal.get(Calendar.DAY_OF_WEEK);
		    if (dayofweek == Calendar.SUNDAY) {
		    	sessionsOnSunday.add(session);
		    } else if (dayofweek == Calendar.MONDAY) {
		    	sessionsOnMonday.add(session);
		    } else if (dayofweek == Calendar.TUESDAY) {
		    	sessionsOnTuesday.add(session);
		    } else if (dayofweek == Calendar.WEDNESDAY) {
		    	sessionsOnWednesday.add(session);
		    } else if (dayofweek == Calendar.THURSDAY) {
		    	sessionsOnThursday.add(session);
		    } else if (dayofweek == Calendar.FRIDAY) {
		    	sessionsOnFriday.add(session);
		    } else if (dayofweek == Calendar.SATURDAY) {
		    	sessionsOnSaturday.add(session);
		    }
		}
		
		daysOfWeekMap.put(daysOfWeek.get(0), sessionsOnSunday);
		daysOfWeekMap.put(daysOfWeek.get(1), sessionsOnMonday);
		daysOfWeekMap.put(daysOfWeek.get(2), sessionsOnTuesday);
		daysOfWeekMap.put(daysOfWeek.get(3), sessionsOnWednesday);
		daysOfWeekMap.put(daysOfWeek.get(4), sessionsOnThursday);
		daysOfWeekMap.put(daysOfWeek.get(5), sessionsOnFriday);
		daysOfWeekMap.put(daysOfWeek.get(6), sessionsOnSaturday);
	}
	
	public boolean isHaveSessionsOnThisWeekDay() {
		return daysOfWeekMap.get(day).size() > 0;
	}
	
	public List<Session> getAllSessionInCurrentWeekDay() {
		return daysOfWeekMap.get(day);
	}

	public List<Session> getSessionInfo() {
		return daysOfWeekMap.get(day);
	}
	
	public String getSessionDateInfo() {
		Date start = courseClass.getStartDate();
		DateFormat formatter = new SimpleDateFormat("MMM. dd ");
		return formatter.format(start);
	}
	
	public String getSessionIntervalInfo() {
		Date start = courseClass.getStartDate();
		Date end = courseClass.getEndDate();
		DateFormat formatter = new SimpleDateFormat("h:mma ");
		String key = "%s - %s";
		return String.format(key, formatter.format(start), formatter.format(end));
	}
}
