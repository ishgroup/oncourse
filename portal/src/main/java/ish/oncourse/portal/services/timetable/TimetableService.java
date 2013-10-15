package ish.oncourse.portal.services.timetable;

import ish.oncourse.model.Contact;
import ish.oncourse.model.Session;
import ish.oncourse.services.courseclass.ICourseClassService;
import ish.oncourse.util.FormatUtils;
import org.apache.tapestry5.json.JSONObject;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

/**
 * User: artem
 * Date: 10/14/13
 * Time: 3:35 PM
 */
public class TimetableService {

    private ICourseClassService courseClassService;

    public JSONObject getJSONCalendarEvents(Contact contact) {
        List<Session> sessions = courseClassService.getContactSessions(contact);


        JSONObject result = new JSONObject();
        for (Session session : sessions) {
            TimeZone timeZone = FormatUtils.getClientTimeZone(session.getCourseClass());
            result.put(FormatUtils.getDateFormat("MM-dd-yyyy",timeZone).format(session.getStartDate()),
                    String.format("<a href='#class-%s'>%s</a>", session.getCourseClass().getId(), formatDate(session)));
        }
        return result;
    }

    public List<CalendarEvent> getCalendarEvents(Contact contact) {

        List<Session> sessions = courseClassService.getContactSessions(contact);

        List<CalendarEvent> result = new ArrayList<>(sessions.size());
        for (Session session : sessions) {
            CalendarEvent calendarEvent = new CalendarEvent();
            calendarEvent.setDate(session.getStartDate());
            calendarEvent.setContent(String.format("<a href='#class-%s'>%s</a>", session.getId(), formatDate(session)));
            result.add(calendarEvent);
        }
        return result;
    }


    public String formatDate(Session session) {
        TimeZone timeZone = FormatUtils.getClientTimeZone(session.getCourseClass());
        return String.format("%s - %s",
                FormatUtils.getDateFormat(FormatUtils.dateFormatForTimeline, timeZone).format(session.getStartDate()),
                FormatUtils.getDateFormat(FormatUtils.timeFormatWithTimeZoneString, timeZone).format(session.getEndDate()));
    }

    public ICourseClassService getCourseClassService() {
        return courseClassService;
    }

    public void setCourseClassService(ICourseClassService courseClassService) {
        this.courseClassService = courseClassService;
    }

    public String getJSONScript(Object bean) throws IOException {
        StringWriter writer = new StringWriter();
        com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
        mapper.writeValue(writer, bean);
        return writer.toString();
    }
}
