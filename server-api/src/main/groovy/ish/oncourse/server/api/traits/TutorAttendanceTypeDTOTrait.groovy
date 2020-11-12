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
import ish.oncourse.server.api.v1.model.TutorAttendanceTypeDTO

trait TutorAttendanceTypeDTOTrait {

    AttendanceType getDbType() {
        switch (this as TutorAttendanceTypeDTO) {
            case TutorAttendanceTypeDTO.CONFIRMED_FOR_PAYROLL:
                return AttendanceType.ATTENDED
            case TutorAttendanceTypeDTO.REJECTED_FOR_PAYROLL:
                return  AttendanceType.DID_NOT_ATTEND_WITHOUT_REASON
            case TutorAttendanceTypeDTO.NOT_CONFIRMED_FOR_PAYROLL:
                return  AttendanceType.UNMARKED
            default:
                throw new IllegalArgumentException("${toString()}")
        }
    }

    TutorAttendanceTypeDTO fromDbType(AttendanceType dataType) {
        switch (dataType) {
            case AttendanceType.PARTIAL:
            case AttendanceType.ATTENDED:
            case AttendanceType.DID_NOT_ATTEND_WITH_REASON:
                return TutorAttendanceTypeDTO.CONFIRMED_FOR_PAYROLL
            case AttendanceType.DID_NOT_ATTEND_WITHOUT_REASON:
                return TutorAttendanceTypeDTO.REJECTED_FOR_PAYROLL
            case AttendanceType.UNMARKED:
                return TutorAttendanceTypeDTO.NOT_CONFIRMED_FOR_PAYROLL
            default:
                throw new IllegalArgumentException("$dataType.displayName")
        }
    }


}
