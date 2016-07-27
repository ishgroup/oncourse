package ish.oncourse.portal.components.courseclass;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import ish.oncourse.model.Attendance;
import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Enrolment;
import ish.oncourse.model.Session;
import ish.oncourse.model.Tutor;
import ish.oncourse.portal.services.IPortalService;
import ish.oncourse.portal.services.PortalUtils;
import ish.oncourse.portal.services.attendance.AttendanceUtils;
import ish.oncourse.portal.services.attendance.SessionResponse;
import ish.oncourse.services.html.IPlainTextExtractor;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.textile.ITextileConverter;
import ish.oncourse.util.ValidationErrors;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.apache.commons.io.IOUtils;
import org.apache.tapestry5.StreamResponse;
import org.apache.tapestry5.annotations.*;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.util.TextStreamResponse;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
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

	public boolean showSurveys()
	{
		return courseClass.getEndDate() == null || courseClass.getEndDate().before(new Date());
	}



	@OnEvent(value = "getAttendences")
	public StreamResponse getAttendences(Long sessionId) throws IOException {
		
		Session session = Cayenne.objectForPK(cayenneService.newContext(), Session.class, sessionId);
		List<ish.oncourse.portal.services.attendance.Attendance> response = new ArrayList<>();
		
		for (Attendance attendance : session.getAttendances()) {
			response.add(ish.oncourse.portal.services.attendance.Attendance.valueOf(attendance));
		}

		return new TextStreamResponse("text/json", mapper.writeValueAsString(response));
	}

	@OnEvent(value = "saveAttendance")
	public StreamResponse saveAttendance() throws IOException {

		String json = IOUtils.toString(httpRequest.getInputStream(), "UTF-8");
		
		ish.oncourse.portal.services.attendance.Attendance attendance = mapper.readValue(json, ish.oncourse.portal.services.attendance.Attendance.class);
		ObjectContext context = cayenneService.newContext();
		Tutor tutor = context.localObject(portalService.getContact().getTutor());
		Date now = new Date();
		Attendance att = Cayenne.objectForPK(context, Attendance.class, attendance.getId());
		att.setAttendanceType(attendance.getType());
		att.setNote(attendance.getNote());
		att.setDurationMinutes(attendance.getDurationMinutes());
		att.setMarkedByTutor(tutor);
		att.setMarkedByTutorDate(now);
		
		context.commitChanges();
		
		return new TextStreamResponse("text/json",  mapper.writeValueAsString(SessionResponse.valueOf(att, portalService)));
	}

}


