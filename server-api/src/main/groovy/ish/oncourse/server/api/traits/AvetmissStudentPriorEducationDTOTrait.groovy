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


import ish.common.types.AvetmissStudentPriorEducation
import ish.oncourse.server.api.v1.model.AvetmissStudentPriorEducationDTO

trait AvetmissStudentPriorEducationDTOTrait {

    AvetmissStudentPriorEducation getDbType() {
        switch (this as AvetmissStudentPriorEducationDTO) {
            case AvetmissStudentPriorEducationDTO.NOT_STATED:
                return AvetmissStudentPriorEducation.DEFAULT_POPUP_OPTION
            case AvetmissStudentPriorEducationDTO.BACHELOR_DEGREE_OR_HIGHER_DEGREE_LEVEL:
                return AvetmissStudentPriorEducation.BACHELOR
            case AvetmissStudentPriorEducationDTO.ADVANCED_DIPLOMA_OR_ASSOCIATE_DEGREE_LEVEL:
                return AvetmissStudentPriorEducation.ADVANCED_DIPLOMA
            case AvetmissStudentPriorEducationDTO.DIPLOMA_LEVEL:
                return AvetmissStudentPriorEducation.DIPLOMA
            case AvetmissStudentPriorEducationDTO.CERTIFICATE_IV:
                return AvetmissStudentPriorEducation.CERTIFICATE_IV
            case AvetmissStudentPriorEducationDTO.CERTIFICATE_III:
                return AvetmissStudentPriorEducation.CERTIFICATE_III
            case AvetmissStudentPriorEducationDTO.CERTIFICATE_II:
                return AvetmissStudentPriorEducation.CERTIFICATE_II
            case AvetmissStudentPriorEducationDTO.CERTIFICATE_I:
                return AvetmissStudentPriorEducation.CERTIFICATE_I
            case AvetmissStudentPriorEducationDTO.MISCELLANEOUS_EDUCATION:
                return AvetmissStudentPriorEducation.MISC
            case AvetmissStudentPriorEducationDTO.NONE:
                return AvetmissStudentPriorEducation.NONE
            default:
                throw new IllegalArgumentException("${toString()}")
        }
    }

    AvetmissStudentPriorEducationDTO fromDbType(AvetmissStudentPriorEducation dataType) {
        if(!dataType) {
            return null
        }
        switch (dataType) {
            case AvetmissStudentPriorEducation.DEFAULT_POPUP_OPTION:
                return AvetmissStudentPriorEducationDTO.NOT_STATED
            case AvetmissStudentPriorEducation.BACHELOR:
                return AvetmissStudentPriorEducationDTO.BACHELOR_DEGREE_OR_HIGHER_DEGREE_LEVEL
            case AvetmissStudentPriorEducation.ADVANCED_DIPLOMA:
                return AvetmissStudentPriorEducationDTO.ADVANCED_DIPLOMA_OR_ASSOCIATE_DEGREE_LEVEL
            case AvetmissStudentPriorEducation.DIPLOMA:
                return AvetmissStudentPriorEducationDTO.DIPLOMA_LEVEL
            case AvetmissStudentPriorEducation.CERTIFICATE_IV:
                return AvetmissStudentPriorEducationDTO.CERTIFICATE_IV
            case AvetmissStudentPriorEducation.CERTIFICATE_III:
                return AvetmissStudentPriorEducationDTO.CERTIFICATE_III
            case AvetmissStudentPriorEducation.CERTIFICATE_II:
                return AvetmissStudentPriorEducationDTO.CERTIFICATE_II
            case AvetmissStudentPriorEducation.CERTIFICATE_I:
                return AvetmissStudentPriorEducationDTO.CERTIFICATE_I
            case AvetmissStudentPriorEducation.MISC:
                return AvetmissStudentPriorEducationDTO.MISCELLANEOUS_EDUCATION
            case AvetmissStudentPriorEducation.NONE:
                return AvetmissStudentPriorEducationDTO.NONE
            default:
                throw new IllegalArgumentException("$dataType.displayName")
        }
    }

}
