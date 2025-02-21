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

import ish.math.Money
import ish.oncourse.server.api.v1.model.PayLineDTO
import ish.oncourse.server.api.v1.model.ClassCostRepetitionTypeDTO
import ish.oncourse.server.api.v1.model.ValidationErrorDTO
import ish.oncourse.server.cayenne.ClassCost
import ish.oncourse.server.cayenne.PayLine
import ish.oncourse.server.cayenne.Payslip
import org.apache.cayenne.ObjectContext
import static org.apache.commons.lang3.StringUtils.isBlank
import static org.apache.commons.lang3.StringUtils.trimToNull

import java.time.LocalDate

class PayLineFunctions {

    static PayLineDTO toRestPayLine(PayLine dbPayLine) {
        new PayLineDTO().with { payLine ->
            payLine.id = dbPayLine.id
            payLine.dateFor = dbPayLine.dateFor
            payLine.description = dbPayLine.description
            if (dbPayLine.classCost) {
                ClassCost classCost = dbPayLine.classCost
                payLine.className = "${classCost.courseClass.uniqueCode} ${classCost.courseClass.course.name}"
                payLine.type = ClassCostRepetitionTypeDTO.values()[0].fromDbType(classCost.repetitionType)
            }

            payLine.budgetedQuantity = dbPayLine.budgetedQuantity
            payLine.quantity = dbPayLine.quantity
            payLine.budgetedValue = dbPayLine.budgetedValue?.toBigDecimal()
            payLine.value = dbPayLine.value?.toBigDecimal()
            payLine
        }
    }

    static void updatePayLines(Payslip dbPayslip, List<PayLineDTO> payLines) {
        ObjectContext context = dbPayslip.context

        List<Long> payLinesToSave = payLines.id.findAll()
        context.deleteObjects(dbPayslip.paylines.findAll{ !payLinesToSave.contains(it.id) })

        payLines.each { PayLineDTO payLine ->
            if (payLine.id) {
                PayLine dbPayLine = dbPayslip.paylines.find { it.id == payLine.id }
                if (dbPayLine.description != trimToNull(payLine.description)) {
                    dbPayLine.description = trimToNull(payLine.description)
                }
                if (dbPayLine.quantity != payLine.quantity) {
                    dbPayLine.quantity = payLine.quantity
                }
                if (dbPayLine.value.toBigDecimal() != payLine.value) {
                    dbPayLine.value = Money.of(payLine.value)
                }
            } else {
                context.newObject(PayLine).with { it ->
                    it.payslip = dbPayslip
                    it.description = trimToNull(payLine.description)
                    it.quantity = new BigDecimal(1)
                    it.value = Money.of(payLine.value)
                    it.dateFor = LocalDate.now()
                }
            }
        }
    }

    static ValidationErrorDTO validateForSave(PayLineDTO payLine) {
        if (isBlank(payLine.description)) {
            return new ValidationErrorDTO(payLine?.id?.toString(), 'description', 'Description is required.')
        }

        if (payLine.value == null) {
            return new ValidationErrorDTO(payLine?.id?.toString(), 'value', 'Amount is required.')
        }

        if (payLine.id && payLine.quantity == null) {
            return new ValidationErrorDTO(payLine?.id?.toString(), 'quantity', 'Quantity is required.')
        }

        null
    }

}
