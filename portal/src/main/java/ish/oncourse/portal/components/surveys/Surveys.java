package ish.oncourse.portal.components.surveys;

import ish.oncourse.model.*;
import ish.oncourse.portal.services.IPortalService;
import ish.oncourse.portal.services.survey.*;
import ish.oncourse.portal.util.RequestToSurvey;
import ish.oncourse.portal.util.SurveyEncoder;
import ish.oncourse.portal.util.SurveyToJSON;
import ish.oncourse.services.persistence.ICayenneService;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.cayenne.query.SelectById;
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
    
    @Property
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
    public TextStreamResponse saveSurvey(Long enrolmentId, Long fieldConfId) throws IOException {

        if (!request.isXHR())
            return null;
        
        Student student = portalService.getContact().getStudent();
        ObjectContext context = cayenneService.newContext();
        
        Enrolment enrolment = SelectById.query(Enrolment.class, enrolmentId).selectOne(context);
        if (enrolment == null || !enrolment.getStudent().getId().equals(student.getId()) ) {
            throw new IllegalArgumentException("Enrolment id is wrong: " + enrolmentId);
        }
        
        FieldConfiguration fieldConf = SelectById.query(FieldConfiguration.class, fieldConfId).selectOne(context);
        if (fieldConf == null) {
            throw new IllegalArgumentException("FieldConfiguration not exist: " + fieldConfId);
        }
        
        List<SurveyContainer> surveyContainers =  GetSurveyContainers.valueOf(enrolment).get();
        
        if (surveyContainers == null) {
            throw new IllegalArgumentException("FieldConfiguration not available  : " + fieldConfId + " enrolment id: "+ enrolmentId);
        }
        
        SurveyContainer container = surveyContainers.stream().filter(c -> c.getFieldConfiguration().getId().equals(fieldConfId)).findAny()
                .orElseThrow(() -> new IllegalArgumentException("FieldConfiguration not available  : " + fieldConfId + " enrolment id: "+ enrolmentId));

        Survey survey = container.getSurvey();
        if (survey == null) {
            survey = context.newObject(Survey.class);
            survey.setEnrolment(enrolment);
            survey.setFieldConfiguration(fieldConf);
            survey.setCollege(enrolment.getCollege());
        }


        RequestToSurvey parser =  RequestToSurvey.valueOf(survey, request, container.getFieldConfiguration()).parse();
        if (parser.getError() != null) {
            context.rollbackChanges();
            return new TextStreamResponse("text/json",  "{\"error\": \"" +  parser.getError() + "\"}");
        }
        survey.getObjectContext().commitChanges();
        return null;
        
    }

    public boolean showDefaultSurvey() {
        return useDefaultSurvey && (enrolment.getCourseClass().getEndDate() == null || enrolment.getCourseClass().getEndDate().before(new Date()));
    }

}
