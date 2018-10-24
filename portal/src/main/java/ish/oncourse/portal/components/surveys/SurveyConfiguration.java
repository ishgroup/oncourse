package ish.oncourse.portal.components.surveys;

import ish.oncourse.common.field.FieldProperty;
import ish.oncourse.model.FieldConfiguration;
import ish.oncourse.model.Field;
import ish.oncourse.model.FieldHeading;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;

import java.util.Arrays;
import java.util.List;

import static ish.oncourse.common.field.FieldProperty.*;
import static ish.oncourse.common.field.FieldProperty.CUSTOM_FIELD_SURVEY;

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

    private FieldProperty[] scoreProps = {NET_PROMOTER_SCORE, COURSE_SCORE, VENUE_SCORE, TUTOR_SCORE};

    private FieldProperty[] textLineProps = {CUSTOM_FIELD_SURVEY};

    private FieldProperty[] multilineProps = {COMMENT};

    @SetupRender
    public void beginRender() {
        fieldsWithoutHeading = configuration.getFields();
        fieldsWithoutHeading.sort((f1, f2) -> f1.getOrder().compareTo(f2.getOrder()));

        headings = configuration.getFieldHeadings();
        headings.sort((h1, h2) -> h1.getOrder().compareTo(h2.getOrder()));
    }

    public boolean isScoreField() {
        return isAnyEquals(scoreProps, field);
    }

    public boolean isTextLineField() {
        return isAnyStartsWith(textLineProps, field);
    }

    public boolean isMultilineField() {
        return isAnyEquals(multilineProps, field);
    }

    private boolean isAnyEquals(FieldProperty[] props, Field f) {
        if (Arrays.asList(props).stream().anyMatch(p -> p.getKey().equals(f.getProperty()))) {
            return true;
        }
        return false;
    }

    private boolean isAnyStartsWith(FieldProperty[] props, Field f) {
        if (Arrays.asList(props).stream().anyMatch(p ->f.getProperty().startsWith(p.getKey()))) {
            return true;
        }
        return false;
    }
}
