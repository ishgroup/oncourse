package ish.oncourse.portal.components.surveys;

import ish.oncourse.model.FieldConfiguration;
import ish.oncourse.model.Field;
import ish.oncourse.model.FieldHeading;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;

import java.util.List;

public class SurveyConfiguration {

    @Property
    @Parameter
    private FieldConfiguration configuration;

    @Property
    private List<Field> fieldsWithoutHeading;

    @Property
    private List<FieldHeading> headings;

    @Property
    private Field field;

    @Property
    private FieldHeading heading;

    @SetupRender
    public void beginRender() {
        fieldsWithoutHeading = configuration.getFields();
        fieldsWithoutHeading.sort((f1, f2) -> f1.getOrder().compareTo(f2.getOrder()));

        headings = configuration.getFieldHeadings();
        headings.sort((h1, h2) -> h1.getOrder().compareTo(h2.getOrder()));
    }

    public boolean isScoreField() {
        String prop = field.getProperty();
        if ("netPromoterScore".equals(prop) || "courseScore".equals(prop) || "venueScore".equals(prop) || "tutorScore".equals(prop)){
            return true;
        }
        return false;
    }

    public boolean isTextLineField() {
        String prop = field.getProperty();
        if (prop.startsWith("customField.")) {
            return true;
        }
        return false;
    }

    public boolean isMultilineField() {
        String prop = field.getProperty();
        if ("comment".equals(prop)) {
            return true;
        }
        return false;
    }
}
