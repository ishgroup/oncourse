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


import ish.common.types.UsiStatus
import ish.oncourse.server.api.v1.model.UsiStatusDTO

trait UsiStatusDTOTrait {

    UsiStatus getDbType() {
        switch (this as UsiStatusDTO) {
            case UsiStatusDTO.NOT_SUPPLIED:
                return UsiStatus.DEFAULT_NOT_SUPPLIED
            case UsiStatusDTO.NOT_VERIFIED:
                return UsiStatus.NON_VERIFIED
            case UsiStatusDTO.VERIFIED:
                return UsiStatus.VERIFIED
            case UsiStatusDTO.EXEMPTION:
                return UsiStatus.EXEMPTION
            case UsiStatusDTO.INTERNATIONAL:
                return UsiStatus.INTERNATIONAL
            default:
                throw new IllegalArgumentException("${toString()}")
        }
    }

    UsiStatusDTO fromDbType(UsiStatus dataType) {
        if(!dataType) {
            return null
        }
        switch (dataType) {
            case UsiStatus.DEFAULT_NOT_SUPPLIED:
                return UsiStatusDTO.NOT_SUPPLIED
            case UsiStatus.NON_VERIFIED:
                return UsiStatusDTO.NOT_VERIFIED
            case UsiStatus.VERIFIED:
                return UsiStatusDTO.VERIFIED
            case UsiStatus.EXEMPTION:
                return UsiStatusDTO.EXEMPTION
            case UsiStatus.INTERNATIONAL:
                return UsiStatusDTO.INTERNATIONAL
            default:
                throw new IllegalArgumentException("$dataType.displayName")
        }
    }

}
