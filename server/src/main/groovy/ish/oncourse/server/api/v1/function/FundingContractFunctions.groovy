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

import ish.oncourse.common.ExportJurisdiction
import ish.oncourse.server.api.v1.model.AvetmissExportFlavourDTO
import ish.oncourse.server.api.v1.model.FundingSourceDTO
import ish.oncourse.server.api.v1.model.ValidationErrorDTO
import ish.oncourse.server.cayenne.FundingSource
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.ObjectSelect
import static org.apache.commons.lang3.StringUtils.isBlank
import static org.apache.commons.lang3.StringUtils.trimToNull

import java.time.ZoneOffset

class FundingContractFunctions {

    static FundingSourceDTO toRestFundingContract(FundingSource dbObj) {
        new FundingSourceDTO().with { fs ->
            fs.id = dbObj.id
            fs.name = dbObj.name
            fs.flavour = AvetmissExportFlavourDTO.values().find { it.toString() == dbObj.flavour.displayName }
            fs.active = dbObj.active
            fs.created = dbObj?.createdOn?.toInstant()?.atZone(ZoneOffset.UTC)?.toLocalDateTime()
            fs.modified = dbObj?.modifiedOn?.toInstant()?.atZone(ZoneOffset.UTC)?.toLocalDateTime()
            fs
        }
    }

    static FundingSource toDbFundingContract(FundingSourceDTO fundingSource, FundingSource dbObj) {
        dbObj.name = trimToNull(fundingSource.name)
        dbObj.flavour = ExportJurisdiction.values().find { it.displayName == fundingSource.flavour.toString() }
        dbObj.active = fundingSource.isActive()
        dbObj
    }


    static ValidationErrorDTO validateForDelete(FundingSource entity) {
        if (!entity.courseClasses.empty) {
            return new ValidationErrorDTO(entity.id?.toString(), 'id', 'Cannot delete funding contract. It\'s assigned to courseClasses')
        }

        if (!entity.enrolments.empty) {
            return new ValidationErrorDTO(entity.id?.toString(), 'id', 'Cannot delete funding contract. It\'s assigned to enrolments')
        }

        null
    }

    static ValidationErrorDTO validateForPatch(FundingSourceDTO entity) {
        if (entity.isActive() == null) {
            return new ValidationErrorDTO(entity.id?.toString(), 'active', 'Active flag cannot be null')
        }
        null
    }

    static ValidationErrorDTO validateForSave(FundingSourceDTO entity, ObjectContext context) {
        if (isBlank(entity.name)) {
            return new ValidationErrorDTO(entity.id?.toString(), 'name', 'Name is required')
        }
        Long accountId = ObjectSelect.query(FundingSource)
                .where(FundingSource.NAME.eq(trimToNull(entity.name)))
                .selectOne(context)?.id
        if (accountId && accountId != entity?.id) {
            return new ValidationErrorDTO(entity?.id?.toString(), 'name', 'Name must be unique.')
        }

        if (entity.flavour == null) {
            return new ValidationErrorDTO(entity.id?.toString(), 'flavour', 'Flavour is required')
        }
        if (entity.isActive() == null) {
            return new ValidationErrorDTO(entity.id?.toString(), 'active', 'Active flag cannot be null')
        }
        null
    }
}
