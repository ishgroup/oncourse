package ish.oncourse.portal.components.timetable;


import ish.oncourse.portal.access.IAuthenticationService;
import ish.oncourse.portal.services.timetable.TimetableService;
import ish.oncourse.services.courseclass.ICourseClassService;
import org.apache.tapestry5.StreamResponse;
import org.apache.tapestry5.annotations.OnEvent;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.util.TextStreamResponse;

import java.io.IOException;

/**
 * User: artem
 * Date: 10/14/13
 * Time: 1:27 PM
 */
public class Calendar {

    @Inject
    private ICourseClassService courseClassService;

    @Inject
    private IAuthenticationService authService;

    @Inject
    private Request request;

    @OnEvent(value = "getCalendarEvents")
   public StreamResponse getCalendarEvents() throws IOException {
       TimetableService timetableService = new TimetableService();
       timetableService.setCourseClassService(courseClassService);

       return new TextStreamResponse("text/json", timetableService.getJSONCalendarEvents(authService.getUser()).toString());
   }

}
