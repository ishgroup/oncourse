package ish.oncourse.portal.components.surveys;

import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Survey;
import ish.oncourse.portal.services.IPortalService;
import org.apache.commons.lang3.StringUtils;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.json.JSONObject;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.util.TextStreamResponse;

import java.io.IOException;

public class Surveys {

    private static final String JSONPROPERTY_readOnly = "readOnly";

    @Inject
    @Property
    private IPortalService portalService;

    @Inject
    private Request request;

    @Parameter(required = true)
	@Property
    private CourseClass courseClass;

    @Parameter(required = true)
    @Property
    private boolean isTutor;

    @OnEvent(value = "getSurvey")
    public TextStreamResponse getSurvey(Long id) throws IOException {
        if (!request.isXHR())
            return null;

        Survey survey;
        //we should check at fist that the current contact is a student and try to load survey for student
        boolean isTutor  = portalService.getContact().getTutor() != null && portalService.isTutorFor(portalService.getCourseClassBy(id));
        if (isTutor) {
            survey = portalService.getAverageSurveyFor(portalService.getCourseClassBy(id));
        } else {
            survey = portalService.getStudentSurveyFor(portalService.getCourseClassBy(id));
            if (survey == null)
                survey = portalService.createStudentSurveyFor(portalService.getCourseClassBy(id));
        }

        //adds readonly for tutor
        JSONObject jsonObject = portalService.getJSONSurvey(survey);
        jsonObject.put(JSONPROPERTY_readOnly, isTutor);
        return new TextStreamResponse("text/json", jsonObject.toString());
    }

    @OnEvent(value = "saveSurvey")
    public void saveSurvey(Long id) throws IOException {
        if (!request.isXHR())
            return;

        Survey survey = portalService.getStudentSurveyFor(portalService.getCourseClassBy(id));
        if (survey == null) {
            survey = portalService.createStudentSurveyFor(portalService.getCourseClassBy(id));
        }

        String value = StringUtils.trimToNull(request.getParameter(Survey.TUTOR_SCORE_PROPERTY));
        if (StringUtils.isNumeric(value)) {
            survey.setTutorScore(Integer.valueOf(value));
        }

        value = StringUtils.trimToNull(request.getParameter(Survey.VENUE_SCORE_PROPERTY));
        if (StringUtils.isNumeric(value)) {
            survey.setVenueScore(Integer.valueOf(value));
        }

        value = StringUtils.trimToNull(request.getParameter(Survey.COURSE_SCORE_PROPERTY));
        if (StringUtils.isNumeric(value)) {
            survey.setCourseScore(Integer.valueOf(value));
        }

        value = StringUtils.trimToEmpty(request.getParameter(Survey.COMMENT_PROPERTY));
        survey.setComment(value);

        value = StringUtils.trimToEmpty(request.getParameter(Survey.NET_PROMOTER_SCORE_PROPERTY));
        if (StringUtils.isNumeric(value)) {
            survey.setNetPromoterScore(Integer.valueOf(value));
        }

        survey.getObjectContext().commitChanges();
    }

}
