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

import ish.common.types.MessageStatus
import ish.oncourse.server.api.v1.model.MessageStatusDTO

trait MessageStatusDTOTrait {

    MessageStatus getDbType() {
        switch (this as MessageStatusDTO) {
            case MessageStatusDTO.SENT:
                return MessageStatus.SENT
            case MessageStatusDTO.QUEUED:
                return MessageStatus.QUEUED
            case MessageStatusDTO.FAILED:
                return MessageStatus.FAILED
            default:
                throw new IllegalArgumentException("${toString()}")
        }
    }

    MessageStatusDTO fromDbType(MessageStatus dataType) {
        if(!dataType) {
            return null
        }
        switch (dataType) {
            case MessageStatus.SENT:
                return MessageStatusDTO.SENT
            case MessageStatus.QUEUED:
                return MessageStatusDTO.QUEUED
            case MessageStatus.FAILED:
                return MessageStatusDTO.FAILED
            default:
                throw new IllegalArgumentException("$dataType.displayName")
        }
    }
}
