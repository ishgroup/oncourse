package ish.oncourse.portal.components.courseclass;


import ish.oncourse.model.*;
import ish.oncourse.portal.access.IAuthenticationService;
import ish.oncourse.portal.pages.*;
import ish.oncourse.portal.pages.Class;
import ish.oncourse.portal.services.JSONPortalService;
import ish.oncourse.services.persistence.CayenneService;
import ish.oncourse.services.persistence.ICayenneService;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.apache.tapestry5.StreamResponse;
import org.apache.tapestry5.annotations.*;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.util.TextStreamResponse;

import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * User: artem
 * Date: 10/16/13
 * Time: 10:00 AM
 */
public class ClassDetails {


    @Inject
    private IAuthenticationService authenticationService;

    @Persist
    @Property
    private Session session;

    @Inject
    private Request request;

    @Property
    @Parameter
    private CourseClass courseClass;

    @Inject
    private ICayenneService cayenneService;




    public boolean isTutor(){
        return authenticationService.isTutor();
    }

    @OnEvent(value = "onSetSession")
    void setSession(Long sessionId) {
        session = Cayenne.objectForPK(courseClass.getObjectContext(), Session.class, sessionId);
    }



    @OnEvent(value = "getAttendences")
    public StreamResponse getAttendences() throws IOException {
        JSONPortalService portalService = new JSONPortalService();
        return new TextStreamResponse("text/json", portalService.getAttendences(session).toString());
    }

    @OnEvent(value = "setAttendences")
    public void setAttendences() throws IOException {

        ObjectContext context = cayenneService.newContext();

        Session localSession = context.localObject(session);

        List<String> params = request.getParameterNames();
        for (String key : params) {
            Long contactId = new Long(key);
            Integer value = new Integer(request.getParameter(key));

           for(Attendance attendance : localSession.getAttendances()){

              if(attendance.getStudent().getId().equals(contactId)){
                  attendance.setAttendanceType(value);
                  break;
              }
           }
        }
        context.commitChanges();

    }


}


