package ish.oncourse.portal.services.survey

import ish.common.types.DeliverySchedule
import ish.oncourse.model.CourseClass
import ish.oncourse.model.Enrolment
import ish.oncourse.model.FieldConfiguration
import ish.oncourse.model.SurveyFieldConfiguration

import static ish.common.types.DeliverySchedule.AT_COMPLETION
import static ish.common.types.DeliverySchedule.MIDWAY
import static ish.common.types.DeliverySchedule.ON_ENROL
import static ish.common.types.DeliverySchedule.ON_START

class GetSurveyContainers {
    private Enrolment enrolment

    List<SurveyContainer> get() {
        List<DeliverySchedule> cases
        Date now = new Date()
        List<FieldConfiguration> forms = enrolment.courseClass.course.fieldConfigurationScheme.surveyFieldConfigurations

        if (forms.empty) {
            return new ArrayList<>()
        }
        Date start = enrolment.courseClass.startDate
        Date end = enrolment.courseClass.endDate

        if (enrolment.courseClass.isDistantLearningCourse
                || start == null
                || end == null) {
            cases = [ON_ENROL, ON_START, MIDWAY, AT_COMPLETION]
        } else {

            Date midway = getClassMidway(enrolment.courseClass)
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

    private static Date getClassMidway(CourseClass courseClass) {
        if (courseClass.sessions.empty) {
            int sessionsCount =  courseClass.sessions.size()
            return courseClass.sessions[(sessionsCount/2).intValue() - 1].endDatetime
        } else {
            return new Date(((courseClass.endDate.time + courseClass.startDate.time)/2).longValue())
        }

    }

    static GetSurveyContainers valueOf(Enrolment enrolment) {
        GetSurveyContainers getter = new GetSurveyContainers()
        getter.enrolment = enrolment
        return getter
    }
}
