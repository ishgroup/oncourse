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

package ish.oncourse.server.api.service

import ish.common.types.PayslipStatus
import ish.oncourse.server.api.dao.PayslipDao
import ish.oncourse.server.api.v1.function.PayLineFunctions
import ish.oncourse.server.api.v1.model.PayLineDTO
import ish.oncourse.server.api.v1.model.PayslipPayTypeDTO
import ish.oncourse.server.api.v1.model.PayslipRequestDTO
import ish.oncourse.server.api.v1.model.PayslipStatusDTO
import ish.oncourse.server.api.v1.model.ValidationErrorDTO
import ish.oncourse.server.cayenne.Contact
import ish.oncourse.server.cayenne.PayslipTagRelation

import java.time.ZoneOffset

import static ish.oncourse.server.api.function.CayenneFunctions.getRecordById
import ish.oncourse.server.api.v1.model.PayslipDTO
import ish.oncourse.server.cayenne.Payslip
import org.apache.cayenne.ObjectContext

import static ish.oncourse.server.api.v1.function.PayLineFunctions.toRestPayLine
import static ish.oncourse.server.api.v1.function.PayLineFunctions.updatePayLines
import static ish.oncourse.server.api.v1.function.TagFunctions.toRestTagMinimized
import static ish.oncourse.server.api.v1.function.TagFunctions.updateTags
import static org.apache.commons.lang3.StringUtils.trimToNull

class PayslipApiService extends TaggableApiService<PayslipDTO, Payslip, PayslipDao> {
    @Override
    Class<Payslip> getPersistentClass() {
        return Payslip
    }

    @Override
    PayslipDTO toRestModel(Payslip dbModel) {
        new PayslipDTO().with { payslip ->
            payslip.id = dbModel.id
            payslip.publicNotes = dbModel.notes
            payslip.privateNotes = dbModel.privateNotes
            payslip.status = PayslipStatusDTO.values()[0].fromDbType(dbModel.status)
            payslip.payType = PayslipPayTypeDTO.values()[0].fromDbType(dbModel.payType)
            payslip.tutorId = dbModel.contact.id
            payslip.tutorFullName = dbModel.contact.fullName
            payslip.tags = dbModel.allTags.collect { it.id }
            payslip.paylines = dbModel.paylines.collect { toRestPayLine(it) }
                    .sort { a, b -> (!a.className ? !b.className ? 0 : 1 : !b.className ? -1 : a.className <=> b.className) ?: a.type <=> b.type ?: a.dateFor <=> b.dateFor }
            payslip.createdOn = dbModel.createdOn.toInstant().atZone(ZoneOffset.UTC).toLocalDateTime()
            payslip.modifiedOn = dbModel.modifiedOn.toInstant().atZone(ZoneOffset.UTC).toLocalDateTime()
            payslip
        }
    }

    @Override
    Payslip toCayenneModel(PayslipDTO dto, Payslip dbModel) {
        ObjectContext context = dbModel.context
        if (dbModel.newRecord) {
            dbModel.status = PayslipStatus.HOLLOW
            dbModel.contact = getRecordById(context, Contact, dto.tutorId)
        }
        dbModel.payType = dto.payType.getDbType()
        dbModel.notes = trimToNull(dto.publicNotes)
        dbModel.privateNotes = trimToNull(dto.privateNotes)
        updateTags(dbModel, dbModel.taggingRelations, dto.tags, PayslipTagRelation, context)
        updatePayLines(dbModel, dto.paylines)

        dbModel
    }

    @Override
    void validateModelBeforeSave(PayslipDTO dto, ObjectContext context, Long id) {
        
        Payslip dbModel = id ? entityDao.getById(context, id) : null as Payslip
        if (dto.tutorId == null) {
            validator.throwClientErrorException(dbModel?.id?.toString(), 'tutor', 'Tutor is required.')
        }
        Contact contact = getRecordById(context, Contact, dto.tutorId)
        if (contact == null) {
            validator.throwClientErrorException(dbModel?.id?.toString(), 'tutor', "Tutor with id=${dto.tutorId} not found.")
        } else if (dbModel?.contact?.id && dbModel.contact.id != contact.id) {
            validator.throwClientErrorException(dbModel?.id?.toString(), 'tutor', 'Cannot change tutor for tutor pay.')
        }

        ValidationErrorDTO error = null
        dto.paylines.eachWithIndex{ PayLineDTO payLine, int i ->
            error = error ?: PayLineFunctions.validateForSave(payLine)?.with(true, { propertyName = "paylines[$i]$propertyName"}) as ValidationErrorDTO
        }
        if (error) {
            validator.throwClientErrorException(error)
        }
        
    }

    @Override
    void validateModelBeforeRemove(Payslip payslip) {
        if (PayslipStatus.APPROVED == payslip.status || PayslipStatus.FINALISED == payslip.status) {
            validator.throwClientErrorException(payslip?.id?.toString(), 'id', "Cannot delete tutor pay with ${payslip.status.displayName} status.")
        }
    }

    void validateRequest(PayslipRequestDTO request) {
        if (request.ids.isEmpty()) {
            validator.throwClientErrorException(null, 'ids', 'No record ids found.')
        }

        if (!request.status) {
            validator.throwClientErrorException(null, 'status', 'Status is required.')
        }
    }

    @Override
    Closure getAction (String key, String value) {
        Closure action = super.getAction(key, value)
        if (!action) {
            validator.throwClientErrorException(key, "Unsupported attribute")
        }
        action
    }
}
