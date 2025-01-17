/*
 * Copyright ish group pty ltd 2023.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.api.service

import ish.common.types.QualificationType
import ish.oncourse.server.api.BidiMap
import ish.oncourse.server.api.dao.QualificationDao
import ish.oncourse.server.api.v1.model.QualificationDTO
import ish.oncourse.server.api.v1.model.QualificationTypeDTO
import ish.oncourse.server.cayenne.Qualification
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.ObjectSelect
import org.apache.cayenne.query.SelectById

import java.time.ZoneOffset

import static ish.common.types.QualificationType.*
import static org.apache.commons.lang3.StringUtils.trimToNull

class QualificationApiService extends EntityApiService<QualificationDTO, Qualification, QualificationDao>{

    private static final BidiMap<QualificationType, QualificationTypeDTO> qualificationTypeMap = new BidiMap<QualificationType, QualificationTypeDTO>() {{
        put(QUALIFICATION_TYPE, QualificationTypeDTO.QUALIFICATION)
        put(COURSE_TYPE, QualificationTypeDTO.ACCREDITED_COURSE)
        put(SKILLSET_TYPE, QualificationTypeDTO.SKILL_SET)
        put(SKILLSET_LOCAL_TYPE, QualificationTypeDTO.LOCAL_SKILL_SET)
        put(HIGHER_TYPE, QualificationTypeDTO.HIGHER_EDUCATION)
    }}

    @Override
    Class<Qualification> getPersistentClass() {
        return Qualification
    }

    @Override
    QualificationDTO toRestModel(Qualification qualification) {
        new QualificationDTO().with { model ->
            model.id = qualification.id
            model.anzsco = qualification.anzsco
            model.fieldOfEducation = qualification.fieldOfEducation
            model.isCustom = qualification.isCustom
            model.isOffered = qualification.isOffered
            model.nationalCode = qualification.nationalCode
            model.newApprenticeship = qualification.newApprenticeship
            model.nominalHours = qualification.nominalHours
            model.qualLevel = qualification.level
            model.reviewDate = qualification.reviewDate
            model.specialization = qualification.specialization
            model.title = qualification.title
            model.type = qualificationTypeMap.get(qualification.type)
            model.createdOn = qualification.createdOn.toInstant().atZone(ZoneOffset.UTC).toLocalDateTime()
            model.modifiedOn = qualification.modifiedOn.toInstant().atZone(ZoneOffset.UTC).toLocalDateTime()
            model
        }
    }

    @Override
    Qualification toCayenneModel(QualificationDTO dto, Qualification qualification) {
        //create always sets these fields
        if (qualification.isCustom == null || qualification.isCustom) {
            qualification.type = qualificationTypeMap.getByValue(dto.type)
            qualification.isCustom = true
            qualification.nationalCode = trimToNull(dto.nationalCode)
            qualification.title = trimToNull(dto.title)
            qualification.fieldOfEducation = trimToNull(dto.fieldOfEducation)
            qualification.level = trimToNull(dto.qualLevel)
            qualification.anzsco = trimToNull(dto.anzsco)

            qualification.newApprenticeship = trimToNull(dto.newApprenticeship)
            qualification.reviewDate = dto.reviewDate
        }

        qualification.isOffered = dto.isOffered
        qualification.nominalHours = dto.nominalHours
        qualification.specialization = trimToNull(dto.specialization)
        qualification
    }

    @Override
    void validateModelBeforeSave(QualificationDTO qualificationDTO, ObjectContext context, Long id) {
        if (qualificationDTO.nominalHours && qualificationDTO.nominalHours <= BigDecimal.ZERO) {
            validator.throwClientErrorException(qualificationDTO?.id, 'nominalHours', "Nominal hours must be greater than 0.")
        }

        if (trimToNull(qualificationDTO.specialization) && qualificationDTO.specialization.length() > 128) {
            validator.throwClientErrorException(qualificationDTO?.id, 'specialization', "Specialization must be less than 128 chars.")
        }

        if (qualificationDTO.isOffered == null) {
            validator.throwClientErrorException(qualificationDTO?.id, 'isOffered', "Flag \'Is offered\' is required.")
        }

        Long qId = ObjectSelect.query(Qualification)
                .where(Qualification.NATIONAL_CODE.eq(qualificationDTO.nationalCode))
                .selectOne(context)?.id

        if (qId && qId != id) {
            validator.throwClientErrorException(qualificationDTO?.id, 'nationalCode', 'National code must be unique.')
        }

        Qualification entity = id == null ? null : SelectById.query(Qualification, id).selectFirst(context)
        if(entity){
            if (qualificationDTO.nationalCode && qualificationDTO.nationalCode != entity.nationalCode) {
                validator.throwClientErrorException(id, 'id', "Qualification national code cannot be updated.")
            }
        }

        if (id == null || entity && entity?.isCustom) {
            if (!qualificationDTO.type) {
                validator.throwClientErrorException(qualificationDTO?.id, 'type', 'Type is required.')
            } else if (!qualificationTypeMap.containsValue(qualificationDTO.type)) {
                validator.throwClientErrorException(qualificationDTO?.id, 'type', 'Unrecognized qualification type.')
            }

            if (!trimToNull(qualificationDTO.nationalCode)) {
                validator.throwClientErrorException(qualificationDTO?.id, 'nationalCode', 'National code is required.')
            } else if (qualificationDTO.nationalCode.length() > 12) {
                validator.throwClientErrorException(qualificationDTO?.id, 'nationalCode', "National code can't be more than 12 chars.")
            }

            if (!trimToNull(qualificationDTO.title)) {
                validator.throwClientErrorException(qualificationDTO?.id, 'title', 'Title is required.')
            } else if (qualificationDTO.title && qualificationDTO.title.length() > 255) {
                validator.throwClientErrorException(qualificationDTO?.id, 'title', "Title can't be more than 255 chars.")
            }

            if (trimToNull(qualificationDTO.qualLevel) != null && qualificationDTO.qualLevel.length() > 255) {
                validator.throwClientErrorException(qualificationDTO?.id, 'qualLevel', "Level can't be more than 255 chars.")
            }

            if (trimToNull(qualificationDTO.anzsco) && qualificationDTO.anzsco.length() > 6) {
                validator.throwClientErrorException(qualificationDTO?.id, 'title', "ANZSCO can't be more than 6 chars.")
            }

            if (trimToNull(qualificationDTO.fieldOfEducation) && qualificationDTO.fieldOfEducation.length() > 6) {
                validator.throwClientErrorException(qualificationDTO?.id, 'fieldOfEducation', "Field Of Education can't be more than 6 chars.")
            }
        }
    }

    @Override
    void validateModelBeforeRemove(Qualification entity) {
        if (!entity.isCustom) {
            validator.throwClientErrorException(entity?.id, 'id', "Only custom qualifications can be deleted.")
        }
        if (!entity.courses.empty || !entity.priorLearnings.empty) {
            validator.throwClientErrorException(entity?.id, 'id', "Assigned qualifications cannot be deleted.")
        }
    }
}
