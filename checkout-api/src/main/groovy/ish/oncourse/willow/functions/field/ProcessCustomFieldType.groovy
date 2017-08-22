package ish.oncourse.willow.functions.field

import ish.oncourse.model.College
import ish.oncourse.model.Contact
import ish.oncourse.model.CustomFieldType
import ish.oncourse.model.Field
import ish.oncourse.willow.model.common.Item
import ish.oncourse.willow.model.field.DataType
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.ObjectSelect
import org.apache.commons.lang3.StringUtils

import static ish.oncourse.common.field.FieldProperty.CUSTOM_FIELD_CONTACT

class ProcessCustomFieldType {

    private static final String OTHER_CHOICE = '*'


    private String fieldKey
    private ObjectContext objectContext
    private College college

    private DataType dataType = DataType.STRING
    private String defaultValue = null
    private List<Item> items = new ArrayList<>()

    ProcessCustomFieldType(Field field) {
        this.fieldKey = field.property
        this.objectContext = field.objectContext
        this.college = field.college 
    }

    ProcessCustomFieldType(String fieldKey, ObjectContext objectContext, College college) {
        this.fieldKey = fieldKey
        this.objectContext = objectContext
        this.college = college
    }
    
    
    ProcessCustomFieldType process() {
        CustomFieldType type = selectCustomFieldType()
        String defaultValue = StringUtils.trimToNull(type.getDefaultValue())

        if (defaultValue) {
            String[] choices = defaultValue.split(";")
            if (choices.length > 1) {
                if (choices.contains(OTHER_CHOICE)) {
                    dataType = DataType.CHOICE
                } else {
                    dataType = DataType.ENUM
                }
                choices.findAll { it != OTHER_CHOICE }.each {  items << new Item(value: it.trim(), key: it.trim()) }
            } else {
                this.defaultValue = defaultValue
            }
        } 
        
        this

    }

    private CustomFieldType selectCustomFieldType() {
        String key = fieldKey.replace("${CUSTOM_FIELD_CONTACT.key}.", '')

        return ((ObjectSelect.query(CustomFieldType)
                .where(CustomFieldType.ENTITY_NAME.eq(Contact.simpleName)) 
                & CustomFieldType.KEY.eq(key)) 
                & CustomFieldType.COLLEGE.eq(college))
                .selectOne(objectContext)
    }

    DataType getDataType() {
        return dataType
    }

    String getDefaultValue() {
        return defaultValue
    }

    List<Item> getItems() {
        return items
    }
}
