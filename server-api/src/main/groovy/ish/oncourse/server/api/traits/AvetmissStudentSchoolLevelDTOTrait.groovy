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

import ish.common.types.AvetmissStudentSchoolLevel
import ish.oncourse.server.api.v1.model.AvetmissStudentSchoolLevelDTO

trait AvetmissStudentSchoolLevelDTOTrait {

    AvetmissStudentSchoolLevel getDbType() {
        switch (this as AvetmissStudentSchoolLevelDTO) {
            case AvetmissStudentSchoolLevelDTO.NOT_STATED:
                return AvetmissStudentSchoolLevel.DEFAULT_POPUP_OPTION
            case AvetmissStudentSchoolLevelDTO.DID_NOT_GO_TO_SCHOOL:
                return AvetmissStudentSchoolLevel.DID_NOT_GO_TO_SCHOOL
            case AvetmissStudentSchoolLevelDTO.YEAR_8_OR_BELOW:
                return AvetmissStudentSchoolLevel.COMPLETED_YEAR_8_OR_BELOW
            case AvetmissStudentSchoolLevelDTO.YEAR_9:
                return AvetmissStudentSchoolLevel.COMPLETED_YEAR_9
            case AvetmissStudentSchoolLevelDTO.YEAR_10:
                return AvetmissStudentSchoolLevel.COMPLETED_YEAR_10
            case AvetmissStudentSchoolLevelDTO.YEAR_11:
                return AvetmissStudentSchoolLevel.COMPLETED_YEAR_11
            case AvetmissStudentSchoolLevelDTO.YEAR_12:
                return AvetmissStudentSchoolLevel.COMPLETED_YEAR_12
            default:
                throw new IllegalArgumentException("${toString()}")
        }
    }

    AvetmissStudentSchoolLevelDTO fromDbType(AvetmissStudentSchoolLevel dataType) {
        if(!dataType) {
            return null
        }
        switch (dataType) {
            case AvetmissStudentSchoolLevel.DEFAULT_POPUP_OPTION:
                return AvetmissStudentSchoolLevelDTO.NOT_STATED
            case AvetmissStudentSchoolLevel.DID_NOT_GO_TO_SCHOOL:
                return AvetmissStudentSchoolLevelDTO.DID_NOT_GO_TO_SCHOOL
            case AvetmissStudentSchoolLevel.COMPLETED_YEAR_8_OR_BELOW:
                return AvetmissStudentSchoolLevelDTO.YEAR_8_OR_BELOW
            case AvetmissStudentSchoolLevel.COMPLETED_YEAR_9:
                return AvetmissStudentSchoolLevelDTO.YEAR_9
            case AvetmissStudentSchoolLevel.COMPLETED_YEAR_10:
                return AvetmissStudentSchoolLevelDTO.YEAR_10
            case AvetmissStudentSchoolLevel.COMPLETED_YEAR_11:
                return AvetmissStudentSchoolLevelDTO.YEAR_11
            case AvetmissStudentSchoolLevel.COMPLETED_YEAR_12:
                return AvetmissStudentSchoolLevelDTO.YEAR_12
            default:
                throw new IllegalArgumentException("$dataType.displayName")
        }
    }

}
