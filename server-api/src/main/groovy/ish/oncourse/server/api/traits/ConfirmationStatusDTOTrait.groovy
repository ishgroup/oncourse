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

import ish.common.types.ConfirmationStatus
import ish.oncourse.server.api.v1.model.ConfirmationStatusDTO

trait ConfirmationStatusDTOTrait {

    ConfirmationStatus getDbType() {
        switch (this as ConfirmationStatusDTO) {
            case ConfirmationStatusDTO.NOT_SENT:
                return ConfirmationStatus.NOT_SENT
            case ConfirmationStatusDTO.SENT:
                return ConfirmationStatus.SENT
            case ConfirmationStatusDTO.DO_NOT_SEND:
                return ConfirmationStatus.DO_NOT_SEND
            default:
                throw new IllegalArgumentException("${toString()}")
        }
    }

    ConfirmationStatusDTO fromDbType(ConfirmationStatus dataType) {
        if(!dataType) {
            return null
        }
        switch(dataType) {
            case ConfirmationStatus.NOT_SENT:
                return ConfirmationStatusDTO.NOT_SENT
            case ConfirmationStatus.SENT:
                return ConfirmationStatusDTO.SENT
            case ConfirmationStatus.DO_NOT_SEND:
                return ConfirmationStatusDTO.DO_NOT_SEND
            default:
                throw new IllegalArgumentException("$dataType.displayName")
        }
    }

}
