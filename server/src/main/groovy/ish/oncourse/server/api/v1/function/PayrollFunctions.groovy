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


import ish.oncourse.server.api.v1.model.PayrollRequestDTO
import ish.oncourse.server.api.v1.model.ValidationErrorDTO
import ish.oncourse.server.api.v1.model.WagesToProcessDTO
import ish.payroll.PayrollGenerationRequest
import ish.payroll.WagesSummaryResponse
import static org.apache.commons.lang3.StringUtils.isNotBlank
import org.apache.commons.lang3.time.DateUtils

class PayrollFunctions {

    static PayrollGenerationRequest toPayrollGenerationRequest(PayrollRequestDTO payrollRequest, boolean confirm) {
        new PayrollGenerationRequest().with { it ->
            //include the whole day
            it.until = DateUtils.addSeconds(payrollRequest.untilDate.plusDays(1).toDate(), -1)
            it.entityName = payrollRequest.entityName
            it.ids = payrollRequest.recordIds
            it.confirm = confirm
            it
        }
    }

    static WagesToProcessDTO toWagesToProcess(WagesSummaryResponse wagesSummaryResponse) {
        new WagesToProcessDTO().with { it ->
            it.totalWagesCount = wagesSummaryResponse.totalWagesCount.toInteger()
            it.unprocessedWagesCount = wagesSummaryResponse.unprocessedWagesCount.toInteger()
            it.unconfirmedWagesCount = wagesSummaryResponse.unconfirmedWagesCount.toInteger()
            it.unconfirmedClassesIds = wagesSummaryResponse.getUnconfirmedClassesIdsString()
            it
        }
    }

    static ValidationErrorDTO validate(PayrollRequestDTO payrollRequest) {
        if (payrollRequest.untilDate == null) {
            return new ValidationErrorDTO(null, null, 'Date until is required.')
        }

        if (isNotBlank(payrollRequest.entityName) && (payrollRequest.recordIds == null || payrollRequest.recordIds.isEmpty())) {
            return new ValidationErrorDTO(null, null, 'selected record ids is required.')
        }

        null
    }
}
