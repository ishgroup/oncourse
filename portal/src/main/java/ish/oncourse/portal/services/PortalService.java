package ish.oncourse.portal.services;

import ish.oncourse.model.*;
import ish.oncourse.portal.access.IAuthenticationService;
import ish.oncourse.services.binary.IBinaryDataService;
import ish.oncourse.services.courseclass.ICourseClassService;
import ish.oncourse.services.preference.PreferenceController;
import ish.oncourse.util.FormatUtils;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.json.JSONObject;

import java.io.IOException;
import java.io.StringWriter;
import java.util.List;
import java.util.TimeZone;

/**
 * User: artem
 * Date: 10/14/13
 * Time: 3:35 PM
 */
public class PortalService implements IPortalService{

    @Inject
    private ICourseClassService courseClassService;

    @Inject
    private PreferenceController preferenceController;

    @Inject
    private IBinaryDataService binaryDataService;

    @Inject
    private IAuthenticationService authenticationService;



    @Override
    public JSONObject getSession(Session session) {
        JSONObject result = new JSONObject();
        result.put("startDate",FormatUtils.getDateFormat("EEEE dd MMMMM h:mma", session.getTimeZone()).format(session.getStartDate()));
        result.put("endDate", FormatUtils.getDateFormat("EEEE dd MMMMM h:mma", session.getTimeZone()).format(session.getEndDate()));
        return result;
    }

    public JSONObject getAttendences(Session session)
   {
       List<Attendance> attendances = session.getAttendances();

       JSONObject result = new JSONObject();
       result.append("session", getSession(session));

        for(Attendance attendance : attendances)
        {
            result.put(String.format("%s",attendance.getStudent().getId()),String.format("%s",attendance.getAttendanceType()));
        }



        return result;
    }



    public JSONObject getNearesSessionIndex(Integer i)
   {

        JSONObject result = new JSONObject();
        result.append("nearesIndex", i);

        return result;
   }


    /**
     * @return contact's sesssions array where entry format is MM-dd-yyyy,<a href='#class-%s'>%s</a>
     * we use it to show sessions callender for contact in Tempalte page
     */
    public JSONObject getCalendarEvents(Contact contact) {
        List<Session> sessions = courseClassService.getContactSessions(contact);


        JSONObject result = new JSONObject();
        for (Session session : sessions) {
            TimeZone timeZone = FormatUtils.getClientTimeZone(session.getCourseClass());
            result.put(FormatUtils.getDateFormat("MM-dd-yyyy",timeZone).format(session.getStartDate()),
                    String.format("<a href='#class-%s'>%s</a>", session.getCourseClass().getId(), formatDate(session)));
        }
        return result;
    }

    private String formatDate(Session session) {
        TimeZone timeZone = FormatUtils.getClientTimeZone(session.getCourseClass());
        return String.format("%s - %s",
                FormatUtils.getDateFormat(FormatUtils.dateFormatForTimeline, timeZone).format(session.getStartDate()),
                FormatUtils.getDateFormat(FormatUtils.timeFormatWithTimeZoneString, timeZone).format(session.getEndDate()));
    }

    public String getJSONScript(Object bean) throws IOException {
        StringWriter writer = new StringWriter();
        com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
        mapper.writeValue(writer, bean);
        return writer.toString();
    }

    public boolean isApproved(Contact tutor, CourseClass courseClass) {
        for (TutorRole t : courseClass.getTutorRoles()) {
            if (t.getTutor().getContact().getId().equals(tutor.getId())) {
                return t.getIsConfirmed();
            }
        }
        return false;
    }

    @Override
    public boolean isHistoryEnabled() {
        return preferenceController.isPortalHistoryEnabled();
    }

    @Override
    public boolean isHasResources(List<CourseClass> courseClasses) {
        boolean result=false;

        for(CourseClass courseClass : courseClasses){
            if(!binaryDataService.getAttachedFiles(courseClass.getId(), CourseClass.class.getSimpleName(), false).isEmpty()){
                result=true;
                break;
            }
        }
        return  result;
    }

    @Override
    public boolean isHasResult() {
        boolean result=false;

        if(authenticationService.getUser().getStudent()!=null){
            for(Enrolment enrolment : authenticationService.getUser().getStudent().getEnrolments()){
                Course course = enrolment.getCourseClass().getCourse();
                if(!course.getModules().isEmpty() || course.getQualification()!=null){
                    result=true;
                    break;
                }
            }
        }
        return result;
    }
}
