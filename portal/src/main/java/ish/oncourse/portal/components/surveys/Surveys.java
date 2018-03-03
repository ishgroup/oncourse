package ish.oncourse.portal.components.surveys;

import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Student;
import ish.oncourse.model.Survey;
import ish.oncourse.portal.services.IPortalService;
import ish.oncourse.portal.util.RequestToSurvey;
import ish.oncourse.portal.util.SurveyToJSON;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.survey.CreateSurvey;
import ish.oncourse.services.survey.GetAverageSurvey;
import ish.oncourse.services.survey.GetSurveyForStudent;
import org.apache.cayenne.ObjectContext;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.util.TextStreamResponse;

import java.io.IOException;

public class Surveys {

    @Inject
    @Property
    private IPortalService portalService;

    @Inject
    private ICayenneService cayenneService;

    @Inject
    private Request request;

    @Parameter(required = true)
	@Property
    private CourseClass courseClass;

    @Parameter(required = true)
    @Property
    private boolean isTutor;

    @OnEvent(value = "getSurvey")
    public TextStreamResponse getSurvey(Long courseClassId) throws IOException {
        if (!request.isXHR())
            return null;

        Survey survey;
        //we should check at fist that the current contact is a student and try to load survey for student
        boolean isTutor = portalService.getContact().getTutor() != null && portalService.isTutorFor(courseClass);
        if (isTutor) {
            survey = GetAverageSurvey.valueOf(cayenneService.newNonReplicatingContext(), courseClass).get();
        } else {
            survey = createIfNotExist(cayenneService.newContext(),
                                        portalService.getCourseClassBy(courseClassId),
                                        portalService.getContact().getStudent());
        }

        return new TextStreamResponse("text/json", SurveyToJSON.valueOf(survey, isTutor).get().toString());
    }

    @OnEvent(value = "saveSurvey")
    public void saveSurvey(Long courseClassId) throws IOException {
        if (!request.isXHR())
            return;

        Survey survey = createIfNotExist(cayenneService.newContext(),
                                portalService.getCourseClassBy(courseClassId),
                                portalService.getContact().getStudent());

        RequestToSurvey.valueOf(survey, request).parse();
        survey.getObjectContext().commitChanges();
    }

    private Survey createIfNotExist(ObjectContext context, CourseClass courseClass, Student student) {
        Survey survey = GetSurveyForStudent.valueOf(student, courseClass).get();
        if (survey == null) {
            survey = CreateSurvey.valueOf(context, courseClass, student).create();
        }
        return survey;
    }

}
