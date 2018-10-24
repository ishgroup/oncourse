package ish.oncourse.portal.components.surveys;

import ish.oncourse.model.Field;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;


public class SurveyTextLineField {

    @Property
    @Parameter
    private Field field;

    @SetupRender
    public void beforeRender() {
    }

    public String getValue() {
        return "";
    }

    public String getName() {
        return field.getName();
    }

    public String getProperty() {
        return field.getProperty();
    }
}
