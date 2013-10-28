package ish.oncourse.portal.services;

import ish.oncourse.model.*;
import ish.oncourse.services.courseclass.ICourseClassService;
import ish.oncourse.util.FormatUtils;
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


   public JSONObject getAttendences(Session session)
    {
       List<Attendance> attendances = session.getAttendances();

        JSONObject result = new JSONObject();
        for(Attendance attendance : attendances)
            result.put(String.format("%s",attendance.getStudent().getId()),String.format("%s",attendance.getAttendanceType()));


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
}
