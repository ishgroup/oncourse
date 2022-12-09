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

import ish.oncourse.server.api.v1.model.ConcessionTypeDTO
import ish.oncourse.server.api.v1.model.ValidationErrorDTO
import ish.oncourse.server.cayenne.ConcessionType
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.ObjectSelect
import org.apache.cayenne.query.SelectById

import java.time.ZoneOffset

class ConcessionTypeFunctions {

    static ValidationErrorDTO validateForDelete(ConcessionType dbType) {
        if (!dbType) {
            return new ValidationErrorDTO(null, null, "Concession type is not exist")
        } else if (!dbType.studentConcessions.empty) {
            return new ValidationErrorDTO(null, null, "Concession type has related student concessions")
        } else if (!dbType.concessionTypeDiscounts.empty) {
            return new ValidationErrorDTO(null, null, "Concession type has related discounts")
        }
        return null
    }

    static ValidationErrorDTO validateData(List<ConcessionTypeDTO> concessionTypes) {
        ConcessionTypeDTO invalidType = concessionTypes.find { ct -> ct.id == null && (ct.isAllowOnWeb() == null || ct.isRequireExpary() == null || ct.requireNumber == null)}
        if (invalidType) {
            if (invalidType.isRequireNumber() == null) {
                return new ValidationErrorDTO(null, 'requireNumber', "Concession requireNumber should be specified")
            }

            if (invalidType.isRequireExpary() == null) {
                return new ValidationErrorDTO(null, 'requireExpary', "Concession requireExpary should be specified")
            }

            if (invalidType.isAllowOnWeb() == null) {
                return new ValidationErrorDTO(null, 'allowOnWeb', "Concession allowOnWeb should be specified")
            }
        }

        List<String> names =  concessionTypes*.name.flatten() as List<String>
        List<String> duplicates = names.findAll{names.count(it) > 1}.unique()

        if (!duplicates.empty) {
            return new ValidationErrorDTO(null, 'name', "Concession name should be unique: ${duplicates.join(', ')}")
        }
        return null
    }

    static ValidationErrorDTO validateForUpdate(ConcessionTypeDTO type, ObjectContext context, List<ConcessionTypeDTO> concessionTypes) {

        if (type.id) {
            ConcessionType dbType = SelectById.query(ConcessionType, type.id).selectFirst(context)
            if (!dbType) {
                return new ValidationErrorDTO(null, null, "Concession type $type.id is not exist")
            }
        }

        if (!type.name || type.name.empty ) {
            return new ValidationErrorDTO(null, 'name', "Concession name should be specified")
        }

        ConcessionType duplicate = ObjectSelect.query(ConcessionType).where(ConcessionType.NAME.eq(type.name)).selectOne(context)


        if (duplicate && (!type.id || duplicate.id != Long.valueOf(type.id)) && !concessionTypes.find {it.id && duplicate.id == Long.valueOf(it.id)}) {
            return new ValidationErrorDTO(null, 'name', "Concession name should be unique")
        }

        return null
    }

    static ConcessionType updateConcession(ConcessionTypeDTO type, ObjectContext context) {
        ConcessionType dbType = type.id? SelectById.query(ConcessionType, type.id).selectFirst(context): context.newObject(ConcessionType)
        dbType.name = type.name
        dbType.isEnabled = type.isAllowOnWeb()
        dbType.hasExpiryDate = type.isRequireExpary()
        dbType.hasConcessionNumber = type.isRequireNumber()

        dbType
    }

    static ConcessionTypeDTO toRestConcessionType(ConcessionType dbType) {
        new ConcessionTypeDTO().with { type ->
            type.id = dbType.id
            type.created = dbType.createdOn.toInstant().atZone(ZoneOffset.UTC).toLocalDateTime()
            type.modified = dbType.modifiedOn.toInstant().atZone(ZoneOffset.UTC).toLocalDateTime()
            type.name = dbType.name
            type.allowOnWeb = dbType.isEnabled
            type.requireExpary = dbType.hasExpiryDate
            type.requireNumber = dbType.hasConcessionNumber
            type
        }
    }

}
