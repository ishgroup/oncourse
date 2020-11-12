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

import ish.common.types.EnrolmentStatus
import ish.oncourse.server.api.v1.model.EnrolmentStatusDTO

trait EnrolmentStatusDTOTrait {

    EnrolmentStatus getDbType() {
        switch (this as EnrolmentStatusDTO) {
            case EnrolmentStatusDTO.EMPTY:
                return EnrolmentStatus.CORRUPTED
            case EnrolmentStatusDTO.NOT_PROCESSED:
                return EnrolmentStatus.NEW
            case EnrolmentStatusDTO.AWAITING_CONFIRMATION:
                return EnrolmentStatus.QUEUED
            case EnrolmentStatusDTO.IN_TRANSACTION:
                return EnrolmentStatus.IN_TRANSACTION
            case EnrolmentStatusDTO.ACTIVE:
                return EnrolmentStatus.SUCCESS
            case EnrolmentStatusDTO.FAILED:
                return EnrolmentStatus.FAILED
            case EnrolmentStatusDTO.CARD_DECLINED:
                return EnrolmentStatus.FAILED_CARD_DECLINED
            case EnrolmentStatusDTO.CANCELLED:
                return EnrolmentStatus.CANCELLED
            case EnrolmentStatusDTO.FAILED_NO_PLACES:
                return EnrolmentStatus.FAILED_NO_PLACES
            case EnrolmentStatusDTO.CREDITED:
                return EnrolmentStatus.REFUNDED
            default:
                throw new IllegalArgumentException("${toString()}")
        }
    }

    EnrolmentStatusDTO fromDbType(EnrolmentStatus dataType) {
        if(!dataType) {
            return null
        }
        switch(dataType) {
            case EnrolmentStatus.CORRUPTED:
                return EnrolmentStatusDTO.EMPTY
            case EnrolmentStatus.NEW:
                return EnrolmentStatusDTO.NOT_PROCESSED
            case EnrolmentStatus.QUEUED:
                return EnrolmentStatusDTO.AWAITING_CONFIRMATION
            case EnrolmentStatus.IN_TRANSACTION:
                return EnrolmentStatusDTO.IN_TRANSACTION
            case EnrolmentStatus.SUCCESS:
                return EnrolmentStatusDTO.ACTIVE
            case EnrolmentStatus.FAILED:
                return EnrolmentStatusDTO.FAILED
            case EnrolmentStatus.FAILED_CARD_DECLINED:
                return EnrolmentStatusDTO.CARD_DECLINED
            case EnrolmentStatus.CANCELLED:
                return EnrolmentStatusDTO.CANCELLED
            case EnrolmentStatus.FAILED_NO_PLACES:
                return EnrolmentStatusDTO.FAILED_NO_PLACES
            case EnrolmentStatus.REFUNDED:
                return EnrolmentStatusDTO.CREDITED
            default:
                throw new IllegalArgumentException("$dataType.displayName")
        }
    }

}
