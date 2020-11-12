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
            CustomField cf = dbCustomFields.find { it.customFieldType.key == k } ?:
                    context.newObject(relationClass).with { it ->
                        it.relatedObject = dbObject
                        it.customFieldType = getCustomFieldType(context, dbObject.class.simpleName, k)
                        it
                    }
            cf.value = trimToNull(v)
        }
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
