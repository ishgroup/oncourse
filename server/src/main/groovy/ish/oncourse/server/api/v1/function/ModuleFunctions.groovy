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

import groovy.transform.CompileStatic
import ish.common.types.ModuleType
import ish.oncourse.server.api.BidiMap
import ish.oncourse.server.api.v1.model.ModuleDTO
import ish.oncourse.server.api.v1.model.ModuleTypeDTO
import ish.oncourse.server.api.v1.model.ValidationErrorDTO
import ish.oncourse.server.cayenne.Module
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.ObjectSelect

import java.time.ZoneOffset

@CompileStatic
class ModuleFunctions {

    static BidiMap<ModuleType, ModuleTypeDTO> bidiModuleType = new BidiMap<>()

    static {
        bidiModuleType.put(ModuleType.UNIT_OF_COMPETENCY, ModuleTypeDTO.UNIT_OF_COMPETENCY)
        bidiModuleType.put(ModuleType.MODULE, ModuleTypeDTO.MODULE)
        bidiModuleType.put(ModuleType.UNIT_OF_STUDY, ModuleTypeDTO.UNIT_OF_STUDY)
        bidiModuleType.put(ModuleType.OTHER, ModuleTypeDTO.OTHER)
    }

    static ModuleDTO toModuleModel(Module module) {
        ModuleDTO model = new ModuleDTO()
        model.id = module.id
        model.creditPoints = module.creditPoints
        model.expiryDays = module.expiryDays
        model.fieldOfEducation = module.fieldOfEducation
        model.isCustom = module.isCustom
        model.isOffered = module.isOffered
        model.nationalCode = module.nationalCode
        model.nominalHours = module.nominalHours
        model.specialization = module.specialization
        model.title = module.title
        model.type = ModuleFunctions.bidiModuleType.get(module.type)
        model.createdOn = module.createdOn.toInstant().atZone(ZoneOffset.UTC).toLocalDateTime()
        model.modifiedOn = module.modifiedOn.toInstant().atZone(ZoneOffset.UTC).toLocalDateTime()
        model
    }

    static void toDbModule(ModuleDTO model, Module module, boolean isCustomModule = true) {
        if (isCustomModule) {
            module.isCustom = true
            module.nationalCode = model.nationalCode?.trim()
            module.title = model.title?.trim()
            module.fieldOfEducation = model.fieldOfEducation?.trim()
        }
        module.isOffered = model.isIsOffered()
        module.nominalHours = model.nominalHours
        module.specialization = model.specialization?.trim()
        module.creditPoints = model.creditPoints
        module.expiryDays = model.expiryDays
        module.type = ModuleFunctions.bidiModuleType.getByValue(model.type)
    }

    static ValidationErrorDTO validateModelRequiredFields(ModuleDTO model, ObjectContext context, boolean isCustomModule = true, Long moduleId = null) {
        if (isCustomModule) {
            if (!model.nationalCode?.trim()) {
                return new ValidationErrorDTO(model.id?.toString(), 'nationalCode', 'National code is required.')
            } else if (model.nationalCode.length() > 12) {
                return new ValidationErrorDTO(model.id?.toString(), 'nationalCode', "National code can't be more than 12 chars.")
            }

            Long mId = ObjectSelect.query(Module)
                    .where(Module.NATIONAL_CODE.eq(model.nationalCode))
                    .selectOne(context)?.id

            if (mId && mId != moduleId) {
                return new ValidationErrorDTO(model.id?.toString(), 'nationalCode', 'National code must be unique.')
            }

            if (!model.title?.trim()) {
                return new ValidationErrorDTO(model.id?.toString(), 'title', 'Title is required.')
            } else if (model.title.length() > 200) {
                return new ValidationErrorDTO(model.id?.toString(), 'title', "Title can't be more than 200 chars.")
            }

            if (model.fieldOfEducation && model.fieldOfEducation.length() > 6) {
                return new ValidationErrorDTO(model.id?.toString(), 'fieldOfEducation', "Field Of Education can't be more than 6 chars.")
            }

            if (!model.type) {
                return new ValidationErrorDTO(model.id?.toString(), 'type', 'Module type is required.')
            }
        }

        if (model.specialization?.trim() && model.specialization.length() > 128) {
            return new ValidationErrorDTO(model.id?.toString(), 'specialization', "Specialization must be less than 128 chars.")
        }

        if (model.isIsOffered() == null) {
            return new ValidationErrorDTO(model.id?.toString(), 'isOffered', "Flag \'Is offered\' is required.")
        }

        if (model.nominalHours && model.nominalHours <= BigDecimal.ZERO) {
            return new ValidationErrorDTO(model.id?.toString(), 'nominalHours', "Nominal hours must be greater than 0.")
        }

        if (model.creditPoints != null) {
            if (model.creditPoints.round(2) <= BigDecimal.ZERO) {
                return new ValidationErrorDTO(model.id?.toString(), 'creditPoints', "Credit points must be greater than 0.")
            } else if (model.creditPoints > 100000000) {
                return new ValidationErrorDTO(model.id?.toString(), 'creditPoints', "The maximum width for credit points is 8 digit in front.")
            }

        }

        if ((model.expiryDays != null) && (model.expiryDays <= 0)) {
            return new ValidationErrorDTO(model.id?.toString(), 'isOffered', "Expiry days should be greater than 0.")
        }

        null
    }

    static ValidationErrorDTO validateValues(ModuleDTO model, Module entity) {
        if (model.nationalCode && model.nationalCode != entity.nationalCode) {
            return new ValidationErrorDTO(entity.id?.toString(), 'id', "Module national code cannot be updated.")
        }

        if (!entity.isCustom && bidiModuleType.get(entity.type) != model.type) {
            return new ValidationErrorDTO(entity.id?.toString(), 'id', "Module type cannot be updated.")
        }

        null
    }

    static ValidationErrorDTO validateForDelete(Module entity) {
        if (!entity.isCustom) {
            return new ValidationErrorDTO(entity.id?.toString(), 'id', "Only custom modules can be deleted.")
        }
        if (!entity.courses.empty) {
            return new ValidationErrorDTO(entity.id?.toString(), 'id', "Assigned modules cannot be deleted.")
        }
        null
    }
}
