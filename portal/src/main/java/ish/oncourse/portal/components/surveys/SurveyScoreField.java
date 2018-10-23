package ish.oncourse.portal.components.surveys;

import ish.oncourse.model.Field;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;

public class SurveyScoreField {

    @Property
    @Parameter
    private Field field;

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
}
