package ish.oncourse.portal.services.survey

import ish.oncourse.model.Survey
import ish.oncourse.model.SurveyFieldConfiguration

class SurveyContainer {

    private Survey survey

    private SurveyFieldConfiguration fieldConfiguration

     SurveyFieldConfiguration getFieldConfiguration() {
        return fieldConfiguration
    }

    void setFieldConfiguration(SurveyFieldConfiguration fieldConfiguration) {
        this.fieldConfiguration = fieldConfiguration
    }

    Survey getSurvey() {
        return survey
    }

    void setSurvey(Survey survey) {
        this.survey = survey
    }
}
