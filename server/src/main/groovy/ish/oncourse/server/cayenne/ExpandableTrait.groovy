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
import ish.math.Money
import ish.oncourse.API
import ish.oncourse.server.api.v1.function.CustomFieldFunctions
import ish.util.LocalDateUtils
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.ObjectSelect

import javax.annotation.Nullable
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeParseException

import static ish.common.types.DataType.*

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
        CustomField customField = customFields.find {
            it.customFieldType.key.toLowerCase().equals(key.toLowerCase())
        }

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

        if (value == null) {
            customField.value = value
            return
        }

        switch (type.dataType) {
            case LIST:
            case MAP:
            case EMAIL:
            case DataType.URL:
            case TEXT:
            case LONG_TEXT:
                if (value instanceof String) {
                    customField.value = value
                } else {
                    throw new IllegalArgumentException(value.class.simpleName + " is not supported for $key field")
                }
                break
            case PATTERN_TEXT:
                if (value instanceof String) {
                    String pattern = customField.customFieldType.pattern
                    if (value ==~ ~pattern) {
                        customField.value = value
                    } else {
                        throw new IllegalArgumentException("`$value` does't match custom field pattern `$pattern`")
                    }
                } else {
                    throw new IllegalArgumentException(value.class.simpleName + " is not supported for $key field")
                }
                break
            case DATE:
                if (value instanceof LocalDate) {
                    customField.value = LocalDateUtils.valueToString(value)
                } else if (value instanceof String) {
                    try {
                        LocalDate dateValue = LocalDateUtils.stringToValue(value)
                        customField.value = LocalDateUtils.valueToString(dateValue)
                    } catch (DateTimeParseException e) {
                        throw new IllegalArgumentException("Cannot parse value `$value` for $key field")
                    }
                } else {
                    throw new IllegalArgumentException(value.class.simpleName + " is not supported for $key field")
                }
                break
            case DATE_TIME:
                if (value instanceof LocalDateTime) {
                    customField.value = LocalDateUtils.timeValueToString(value)
                } else if (value instanceof String) {
                    try {
                        LocalDateTime dateTimeValue = LocalDateUtils.stringToTimeValue(value)
                        customField.value = LocalDateUtils.timeValueToString(dateTimeValue)
                    } catch (DateTimeParseException e) {
                        throw new IllegalArgumentException("Cannot parse value `$value` for $key field")
                    }
                } else {
                    throw new IllegalArgumentException(value.class.simpleName + " is not supported for $key field")
                }
                break
            case MONEY:
                if (value instanceof String) {
                    try {
                        customField.value = value.toString()
                    } catch (NumberFormatException e) {
                        throw new NumberFormatException(e.message)
                    }
                } else if (value instanceof Money) {
                    customField.value = (value as Money).toPlainString()
                } else {
                    throw new IllegalArgumentException(value.class.simpleName + " is not supported for $key field")
                }
                break
            case BOOLEAN:
                if (value instanceof Boolean) {
                    customField.value = value
                } else if (value instanceof String) {
                    if (value.equals("true") || value.equals("false")) {
                        customField.value = value == "true" ?: "false"
                    } else {
                        throw new IllegalArgumentException("$value cannot be the value for $key field")
                    }
                } else {
                    throw new IllegalArgumentException(value.class.simpleName + " is not supported for $key field")
                }
                break
            case PORTAL_SUBDOMAIN:
                 if (value instanceof String && !value.empty) {
                     CustomFieldFunctions.validateSubDomain(context, value, customField.id)
                     customField.value = value
                } else {
                    throw new IllegalArgumentException(value.class.simpleName + " is not supported for $key field")
                }
                break
            default:
                throw new IllegalArgumentException("$type.dataType type is not supported")
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
