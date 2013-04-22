package ish.oncourse.portal.pages;

import ish.oncourse.model.Contact;
import ish.oncourse.model.Room;
import ish.oncourse.model.Session;
import ish.oncourse.model.SessionTutor;
import ish.oncourse.portal.access.IAuthenticationService;
import ish.oncourse.services.courseclass.ICourseClassService;
import ish.oncourse.util.FormatUtils;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.tapestry5.StreamResponse;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.json.JSONArray;
import org.apache.tapestry5.json.JSONObject;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.Response;
import org.apache.tapestry5.util.TextStreamResponse;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static ish.oncourse.util.FormatUtils.*;

public class TimetableJson {
	
	private static final Logger logger = Logger.getLogger(TimetableJson.class);
	

	@Inject
	private Request request;
	
	@Inject
	private Response response;
	
	@Inject
	private IAuthenticationService authenticationService;
	
	@Inject
	private ICourseClassService courseClassService;
	
	public StreamResponse onActivate(final String monthStr) {
		
		Contact contact = authenticationService.getUser();
		
		List<Session> sessions = courseClassService.getContactSessionsForMonth(contact, currentMonth(monthStr));
		
		Calendar cal = Calendar.getInstance();

		JSONObject jsonTimetable = new JSONObject();
		JSONArray jsonSessions = null;
		
		int currentDay = -1;
		
		for (Session s : sessions) {
            DateFormat timeFormatter = FormatUtils.getTimeFormat_h_mm_a(s.getTimeZone());
            DateFormat monthFormatter = getDateFormat(DATE_FORMAT_MMM, s.getTimeZone());
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
			
			String href = String.format("%s://%s%s/class/%s", request.isSecure() ? "https" : "http" , request.getServerName(), request.getContextPath() , s.getCourseClass().getId());
			jsonSession.put("href", response.encodeURL(href));
			
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
		
		DateFormat formatter = getDateFormat(DATE_FORMAT_MM_yyyy, StringUtils.EMPTY);
		
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
