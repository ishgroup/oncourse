package ish.oncourse.portal.pages;

import ish.oncourse.model.Session;
import ish.oncourse.model.Tutor;
import ish.oncourse.model.TutorRole;
import ish.oncourse.portal.access.IAuthenticationService;
import ish.oncourse.portal.services.PortalUtils;
import ish.oncourse.services.courseclass.ICourseClassService;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ish.oncourse.util.FormatUtils;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

public class TimetableList {


	@Inject
	private ICourseClassService classService;
	
	@Inject
	private IAuthenticationService authService;
	
	@Property
	private Map.Entry<Date, List<Session>> currentBucket;
	
	@Property
	private Tutor tutor;
	
	@Property
	private Session session;
	
	private Map<Date, List<Session>> sessionsByDate;
	

	@Inject
	private Request request;
	
	public String getContextPath() {
		return request.getContextPath();
	}

	@SetupRender
	void setupRender() {
		this.sessionsByDate = new LinkedHashMap<>();

		Calendar cal = Calendar.getInstance();

		int curDay = -1, curMonth = -1, curYear = -1;

		List<Session> sessions = classService.getContactSessions(authService.getUser());

		if (sessions.size() > 0) {
			cal.setTime(sessions.get(0).getStartDate());
			curDay = cal.get(Calendar.DAY_OF_MONTH);
			curMonth = cal.get(Calendar.MONTH);
			curYear = cal.get(Calendar.YEAR);

			List<Session> bucket = new ArrayList<>(5);
			bucket.add(sessions.get(0));
			sessionsByDate.put(sessions.get(0).getStartDate(), bucket);

			for (int i = 1; i < sessions.size(); i++) {
				Session s = sessions.get(i);
				cal.setTime(s.getStartDate());

				if (cal.get(Calendar.DAY_OF_MONTH) != curDay || cal.get(Calendar.MONTH) != curMonth || cal.get(Calendar.YEAR) != curYear) {
					bucket = new ArrayList<>(5);
					sessionsByDate.put(s.getStartDate(), bucket);
				}

				bucket.add(s);
			}
		}
	}

	public Set<Map.Entry<Date, List<Session>>> getSessionsByDate() {
		return sessionsByDate.entrySet();
	}

	public String getDayClass() {
		return isToday() ? "date" : "date3";
	}
	
	public boolean getHasSessions() {
		return getSessionsByDate().size() > 0 ? true: false;
	}

	public String getDay() {
        return  FormatUtils.getDateFormat_dd_MMM_E(null).format(currentBucket.getKey()).split("/")[0];
	}

	public String getMonth() {
        return  FormatUtils.getDateFormat_dd_MMM_E(null).format(currentBucket.getKey()).split("/")[1];
    }

	public boolean isToday() {
		return Math.abs(System.currentTimeMillis() - currentBucket.getKey().getTime()) <= PortalUtils.MILLISECONDS_IN_DAY;
	}

	public String getTime() {

        DateFormat timeFormat = FormatUtils.getTimeFormat_h_mm_a(session.getTimeZone());
		return String.format("%s - %s", timeFormat.format(session.getStartDate()), timeFormat.format(session.getEndDate()));
	}

	public String getName() {
		return session.getCourseClass().getCourse().getName();
	}

	public boolean isHasRoomAndSite() {
		return session.getRoom() != null && session.getRoom().getSite() != null;
	}
	
	public String getMonthPageName() {
		return "timetable";
	}
	
	public String getListPageName() {
		return "timetableList";
	}
	
	public List<Tutor> getTutors() {
		List<Tutor> tutors = new ArrayList<>();
		List<TutorRole> tutorRoles = session.getCourseClass().getTutorRoles();
		for (TutorRole tutorRole : tutorRoles) {
			tutors.add(tutorRole.getTutor());
		}
		return tutors;
	}
}
