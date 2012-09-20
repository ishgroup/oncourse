package ish.oncourse.portal.pages.tutor;

import ish.common.types.AttendanceType;
import ish.oncourse.model.*;
import ish.oncourse.portal.access.IAuthenticationService;
import ish.oncourse.portal.annotations.UserRole;
import ish.oncourse.portal.pages.PageNotFound;
import ish.oncourse.portal.services.PortalUtils;
import ish.oncourse.services.courseclass.ICourseClassService;
import ish.oncourse.services.preference.PreferenceController;
import ish.oncourse.util.FormatUtils;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.apache.cayenne.query.SortOrder;
import org.apache.log4j.Logger;
import org.apache.tapestry5.StreamResponse;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.util.TextStreamResponse;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

import static ish.oncourse.util.FormatUtils.getDateFormat_dd_MMM_E;
import static ish.oncourse.util.FormatUtils.getShortDateFormat;

@UserRole("tutor")
public class ClassRoll {

    private static final Logger LOGGER = Logger.getLogger(ClassRoll.class);

    private static final String DATE_FORMAT_E = "E ";
    private static final String DATE_FORMAT_HH_mm_a = "HH:mm a";

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
	private PreferenceController preferenceController;

	@Inject
	@Property
	private IAuthenticationService authenticationService;
	
	@InjectPage
	private PageNotFound pageNotFound;

	Object onActivate(String id) {
		if (id != null && id.length() > 0 && id.matches("\\d+")) {
			List<CourseClass> list = courseClassService.loadByIds(Long.parseLong(id));
			this.courseClass = (!list.isEmpty()) ? list.get(0) : null;
		}

        if (this.courseClass == null )
        {
            return pageNotFound;
        }

        List<Enrolment> enrolments = courseClass.getValidEnrolments();
        enrolmentsCount = enrolments.size();

        sessions = searchSessionBy(courseClass);
        availableEnrolmentPlaces = courseClass.getAvailableEnrolmentPlaces();
        return null;
	}

    /**
     *  The method searches Sessions for the  courseClass and sorts them by startDate field.
     *  The method has been introduced because we need sorting by startDate field, CourseClass.getSessions()
     */
    List<Session> searchSessionBy(CourseClass courseClass) {
        Expression exp = ExpressionFactory.matchDbExp(Session.COURSE_CLASS_PROPERTY, courseClass);
        SelectQuery selectQuery = new SelectQuery(Session.class, exp);
        selectQuery.addOrdering(Session.START_DATE_PROPERTY, SortOrder.ASCENDING);
        //the prefetch has been added to reload ATTENDANCES every time when we reload the page.
        selectQuery.addPrefetch(Session.ATTENDANCES_PROPERTY);
        return  (List<Session>) courseClass.getObjectContext().performQuery(selectQuery);
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
		DateFormat formatterWeakDay = FormatUtils.getDateFormat(DATE_FORMAT_E, currentSession.getTimeZone());
		DateFormat formatter = FormatUtils.getDateFormat(DATE_FORMAT_HH_mm_a, currentSession.getTimeZone());;
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
		return getDateFormat_dd_MMM_E(currentSession.getTimeZone()).format(currentSession.getStartDate()).split("/")[0];
	}

	public String getMonth() {
		return getDateFormat_dd_MMM_E(currentSession.getTimeZone()).format(currentSession.getStartDate()).split("/")[1];
	}

	public boolean isToday() {
		return Math.abs(System.currentTimeMillis() - currentSession.getStartDate().getTime()) <= PortalUtils.MILLISECONDS_IN_DAY;
	}
	
	public boolean isAttended() {
		return AttendanceType.ATTENDED.getDatabaseValue().equals(attendance.getAttendanceType());
	}
	
	public boolean isAbsent() {
		return (AttendanceType.DID_NOT_ATTEND_WITH_REASON.getDatabaseValue().equals(attendance.getAttendanceType())
                || AttendanceType.DID_NOT_ATTEND_WITHOUT_REASON.getDatabaseValue().equals(attendance.getAttendanceType()));
	}
	
	public boolean getHideDetails() {
		return preferenceController.getHideStudentDetailsFromTutor();
	}
	
	public String getEnrolmentDate() {
		if (this.attendance != null && this.attendance.getCreated() != null) {
			return getShortDateFormat(currentSession.getTimeZone()).format(this.attendance.getCreated());
		}
		return "";
	}

	StreamResponse onActionFromAttendance(){
        try {
            Contact contact = authenticationService.getUser();
            if (contact != null && contact.getTutor() != null) {
                String id = request.getParameter("attendanceId");
                String action = request.getParameter("action");

                Attendance attandance = courseClassService.loadAttendanceById(id);
                boolean isInCourseClassOrSessions = false;

                for (SessionTutor tutor
                        : attandance.getSession().getSessionTutors()) {
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
                        attandance.setAttendanceType(AttendanceType.ATTENDED.getDatabaseValue());
                        attandance.getObjectContext().commitChanges();
                        return new TextStreamResponse(PortalUtils.CONTENT_TYPE, "SUCCESS");
                    } else if ("absent".equals(action)) {
                        attandance.setAttendanceType(AttendanceType.DID_NOT_ATTEND_WITH_REASON.getDatabaseValue());
                        attandance.getObjectContext().commitChanges();
                        return new TextStreamResponse(PortalUtils.CONTENT_TYPE, "SUCCESS");
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.warn("Cannot handle the action", e);
        }
        return new TextStreamResponse(PortalUtils.CONTENT_TYPE, "NOT SUCCESS");
		
	}
}
