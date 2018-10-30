package ish.oncourse.portal.services.survey

import ish.common.types.DeliverySchedule
import ish.oncourse.model.Enrolment
import ish.oncourse.model.FieldConfiguration
import ish.oncourse.model.SurveyFieldConfiguration
import org.apache.cayenne.query.ObjectSelect

import static ish.common.types.DeliverySchedule.AT_COMPLETION
import static ish.common.types.DeliverySchedule.MIDWAY
import static ish.common.types.DeliverySchedule.ON_ENROL
import static ish.common.types.DeliverySchedule.ON_START

class GetSurveyContainers {
    private static final Long DEFAULT_FORM_ANGEL_ID = -2
    private Enrolment enrolment

    List<SurveyContainer> get() {
        List<DeliverySchedule> cases
        Date now = new Date()
        List<FieldConfiguration> forms = enrolment.courseClass.course.fieldConfigurationScheme.surveyFieldConfigurations

        if (forms.empty) {
            FieldConfiguration defaultForm = getDefaultForm()
            
            if (defaultForm != null) {
                forms << defaultForm
            } else {
                return null
            }
        }
        
        Date start = enrolment.courseClass.startDate
        Date end = enrolment.courseClass.endDate

        if (enrolment.courseClass.isDistantLearningCourse
                || start == null
                || end == null) {
            cases = [ON_ENROL, ON_START, MIDWAY, AT_COMPLETION]
        } else {

            Date midway = getClassMidway()
            if (start.after(now)) {
                cases = [ON_ENROL]
            } else if (midway.after(now) ) {
                cases = [ON_ENROL, ON_START]
            } else  if (end.after(now) ){
                cases = [ON_ENROL, ON_START, MIDWAY]
            } else {
                cases = [ON_ENROL, ON_START, MIDWAY, AT_COMPLETION]
            }
        }

        return forms.findAll { f -> (f as SurveyFieldConfiguration).deliverySchedule in cases }.collect { f->
                new SurveyContainer(fieldConfiguration:(f as SurveyFieldConfiguration) , survey: enrolment.surveys.find {s -> f == s.fieldConfiguration} )
            }.sort { c -> c.fieldConfiguration.deliverySchedule.databaseValue }
        

    }

    private Date getClassMidway() {
        if (enrolment.courseClass.sessions.empty) {
            int sessionsCount =  enrolment.courseClass.sessions.size()
            return enrolment.courseClass.sessions[(sessionsCount/2).intValue() - 1].endDatetime
        } else {
            return new Date(((enrolment.courseClass.endDate.time + enrolment.courseClass.startDate.time)/2).longValue())
        }

    }
    
    private FieldConfiguration getDefaultForm() {
        return ObjectSelect.query(FieldConfiguration)
                .where(FieldConfiguration.ANGEL_ID.eq(DEFAULT_FORM_ANGEL_ID))
                .and(FieldConfiguration.COLLEGE.eq(enrolment.college))
                .selectOne(enrolment.objectContext)
    }

    static GetSurveyContainers valueOf(Enrolment enrolment) {
        GetSurveyContainers getter = new GetSurveyContainers()
        getter.enrolment = enrolment
        return getter
    }
}
