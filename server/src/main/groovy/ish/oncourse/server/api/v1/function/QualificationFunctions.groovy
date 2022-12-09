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

import static ish.common.types.QualificationType.COURSE_TYPE
import static ish.common.types.QualificationType.HIGHER_TYPE
import static ish.common.types.QualificationType.QUALIFICATION_TYPE
import static ish.common.types.QualificationType.SKILLSET_LOCAL_TYPE
import static ish.common.types.QualificationType.SKILLSET_TYPE
import ish.oncourse.server.api.BidiMap
import ish.oncourse.server.api.v1.model.QualificationDTO
import ish.oncourse.server.api.v1.model.QualificationTypeDTO
import ish.oncourse.server.api.v1.model.ValidationErrorDTO
import ish.oncourse.server.cayenne.Qualification
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.ObjectSelect
import static org.apache.commons.lang3.StringUtils.trimToNull

import java.time.ZoneOffset

class QualificationFunctions {

    private static final BidiMap<ish.common.types.QualificationType, QualificationTypeDTO> qualificationTypeMap = new BidiMap() {{
        put(QUALIFICATION_TYPE, QualificationTypeDTO.QUALIFICATION)
        put(COURSE_TYPE, QualificationTypeDTO.ACCREDITED_COURSE)
        put(SKILLSET_TYPE, QualificationTypeDTO.SKILL_SET)
        put(SKILLSET_LOCAL_TYPE, QualificationTypeDTO.LOCAL_SKILL_SET)
        put(HIGHER_TYPE, QualificationTypeDTO.HIGHER_EDUCATION)
    }}

    static QualificationDTO toQualificationModel(Qualification qualification) {
        QualificationDTO model = new QualificationDTO()
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

    static void toDbQualification(QualificationDTO model, Qualification qualification, boolean isCustomModule = true) {
        if (isCustomModule) {
            qualification.type = qualificationTypeMap.getByValue(model.type)
            qualification.isCustom = true
            qualification.nationalCode = trimToNull(model.nationalCode)
            qualification.title = trimToNull(model.title)
            qualification.fieldOfEducation = trimToNull(model.fieldOfEducation)
            qualification.level = trimToNull(model.qualLevel)
            qualification.anzsco = trimToNull(model.anzsco)

            qualification.newApprenticeship = trimToNull(model.newApprenticeship)
            qualification.reviewDate = model.reviewDate
        }

        qualification.isOffered = model.isIsOffered()
        qualification.nominalHours = model.nominalHours
        qualification.specialization = trimToNull(model.specialization)
    }

    static ValidationErrorDTO validateModelRequiredFields(QualificationDTO qualification, ObjectContext context, boolean isCustomModule = true, Long qualId = null) {
        if (isCustomModule) {
            if (!qualification.type) {
                return new ValidationErrorDTO(qualification?.id?.toString(), 'type', 'Type is required.')
            } else if (!qualificationTypeMap.containsValue(qualification.type)) {
                return new ValidationErrorDTO(qualification?.id?.toString(), 'type', 'Unrecognized qualification type.')
            }

            if (!trimToNull(qualification.nationalCode)) {
                return new ValidationErrorDTO(qualification?.id?.toString(), 'nationalCode', 'National code is required.')
            } else if (qualification.nationalCode.length() > 12) {
                return new ValidationErrorDTO(qualification?.id?.toString(), 'nationalCode', "National code can't be more than 12 chars.")
            }

            Long qId = ObjectSelect.query(Qualification)
                    .where(Qualification.NATIONAL_CODE.eq(qualification.nationalCode))
                    .selectOne(context)?.id

            if (qId && qId != qualId) {
                return new ValidationErrorDTO(qualification?.id?.toString(), 'nationalCode', 'National code must be unique.')
            }

            if (!trimToNull(qualification.title)) {
                return new ValidationErrorDTO(qualification?.id?.toString(), 'title', 'Title is required.')
            } else if (qualification.title && qualification.title .length() > 255) {
                return new ValidationErrorDTO(qualification?.id?.toString(), 'title', "Title can't be more than 255 chars.")
            }

            if (trimToNull(qualification.qualLevel) != null && qualification.qualLevel .length() > 255) {
                return new ValidationErrorDTO(qualification?.id?.toString(), 'qualLevel', "Level can't be more than 255 chars.")
            }

            if (trimToNull(qualification.anzsco) && qualification.anzsco .length() > 6) {
                return new ValidationErrorDTO(qualification?.id?.toString(), 'title', "ANZSCO can't be more than 6 chars.")
            }

            if (trimToNull(qualification.fieldOfEducation) && qualification.fieldOfEducation.length() > 6) {
                return new ValidationErrorDTO(qualification?.id?.toString(), 'fieldOfEducation', "Field Of Education can't be more than 6 chars.")
            }
        }


        if (qualification.nominalHours && qualification.nominalHours <= BigDecimal.ZERO) {
            return new ValidationErrorDTO(qualification?.id?.toString(), 'nominalHours', "Nominal hours must be greater than 0.")
        }

        if (trimToNull(qualification.specialization) && qualification.specialization.length() > 128) {
            return new ValidationErrorDTO(qualification?.id?.toString(), 'specialization', "Specialization must be less than 128 chars.")
        }

        if (qualification.isIsOffered() == null) {
            return new ValidationErrorDTO(qualification?.id?.toString(), 'isOffered', "Flag \'Is offered\' is required.")
        }
        null
    }

    static ValidationErrorDTO validateValues(QualificationDTO model, Qualification entity) {
        if (model.nationalCode && model.nationalCode != entity.nationalCode) {
            return new ValidationErrorDTO(entity?.id?.toString(), 'id', "Qualification national code cannot be updated.")
        }
        null
    }

    static ValidationErrorDTO validateForDelete(Qualification entity) {
        if (!entity.isCustom) {
            return new ValidationErrorDTO(entity?.id?.toString(), 'id', "Only custom qualifications can be deleted.")
        }
        if (!entity.courses.empty || !entity.priorLearnings.empty) {
            return new ValidationErrorDTO(entity?.id?.toString(), 'id', "Assigned qualifications cannot be deleted.")
        }
        null
    }
}
