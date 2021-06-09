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

import ish.oncourse.API
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.ObjectSelect

import javax.annotation.Nullable

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
    String propertyMissing(String key) {
        return getCustomFieldValue(key)
    }

    /**
     * set Custom Field value
     * contact.passportNumber = '123'
     * @throws MissingPropertyException
     */
    void propertyMissing(String key, String value) {
        CustomField customField = customFields.find {it.customFieldType.key == key}
        if (customField) {
            customField.value = value
            return
        }

        CustomFieldType type = ObjectSelect.query(CustomFieldType).where(CustomFieldType.KEY.eq(key)).selectFirst(context)
        if (type) {
            customField = context.newObject(customFieldClass)
            customField.relatedObject = this
            customField.customFieldType = type
            customField.value = value
        } else {
            throw new MissingPropertyException("The record attribute: $key does not exist. If you are attempting to access a custom field, check the keycode of that field.")
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
