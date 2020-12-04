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

package ish.oncourse.server.api.v1.function

import ish.common.types.PayslipStatus
import ish.oncourse.server.api.BidiMap
import ish.oncourse.server.api.v1.model.PayslipStatusDTO
import static ish.oncourse.server.api.v1.model.PayslipStatusDTO.APPROVED
import static ish.oncourse.server.api.v1.model.PayslipStatusDTO.COMPLETED
import static ish.oncourse.server.api.v1.model.PayslipStatusDTO.NEW
import static ish.oncourse.server.api.v1.model.PayslipStatusDTO.PAID_EXPORTED
import ish.oncourse.server.cayenne.Payslip


class PayslipFunctions {

    private static final BidiMap<PayslipStatus, PayslipStatusDTO> payslipStatusBidiMap = new BidiMap<PayslipStatus, PayslipStatusDTO>() {{
        put(PayslipStatus.HOLLOW, NEW)
        put(PayslipStatus.COMPLETED, COMPLETED)
        put(PayslipStatus.APPROVED, APPROVED)
        put(PayslipStatus.FINALISED, PAID_EXPORTED)

    }}

    static void markAs(Payslip self, PayslipStatusDTO status) {
        PayslipStatus dbStatus = payslipStatusBidiMap.getByValue(status)

        if (self.status != PayslipStatus.FINALISED) {
            self.status = dbStatus
        }
    }
}
