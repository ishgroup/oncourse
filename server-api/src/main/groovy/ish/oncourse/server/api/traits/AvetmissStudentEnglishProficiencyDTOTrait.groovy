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

import ish.common.types.AvetmissStudentEnglishProficiency
import ish.oncourse.server.api.v1.model.AvetmissStudentEnglishProficiencyDTO

trait AvetmissStudentEnglishProficiencyDTOTrait {

    AvetmissStudentEnglishProficiency getDbType() {
        switch (this as AvetmissStudentEnglishProficiencyDTO) {
            case AvetmissStudentEnglishProficiencyDTO.NOT_STATED:
                return AvetmissStudentEnglishProficiency.DEFAULT_POPUP_OPTION
            case AvetmissStudentEnglishProficiencyDTO.VERY_WELL:
                return AvetmissStudentEnglishProficiency.VERY_WELL
            case AvetmissStudentEnglishProficiencyDTO.WELL:
                return AvetmissStudentEnglishProficiency.WELL
            case AvetmissStudentEnglishProficiencyDTO.NOT_WELL:
                return AvetmissStudentEnglishProficiency.NOT_WELL
            case AvetmissStudentEnglishProficiencyDTO.NOT_AT_ALL:
                return AvetmissStudentEnglishProficiency.NOT_AT_ALL
            default:
                throw new IllegalArgumentException("${toString()}")
        }
    }

    AvetmissStudentEnglishProficiencyDTO fromDbType(AvetmissStudentEnglishProficiency dataType) {
        if(!dataType) {
            return null
        }
        switch (dataType) {
            case AvetmissStudentEnglishProficiency.DEFAULT_POPUP_OPTION:
                return AvetmissStudentEnglishProficiencyDTO.NOT_STATED
            case AvetmissStudentEnglishProficiency.VERY_WELL:
                return AvetmissStudentEnglishProficiencyDTO.VERY_WELL
            case AvetmissStudentEnglishProficiency.WELL:
                return AvetmissStudentEnglishProficiencyDTO.WELL
            case AvetmissStudentEnglishProficiency.NOT_WELL:
                return AvetmissStudentEnglishProficiencyDTO.NOT_WELL
            case AvetmissStudentEnglishProficiency.NOT_AT_ALL:
                return AvetmissStudentEnglishProficiencyDTO.NOT_AT_ALL
            default:
                throw new IllegalArgumentException("$dataType.displayName")
        }
    }

}
