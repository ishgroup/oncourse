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

package ish.oncourse.server.api.v1.service.impl

import javax.inject.Inject
import static ish.oncourse.server.api.function.EntityFunctions.checkForBadRequest
import static ish.oncourse.server.api.v1.function.PayrollFunctions.toPayrollGenerationRequest
import static ish.oncourse.server.api.v1.function.PayrollFunctions.toWagesToProcess
import static ish.oncourse.server.api.v1.function.PayrollFunctions.validate
import ish.oncourse.server.api.v1.model.PayrollRequestDTO
import ish.oncourse.server.api.v1.model.WagesToProcessDTO
import ish.oncourse.server.api.v1.service.PayrollApi
import ish.oncourse.server.concurrent.ExecutorManager
import ish.oncourse.server.payroll.PayrollService

import java.util.concurrent.Callable

class PayrollApiImpl implements PayrollApi {

    private final String PAYSLIP_GENERATING_RESULT = "Finished"

    @Inject
    private PayrollService payrollService

    @Inject
    private ExecutorManager executorManager

    @Override
    String execute(String entity, Boolean bulkConfirmTutorWages, PayrollRequestDTO payrollRequest) {
        checkForBadRequest(validate(payrollRequest))
        executorManager.submit(new Callable<Object>() {
            @Override
            Object call() throws Exception {
                payrollService.generatePayslips(toPayrollGenerationRequest(payrollRequest, Boolean.TRUE.equals(bulkConfirmTutorWages)))
                return PAYSLIP_GENERATING_RESULT
            }
        })
    }

    @Override
    WagesToProcessDTO prepare(String entity, PayrollRequestDTO payrollRequest) {
        checkForBadRequest(validate(payrollRequest))
        toWagesToProcess(payrollService.getWagesSummary(toPayrollGenerationRequest(payrollRequest, false)))
    }
}
