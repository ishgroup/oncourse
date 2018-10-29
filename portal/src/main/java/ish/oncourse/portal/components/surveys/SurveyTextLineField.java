package ish.oncourse.portal.components.surveys;

import ish.oncourse.model.*;
import ish.oncourse.portal.util.SurveyEncoder;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.commons.lang3.StringUtils;
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

    public boolean isCustomField() {
        return field.getProperty().startsWith("customField.");
    }

    public String getValue() {
        if (isCustomField()) {
            ObjectContext context = survey.getObjectContext();
            SurveyCustomField customField = ObjectSelect.query(SurveyCustomField.class)
                    .where(SurveyCustomField.RELATED_OBJECT.eq(survey).andExp(SurveyCustomField.CUSTOM_FIELD_TYPE.dot(CustomFieldType.KEY).eq(field.getProperty())))
                    .selectFirst(context);
            if (customField != null) {
                return customField.getValue();
            }
        }
        return StringUtils.EMPTY;
    }

    public String getName() {
        return field.getName();
    }

    public String getProperty() {
        return field.getProperty();
    }
    public SurveyEncoder getSurveyEncoder() {
        return SurveyEncoder.valueOf();
    }

}
