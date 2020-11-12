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

import ish.common.types.ClassCostRepetitionType
import ish.oncourse.server.api.v1.model.ClassCostRepetitionTypeDTO

trait ClassCostRepetitionTypeDTOTrait {
    ClassCostRepetitionType getDbType() {
        switch (this as ClassCostRepetitionTypeDTO) {
            case ClassCostRepetitionTypeDTO.DISCOUNT:
                return ClassCostRepetitionType.DISCOUNT
            case ClassCostRepetitionTypeDTO.FIXED:
                return ClassCostRepetitionType.FIXED
            case ClassCostRepetitionTypeDTO.PER_ENROLMENT:
                return ClassCostRepetitionType.PER_ENROLMENT
            case ClassCostRepetitionTypeDTO.PER_SESSION:
                return ClassCostRepetitionType.PER_SESSION
            case ClassCostRepetitionTypeDTO.PER_STUDENT_CONTACT_HOUR:
                return ClassCostRepetitionType.PER_STUDENT_CONTACT_HOUR
            case ClassCostRepetitionTypeDTO.PER_TIMETABLED_HOUR:
                return ClassCostRepetitionType.PER_TIMETABLED_HOUR
            case ClassCostRepetitionTypeDTO.PER_UNIT:
                return ClassCostRepetitionType.PER_UNIT
            default:
                throw new IllegalArgumentException("${toString()}")
        }
    }

    ClassCostRepetitionTypeDTO fromDbType(ClassCostRepetitionType dataType) {
        switch (dataType) {
            case ClassCostRepetitionType.DISCOUNT:
                return ClassCostRepetitionTypeDTO.DISCOUNT
            case ClassCostRepetitionType.FIXED:
                return ClassCostRepetitionTypeDTO.FIXED
            case ClassCostRepetitionType.PER_ENROLMENT:
                return ClassCostRepetitionTypeDTO.PER_ENROLMENT
            case ClassCostRepetitionType.PER_SESSION:
                return ClassCostRepetitionTypeDTO.PER_SESSION
            case ClassCostRepetitionType.PER_STUDENT_CONTACT_HOUR:
                return ClassCostRepetitionTypeDTO.PER_STUDENT_CONTACT_HOUR
            case ClassCostRepetitionType.PER_TIMETABLED_HOUR:
                return ClassCostRepetitionTypeDTO.PER_TIMETABLED_HOUR
            case ClassCostRepetitionType.PER_UNIT:
                return ClassCostRepetitionTypeDTO.PER_UNIT
            default:
                throw new IllegalArgumentException("$dataType.displayName")
        }
    }
}
