package ish.oncourse.portal.services.survey

import ish.oncourse.model.Enrolment
import ish.oncourse.model.FieldConfiguration
import ish.oncourse.model.SurveyFieldConfiguration

class GetSurveyContainers {
    private Enrolment enrolment

    List<SurveyContainer> get() {
        List<FieldConfiguration> configurations = GetAvailableSurveyForms.valueOf(enrolment.courseClass).get()
        if (configurations) {
            return configurations.collect { f->
                new SurveyContainer(fieldConfiguration:(f as SurveyFieldConfiguration) , survey: enrolment.surveys.find {s -> f == s.fieldConfiguration} )
            }.sort { c -> c.fieldConfiguration.deliverySchedule.databaseValue }
        } else {
            return null
        }
        
    }

    static GetSurveyContainers valueOf(Enrolment enrolment) {
        GetSurveyContainers getter = new GetSurveyContainers()
        getter.enrolment = enrolment
        return getter
    }
}
