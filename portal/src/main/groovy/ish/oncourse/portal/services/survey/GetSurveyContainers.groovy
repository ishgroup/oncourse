package ish.oncourse.portal.services.survey

import ish.oncourse.model.Enrolment
import ish.oncourse.model.SurveyFieldConfiguration

class GetSurveyContainers {
    private Enrolment enrolment

    List<SurveyContainer> get() {
        return GetAvailableSurveyForms.valueOf(enrolment.courseClass).get().collect { f->
                new SurveyContainer(fieldConfiguration:(f as SurveyFieldConfiguration) , survey: enrolment.surveys.find {s -> f == s.fieldConfiguration} )
            }.sort { c -> c.fieldConfiguration.deliverySchedule.databaseValue }
    }

    static GetSurveyContainers valueOf(Enrolment enrolment) {
        GetSurveyContainers getter = new GetSurveyContainers()
        getter.enrolment = enrolment
        return getter
    }
}
