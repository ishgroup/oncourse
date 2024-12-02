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

package ish.oncourse.server.api.service

import com.google.inject.Inject
import ish.oncourse.server.api.dao.DefinedTutorRoleDao
import ish.oncourse.server.api.dao.PayRateDao
import ish.oncourse.server.api.v1.model.ClassCostRepetitionTypeDTO
import ish.oncourse.server.api.v1.model.DefinedTutorRoleDTO
import ish.oncourse.server.api.v1.model.TutorRolePayRateDTO
import ish.oncourse.server.cayenne.DefinedTutorRole
import ish.oncourse.server.cayenne.PayRate
import ish.oncourse.server.users.SystemUserService
import org.apache.cayenne.ObjectContext
import org.apache.commons.lang3.StringUtils

import java.time.ZoneOffset

import static ish.oncourse.server.api.function.MoneyFunctions.toMoneyValue
import static org.apache.commons.lang3.StringUtils.trimToNull

class TutorRoleApiService extends EntityApiService<DefinedTutorRoleDTO, DefinedTutorRole, DefinedTutorRoleDao> {

    @Inject
    private PayRateDao payRateDao

    @Inject
    private SystemUserService systemUserService

    @Override
    Class<DefinedTutorRole> getPersistentClass() {
        return DefinedTutorRole
    }

    @Override
    DefinedTutorRoleDTO toRestModel(DefinedTutorRole definedTutorRole) {
        new DefinedTutorRoleDTO().with { tutorRoleDTO ->
            tutorRoleDTO.id = definedTutorRole.id
            tutorRoleDTO.name = definedTutorRole.name
            tutorRoleDTO.description = definedTutorRole.description
            tutorRoleDTO.active = definedTutorRole.active
            tutorRoleDTO.payRates = definedTutorRole.payRates.collect { PayRate payRate ->
                new TutorRolePayRateDTO().with { rate ->
                    rate.id = payRate.id
                    rate.type = ClassCostRepetitionTypeDTO.values()[0].fromDbType(payRate.type)
                    rate.validFrom = payRate.validFrom?.toInstant()?.atZone(ZoneOffset.UTC)?.toLocalDate()
                    rate.rate = payRate.rate?.toBigDecimal()
                    rate.oncostRate = payRate.oncostRate
                    rate.notes = payRate.description
                    rate
                }
            }
            tutorRoleDTO
        }
    }

    @Override
    DefinedTutorRole toCayenneModel(DefinedTutorRoleDTO tutorRoleDTO, DefinedTutorRole definedTutorRole) {
        definedTutorRole.name = trimToNull(tutorRoleDTO.name)
        definedTutorRole.description = trimToNull(tutorRoleDTO.description)
        definedTutorRole.active = tutorRoleDTO.active
        updatePayRates(definedTutorRole, tutorRoleDTO.payRates)
        definedTutorRole
    }

    @Override
    void validateModelBeforeSave(DefinedTutorRoleDTO tutorRoleDTO, ObjectContext context, Long id) {
        if (StringUtils.isBlank(tutorRoleDTO.name)) {
            validator.throwClientErrorException(id, 'name', 'Name is required.')
        } else if (trimToNull(tutorRoleDTO.name).length() > 64) {
            validator.throwClientErrorException(id, 'name', 'Name cannot be more than 64 chars.')
        }

        if(!tutorRoleDTO.description || !trimToNull(tutorRoleDTO.description)) {
            validator.throwClientErrorException(id, 'description', 'Description cannot be empty.')
        }

        if (trimToNull(tutorRoleDTO.description) && trimToNull(tutorRoleDTO.description).length() > 128) {
            validator.throwClientErrorException(id, 'description', 'Description cannot be more than 128 chars.')
        }
        if (tutorRoleDTO.active == null) {
            validator.throwClientErrorException(id, 'active', 'Active flag is required.')
        }

        tutorRoleDTO.payRates.eachWithIndex { TutorRolePayRateDTO payRate, int i ->
            if (!payRate.type) {
                validator.throwClientErrorException(id, "payRates[$i].type", 'Type is required.')
            }

            if (!payRate.validFrom) {
                validator.throwClientErrorException(id, "payRates[$i].validFrom", 'Valid from is required.')
            }

            if (payRate.rate == null) {
                validator.throwClientErrorException(id, "payRates[$i].rate", 'Rate is required.')
            }

            if (payRate.oncostRate == null) {
                validator.throwClientErrorException(id, "payRates[$i].oncostRate", 'Oncost rate from is required.')
            }

            if (trimToNull(payRate.notes) && trimToNull(payRate.notes).length() > 128) {
                validator.throwClientErrorException(id, "payRates[$i].notes", 'Notes cannot be more than 128 chars.')
            }
        }
    }

    @Override
    void validateModelBeforeRemove(DefinedTutorRole definedTutorRole) {
        if (!definedTutorRole.tutorRoles.empty) {
            validator.throwClientErrorException(definedTutorRole.id, 'assessmentClasses', 'Cannot delete defined tutor role attached to tutor.')
        }
    }

    private void updatePayRates(DefinedTutorRole definedTutorRole, List<TutorRolePayRateDTO> payRateDTOs) {
        ObjectContext context = definedTutorRole.context
        List<Long> relationsToSave = payRateDTOs*.id ?: [] as List<Long>
        definedTutorRole.context.deleteObjects(definedTutorRole.payRates.findAll { !relationsToSave.contains(it.id) })
        payRateDTOs.each { payRate ->
            PayRate rate = definedTutorRole.payRates.find { payRate.id != null && it.id == payRate.id } ?:
                    payRateDao.newObject(context)
            rate.definedTutorRole = definedTutorRole
            if (definedTutorRole.newRecord || rate.newRecord ||
                    (rate.type == payRate.type.getDbType() &&
                            rate.validFrom?.toInstant()?.atZone(ZoneOffset.UTC)?.toLocalDate() == payRate.validFrom) &&
                    rate.description == trimToNull(payRate.notes) &&
                    rate.rate == payRate.rate &&
                    rate.oncostRate == payRate.oncostRate) {
                rate.editedByUser =  context.localObject(systemUserService.currentUser)
            }
            rate.type = payRate.type.getDbType()
            rate.validFrom = payRate.validFrom?.atStartOfDay(ZoneOffset.UTC)?.toDate()
            rate.rate = toMoneyValue(payRate.rate)
            rate.oncostRate = payRate.oncostRate
            rate.description = trimToNull(payRate.notes)
        }
    }
}
