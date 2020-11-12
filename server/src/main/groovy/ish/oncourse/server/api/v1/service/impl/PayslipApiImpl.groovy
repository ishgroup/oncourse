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

import com.google.inject.Inject
import ish.oncourse.server.ICayenneService
import static ish.oncourse.server.api.function.CayenneFunctions.deleteRecord
import static ish.oncourse.server.api.function.CayenneFunctions.getRecordById
import static ish.oncourse.server.api.function.EntityFunctions.checkForBadRequest
import static ish.oncourse.server.api.function.EntityFunctions.validateEntityExistence
import static ish.oncourse.server.api.function.EntityFunctions.validateIdParam
import ish.oncourse.server.api.service.PayslipApiService
import static ish.oncourse.server.api.v1.function.PayslipFunctions.markAs
import static ish.oncourse.server.api.v1.function.PayslipFunctions.toDbPayslip
import static ish.oncourse.server.api.v1.function.PayslipFunctions.toRestPayslip
import static ish.oncourse.server.api.v1.function.PayslipFunctions.validateForDelete
import static ish.oncourse.server.api.v1.function.PayslipFunctions.validateForSave
import static ish.oncourse.server.api.v1.function.PayslipFunctions.validateRequest
import ish.oncourse.server.api.v1.model.DiffDTO
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
    private PayslipApiService entityApiService

    @Override
    void create(PayslipDTO payslip) {
        ObjectContext context = cayenneService.newContext

        checkForBadRequest(validateForSave(payslip, context))

        toDbPayslip(payslip, context.newObject(Payslip), context)
        context.commitChanges()
    }

    @Override
    void execute(PayslipRequestDTO request) {

        ObjectContext context = cayenneService.newContext

        checkForBadRequest(validateRequest(request))

        ObjectSelect.query(Payslip)
                .where(Payslip.ID.in(request.ids))
                .select(context)
                .each { markAs(it, request.status) }

        context.commitChanges()
    }

    @Override
    Object get(Long id) {
        toRestPayslip(getRecordById(cayenneService.newContext, Payslip, id,
                Payslip.CONTACT.joint(),
                Payslip.PAYLINES.joint()
        ))
    }

    @Override
    void remove(Long id) {
        checkForBadRequest(validateIdParam ( id))

        ObjectContext context = cayenneService.newContext
        Payslip entity = getRecordById(context, Payslip, id)
        checkForBadRequest(validateEntityExistence(id, entity))
        checkForBadRequest(validateForDelete(entity))

        deleteRecord(context, entity)
    }

    @Override
    void update(Long id, PayslipDTO payslip) {
        checkForBadRequest(validateIdParam(id))

        ObjectContext context = cayenneService.newContext
        Payslip dbPayslip = getRecordById(context, Payslip, id)
        checkForBadRequest(validateEntityExistence(id, dbPayslip))
        checkForBadRequest(validateForSave(payslip, context, dbPayslip))

        toDbPayslip(payslip, dbPayslip, context)
        context.commitChanges()
    }

    @Override
    void bulkChange(DiffDTO diff) {
        entityApiService.bulkChange(diff)
    }
}
