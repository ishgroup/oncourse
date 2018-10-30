package ish.oncourse.portal.components.surveys;

import ish.oncourse.model.Field;
import ish.oncourse.model.Survey;
import ish.oncourse.portal.services.survey.GetSurveyValue;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;

public class SurveyMultilineField {
    
    @Property
    @Parameter
    private Field field;

    @Property
    @Parameter
    private Survey survey;
    
    public String  getValue() {
        return (String) GetSurveyValue.valueOf(survey, field).get();
    }

}

