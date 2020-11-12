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

import ish.common.types.AvetmissStudentLabourStatus
import ish.oncourse.server.api.v1.model.AvetmissStudentLabourStatusDTO

trait AvetmissStudentLabourStatusDTOTrait {

    AvetmissStudentLabourStatus getDbType() {
        switch (this as AvetmissStudentLabourStatusDTO) {
            case AvetmissStudentLabourStatusDTO.NOT_STATED:
                return AvetmissStudentLabourStatus.DEFAULT_POPUP_OPTION
            case AvetmissStudentLabourStatusDTO.UNPAID_FAMILY_WORKER:
                return AvetmissStudentLabourStatus.UNPAID_FAMILY_WORKER
            case AvetmissStudentLabourStatusDTO.FULL_TIME:
                return AvetmissStudentLabourStatus.FULL_TIME
            case AvetmissStudentLabourStatusDTO.UNEMPLOYED_NON_SEEKING:
                return AvetmissStudentLabourStatus.UNEMPLOYED_NOT_SEEKING
            case AvetmissStudentLabourStatusDTO.PART_TIME:
                return AvetmissStudentLabourStatus.PART_TIME
            case AvetmissStudentLabourStatusDTO.SELF_EMPLOYED:
                return AvetmissStudentLabourStatus.SELF_EMPLOYED
            case AvetmissStudentLabourStatusDTO.EMPLOYER:
                return AvetmissStudentLabourStatus.EMPLOYER
            case AvetmissStudentLabourStatusDTO.UNEMPLOYED_SEEKING_FULL_TIME:
                return AvetmissStudentLabourStatus.UNEMPLOYED_SEEKING_FULL_TIME
            case AvetmissStudentLabourStatusDTO.UNEMPLOYED_SEEKING_PART_TIME:
                return AvetmissStudentLabourStatus.UNEMPLOYED_SEEKING_PART_TIME
            default:
                throw new IllegalArgumentException("${toString()}")
        }
    }

    AvetmissStudentLabourStatusDTO fromDbType(AvetmissStudentLabourStatus dataType) {
        if(!dataType) {
            return null
        }
        switch (dataType) {
            case AvetmissStudentLabourStatus.DEFAULT_POPUP_OPTION:
                return AvetmissStudentLabourStatusDTO.NOT_STATED
            case AvetmissStudentLabourStatus.UNPAID_FAMILY_WORKER:
                return AvetmissStudentLabourStatusDTO.UNPAID_FAMILY_WORKER
            case AvetmissStudentLabourStatus.FULL_TIME:
                return AvetmissStudentLabourStatusDTO.FULL_TIME
            case AvetmissStudentLabourStatus.UNEMPLOYED_NOT_SEEKING:
                return AvetmissStudentLabourStatusDTO.UNEMPLOYED_NON_SEEKING
            case AvetmissStudentLabourStatus.PART_TIME:
                return AvetmissStudentLabourStatusDTO.PART_TIME
            case AvetmissStudentLabourStatus.SELF_EMPLOYED:
                return AvetmissStudentLabourStatusDTO.SELF_EMPLOYED
            case AvetmissStudentLabourStatus.EMPLOYER:
                return AvetmissStudentLabourStatusDTO.EMPLOYER
            case AvetmissStudentLabourStatus.UNEMPLOYED_SEEKING_FULL_TIME:
                return AvetmissStudentLabourStatusDTO.UNEMPLOYED_SEEKING_FULL_TIME
            case AvetmissStudentLabourStatus.UNEMPLOYED_SEEKING_PART_TIME:
                return AvetmissStudentLabourStatusDTO.UNEMPLOYED_SEEKING_PART_TIME
            default:
                throw new IllegalArgumentException("$dataType.displayName")
        }
    }
}
