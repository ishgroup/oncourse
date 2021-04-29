package ish.oncourse.portal.util;

import ish.oncourse.common.field.FieldProperty;
import ish.oncourse.model.*;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.commons.lang3.StringUtils;
import org.apache.tapestry5.services.Request;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static ish.oncourse.common.field.FieldProperty.COMMENT;
import static ish.oncourse.common.field.FieldProperty.CUSTOM_FIELD_SURVEY;

public class RequestToSurvey {

    private Survey survey;
    private Request src;
    private String error = null;
    private FieldConfiguration fieldConfiguration;

    private RequestToSurvey() {}

    public static RequestToSurvey valueOf(Survey survey, Request request, FieldConfiguration fieldConfiguration) {
        RequestToSurvey obj = new RequestToSurvey();
        obj.survey = survey;
        obj.src = request; 
        obj.fieldConfiguration = fieldConfiguration;
        return obj;
    }

    public RequestToSurvey parse() {

        for (Field field : fieldConfiguration.getFields()) {
            FieldProperty property = FieldProperty.getByKey(field.getProperty());
            String value;
            switch (property) {
                case COMMENT:
                    value = StringUtils.trimToNull(src.getParameter(field.getProperty()));
                    survey.setComment(value);
                    if (field.getMandatory() && value == null) {
                        recordFieldError(field);
                        return this;
                    }
                    break;
                case CUSTOM_FIELD_SURVEY:
                    CustomFieldType customFieldType = getCustomFieldType(field);
                    if (customFieldType == null) {
                        throw new IllegalArgumentException("Unsupported survey field: " + field.getProperty() );
                    }
                             
                    value = StringUtils.trimToNull(src.getParameter(field.getProperty()));
                    
                    SurveyCustomField customField = survey.getCustomFields().stream()
                            .filter(cf -> cf.getCustomFieldType().getId().equals(customFieldType.getId()))
                            .findAny().orElse(null);
                    
                    if (value != null) {
                        if (customField == null) {
                            customField = survey.getObjectContext().newObject(SurveyCustomField.class);
                            customField.setRelatedObject(survey);
                            customField.setCustomFieldType(customFieldType);
                            customField.setCollege(survey.getCollege());
                        }
                        customField.setValue(value);  
                                
                    } else if (field.getMandatory()) {
                        recordFieldError(field);
                        return this;
                    } else if (customField != null) {
                        survey.getObjectContext().deleteObject(customField);
                    }
                    
                    break;
                case VENUE_SCORE:
                    if (setScoreValue(field, survey::setVenueScore)) {
                        break;
                    }
                    return this;
                case TUTOR_SCORE:
                    if (setScoreValue(field, survey::setTutorScore)) {
                        break;
                    }
                    return this;
                case COURSE_SCORE:
                    if (setScoreValue(field, survey::setCourseScore)) {
                        break;
                    }
                    return this;
                case NET_PROMOTER_SCORE:
                    if (setScoreValue(field, survey::setNetPromoterScore)) {
                        break;
                    }
                    return this;
                default:
                    throw new IllegalArgumentException("Unsupported survey field: " + field.getProperty() );
                    
            }
            
        }

        return this;
    }

    public String getError() {
        return error;
    }

    private boolean setScoreValue(Field field, Consumer<Integer> setter) {
        String value = StringUtils.trimToNull(src.getParameter(field.getProperty()));
        if (value != null &&  StringUtils.isNumeric(value) && Integer.valueOf(value) > 0) {
            setter.accept(Integer.valueOf(value));
        } else if (field.getMandatory()) {
            recordFieldError(field);
            return false;
        }
        return true;
    }
    
    private void recordFieldError(Field field) {
        error = String.format("'%s' is required", field.getName());
    }
    
    public static CustomFieldType getCustomFieldType(Field field) {
        return ObjectSelect.query(CustomFieldType.class)
                .where(CustomFieldType.KEY.eq(field.getProperty().split("\\.")[2]))
                .and(CustomFieldType.COLLEGE.eq(field.getCollege()))
                .and(CustomFieldType.ENTITY_NAME.eq(Survey.class.getSimpleName()))
                .selectOne(field.getObjectContext());
    }
}
