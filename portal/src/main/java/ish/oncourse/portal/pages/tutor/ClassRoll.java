package ish.oncourse.portal.pages.tutor;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.tapestry5.StreamResponse;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.util.TextStreamResponse;

import ish.oncourse.model.Attendance;
import ish.oncourse.model.Contact;
import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Enrolment;
import ish.oncourse.model.Session;
import ish.oncourse.model.SessionTutor;
import ish.oncourse.model.TutorRole;
import ish.oncourse.portal.access.IAuthenticationService;
import ish.oncourse.portal.annotations.UserRole;
import ish.oncourse.portal.pages.PageNotFound;
import ish.oncourse.services.courseclass.ICourseClassService;

@UserRole("tutor")
public class ClassRoll {

	private static final int MILLISECONDS_IN_DAY = 1000 * 60 * 60 * 24;
	
	private static final String DATE_FORMAT = "dd/MMM";
	
	private static final String CONTENT_TYPE = "text/json";
	
	@Inject
	private Request request;

	@Property
	private CourseClass courseClass;

	@Property
	private int enrolmentsCount;
	
	@Property
	private int availableEnrolmentPlaces;

	@Property
	private List<Session> sessions;
	
	@Property
	private Attendance attendance;
	
	@Property
	private Session currentSession;

	@Inject
	private ICourseClassService courseClassService;

	@Inject
	@Property
	private IAuthenticationService authenticationService;
	
	
	private SimpleDateFormat dateFormatter;
	
	@InjectPage
	private PageNotFound pageNotFound;
	
	Object onActivate(String id) {
		if (id != null && id.length() > 0 && id.matches("\\d+")) {
			List<CourseClass> list = courseClassService.loadByIds(Long.parseLong(id));
			this.courseClass = (!list.isEmpty()) ? list.get(0) : null;
		} else {
			return pageNotFound;
		}
		return null;
	}

	@SetupRender
	boolean setupRender() {
		if (courseClass == null) {
			return false;
		}
		List<Enrolment> enrolments = courseClass.getValidEnrolments();
		enrolmentsCount = enrolments.size();
		sessions = courseClass.getSessions();
		availableEnrolmentPlaces = courseClass.getAvailableEnrolmentPlaces();
		this.dateFormatter = new SimpleDateFormat(DATE_FORMAT);
		return true;
	}

	public String getClassInfoPageName() {
		return "class";
	}

	public String getClassDetailsPageName() {
		return"classdetails";
	}

	public String getSessionIntervalInfo() {
		Date start = currentSession.getStartDate();
		Date end = currentSession.getEndDate();
		DateFormat formatterWeakDay = new SimpleDateFormat("E ");
		DateFormat formatter = new SimpleDateFormat("HH:mm a");
		String key = "%s %s - %s ";
		if (start == null && end == null) {
			return "";
		} 

		return String.format(key, formatterWeakDay.format(start), formatter.format(start),
				formatter.format(end));
	}
	
	public String getRoomName() {
		return (currentSession.getRoom() != null) ? currentSession.getRoom().getName() : "room not set";
	}
	
	public String getDay() {
		return dateFormatter.format(currentSession.getStartDate()).split("/")[0];
	}

	public String getMonth() {
		return dateFormatter.format(currentSession.getStartDate()).split("/")[1];
	}

	public boolean isToday() {
		return Math.abs(System.currentTimeMillis() - currentSession.getStartDate().getTime()) <= MILLISECONDS_IN_DAY;
	}
	
	public boolean isAttended() {
		return attendance.getAttendanceType() == 1;
	}
	
	public boolean isAbsent() {
		return (attendance.getAttendanceType() == 2 || attendance.getAttendanceType() == 3);
	}

	StreamResponse onActionFromAttendance(){
	
		Contact contact = authenticationService.getUser();
		if (contact != null && contact.getTutor() != null) {
			String id = request.getParameter("attendanceId");
			String action = request.getParameter("action");
			
			Attendance attandance = courseClassService.loadAttendanceById(id);
			boolean isInCourseClassOrSessions = false;

			for (SessionTutor tutor: attandance.getSession().getSessionTutors()) {
				isInCourseClassOrSessions = (contact.getTutor().getId().equals(tutor.getTutor().getId()));
				if(isInCourseClassOrSessions){
					break;
				}
			}
			for (TutorRole tutor: attandance.getSession().getCourseClass().getTutorRoles()) {
				isInCourseClassOrSessions = (contact.getTutor().getId().equals(tutor.getTutor().getId()));
				if(isInCourseClassOrSessions){
					break;
				}
				
			}
			
			if (isInCourseClassOrSessions) {
				
				if ("attended".equals(action)){ 
					attandance.setAttendanceType(1);
					attandance.getObjectContext().commitChanges();
					return new TextStreamResponse(CONTENT_TYPE, "SUCCESS");
				} else if ("absent".equals(action)) {
					attandance.setAttendanceType(2);
					attandance.getObjectContext().commitChanges();
					return new TextStreamResponse(CONTENT_TYPE, "SUCCESS");
				} 
			}
		} 
		
		return new TextStreamResponse(CONTENT_TYPE, "NOT SUCCESS");
		
	}
}
