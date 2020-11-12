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

import ish.common.types.AttendanceType
import ish.common.types.ClassCostRepetitionType
import ish.oncourse.server.api.v1.model.AttendanceTypeDTO
import ish.oncourse.server.api.v1.model.ClassCostRepetitionTypeDTO

trait AttendanceTypeDTOTrait {

    AttendanceType getDbType() {
        switch (this as AttendanceTypeDTO) {
            case AttendanceTypeDTO.UNMARKED:
                return AttendanceType.UNMARKED
            case AttendanceTypeDTO.ATTENDED:
                return AttendanceType.ATTENDED
            case AttendanceTypeDTO.ABSENT_WITH_REASON:
                return AttendanceType.DID_NOT_ATTEND_WITH_REASON
            case AttendanceTypeDTO.ABSENT_WITHOUT_REASON:
                return AttendanceType.DID_NOT_ATTEND_WITHOUT_REASON
            case AttendanceTypeDTO.PARTIAL:
                return AttendanceType.PARTIAL
            default:
                throw new IllegalArgumentException("${toString()}")
        }
    }

    AttendanceTypeDTO fromDbType(AttendanceType dataType) {
        switch (dataType) {
            case AttendanceType.UNMARKED:
                return AttendanceTypeDTO.UNMARKED
            case AttendanceType.ATTENDED:
                return AttendanceTypeDTO.ATTENDED
            case AttendanceType.DID_NOT_ATTEND_WITH_REASON:
                return AttendanceTypeDTO.ABSENT_WITH_REASON
            case AttendanceType.DID_NOT_ATTEND_WITHOUT_REASON:
                return AttendanceTypeDTO.ABSENT_WITHOUT_REASON
            case AttendanceType.PARTIAL:
                return AttendanceTypeDTO.PARTIAL
            default:
                throw new IllegalArgumentException("$dataType.displayName")
        }
    }

}
