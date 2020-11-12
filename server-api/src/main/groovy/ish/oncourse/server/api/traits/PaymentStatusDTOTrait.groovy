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

import ish.common.types.PaymentStatus
import ish.oncourse.server.api.v1.model.PaymentStatusDTO

trait PaymentStatusDTOTrait {
    PaymentStatus getDbType() {
        switch (this as PaymentStatusDTO) {
            case PaymentStatusDTO.CARD_DECLINED:
                return PaymentStatus.FAILED_CARD_DECLINED
            case PaymentStatusDTO.CREDIT_CARD_DETAILS_REQUIRED:
                return PaymentStatus.CARD_DETAILS_REQUIRED
            case PaymentStatusDTO.FAILED:
                return PaymentStatus.FAILED
            case PaymentStatusDTO.IN_TRANSACTION:
                return PaymentStatus.IN_TRANSACTION
            case PaymentStatusDTO.SUCCESS:
                return PaymentStatus.SUCCESS
            case PaymentStatusDTO.REJECTED_NO_PLACES_AVAILABLE:
                return PaymentStatus.FAILED_NO_PLACES
            case PaymentStatusDTO.QUEUED:
                return PaymentStatus.QUEUED
            default:
                throw new IllegalArgumentException("${toString()}")
        }
    }

    PaymentStatusDTO fromDbType(PaymentStatus dataType) {
        if(!dataType) {
            return null
        }
        switch (dataType) {
            case PaymentStatus.FAILED_CARD_DECLINED:
                return PaymentStatusDTO.CARD_DECLINED
            case PaymentStatus.CARD_DETAILS_REQUIRED:
                return PaymentStatusDTO.CREDIT_CARD_DETAILS_REQUIRED
            case PaymentStatus.FAILED:
                return PaymentStatusDTO.FAILED
            case PaymentStatus.IN_TRANSACTION:
                return PaymentStatusDTO.IN_TRANSACTION
            case PaymentStatus.SUCCESS:
                return PaymentStatusDTO.SUCCESS
            case PaymentStatus.FAILED_NO_PLACES:
                return PaymentStatusDTO.REJECTED_NO_PLACES_AVAILABLE
            case PaymentStatus.QUEUED:
                return PaymentStatusDTO.QUEUED
            default:
                throw new IllegalArgumentException("$dataType.displayName")
        }
    }
}
