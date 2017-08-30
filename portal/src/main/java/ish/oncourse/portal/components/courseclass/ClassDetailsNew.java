package ish.oncourse.portal.components.courseclass;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import ish.oncourse.model.Attendance;
import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Enrolment;
import ish.oncourse.model.Session;
import ish.oncourse.portal.services.IPortalService;
import ish.oncourse.portal.services.PortalUtils;
import ish.oncourse.portal.services.attendance.AttendanceTransportUtils;
import ish.oncourse.portal.services.attendance.SessionResponse;
import ish.oncourse.services.html.IPlainTextExtractor;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.textile.ITextileConverter;
import ish.oncourse.util.ValidationErrors;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.tapestry5.StreamResponse;
import org.apache.tapestry5.annotations.*;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.util.TextStreamResponse;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * User: artem
 * Date: 10/16/13
 * Time: 10:00 AM
 */
public class ClassDetailsNew {

	@Property
	@Parameter
	private boolean isTutor;

	@Property
	private Enrolment enrolment;

	@Persist
	@Property
	private Session session;

	@Inject
	private Request request;

	@Property
	@Parameter
	private CourseClass courseClass;

	@Parameter
	private boolean activeTab = true;

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
	
	private ObjectMapper mapper = new ObjectMapper();

	private CollectionType requestType = mapper.getTypeFactory().constructCollectionType(List.class, ish.oncourse.portal.services.attendance.Attendance.class);


	@Inject
	private HttpServletRequest httpRequest;

	@SetupRender
	void setupRender() {
		if (courseClass != null) {
			details = PortalUtils.getClassDetailsBy(courseClass, textileConverter, extractor);
			fullDetails = textileConverter.convertCustomTextile(PortalUtils.getClassDetails(courseClass), new ValidationErrors());
		}
		if (!isTutor) {
			enrolment = portalService.getEnrolmentBy(portalService.getContact().getStudent(), courseClass);
		}
	}

	@OnEvent(value = "getAttendences")
	public StreamResponse getAttendences(Long sessionId) throws IOException {
		Session session = Cayenne.objectForPK(cayenneService.newContext(), Session.class, sessionId);

		List<ish.oncourse.portal.services.attendance.Attendance> list =
				AttendanceTransportUtils.toContainerAttendanceList(session.getAttendances());

		return new TextStreamResponse("text/json",
				mapper.writeValueAsString(list));
	}

	@OnEvent(value = "saveAttendance")
	public StreamResponse saveAttendance() throws IOException {
		String json = IOUtils.toString(httpRequest.getInputStream(), "UTF-8");
		ish.oncourse.portal.services.attendance.Attendance attendance = mapper.readValue(json, ish.oncourse.portal.services.attendance.Attendance.class);

		ObjectContext context = cayenneService.newContext();
		Attendance att = AttendanceTransportUtils
				.toDBOAttendance(context, attendance, context.localObject(portalService.getContact().getTutor()));
		context.commitChanges();
		
		return new TextStreamResponse("text/json",  mapper.writeValueAsString(SessionResponse.valueOf(att, portalService)));
	}
	
	public String getActiveClass() {
		return activeTab ? "active" : StringUtils.EMPTY;
	}

	public boolean showSurveys()
	{
		return courseClass.getEndDate() == null || courseClass.getEndDate().before(new Date());
	}

}


