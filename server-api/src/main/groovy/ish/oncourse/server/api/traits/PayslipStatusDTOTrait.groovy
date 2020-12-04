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

import ish.common.types.PayslipStatus
import ish.oncourse.server.api.v1.model.PayslipStatusDTO

trait PayslipStatusDTOTrait {

    PayslipStatus getDbType() {
        switch (this as PayslipStatusDTO) {
            case PayslipStatusDTO.NEW:
                return PayslipStatus.HOLLOW
            case PayslipStatusDTO.COMPLETED:
                return PayslipStatus.COMPLETED
            case PayslipStatusDTO.APPROVED:
                return PayslipStatus.APPROVED
            case PayslipStatusDTO.PAID_EXPORTED:
                return PayslipStatus.FINALISED
            default:
                throw new IllegalArgumentException("Wrong payslip status: ${toString()}")
        }
    }

    PayslipStatusDTO fromDbType(PayslipStatus status) {
        if (!status) {
            return null
        }
        switch (status) {
            case PayslipStatus.HOLLOW:
                return PayslipStatusDTO.NEW
            case PayslipStatus.COMPLETED:
                return PayslipStatusDTO.COMPLETED
            case PayslipStatus.APPROVED:
                return PayslipStatusDTO.APPROVED
            case PayslipStatus.FINALISED:
                return PayslipStatusDTO.PAID_EXPORTED
            default:
                throw new IllegalArgumentException("Wrong payslip status: $status.displayName")
        }
    }
}
