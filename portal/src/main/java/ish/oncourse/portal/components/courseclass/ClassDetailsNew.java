package ish.oncourse.portal.components.courseclass;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import ish.common.types.DeliverySchedule;
import ish.oncourse.model.*;
import ish.oncourse.portal.services.IPortalService;
import ish.oncourse.portal.services.PortalUtils;
import ish.oncourse.portal.services.attendance.AttendanceTransportUtils;
import ish.oncourse.portal.services.attendance.SessionResponse;
import ish.oncourse.portal.services.survey.CreateSurvey;
import ish.oncourse.portal.services.survey.GetSurveyContainers;
import ish.oncourse.portal.services.survey.SurveyContainer;
import ish.oncourse.portal.util.SurveyEncoder;
import ish.oncourse.services.html.IPlainTextExtractor;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.textile.ITextileConverter;
import ish.oncourse.survey.CreateOrGetEnrolmentSurveysForDate;
import ish.oncourse.util.ValidationErrors;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.tapestry5.EventConstants;
import org.apache.tapestry5.StreamResponse;
import org.apache.tapestry5.annotations.*;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.util.TextStreamResponse;
import org.apache.tapestry5.corelib.components.Form;

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
	@Property
	private IPortalService portalService;

	@Property
	private String details;

	@Property
	private String fullDetails;

	@Inject
	private ITextileConverter textileConverter;

	@Inject
	private IPlainTextExtractor extractor;

	@Property
	private List<SurveyContainer> surveyContainers;

	@Property
	private SurveyContainer surveyContainer;
	
	@Property
	private Boolean useDefaultSurvey = false;
	@Property
	private Survey defaultSurvey = null;


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

		surveyContainers = GetSurveyContainers.valueOf(enrolment).get();
		
		if (surveyContainers == null) {
			useDefaultSurvey = true;

			if (!enrolment.getSurveys().isEmpty()) {
				defaultSurvey = enrolment.getSurveys().get(0);
			}
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

	public boolean showDefaultSurvey() {
		return useDefaultSurvey && (courseClass.getEndDate() == null || courseClass.getEndDate().before(new Date()));
	}

	@OnEvent(value = "surveysform")
	public void onSuccess() {
		System.out.println("SAVE");
	}
}


