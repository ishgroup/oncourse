package ish.oncourse.portal.services.survey

import ish.oncourse.common.field.FieldProperty
import ish.oncourse.model.Field
import ish.oncourse.model.Survey

import static ish.oncourse.common.field.FieldProperty.COMMENT
import static ish.oncourse.common.field.FieldProperty.COURSE_SCORE
import static ish.oncourse.common.field.FieldProperty.CUSTOM_FIELD_SURVEY
import static ish.oncourse.common.field.FieldProperty.NET_PROMOTER_SCORE
import static ish.oncourse.common.field.FieldProperty.TUTOR_SCORE
import static ish.oncourse.common.field.FieldProperty.VENUE_SCORE

class GetSurveyValue {
    
    private static final List<FieldProperty> INTEGER_FIELDS = [NET_PROMOTER_SCORE, COURSE_SCORE, VENUE_SCORE, TUTOR_SCORE]
    
    private Survey survey
    private Field field
    
    
    
    static GetSurveyValue valueOf(Survey survey, Field field) {
        GetSurveyValue getter = new GetSurveyValue()
        getter.survey = survey
        getter.field = field
        return getter
    }
    
    Object get() {

        FieldProperty property = FieldProperty.getByKey(field.property)
        if (survey == null) {
            if (property in INTEGER_FIELDS) {
                return 0
            } else {
                return null
            }

        } else {
            switch (property) {
                case NET_PROMOTER_SCORE:
                    return survey.netPromoterScore
                case COURSE_SCORE:
                    return survey.courseScore
                case VENUE_SCORE:
                    return survey.venueScore
                case TUTOR_SCORE:
                    return survey.tutorScore
                case COMMENT:
                    return survey.comment
                case CUSTOM_FIELD_SURVEY:
                    survey.customFields.find { cf -> cf.customFieldType.name == property.key.split("\\.")[1] }.value
            }
        }
    }
}
