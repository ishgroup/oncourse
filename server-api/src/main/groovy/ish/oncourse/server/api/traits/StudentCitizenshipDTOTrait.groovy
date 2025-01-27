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

import ish.common.types.StudentCitizenship
import ish.oncourse.server.api.v1.model.StudentCitizenshipDTO


trait StudentCitizenshipDTOTrait {

    StudentCitizenship getDbType() {
        switch (this as StudentCitizenshipDTO) {
            case StudentCitizenshipDTO.AUSTRALIAN_CITIZEN:
                return StudentCitizenship.AUSTRALIAN_CITIZEN
            case StudentCitizenshipDTO.NEW_ZEALAND_CITIZEN:
                return StudentCitizenship.NEW_ZELAND_CITIZEN
            case StudentCitizenshipDTO.STUDENTS_APPLICANTS_WITH_PERMANENT_VISA:
                return StudentCitizenship.STUDENT_WITH_PERMANENT_VISA
            case StudentCitizenshipDTO.STUDENTS_APPLICANTS_WITH_PERMANENT_HUMANITARIAN_VISA:
                return StudentCitizenship.STUDENT_WITH_PERMANENT_HUMANITARIAN_VISA
            case StudentCitizenshipDTO.STUDENT_APPLICANT_HAS_A_TEMPORARY_ENTRY_PERMIT:
                return StudentCitizenship.STUDENT_WITH_TEMPORARY_ENTRY_PERMIT
            case StudentCitizenshipDTO.NOT_ONE_OF_THE_ABOVE_CATEGORIES:
                return StudentCitizenship.NONE_OF_THE_ABOVE_CATEGORIES
            case StudentCitizenshipDTO.NO_INFORMATION:
                return StudentCitizenship.NO_INFORMATION
            case StudentCitizenshipDTO.STUDENTS_APPLICANTS_WITH_PACIFIC_ENGAGEMENT_VISA:
                return StudentCitizenship.STUDENT_WITH_PACIFIC_ENGAGEMENT_VISA
            default:
                throw new IllegalArgumentException("${toString()}")
        }
    }

    StudentCitizenshipDTO fromDbType(StudentCitizenship dataType) {
        if(!dataType) {
            return null
        }
        switch (dataType) {
            case StudentCitizenship.AUSTRALIAN_CITIZEN:
                return StudentCitizenshipDTO.AUSTRALIAN_CITIZEN
            case StudentCitizenship.NEW_ZELAND_CITIZEN:
                return StudentCitizenshipDTO.NEW_ZEALAND_CITIZEN
            case StudentCitizenship.STUDENT_WITH_PERMANENT_VISA:
                return StudentCitizenshipDTO.STUDENTS_APPLICANTS_WITH_PERMANENT_VISA
            case StudentCitizenship.STUDENT_WITH_PERMANENT_HUMANITARIAN_VISA:
                return StudentCitizenshipDTO.STUDENTS_APPLICANTS_WITH_PERMANENT_HUMANITARIAN_VISA
            case StudentCitizenship.STUDENT_WITH_TEMPORARY_ENTRY_PERMIT:
                return StudentCitizenshipDTO.STUDENT_APPLICANT_HAS_A_TEMPORARY_ENTRY_PERMIT
            case StudentCitizenship.NONE_OF_THE_ABOVE_CATEGORIES:
                return StudentCitizenshipDTO.NOT_ONE_OF_THE_ABOVE_CATEGORIES
            case StudentCitizenship.NO_INFORMATION:
                return StudentCitizenshipDTO.NO_INFORMATION
            case StudentCitizenship.STUDENT_WITH_PACIFIC_ENGAGEMENT_VISA:
                return StudentCitizenshipDTO.STUDENTS_APPLICANTS_WITH_PACIFIC_ENGAGEMENT_VISA
            default:
                throw new IllegalArgumentException("$dataType.displayName")
        }
    }
}
