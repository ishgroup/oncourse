package ish.oncourse.willow.functions.field

import ish.common.util.DisplayableExtendedEnumeration
import ish.oncourse.common.field.FieldProperty
import ish.oncourse.model.Field
import ish.oncourse.willow.model.common.Item
import ish.oncourse.willow.model.field.DataType

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

            if (FieldProperty.EMAIL_ADDRESS.key == field.property) {
                f.dataType = DataType.EMAIL
            } else if (aClass.enum) {
                f.dataType = DataType.ENUM
                f.enumType = aClass.simpleName

                aClass.enumConstants.each { DisplayableExtendedEnumeration item ->
                    f.enumItems  << new Item(value: item.displayName, key: item.databaseValue as Integer)
                }
            } else {
                f.dataType = DataType.fromValue(aClass.simpleName.toLowerCase())
            }

            f
        }
    }
}
