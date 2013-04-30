package ish.oncourse.portal.components;

import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Session;
import ish.oncourse.portal.pages.Classes;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ish.oncourse.util.FormatUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

public class ClassWeek {

	@Parameter
	private CourseClass courseClass;
	
	@SuppressWarnings("all")
	@InjectPage
	private Classes classes;
	
	@Parameter
	@Property
	private String day;

	private List<String> daysOfWeek;
	
	@Inject
	private Request request;


    private Map<String, List<Session>> daysOfWeekMap;

    @Parameter
    @Property
    private Session session;

    public String getContextPath() {
		return request.getContextPath();
	}
	
	public List<String> getDaysOfWeek() {
		return daysOfWeek;
	}


	@SetupRender
	void beforeRender() {
		
		daysOfWeekMap = new HashMap<>();

		daysOfWeek = new ArrayList<>(Arrays.asList("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"));
		
		List<Session> sessionsOnMonday = new ArrayList<>();
		List<Session> sessionsOnTuesday = new ArrayList<>();
		List<Session> sessionsOnWednesday = new ArrayList<>();
		List<Session> sessionsOnThursday = new ArrayList<>();
		List<Session> sessionsOnFriday = new ArrayList<>();
		List<Session> sessionsOnSaturday = new ArrayList<>();
		List<Session> sessionsOnSunday = new ArrayList<>();
		
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
		Date start = session.getStartDate();
		return start != null ? FormatUtils.getDateFormat(FormatUtils.DATE_FORMAT_MMM_dd, session.getTimeZone()).format(start) : StringUtils.EMPTY;
	}
	
	public String getSessionIntervalInfo() {
		Date start = session.getStartDate();
		Date end = session.getEndDate();
        DateFormat formatter =  FormatUtils.getTimeFormat_h_mm_a(session.getTimeZone());
		String key = "%s - %s";
		return String.format(key, formatter.format(start), formatter.format(end));
	}
}
