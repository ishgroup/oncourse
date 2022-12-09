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

import ish.oncourse.server.api.v1.model.ContactRelationTypeDTO
import ish.oncourse.server.api.v1.model.ValidationErrorDTO
import ish.oncourse.server.cayenne.ContactRelationType
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.SelectById
import org.apache.cayenne.validation.ValidationResult

import java.time.ZoneOffset

class ContactRelationFunctions {

    static ValidationErrorDTO validateForDelete(ObjectContext context, String id) {
        try {
            Long.parseLong(id)
        } catch (NumberFormatException ignored) {
            return new ValidationErrorDTO(id, 'id', "Contact relation id '$id' is incorrect. It must contain of only numbers")
        }

        ContactRelationType dbType = SelectById.query(ContactRelationType, id).selectOne(context)

        if (!dbType) {
            return new ValidationErrorDTO(null, 'id', "Contact relation type is not exist")
        }

        if (dbType.id <= 0 ) {
            return new ValidationErrorDTO(dbType?.id?.toString(), 'id', "System contact relation type cannot be deleted")
        }

        if (!dbType.contactRelations.empty) {
            return new ValidationErrorDTO(dbType?.id?.toString(), 'id', "Contact relation type cannot be deleted. It has assigned Contact relations.")

        }

        if (!dbType.membeshipDiscountRelations.empty) {
            return new ValidationErrorDTO(dbType?.id?.toString(), 'id', "Contact relation type cannot be deleted. It has assigned Membership Discount relations.")
        }

        ValidationResult result = new ValidationResult()
        dbType.validateForDelete(result)

        if (result.hasFailures()) {
            return new ValidationErrorDTO(dbType?.id?.toString(), null, result.failures[0].description)
        }
        return null
    }

    static ValidationErrorDTO validateForUpdate(ObjectContext context, ContactRelationTypeDTO type) {

        if (type.id) {
            ContactRelationType dbType = SelectById.query(ContactRelationType, type.id).selectOne(context)
            if (!dbType) {
                return new ValidationErrorDTO(type.id, 'id', "Contact relation type $type.id is not exist")
            }
        }

        if (!type.relationName || type.relationName.empty) {
            return new ValidationErrorDTO(type.id, 'relationName', "Relation name can not be empty")
        }

        if (!type.reverseRelationName || type.reverseRelationName.empty) {
            return new ValidationErrorDTO(type.id, 'reverseRelationName', "Reverse relationName name can not be empty")
        }
    }

    static ContactRelationType updateContactRelation(ObjectContext context, ContactRelationTypeDTO type) {
        ContactRelationType dbType = type.id ? SelectById.query(ContactRelationType, type.id).selectOne(context) : context.newObject(ContactRelationType)

        dbType.delegatedAccessToContact = type.isPortalAccess()
        if (type.id == null || Long.valueOf(type.id) > 0) {
            dbType.fromContactName = type.relationName
            dbType.toContactName = type.reverseRelationName
        }
        dbType
    }

    static ContactRelationTypeDTO toRestContactRelationType(ContactRelationType dbType) {
        new ContactRelationTypeDTO().with { type ->
            type.id = dbType.id.toString()
            type.created = dbType.createdOn.toInstant().atZone(ZoneOffset.UTC).toLocalDateTime()
            type.modified = dbType.modifiedOn.toInstant().atZone(ZoneOffset.UTC).toLocalDateTime()
            type.relationName = dbType.fromContactName
            type.reverseRelationName = dbType.toContactName
            type.portalAccess = dbType.delegatedAccessToContact
            type.systemType = dbType.id <= 0
            type
        }
    }
}
