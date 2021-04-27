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

import ish.oncourse.server.api.v1.model.DataTypeDTO

import static ish.oncourse.common.field.PropertyGetSetFactory.CUSTOM_FIELD_PROPERTY_PATTERN
import ish.oncourse.server.api.v1.model.CustomFieldTypeDTO
import ish.oncourse.server.api.v1.model.EntityTypeDTO
import ish.oncourse.server.api.v1.model.ValidationErrorDTO
import ish.oncourse.server.cayenne.CustomFieldType
import ish.oncourse.server.cayenne.Field
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.ObjectSelect
import org.apache.cayenne.query.SelectById
import org.apache.cayenne.validation.ValidationResult

class CustomFieldTypeFunctions {

    static ValidationErrorDTO validateForDelete(ObjectContext context, String id) {
        try {
            Long.parseLong(id)
        } catch (NumberFormatException ignored) {
            return new ValidationErrorDTO(id, 'id', "Custom field id '$id' is incorrect. It must contain of only numbers")
        }

        CustomFieldType dbType = SelectById.query(CustomFieldType, id).selectOne(context)
        if (!dbType) {
            return new ValidationErrorDTO(null, 'id', "Custom field type is not exist")
        }

        String matchString = CUSTOM_FIELD_PROPERTY_PATTERN + dbType.getEntityIdentifier() + "." + dbType.getKey()
        List<Field> fields = ObjectSelect.query(Field.class).where(Field.PROPERTY.eq(matchString)).select(context)

        if (fields) {
            return new ValidationErrorDTO(dbType.id?.toString(), 'id', "Custom field is assigned to data collection form")
        }

        ValidationResult result = new ValidationResult()
        dbType.validateForDelete(result)
        if (result.hasFailures()) {
            return new ValidationErrorDTO(dbType.id?.toString(), null, result.failures[0].description)
        }

        return null
    }


    static ValidationErrorDTO validateData(List<CustomFieldTypeDTO> customFieldTypes) {

        List<String> names =  customFieldTypes*.name as List<String>
        List<String> duplicates = names.findAll{names.count(it) > 1}.unique()

        if (!duplicates.empty) {
            return new ValidationErrorDTO(null, 'name', "Custom field type name should be unique: ${duplicates.join(', ')}")
        }


        List<String> keys =  customFieldTypes*.fieldKey as List<String>
        List<String> keyDuplicates = keys.findAll{keys.count(it) > 1}.unique()

        if (!keyDuplicates.empty) {
            return new ValidationErrorDTO(null, 'fieldKey', "Custom field type key should be unique: ${keyDuplicates.join(', ')}")
        }

        Map<EntityTypeDTO, List<CustomFieldTypeDTO>> orders = customFieldTypes.groupBy { it.entityType }
        orders.each { k, v ->
            List<Long> orderList = v*.sortOrder.sort()
            if(orderList.size() != orderList.unique().size()) {
                return new ValidationErrorDTO(null, 'sortOrder', "Custom field type sortOrder should be unique for ${k} group: ${orderList}")
            }
        }

        return null
    }


    static ValidationErrorDTO validateForUpdate(CustomFieldTypeDTO type, ObjectContext context, List<CustomFieldTypeDTO> types) {
        CustomFieldType dbType = null

        if (type.id) {
            dbType = SelectById.query(CustomFieldType, type.id).selectOne(context)
            if (!dbType) {
                return new ValidationErrorDTO(type.id, 'id', "Custom field type $type.id is not exist")
            }
        }

        if (!type.name || type.name.empty) {
            return new ValidationErrorDTO(type.id, 'name', "Custom field name should be specified")
        }
        if (!type.fieldKey || type.name.empty) {
            return new ValidationErrorDTO(type.id, 'fieldKey', "Custom field key should be specified")
        }
        if (!type.fieldKey.matches(/^[a-zA-Z0-9]*$/)) {
            return new ValidationErrorDTO(type.id, 'fieldKey', "The custom field key can contains alphanumeric symbols only")
        }

        if (!type.entityType) {
            return new ValidationErrorDTO(type.id, 'entityType', "Custom field entity type should be specified")
        }
        if (!type.dataType) {
            return new ValidationErrorDTO(type.id, 'dataType', "Custom field data type should be specified")
        }
        if (DataTypeDTO.PATTERN_TEXT == type.dataType && !type.pattern) {
            return new ValidationErrorDTO(type.id, 'pattern', "Custom field with 'pattern text' data type should have a regular expression")
        }
        if (dbType) {
            CustomFieldType duplicate = ObjectSelect.query(CustomFieldType).where(CustomFieldType.NAME.eq(type.name)).selectOne(context)
            if (duplicate && duplicate.id != dbType.id && !types.find {duplicate.id == Long.valueOf(it.id)}) {
                return new ValidationErrorDTO(null, 'name', "Custom field name should be unique")
            }

            if (dbType.key != type.fieldKey) {
                return new ValidationErrorDTO(type.id, 'fieldKey', "Custom field key can not be changed")
            }
            if (dbType.entityIdentifier != type.entityType.toString()) {
                return new ValidationErrorDTO(type.id, 'entityType', "Custom field entity type can not be changed")
            }
            if (dbType.dataType != type.dataType.dbType) {
                return new ValidationErrorDTO(type.id, 'dataType', "Custom field data type can not be changed")
            }
        } else {
            if (getByName(context, type.name)) {
                return new ValidationErrorDTO(type.id, 'name', "Custom field name should be unique")
            }
            if (getByKey(context, type.fieldKey)) {
                return new ValidationErrorDTO(type.id, 'fieldKey', "Custom field key should be unique")
            }
        }

        return null
    }

    private static CustomFieldType getByName(ObjectContext context, String name) {
        return ObjectSelect.query(CustomFieldType).where(CustomFieldType.NAME.eq(name)).selectOne(context)
    }

    private static CustomFieldType getByKey(ObjectContext context, String key) {
        return ObjectSelect.query(CustomFieldType).where(CustomFieldType.KEY.eq(key)).selectOne(context)

    }

    static CustomFieldType updateCustomField(ObjectContext context, CustomFieldTypeDTO type) {
        CustomFieldType dbType = type.id ? SelectById.query(CustomFieldType, type.id).selectOne(context) : context.newObject(CustomFieldType)

        if (dbType.newRecord) {
            dbType.key = type.fieldKey
            dbType.entityIdentifier = type.entityType.toString()
        }
        dbType.name = type.name
        dbType.defaultValue = type.defaultValue
        dbType.isMandatory = type.mandatory
        dbType.sortOrder = type.sortOrder
        dbType.dataType = type.dataType.dbType
        dbType.pattern = type.pattern
        dbType
    }

}
