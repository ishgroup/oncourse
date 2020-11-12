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

import ish.common.types.AvetmissStudentIndigenousStatus
import ish.oncourse.server.api.v1.model.AvetmissStudentIndigenousStatusDTO

trait AvetmissStudentIndigenousStatusDTOTrait {

    AvetmissStudentIndigenousStatus getDbType() {
        switch (this as AvetmissStudentIndigenousStatusDTO) {
            case AvetmissStudentIndigenousStatusDTO.NOT_STATED:
                return AvetmissStudentIndigenousStatus.DEFAULT_POPUP_OPTION
            case AvetmissStudentIndigenousStatusDTO.ABORIGINAL:
                return AvetmissStudentIndigenousStatus.ABORIGINAL
            case AvetmissStudentIndigenousStatusDTO.TORRES_STRAIT_ISLANDER:
                return AvetmissStudentIndigenousStatus.TORRES
            case AvetmissStudentIndigenousStatusDTO.ABORIGINAL_AND_TORRES_STRAIT_ISLANDER:
                return AvetmissStudentIndigenousStatus.ABORIGINAL_AND_TORRES
            case AvetmissStudentIndigenousStatusDTO.NEITHER:
                return AvetmissStudentIndigenousStatus.NEITHER
            default:
                throw new IllegalArgumentException("${toString()}")
        }
    }

    AvetmissStudentIndigenousStatusDTO fromDbType(AvetmissStudentIndigenousStatus dataType) {
        if(!dataType) {
            return null
        }
        switch (dataType) {
            case AvetmissStudentIndigenousStatus.DEFAULT_POPUP_OPTION:
                return AvetmissStudentIndigenousStatusDTO.NOT_STATED
            case AvetmissStudentIndigenousStatus.ABORIGINAL:
                return AvetmissStudentIndigenousStatusDTO.ABORIGINAL
            case AvetmissStudentIndigenousStatus.TORRES:
                return AvetmissStudentIndigenousStatusDTO.TORRES_STRAIT_ISLANDER
            case AvetmissStudentIndigenousStatus.ABORIGINAL_AND_TORRES:
                return AvetmissStudentIndigenousStatusDTO.ABORIGINAL_AND_TORRES_STRAIT_ISLANDER
            case AvetmissStudentIndigenousStatus.NEITHER:
                return AvetmissStudentIndigenousStatusDTO.NEITHER
            default:
                throw new IllegalArgumentException("$dataType.displayName")
        }
    }

}
