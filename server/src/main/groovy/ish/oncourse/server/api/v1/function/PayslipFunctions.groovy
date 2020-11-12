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

import groovy.transform.CompileStatic
import groovy.transform.TypeCheckingMode
import ish.common.types.PayslipStatus
import ish.oncourse.server.api.BidiMap
import static ish.oncourse.server.api.function.CayenneFunctions.getRecordById
import static ish.oncourse.server.api.v1.function.PayLineFunctions.toRestPayLine
import static ish.oncourse.server.api.v1.function.PayLineFunctions.updatePayLines
import static ish.oncourse.server.api.v1.function.TagFunctions.toRestTagMinimized
import static ish.oncourse.server.api.v1.function.TagFunctions.updateTags
import ish.oncourse.server.api.v1.model.PayLineDTO
import ish.oncourse.server.api.v1.model.PayslipDTO
import ish.oncourse.server.api.v1.model.PayslipRequestDTO
import ish.oncourse.server.api.v1.model.PayslipStatusDTO
import static ish.oncourse.server.api.v1.model.PayslipStatusDTO.APPROVED
import static ish.oncourse.server.api.v1.model.PayslipStatusDTO.COMPLETED
import static ish.oncourse.server.api.v1.model.PayslipStatusDTO.NEW
import static ish.oncourse.server.api.v1.model.PayslipStatusDTO.PAID_EXPORTED
import ish.oncourse.server.api.v1.model.ValidationErrorDTO
import ish.oncourse.server.cayenne.Contact
import ish.oncourse.server.cayenne.Payslip
import ish.oncourse.server.cayenne.PayslipTagRelation
import org.apache.cayenne.ObjectContext
import static org.apache.commons.lang3.StringUtils.trimToNull

import java.time.ZoneOffset

class PayslipFunctions {

    private static final BidiMap<PayslipStatus, PayslipStatusDTO> payslipStatusBidiMap = new BidiMap<PayslipStatus, PayslipStatusDTO>() {{
        put(PayslipStatus.HOLLOW, NEW)
        put(PayslipStatus.COMPLETED, COMPLETED)
        put(PayslipStatus.APPROVED, APPROVED)
        put(PayslipStatus.FINALISED, PAID_EXPORTED)

    }}

    @CompileStatic(TypeCheckingMode.SKIP)
    static PayslipDTO toRestPayslip(Payslip dbPayslip) {
        new PayslipDTO().with { payslip ->
            payslip.id = dbPayslip.id
            payslip.publicNotes = dbPayslip.notes
            payslip.privateNotes = dbPayslip.privateNotes
            payslip.status = payslipStatusBidiMap.get(dbPayslip.status)
            payslip.tutorId = dbPayslip.contact.id
            payslip.tutorFullName = dbPayslip.contact.fullName
            payslip.tags = dbPayslip.tags.collect { toRestTagMinimized(it) }
            payslip.paylines dbPayslip.paylines.collect { toRestPayLine(it) }
                    .sort { a, b -> (!a.className ? !b.className ? 0 : 1 : !b.className ? -1 : a.className <=> b.className) ?: a.type <=> b.type ?: a.dateFor <=> b.dateFor }
            payslip.createdOn = dbPayslip.createdOn.toInstant().atZone(ZoneOffset.UTC).toLocalDateTime()
            payslip.modifiedOn = dbPayslip.modifiedOn.toInstant().atZone(ZoneOffset.UTC).toLocalDateTime()
            payslip
        }
    }

    static ValidationErrorDTO validateForDelete(Payslip payslip) {
        if (PayslipStatus.APPROVED == payslip.status || PayslipStatus.FINALISED == payslip.status) {
            return new ValidationErrorDTO(payslip?.id?.toString(), 'id', "Cannot delete tutor pay with ${payslip.status.displayName} status.")
        }

        null
    }

    static ValidationErrorDTO validateForSave(PayslipDTO payslip, ObjectContext context, Payslip dbPayslip = null) {
        if (payslip.tutorId == null) {
            return new ValidationErrorDTO(dbPayslip?.id?.toString(), 'tutor', 'Tutor is required.')
        }
        Contact contact = getRecordById(context, Contact, payslip.tutorId)
        if (contact == null) {
            return new ValidationErrorDTO(dbPayslip?.id?.toString(), 'tutor', "Tutor with id=${payslip.tutorId} not found.")
        } else if (dbPayslip?.contact?.id && dbPayslip.contact.id != contact.id) {
            return new ValidationErrorDTO(dbPayslip?.id?.toString(), 'tutor', 'Cannot change tutor for tutor pay.')
        }

        ValidationErrorDTO error = null

        payslip.paylines.eachWithIndex{ PayLineDTO payLine, int i ->
            error = error ?: PayLineFunctions.validateForSave(payLine)?.with(true, { propertyName = "paylines[$i]$propertyName"}) as ValidationErrorDTO
        }

        error

    }

    static Payslip toDbPayslip(PayslipDTO payslip, Payslip dbPayslip, ObjectContext context) {
        if (dbPayslip.newRecord) {
            dbPayslip.status = PayslipStatus.HOLLOW
            dbPayslip.contact = getRecordById(context, Contact, payslip.tutorId)
        }

        dbPayslip.notes = trimToNull(payslip.publicNotes)
        dbPayslip.privateNotes = trimToNull(payslip.privateNotes)
        updateTags(dbPayslip, dbPayslip.taggingRelations, payslip.tags*.id, PayslipTagRelation, context)
        updatePayLines(dbPayslip, payslip.paylines)

        dbPayslip
    }

    static void markAs(Payslip self, PayslipStatusDTO status) {
        PayslipStatus dbStatus = payslipStatusBidiMap.getByValue(status)

        if (self.status != PayslipStatus.FINALISED) {
            self.status = dbStatus
        }
    }

    static ValidationErrorDTO validateRequest(PayslipRequestDTO request) {
        if (request.ids.isEmpty()) {
            return new ValidationErrorDTO(null, 'ids', 'No record ids found.')
        }

        if (!request.status) {
            return new ValidationErrorDTO(null, 'status', 'Status is required.')
        }
        null
    }
}
