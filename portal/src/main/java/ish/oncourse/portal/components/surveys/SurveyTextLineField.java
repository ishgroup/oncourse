package ish.oncourse.portal.components.surveys;

import ish.oncourse.model.*;
import ish.oncourse.portal.services.survey.GetSurveyValue;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;


public class SurveyTextLineField {

    @Property
    @Parameter
    private Field field;

    @Property
    @Parameter
    private Survey survey;

    @SetupRender
    public void beforeRender() {
    }
    
    public String getValue() {
        return (String) GetSurveyValue.valueOf(survey, field).get();
    }

}
