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

import ish.common.types.CourseClassAttendanceType
import ish.oncourse.server.api.v1.model.CourseClassAttendanceTypeDTO

trait CourseClassAttendanceTypeDTOTrait {
    CourseClassAttendanceType getDbType() {
        switch (this as CourseClassAttendanceTypeDTO) {
            case CourseClassAttendanceTypeDTO.FULL_TIME_ATTENDANCE:
                return CourseClassAttendanceType.FULL_TIME_ATTENDANCE
            case CourseClassAttendanceTypeDTO.NO_INFORMATION:
                return CourseClassAttendanceType.NO_INFORMATION
            case CourseClassAttendanceTypeDTO.OUA_AND_NON_HIGHER_DEGREE_RESEARCH_STUDENT_USE_ONLY:
                return CourseClassAttendanceType.OUA_AND_NOT_HIGHER_DEGREE_REASEARCH_STUDENT_USE
            case CourseClassAttendanceTypeDTO.PART_TIME_ATTENDANCE:
                return CourseClassAttendanceType.PART_TIME_ATTENDANCE
            default:
                throw new IllegalArgumentException("${toString()}")
        }
    }

    CourseClassAttendanceTypeDTO fromDbType(CourseClassAttendanceType dataType) {
        switch (dataType) {
            case CourseClassAttendanceType.FULL_TIME_ATTENDANCE:
                return CourseClassAttendanceTypeDTO.FULL_TIME_ATTENDANCE
            case CourseClassAttendanceType.NO_INFORMATION:
                return CourseClassAttendanceTypeDTO.NO_INFORMATION
            case CourseClassAttendanceType.OUA_AND_NOT_HIGHER_DEGREE_REASEARCH_STUDENT_USE:
                return CourseClassAttendanceTypeDTO.OUA_AND_NON_HIGHER_DEGREE_RESEARCH_STUDENT_USE_ONLY
            case CourseClassAttendanceType.PART_TIME_ATTENDANCE:
                return CourseClassAttendanceTypeDTO.PART_TIME_ATTENDANCE
            default:
                throw new IllegalArgumentException("$dataType.displayName")
        }
    }
}
