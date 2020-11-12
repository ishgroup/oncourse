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

import ish.common.types.MessageType
import ish.oncourse.server.api.v1.model.MessageTypeDTO

trait MessageTypeDTOTrait {

    MessageType getDbType() {
        switch (this as MessageTypeDTO) {
            case MessageTypeDTO.EMAIL:
                return MessageType.EMAIL
            case MessageTypeDTO.SMS:
                return MessageType.SMS
            case MessageTypeDTO.POST:
                return MessageType.POST
            default:
                throw new IllegalArgumentException("${toString()}")
        }
    }

    MessageTypeDTO fromDbType(MessageType dataType) {
        if (!dataType) {
            return null
        }
        switch (dataType) {
            case MessageType.EMAIL:
                return MessageTypeDTO.EMAIL
            case MessageType.SMS:
                return MessageTypeDTO.SMS
            case MessageType.POST:
                return MessageTypeDTO.POST
            default:
                throw new IllegalArgumentException("$dataType.displayName")
        }
    }
}
