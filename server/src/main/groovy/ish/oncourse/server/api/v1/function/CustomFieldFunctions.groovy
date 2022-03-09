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

package ish.oncourse.server.api.v1.function

import ish.common.types.DataType
import ish.oncourse.server.api.validation.EntityValidator
import ish.oncourse.server.cayenne.CustomField
import ish.oncourse.server.cayenne.CustomFieldType
import ish.oncourse.server.cayenne.ExpandableTrait
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.ObjectSelect
import static org.apache.commons.lang3.StringUtils.isBlank
import static org.apache.commons.lang3.StringUtils.trimToNull

class CustomFieldFunctions {

    static void updateCustomFields(ObjectContext context, ExpandableTrait dbObject, Map<String, String> customFields, Class<? extends CustomField> relationClass) {
        customFields = customFields == null ? new HashMap<String, String>() : customFields
        List<String> customFieldsToSave = customFields.keySet().toList() ?: [] as List<String>
        List<CustomField> dbCustomFields = dbObject.customFields as List<CustomField>

        context.deleteObjects(dbCustomFields.findAll { !customFieldsToSave.contains(it.customFieldType.key) })
        customFields.each { k, v ->
            CustomField cf = dbCustomFields.find { it.customFieldType.key == k }

            if (cf) {
                cf.value = trimToNull(v)
            } else if (v) {
                cf = context.newObject(relationClass)
                cf.relatedObject = dbObject
                cf.customFieldType = getCustomFieldType(context, dbObject.class.simpleName, k)
                cf.value = trimToNull(v)
            }
        }
        dbObject.modifiedOn = new Date()
    }

    static void addCustomFieldWithoutCommit(String key, String value, ExpandableTrait entity, ObjectContext context) {
        CustomField cf = entity.customFields.find { it.customFieldType.key == key }

        if (cf) {
            cf.value = trimToNull(value)
        } else if (value) {
            cf = context.newObject(entity.getCustomFieldClass())
            cf.relatedObject = entity
            cf.customFieldType = getOrCreateCustomFieldType(context, entity, key)
            cf.value = trimToNull(value)
        }
    }

    static CustomFieldType getOrCreateCustomFieldType(ObjectContext objectContext, ExpandableTrait entity, String customFieldKey) {
        CustomFieldType cft = getCustomFieldType(objectContext, entity.class.simpleName, customFieldKey)
        if (cft) {
            return cft
        } else {
            return createCustomFieldType(objectContext, entity, customFieldKey)
        }
    }

    static CustomFieldType createCustomFieldType(ObjectContext objectContext, ExpandableTrait entity, String customFieldKey) {
        CustomFieldType cft = objectContext.newObject(CustomFieldType)
        cft.entityIdentifier = entity.class.simpleName
        cft.name = customFieldKey
        cft.key = customFieldKey
        cft.isMandatory = false
        cft.dataType = DataType.TEXT
        cft.sortOrder = ObjectSelect.query(CustomFieldType).selectCount(objectContext)
        return cft
    }

    static List<CustomFieldType> getCustomFieldTypes(ObjectContext objectContext, String entityName) {
        ObjectSelect.query(CustomFieldType)
                .where(CustomFieldType.ENTITY_IDENTIFIER.eq(entityName))
                .select(objectContext)
    }

    static CustomFieldType getCustomFieldType(ObjectContext objectContext, String entityName, String customFieldKey) {
        ObjectSelect.query(CustomFieldType)
                .where(CustomFieldType.ENTITY_IDENTIFIER.eq(entityName))
                .and(CustomFieldType.KEY.eq(customFieldKey))
                .selectOne(objectContext)
    }

    static void validateCustomFields(ObjectContext objectContext, String entityName, Map<String, String> customFields, String entityId, EntityValidator validator) {
        getCustomFieldTypes(objectContext, entityName)
                .findAll { it.isMandatory }
                .each { it ->
                    if (isBlank(customFields[it.key])) {
                        validator.throwClientErrorException(entityId, 'customFields', "$it.name is required.")
                    }
                }
    }
}
