package ish.oncourse.portal.components.surveys;

import ish.oncourse.model.*;
import ish.oncourse.portal.services.survey.GetSurveyValue;
import ish.oncourse.portal.util.RequestToSurvey;
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
    
    private CustomFieldType customFieldType;

    @Property
    private Boolean hasDefaultValue;

    @Property
    private String defaultValue;

    @SetupRender
    public void beforeRender() {
        customFieldType = RequestToSurvey.getCustomFieldType(field);
        if (customFieldType == null) {
            throw new IllegalArgumentException(String.format("Field key is wrong, id: %d, property: %s", field.getId(), field.getProperty()));
        } else if (hasDefaultValue = StringUtils.trimToNull(customFieldType.getDefaultValue()) != null) {
            defaultValue = customFieldType.getDefaultValue();
        }
    }
    
    public String getValue() {
        return (String) GetSurveyValue.valueOf(survey, field, customFieldType).get();
    }

}
