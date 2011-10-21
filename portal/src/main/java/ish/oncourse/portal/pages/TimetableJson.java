package ish.oncourse.portal.pages;

import ish.oncourse.model.Contact;
import ish.oncourse.model.Room;
import ish.oncourse.model.Session;
import ish.oncourse.model.SessionTutor;
import ish.oncourse.portal.access.IAuthenticationService;
import ish.oncourse.services.courseclass.ICourseClassService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.tapestry5.StreamResponse;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.json.JSONArray;
import org.apache.tapestry5.json.JSONObject;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.util.TextStreamResponse;

public class TimetableJson {
	
	private static final Logger logger = Logger.getLogger(TimetableJson.class);
	
	/**
	 * Format for parsing month parameter.
	 */
	private static final String DATE_FORMAT = "MM-yyyy";
	
	/**
	 * Format for month.
	 */
	private static final String MONTH_FORMAT = "MMM";
	
	/**
	 * Format for printing classes time.
	 */
	private static final String TIME_FORMAT = "h:mm a";
	
	@Inject
	private Request request;
	
	@Inject
	private IAuthenticationService authenticationService;
	
	@Inject
	private ICourseClassService courseClassService;
	
	public StreamResponse onActivate(final String monthStr) {
		
		Contact contact = authenticationService.getUser();
		
		List<Session> sessions = courseClassService.getContactSessionsForMonth(contact, currentMonth(monthStr));
		
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat timeFormatter = new SimpleDateFormat(TIME_FORMAT);
		SimpleDateFormat monthFormatter = new SimpleDateFormat(MONTH_FORMAT);
		
		JSONObject jsonTimetable = new JSONObject();
		JSONArray jsonSessions = null;
		
		int currentDay = -1;
		
		for (Session s : sessions) {
			cal.setTime(s.getStartDate());
			int day = cal.get(Calendar.DAY_OF_MONTH);
			
			if (day != currentDay) {
				currentDay = day;
				JSONObject bucket =  new JSONObject();
				
				bucket.put("month", monthFormatter.format(s.getStartDate()));
				bucket.put("day", day);
				
				jsonSessions = new JSONArray();
				bucket.put("sessions", jsonSessions);
				jsonTimetable.put(String.valueOf(day), bucket);
			}
			
			JSONObject jsonSession = new JSONObject();
			jsonSession.put("name", s.getCourseClass().getCourse().getName());
			
			String href = String.format("http://%s/%s/class/%s", request.getServerName(), request.getContextPath() , s.getCourseClass().getId());
			jsonSession.put("href", href);
			
			String time = String.format("%s - %s", timeFormatter.format(s.getStartDate()), timeFormatter.format(s.getEndDate()));
			jsonSession.put("time", time);
			
			JSONArray jsonTutors = new JSONArray();
			
			for (SessionTutor st : s.getSessionTutors()) {
				JSONObject jsonTutor = new JSONObject();
				jsonTutor.put("tutorId", st.getTutor().getId());
				jsonTutor.put("tutorFullName", String.format("%s %s", st.getTutor().getContact().getGivenName(), st.getTutor().getContact().getFamilyName()));
				jsonTutors.put(jsonTutor);
			}
			
			Room room = s.getRoom();
			if(room != null && room.getSite() != null) {
				JSONObject jsonRoom = new JSONObject();
				jsonRoom.put("roomName", room.getName());
				jsonRoom.put("roomId", room.getId());
				jsonRoom.put("siteName", room.getSite().getName());
				jsonRoom.put("siteId", room.getSite().getId());
				jsonSession.put("room", jsonRoom);
			}
			
			jsonSession.put("tutors", jsonTutors);
			jsonSessions.put(jsonSession);
		}
		
		return new TextStreamResponse("text/json", jsonTimetable.toString());
	}
	
	private Date currentMonth(String monthStr) {
		
		SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT);
		
		Date currentMonth = null; 
		
		if (monthStr != null) {
			try {
				currentMonth = formatter.parse(monthStr);
			} catch (ParseException e) {
				logger.warn(String.format("Unable to parse current month parameter:%s.", monthStr));
			}
		}
		else {
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.DAY_OF_MONTH, 1);
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
			currentMonth = cal.getTime();
		}
		
		return currentMonth;
	}
}
