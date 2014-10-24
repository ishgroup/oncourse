package ish.oncourse.portal.components.surveys;

import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Survey;
import ish.oncourse.portal.services.IPortalService;
import org.apache.commons.lang3.StringUtils;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.util.TextStreamResponse;

import java.io.IOException;

public class StudentSurvey {

    @Inject
    private IPortalService portalService;

    @Inject
    private Request request;

    @Parameter(required = true)
    private CourseClass courseClass;

    @OnEvent(value = "getSurvey")
    public TextStreamResponse getSurvey() throws IOException {
        if (!request.isXHR())
            return null;

        Survey survey = portalService.getStudentSurveyFor(courseClass);
        if (survey == null)
            survey = portalService.createStudentSurveyFor(courseClass);

        return new TextStreamResponse("text/json", portalService.getJSONSurvey(survey).toString());

    }

    @OnEvent(value = "saveSurvey")
    public void saveSurvey() throws IOException {
        if (!request.isXHR())
            return;

        Survey survey = portalService.getStudentSurveyFor(courseClass);
        if (survey == null)
        {
            survey = portalService.createStudentSurveyFor(courseClass);
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

        survey.getObjectContext().commitChanges();
    }

}
