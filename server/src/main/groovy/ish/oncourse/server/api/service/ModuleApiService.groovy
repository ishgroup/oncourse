/*
 * Copyright ish group pty ltd 2023.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.api.service

import ish.common.types.ModuleType
import ish.oncourse.server.api.BidiMap
import ish.oncourse.server.api.dao.ModuleDao
import ish.oncourse.server.api.v1.model.ModuleDTO
import ish.oncourse.server.api.v1.model.ModuleTypeDTO
import ish.oncourse.server.cayenne.Module
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.ObjectSelect
import org.apache.cayenne.query.SelectById

import java.time.ZoneOffset

class ModuleApiService extends EntityApiService<ModuleDTO, Module, ModuleDao>{

    static BidiMap<ModuleType, ModuleTypeDTO> bidiModuleType = new BidiMap<>()

    static {
        bidiModuleType.put(ModuleType.UNIT_OF_COMPETENCY, ModuleTypeDTO.UNIT_OF_COMPETENCY)
        bidiModuleType.put(ModuleType.MODULE, ModuleTypeDTO.MODULE)
        bidiModuleType.put(ModuleType.UNIT_OF_STUDY, ModuleTypeDTO.UNIT_OF_STUDY)
        bidiModuleType.put(ModuleType.OTHER, ModuleTypeDTO.OTHER)
    }

    @Override
    Class<Module> getPersistentClass() {
        return Module
    }

    @Override
    ModuleDTO toRestModel(Module module) {
        ModuleDTO dto = new ModuleDTO()
        dto.id = module.id
        dto.creditPoints = module.creditPoints
        dto.expiryDays = module.expiryDays
        dto.fieldOfEducation = module.fieldOfEducation
        dto.isCustom = module.isCustom
        dto.isOffered = module.isOffered
        dto.nationalCode = module.nationalCode
        dto.nominalHours = module.nominalHours
        dto.specialization = module.specialization
        dto.title = module.title
        dto.type = bidiModuleType.get(module.type)
        dto.createdOn = module.createdOn.toInstant().atZone(ZoneOffset.UTC).toLocalDateTime()
        dto.modifiedOn = module.modifiedOn.toInstant().atZone(ZoneOffset.UTC).toLocalDateTime()
        dto
    }

    @Override
    Module toCayenneModel(ModuleDTO dto, Module module) {
        //create always sets these fields
        if (module.isCustom == null || module.isCustom) {
            module.isCustom = true
            module.nationalCode = dto.nationalCode?.trim()
            module.title = dto.title?.trim()
            module.fieldOfEducation = dto.fieldOfEducation?.trim()
        } else {
            module.isCustom = false
        }

        module.isOffered = dto.isOffered
        module.nominalHours = dto.nominalHours
        module.specialization = dto.specialization?.trim()
        module.creditPoints = dto.creditPoints
        module.expiryDays = dto.expiryDays
        module.type = bidiModuleType.getByValue(dto.type)
        module
    }

    @Override
    void validateModelBeforeSave(ModuleDTO dto, ObjectContext context, Long moduleId) {
        if (dto.isCustom) {
            if (!dto.nationalCode?.trim()) {
                validator.throwClientErrorException(dto.id, 'nationalCode', 'National code is required.')
            } else if (dto.nationalCode.length() > 12) {
                validator.throwClientErrorException(dto.id, 'nationalCode', "National code can't be more than 12 chars.")
            }

            Long mId = ObjectSelect.query(Module)
                    .where(Module.NATIONAL_CODE.eq(dto.nationalCode))
                    .selectOne(context)?.id

            if (mId && mId != moduleId) {
                validator.throwClientErrorException(dto.id, 'nationalCode', 'National code must be unique.')
            }

            if (!dto.title?.trim()) {
                validator.throwClientErrorException(dto.id, 'title', 'Title is required.')
            } else if (dto.title.length() > 200) {
                validator.throwClientErrorException(dto.id, 'title', "Title can't be more than 200 chars.")
            }

            if (dto.fieldOfEducation && dto.fieldOfEducation.length() > 6) {
                validator.throwClientErrorException(dto.id, 'fieldOfEducation', "Field Of Education can't be more than 6 chars.")
            }

            if (!dto.type) {
                validator.throwClientErrorException(dto.id, 'type', 'Module type is required.')
            }
        }

        if (dto.specialization?.trim() && dto.specialization.length() > 128) {
            validator.throwClientErrorException(dto.id, 'specialization', "Specialization must be less than 128 chars.")
        }

        if (dto.isOffered == null) {
            validator.throwClientErrorException(dto.id, 'isOffered', "Flag \'Is offered\' is required.")
        }

        if (dto.nominalHours && dto.nominalHours <= BigDecimal.ZERO) {
            validator.throwClientErrorException(dto.id, 'nominalHours', "Nominal hours must be greater than 0.")
        }

        if (dto.creditPoints != null) {
            if (dto.creditPoints.round(2) <= BigDecimal.ZERO) {
                validator.throwClientErrorException(dto.id, 'creditPoints', "Credit points must be greater than 0.")
            } else if (dto.creditPoints > 100000000) {
                validator.throwClientErrorException(dto.id, 'creditPoints', "The maximum width for credit points is 8 digit in front.")
            }

        }

        if ((dto.expiryDays != null) && (dto.expiryDays <= 0)) {
            validator.throwClientErrorException(dto.id, 'isOffered', "Expiry days should be greater than 0.")
        }

        if(moduleId != null){
            Module entity = SelectById.query(Module, moduleId).selectOne(context)
            if (dto.nationalCode && dto.nationalCode != entity.nationalCode) {
                validator.throwClientErrorException(entity.id, 'id', "Module national code cannot be updated.")
            }

            if (!entity.isCustom && bidiModuleType.get(entity.type) != dto.type) {
                validator.throwClientErrorException(entity.id, 'id', "Module type cannot be updated.")
            }
        }
    }

    @Override
    void validateModelBeforeRemove(Module entity) {
        if (!entity.isCustom) {
            validator.throwClientErrorException(entity?.id, 'id', "Only custom modules can be deleted.")
        }
        if (!entity.courses.empty) {
            validator.throwClientErrorException(entity?.id, 'id', "Assigned modules cannot be deleted.")
        }
    }
}
