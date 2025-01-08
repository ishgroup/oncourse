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
import ish.oncourse.server.ICayenneService
import static ish.oncourse.server.api.function.CayenneFunctions.getRecordById
import ish.oncourse.server.api.service.PayslipApiService
import static ish.oncourse.server.api.v1.function.PayslipFunctions.markAs
import ish.oncourse.server.api.v1.model.PayslipDTO
import ish.oncourse.server.api.v1.model.PayslipRequestDTO
import ish.oncourse.server.api.v1.service.PayslipApi
import ish.oncourse.server.cayenne.Payslip
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.ObjectSelect

class PayslipApiImpl implements PayslipApi {

    @Inject
    private ICayenneService cayenneService

    @Inject
    private PayslipApiService payslipApiService

    @Override
    void create(PayslipDTO payslip) {
        payslipApiService.create(payslip)
    }

    @Override
    void execute(PayslipRequestDTO request) {

        ObjectContext context = cayenneService.newContext

        payslipApiService.validateRequest(request)

        ObjectSelect.query(Payslip)
                .where(Payslip.ID.in(request.ids))
                .select(context)
                .each { markAs(it, request.status) }

        context.commitChanges()
    }

    @Override
    Object get(Long id) {
        payslipApiService.toRestModel(getRecordById(cayenneService.newContext, Payslip, id,
                Payslip.CONTACT.joint(),
                Payslip.PAYLINES.joint()
        ))
    }

    @Override
    void remove(Long id) {
        payslipApiService.remove(id)
    }

    @Override
    void update(Long id, PayslipDTO payslip) {
        payslipApiService.update(id, payslip)
    }
}
