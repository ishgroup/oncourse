package ish.oncourse.willow.functions.field

import ish.common.types.YesNoOptions
import ish.common.util.DisplayableExtendedEnumeration
import ish.oncourse.model.Field
import ish.oncourse.willow.model.common.Item
import ish.oncourse.willow.model.field.DataType

import static ish.oncourse.common.field.FieldProperty.*

class FieldBuilder<T> {
    
    Field field
    Class<T> aClass
    
    ish.oncourse.willow.model.field.Field build() {
        new ish.oncourse.willow.model.field.Field().with { f ->
            f.id = field.id?.toString()
            f.name = field.name
            f.description = field.description
            f.mandatory = field.mandatory
            f.key = field.property
            f.defaultValue = field.defaultValue
            f.ordering = field.order

            
            switch (getByKey(field.property)) {
                case SPECIAL_NEEDS:
                case DETAIL:
                    f.dataType = DataType.LONG_STRING
                    break
                case STUDENTS_COUNT:
                    f.dataType = DataType.INTEGER
                    break
                case EMAIL_ADDRESS:
                    f.dataType = DataType.EMAIL
                    break
                case POSTCODE:
                    f.dataType = DataType.POSTCODE
                    break
                case SUBURB:
                    f.dataType = DataType.SUBURB
                    break
                case HOME_PHONE_NUMBER:
                case BUSINESS_PHONE_NUMBER:
                case FAX_NUMBER:
                case MOBILE_PHONE_NUMBER:
                    f.dataType = DataType.PHONE
                    break
                case IS_MALE:
                case CITIZENSHIP:
                case ENGLISH_PROFICIENCY:
                case INDIGENOUS_STATUS:
                case HIGHEST_SCHOOL_LEVEL:
                case PRIOR_EDUCATION_CODE:
                case LABOUR_FORCE_STATUS:
                case DISABILITY_TYPE:
                    f.dataType = DataType.ENUM
                    f.enumType = aClass.simpleName
                     ((Class<DisplayableExtendedEnumeration>)aClass).enumConstants.each {
                         DisplayableExtendedEnumeration item ->
                            f.enumItems << new Item(value: item.displayName, key: item.databaseValue.toString())
                     }
                    break
                case CUSTOM_FIELD_CONTACT:
                case CUSTOM_FIELD_ENROLMENT:
                case CUSTOM_FIELD_APPLICATION:
                case CUSTOM_FIELD_WAITING_LIST:
                case CUSTOM_FIELD_ARTICLE:
                case CUSTOM_FIELD_MEMBERSHIP:
                case CUSTOM_FIELD_VOUCHER:
                    ProcessCustomFieldType processor = new ProcessCustomFieldType(field).process()
                    f.dataType = processor.dataType
                    f.defaultValue = processor.defaultValue
                    f.enumItems = processor.items
                    f.pattern = processor.pattern
                    break
                case IS_MARKETING_VIA_EMAIL_ALLOWED_PROPERTY:
                case IS_MARKETING_VIA_POST_ALLOWED_PROPERTY:
                case IS_MARKETING_VIA_SMS_ALLOWED_PROPERTY:
                    f.defaultValue = Boolean.TRUE.toString()
                    f.dataType = DataType.BOOLEAN
                    break
                case IS_STILL_AT_SCHOOL:
                    f.dataType = DataType.ENUM
                    f.enumType = YesNoOptions.class.getSimpleName()
                    YesNoOptions.class.getEnumConstants().each { DisplayableExtendedEnumeration item ->
                        f.enumItems  << new Item(value: item.displayName, key: item.databaseValue.toString())
                    }
                    break
                default:
                    f.dataType = DataType.fromValue(aClass.simpleName.toUpperCase())
                    break
            }
           
            f
        }
    }
}
