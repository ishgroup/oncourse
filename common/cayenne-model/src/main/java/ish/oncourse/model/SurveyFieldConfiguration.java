package ish.oncourse.model;

import ish.common.types.FieldConfigurationType;
import ish.oncourse.model.auto._SurveyFieldConfiguration;

public class SurveyFieldConfiguration extends _SurveyFieldConfiguration {

    private static final long serialVersionUID = 1L;

    @Override
    public FieldConfigurationType getType() {
        return FieldConfigurationType.SURVEY;
    }
}
