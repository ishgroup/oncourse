package ish.oncourse.portal.components.surveys;

import ish.oncourse.model.Field;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;

public class SurveyMultilineField {
    @Property
    @Parameter
    private Field field;

    public boolean isComment() {
        return "comment".equals(field.getProperty());
    }
}
