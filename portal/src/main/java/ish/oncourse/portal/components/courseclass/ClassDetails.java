package ish.oncourse.portal.components.courseclass;


import ish.oncourse.model.Attendance;
import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Session;
import ish.oncourse.portal.services.IPortalService;
import ish.oncourse.portal.services.PortalUtils;
import ish.oncourse.services.html.IPlainTextExtractor;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.textile.ITextileConverter;
import ish.oncourse.util.ValidationErrors;
import org.apache.cayenne.Cayenne;
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

	@Property
	@Parameter
	private boolean isTutor;

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

    @Inject
    private IPortalService portalService;

	@Property
	private String details;

	@Property
	private String fullDetails;

	@Inject
	private ITextileConverter textileConverter;

	@Inject
	private IPlainTextExtractor extractor;

    @SetupRender
	void setupRender() {

        isTutor = portalService.getContact().getTutor() != null && portalService.isTutorFor(this.courseClass);

		if (courseClass != null) {
			details = PortalUtils.getClassDetailsBy(courseClass, textileConverter, extractor);
			fullDetails = textileConverter.convertCustomTextile(PortalUtils.getClassDetails(courseClass), new ValidationErrors());
		}
	}

    @OnEvent(value = "onSetSession")
    void setSession(Long sessionId) {
        session = Cayenne.objectForPK(cayenneService.newContext(), Session.class, sessionId);
    }



    @OnEvent(value = "getAttendences")
    public StreamResponse getAttendences() throws IOException {
        return new TextStreamResponse("text/json", portalService.getJSONSession(session).toString());
    }

    @OnEvent(value = "setAttendences")
    public void setAttendences() throws IOException {
        List<String> params = request.getParameterNames();
        for (String key : params) {
            Long contactId = new Long(key);
            Integer value = new Integer(request.getParameter(key));

           for(Attendance attendance : session.getAttendances()){

              if(attendance.getStudent().getId().equals(contactId)){
                  attendance.setAttendanceType(value);
                  break;
              }
           }
        }
        session.getObjectContext().commitChanges();
    }

    public boolean showSurveys()
    {
        return courseClass.getEndDate() == null || courseClass.getEndDate().before(new Date());
    }


}


