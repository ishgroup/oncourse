package ish.oncourse.willow.functions.field

import ish.common.util.DisplayableExtendedEnumeration
import ish.oncourse.model.Field
import ish.oncourse.willow.model.common.Item
import ish.oncourse.willow.model.field.DataType

import static ish.oncourse.common.field.FieldProperty.*

class FieldBuilder {
    
    Field field
    Class aClass
    
    ish.oncourse.willow.model.field.Field build() {
        new ish.oncourse.willow.model.field.Field().with { f ->
            f.id = field.id.toString()
            f.name = field.name
            f.description = field.description
            f.mandatory = field.mandatory
            f.key = field.property
            f.defaultValue = field.defaultValue
            f.ordering = field.order

            
            switch (getByKey(field.property)) {
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
                case CITIZENSHIP:
                case ENGLISH_PROFICIENCY:
                case INDIGENOUS_STATUS:
                case HIGHEST_SCHOOL_LEVEL:
                case PRIOR_EDUCATION_CODE:
                case LABOUR_FORCE_STATUS:
                case DISABILITY_TYPE:
                    f.dataType = DataType.ENUM
                    f.enumType = aClass.simpleName

                    aClass.enumConstants.each { DisplayableExtendedEnumeration item ->
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
