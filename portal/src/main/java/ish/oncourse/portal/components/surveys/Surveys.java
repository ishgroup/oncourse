package ish.oncourse.portal.components.surveys;

import ish.oncourse.model.*;
import ish.oncourse.portal.services.IPortalService;
import ish.oncourse.portal.services.survey.*;
import ish.oncourse.portal.util.RequestToSurvey;
import ish.oncourse.portal.util.SurveyEncoder;
import ish.oncourse.portal.util.SurveyToJSON;
import ish.oncourse.services.persistence.ICayenneService;
import org.apache.cayenne.ObjectContext;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.util.TextStreamResponse;

import java.io.IOException;
import java.util.Date;
import java.util.List;

public class Surveys {

    @Inject
    private Request request;

    @Inject
    private ICayenneService cayenneService;
    
    @Property
    private List<SurveyContainer> surveyContainers;

    @Property
    private SurveyContainer surveyContainer;

    @Property
    private Boolean useDefaultSurvey = false;
    
    @Property
    private Survey defaultSurvey = null;

    @Property
    private Enrolment enrolment;

    @Parameter
    private CourseClass courseClass;
    
    @Inject
    @Property
    private IPortalService portalService;

    @Property
    private Survey averageSurvey;

    @Property
    private Boolean isTutor;

    @Property
    @Parameter
    private Boolean list;
    
    @SetupRender
    public void beforeRender() {
        isTutor = portalService.getContact().getTutor() != null && portalService.isTutorFor(courseClass);
        if (isTutor) {
            averageSurvey = GetAverageSurvey.valueOf(cayenneService.newNonReplicatingContext(), courseClass).get();
        } else  {
            enrolment = portalService.getEnrolmentBy(portalService.getContact().getStudent(), courseClass);

            surveyContainers = GetSurveyContainers.valueOf(enrolment).get();

            if (surveyContainers == null) {
                useDefaultSurvey = true;

                if (!enrolment.getSurveys().isEmpty()) {
                    defaultSurvey = enrolment.getSurveys().get(0);
                }
            }
        }
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
            //survey = CreateSurvey.valueOf(context, courseClass, student).create();
        }
        return survey;
    }

    public boolean showDefaultSurvey() {
        return useDefaultSurvey && (enrolment.getCourseClass().getEndDate() == null || enrolment.getCourseClass().getEndDate().before(new Date()));
    }

}
