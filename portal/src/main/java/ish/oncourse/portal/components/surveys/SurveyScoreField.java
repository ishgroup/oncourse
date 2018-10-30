package ish.oncourse.portal.components.surveys;

import ish.oncourse.model.Field;
import ish.oncourse.model.Survey;
import ish.oncourse.portal.services.survey.GetSurveyValue;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;

public class SurveyScoreField {

    @Property
    @Parameter
    private Field field;

    @Property
    @Parameter
    private Survey survey;

    public boolean isNetPromoterScore() {
        return "netPromoterScore".equals(field.getProperty());
    }

    public boolean isCourseScore() {
        return "courseScore".equals(field.getProperty());
    }

    public boolean isVenueScore() {
        return "venueScore".equals(field.getProperty());
    }

    public boolean isTutorScore() {
        return "tutorScore".equals(field.getProperty());
    }

    public int getValue() {
        return (int) GetSurveyValue.valueOf(survey, field).get();
    }
    
}
