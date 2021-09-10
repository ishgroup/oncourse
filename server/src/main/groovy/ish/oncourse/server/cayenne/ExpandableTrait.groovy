/*
 * Copyright ish group pty ltd 2020.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.cayenne

import ish.common.types.DataType
import ish.oncourse.API
import ish.util.LocalDateUtils
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.ObjectSelect

import javax.annotation.Nullable
import static ish.common.types.DataType.BOOLEAN
import static ish.common.types.DataType.DATE
import static ish.common.types.DataType.DATE_TIME
import static ish.common.types.DataType.EMAIL
import static ish.common.types.DataType.ENTITY
import static ish.common.types.DataType.FILE
import static ish.common.types.DataType.LIST
import static ish.common.types.DataType.LONG_TEXT
import static ish.common.types.DataType.MAP
import static ish.common.types.DataType.MESSAGE_TEMPLATE
import static ish.common.types.DataType.MONEY
import static ish.common.types.DataType.PATTERN_TEXT
import static ish.common.types.DataType.TEXT

trait ExpandableTrait {

    abstract List<? extends CustomField> getCustomFields()

    abstract Long getId()

    abstract void setModifiedOn(Date date)

    abstract ObjectContext getContext()

    abstract Class<? extends CustomField> getCustomFieldClass()

    @Deprecated
    String customField(String key) {
        return customFields.find {it.customFieldType.key == key || it.customFieldType.name == key }?.value
    }

    /**
     * This implements getters for custom fields, for example like
     * contact.passportNumber
     *
     * @param key
     * @return
     */
    Object propertyMissing(String key) {
        return getCustomFieldValue(key)
    }

    /**
     * set Custom Field value
     * contact.passportNumber = '123'
     * @throws MissingPropertyException
     */
    void propertyMissing(String key, Object value) {
        CustomField customField = customFields.find { it.customFieldType.key == key }
        CustomFieldType type
        if (customField) {
            type = customField.customFieldType
        } else {
            type = ObjectSelect.query(CustomFieldType).where(CustomFieldType.KEY.eq(key)).selectFirst(context)
            if (type) {
                customField = context.newObject(customFieldClass)
                customField.relatedObject = this
                customField.customFieldType = type
            } else {
                throw new MissingPropertyException("The record attribute: $key does not exist. If you are attempting to access a custom field, check the keycode of that field.")
            }
        }

        switch (type.dataType) {
            case LIST:
            case MAP:
            case EMAIL:
            case DataType.URL:
            case MONEY:
            case TEXT:
            case LONG_TEXT:
            case PATTERN_TEXT:
                customField.value = value
                break
            case DATE:
                customField.value = LocalDateUtils.valueToString(LocalDateUtils.stringToValue(value.toString()))
                break
            case DATE_TIME:
                customField.value = LocalDateUtils.timeValueToString(LocalDateUtils.stringToTimeValue(value.toString()))
                break
            case BOOLEAN:
                customField.value = Boolean.valueOf(value.toString())
                break
            case ENTITY:
            case FILE:
            case MESSAGE_TEMPLATE:
                throw new IllegalArgumentException("$ENTITY.displayName type is not supported for automation options")
        }
    }

    /**
     * @param key custom field unique key
     * @return custom field value
     * @throws MissingPropertyException
     */
    @Nullable @API
    Object getCustomFieldValue(String key) {
        CustomField customField = customFields.find {it.customFieldType.key == key}
        if (customField) {
            return customField.objectValue
        }

        CustomFieldType type = ObjectSelect.query(CustomFieldType).where(CustomFieldType.KEY.eq(key)).selectFirst(context)
        if (type) {
            return null
        } else {
            throw new MissingPropertyException("The record attribute: $key does not exist. If you are attempting to access a custom field, check the keycode of that field.")
        }
    }

    /**
     * @return all custom field values
     */
    @API
    Map<String, Object> getAllCustomFields() {
        return customFields.collectEntries {[(it.customFieldType.key) : it.objectValue] }
    }

}
