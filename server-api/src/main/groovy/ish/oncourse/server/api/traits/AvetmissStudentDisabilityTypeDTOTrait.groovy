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

package ish.oncourse.server.api.traits

import ish.common.types.AvetmissStudentDisabilityType
import ish.oncourse.server.api.v1.model.AvetmissStudentDisabilityTypeDTO

trait AvetmissStudentDisabilityTypeDTOTrait {

    AvetmissStudentDisabilityType getDbType() {
        switch (this as AvetmissStudentDisabilityTypeDTO) {
            case AvetmissStudentDisabilityTypeDTO.NOT_STATED:
                return AvetmissStudentDisabilityType.DEFAULT_POPUP_OPTION
            case AvetmissStudentDisabilityTypeDTO.NONE:
                return AvetmissStudentDisabilityType.NONE
            case AvetmissStudentDisabilityTypeDTO.HEARING_DEAF:
                return AvetmissStudentDisabilityType.HEARING
            case AvetmissStudentDisabilityTypeDTO.PHYSICAL:
                return AvetmissStudentDisabilityType.PHYSICAL
            case AvetmissStudentDisabilityTypeDTO.INTELLECTUAL:
                return AvetmissStudentDisabilityType.INTELLECTUAL
            case AvetmissStudentDisabilityTypeDTO.LEARNING:
                return AvetmissStudentDisabilityType.LEARNING
            case AvetmissStudentDisabilityTypeDTO.MENTAL_ILLNESS:
                return AvetmissStudentDisabilityType.MENTAL
            case AvetmissStudentDisabilityTypeDTO.ACQUIRED_BRAIN_IMPAIRMENT:
                return AvetmissStudentDisabilityType.BRAIN_IMPAIRMENT
            case AvetmissStudentDisabilityTypeDTO.VISION:
                return AvetmissStudentDisabilityType.VISION
            case AvetmissStudentDisabilityTypeDTO.MEDICAL_CONDITION:
                return AvetmissStudentDisabilityType.MEDICAL_CONDITION
            case AvetmissStudentDisabilityTypeDTO.OTHER:
                return AvetmissStudentDisabilityType.OTHER
            default:
                throw new IllegalArgumentException("${toString()}")
        }
    }

    AvetmissStudentDisabilityTypeDTO fromDbType(AvetmissStudentDisabilityType dataType) {
        if(!dataType) {
            return null
        }
        switch (dataType) {
            case AvetmissStudentDisabilityType.DEFAULT_POPUP_OPTION:
                return AvetmissStudentDisabilityTypeDTO.NOT_STATED
            case AvetmissStudentDisabilityType.NONE:
                return AvetmissStudentDisabilityTypeDTO.NONE
            case AvetmissStudentDisabilityType.HEARING:
                return AvetmissStudentDisabilityTypeDTO.HEARING_DEAF
            case AvetmissStudentDisabilityType.PHYSICAL:
                return AvetmissStudentDisabilityTypeDTO.PHYSICAL
            case AvetmissStudentDisabilityType.INTELLECTUAL:
                return AvetmissStudentDisabilityTypeDTO.INTELLECTUAL
            case AvetmissStudentDisabilityType.LEARNING:
                return AvetmissStudentDisabilityTypeDTO.LEARNING
            case AvetmissStudentDisabilityType.MENTAL:
                return AvetmissStudentDisabilityTypeDTO.MENTAL_ILLNESS
            case AvetmissStudentDisabilityType.BRAIN_IMPAIRMENT:
                return AvetmissStudentDisabilityTypeDTO.ACQUIRED_BRAIN_IMPAIRMENT
            case AvetmissStudentDisabilityType.VISION:
                return AvetmissStudentDisabilityTypeDTO.VISION
            case AvetmissStudentDisabilityType.MEDICAL_CONDITION:
                return AvetmissStudentDisabilityTypeDTO.MEDICAL_CONDITION
            case AvetmissStudentDisabilityType.OTHER:
                return AvetmissStudentDisabilityTypeDTO.OTHER
            default:
                throw new IllegalArgumentException("$dataType.displayName")

        }
    }
}
