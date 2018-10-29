package ish.oncourse.survey;

import ish.common.types.DeliverySchedule;
import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Enrolment;
import ish.oncourse.model.Survey;
import ish.oncourse.model.SurveyFieldConfiguration;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.ObjectSelect;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class CreateOrGetEnrolmentSurveysForDate {

    private ObjectContext context;
    private Enrolment enrolment;
    private Date date;

    private CreateOrGetEnrolmentSurveysForDate() {}

    public static CreateOrGetEnrolmentSurveysForDate valueOf(ObjectContext context, Enrolment enrolment, Date date) {
        CreateOrGetEnrolmentSurveysForDate obj = new CreateOrGetEnrolmentSurveysForDate();
        obj.context = context;
        obj.enrolment = context.localObject(enrolment);
        obj.date = date;
        return obj;
    }

    public List<Survey> get() {
        List<Survey> enrolmentSurveys = getEnrolmentSurveys(context, enrolment);
        List<SurveyFieldConfiguration> enrolmentConfigurations = getEnrolmentConfigurations(enrolment);

        CourseClass courseClass = enrolment.getCourseClass();

        enrolmentConfigurations.forEach(c -> {
            switch (c.getDeliverySchedule()) {
                case ON_ENROL: createIfNotExist(context, enrolmentSurveys, isDateOnEnrol(courseClass, date), c);
                break;
                case ON_START: createIfNotExist(context, enrolmentSurveys, isDateOnStart(courseClass, date), c);
                break;
                case AT_COMPLETION: createIfNotExist(context, enrolmentSurveys, isDateAtCompletion(courseClass, date), c);
                break;
            }
        });

        enrolmentSurveys.sort((s1, s2) -> {
            SurveyFieldConfiguration f1 = (SurveyFieldConfiguration) s1.getFieldConfiguration();
            SurveyFieldConfiguration f2 = (SurveyFieldConfiguration) s2.getFieldConfiguration();
            return f1.getDeliverySchedule().getDatabaseValue().compareTo(f2.getDeliverySchedule().getDatabaseValue());
        });
        return enrolmentSurveys;
    }

    private boolean isDateOnEnrol(CourseClass courseClass, Date date) {
        return courseClass.getStartDate().before(date);
    }

    private boolean isDateOnStart(CourseClass courseClass, Date date) {
        return courseClass.getStartDate().after(date) && courseClass.getEndDate().before(date);
    }

    private boolean isDateAtCompletion(CourseClass courseClass, Date date) {
        return  courseClass.getEndDate().after(date);
    }

    private List<Survey> getEnrolmentSurveys(ObjectContext context, Enrolment enrolment) {
        return ObjectSelect.query(Survey.class)
                .where(Survey.ENROLMENT.eq(enrolment))
                .select(context);
    }

    private List<SurveyFieldConfiguration> getEnrolmentConfigurations(Enrolment enrolment) {
        List<SurveyFieldConfiguration> configurations = enrolment.getCourseClass().getCourse().getFieldConfigurationScheme().getSurveyFieldConfigurations().stream().map(c -> (SurveyFieldConfiguration) c).collect(Collectors.toList());
        configurations.sort((c1, c2) -> {
            SurveyFieldConfiguration s1 = c1;
            SurveyFieldConfiguration s2 = c2;
            return s1.getDeliverySchedule().getDatabaseValue().compareTo(s2.getDeliverySchedule().getDatabaseValue());
        });
        return configurations;
    }

    private void createIfNotExist(ObjectContext context, List<Survey> surveys, boolean dateExpr, SurveyFieldConfiguration conf) {
        if (dateExpr) {
            if (!surveysContainDeliveryType(surveys, conf.getDeliverySchedule())) {
                Survey s = context.newObject(Survey.class);
                s.setCollege(enrolment.getCollege());
                s.setEnrolment(enrolment);
                s.setFieldConfiguration(conf);
            }
        }
    }

    private boolean surveysContainDeliveryType(List<Survey> surveys, DeliverySchedule deliveryType) {
        return surveys.stream().anyMatch(s -> {
            SurveyFieldConfiguration c = (SurveyFieldConfiguration) s.getFieldConfiguration();
            return deliveryType.equals(c.getDeliverySchedule());
        });
    }
}
