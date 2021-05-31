package ish.oncourse.willow.functions.field

import groovy.json.JsonSlurper
import groovy.transform.CompileStatic
import ish.oncourse.common.field.FieldProperty
import ish.oncourse.model.College
import ish.oncourse.model.CustomFieldType
import ish.oncourse.model.Field
import ish.oncourse.willow.model.common.Item
import ish.oncourse.willow.model.field.DataType
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.ObjectSelect
import org.apache.commons.lang3.StringUtils

@CompileStatic
class ProcessCustomFieldType {

    private static final String OTHER_CHOICE = '*'
    private static final String SPLITTER = ';'

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
        CustomFieldType customField = selectCustomFieldType()
        String defaultValue = StringUtils.trimToNull(customField.getDefaultValue())

        if (customField.dataType) {
            switch (customField.dataType) {
                case ish.common.types.DataType.TEXT:
                    this.dataType = DataType.STRING
                    this.defaultValue = defaultValue
                    break
                case ish.common.types.DataType.LONG_TEXT:
                    this.dataType = DataType.LONG_STRING
                    break
                case ish.common.types.DataType.DATE:
                    this.dataType = DataType.DATE
                    break
                case ish.common.types.DataType.DATE_TIME:
                    this.dataType = DataType.DATETIME
                    break
                case ish.common.types.DataType.BOOLEAN:
                    this.dataType = DataType.BOOLEAN
                    break
                case ish.common.types.DataType.MONEY:
                    this.dataType = DataType.MONEY
                    break
                case ish.common.types.DataType.URL:
                    this.dataType = DataType.URL
                    break
                case ish.common.types.DataType.EMAIL:
                    this.dataType = DataType.EMAIL
                    break
                case ish.common.types.DataType.LIST:

                    List<CustomFieldItem> choices = new JsonSlurper().parseText(defaultValue).collect { it as CustomFieldItem }

                    if (choices.find {it.value == OTHER_CHOICE}) {
                        this.dataType = DataType.CHOICE
                    } else {
                        this.dataType = DataType.ENUM
                    }

                    choices.findAll { it.value != OTHER_CHOICE }.each {  items << new Item(value: it.value, key: it.value.trim()) }

                    break
                case ish.common.types.DataType.MAP:
                    this.dataType = DataType.ENUM
                    List<CustomFieldItem> choices = new JsonSlurper().parseText(defaultValue).collect { it as CustomFieldItem }
                    choices.each {  items << new Item(value: "$it.label ($it.value)".toString(), key: it.value.trim()) }
                    break
                default:
                    throw new IllegalArgumentException("Unsupported custom field data type: $customField.dataType," +
                            " college id: $customField.college.id," +
                            " field key: $customField.key")

            }
        } else if (defaultValue) {
            String[] choices = defaultValue.split(SPLITTER)*.trim()
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
        FieldProperty fieldProperty = FieldProperty.getByKey(fieldKey)
        String customFieldKey = fieldKey.split("\\.")[2]
        
        return ((ObjectSelect.query(CustomFieldType)
                .where(CustomFieldType.ENTITY_NAME.eq(fieldProperty.contextType.identifier.capitalize())) 
                & CustomFieldType.KEY.eq(customFieldKey)) 
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

    private class CustomFieldItem {
        String value
        String label

    }
}
