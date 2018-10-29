package ish.oncourse.portal.components.surveys;

import ish.oncourse.common.field.FieldProperty;
import ish.oncourse.model.Field;
import ish.oncourse.model.FieldHeading;
import ish.oncourse.model.Survey;
import ish.oncourse.model.auto._Field;
import ish.oncourse.model.auto._FieldHeading;
import ish.oncourse.portal.services.survey.SurveyContainer;
import ish.oncourse.portal.util.SurveyEncoder;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import static ish.oncourse.common.field.FieldProperty.*;

public class SurveyComponent {

    @Parameter
    @Property
    private SurveyContainer surveyContainer;

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
        fieldsWithoutHeading = surveyContainer.getFieldConfiguration().getFields();
        fieldsWithoutHeading.sort(Comparator.comparing(_Field::getOrder));

        headings = surveyContainer.getFieldConfiguration().getFieldHeadings();
        headings.sort(Comparator.comparing(_FieldHeading::getOrder));
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
