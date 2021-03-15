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

import ish.common.types.PaymentStatus
import ish.math.Money
import ish.oncourse.server.api.v1.model.InvoiceInvoiceLineDTO
import ish.oncourse.server.api.v1.model.InvoicePaymentPlanDTO
import ish.oncourse.server.cayenne.Invoice
import ish.oncourse.server.cayenne.InvoiceDueDate
import ish.oncourse.server.cayenne.InvoiceLine
import ish.oncourse.server.cayenne.PaymentIn
import ish.oncourse.server.cayenne.PaymentInLine
import ish.oncourse.server.cayenne.PaymentOut
import ish.oncourse.server.cayenne.PaymentOutLine
import ish.util.InvoiceUtil

import java.time.ZoneOffset

class InvoiceFunctions {

    static InvoiceInvoiceLineDTO toRestInvoiceLineModel(InvoiceLine cayenneModel) {
        new InvoiceInvoiceLineDTO().with { invoiceLine ->
            invoiceLine.id = cayenneModel.id
            invoiceLine.title = cayenneModel.title
            invoiceLine.quantity = cayenneModel.quantity
            invoiceLine.unit = cayenneModel.unit
            invoiceLine.incomeAccountId = cayenneModel.account.id
            invoiceLine.incomeAccountName = cayenneModel.account.with { "$it.description $it.accountCode"}
            invoiceLine.discountId = cayenneModel.discounts[0]?.id
            invoiceLine.discountName = cayenneModel.discounts[0]?.name
            invoiceLine.priceEachExTax = cayenneModel.priceEachExTax?.toBigDecimal()
            invoiceLine.discountEachExTax = cayenneModel.discountEachExTax?.toBigDecimal()
            invoiceLine.taxEach = cayenneModel.taxEach?.toBigDecimal()
            invoiceLine.taxId = cayenneModel.tax.id
            invoiceLine.taxName = cayenneModel.tax.description
            invoiceLine.description = cayenneModel.description
            if (cayenneModel.courseClass) {
                invoiceLine.courseClassId = cayenneModel.courseClass.id
                invoiceLine.courseName = cayenneModel.courseClass.course.name
                invoiceLine.courseCode = cayenneModel.courseClass.course.code
                invoiceLine.classCode = cayenneModel.courseClass.code
                invoiceLine.enrolmentId = cayenneModel.enrolment?.id
                invoiceLine.enrolledStudent = cayenneModel.enrolment?.student?.fullName
                invoiceLine.courseId = cayenneModel.courseClass.course.id
            }
            invoiceLine
        }
    }

    static InvoicePaymentPlanDTO toRestPaymentPlan(Invoice dbInvoice) {
        new InvoicePaymentPlanDTO().with { pp ->
            pp.id = dbInvoice.id
            pp.entityName = Invoice.simpleName
            pp.date = dbInvoice.createdOn?.toInstant()?.atZone(ZoneOffset.systemDefault())?.toLocalDate()
            pp.type = "${InvoiceUtil.sumInvoiceLines(dbInvoice.invoiceLines).isLessThan(Money.ZERO) ? 'Credit note' : 'Invoice'} $dbInvoice.source.displayName"
            pp.successful = true
            pp.amount = dbInvoice.totalIncTax.toBigDecimal()
            pp
        }
    }

    static InvoicePaymentPlanDTO toRestPaymentPlan(PaymentInLine dbPaymentInLine) {
        new InvoicePaymentPlanDTO().with { pp ->
            pp.id = dbPaymentInLine.paymentIn.id
            pp.entityName = PaymentIn.simpleName
            pp.date = dbPaymentInLine.payment.createdOn?.toInstant()?.atZone(ZoneOffset.systemDefault())?.toLocalDate()
            pp.type = "$dbPaymentInLine.paymentIn.typeOfPayment ($dbPaymentInLine.paymentIn.paymentMethod.name)"
            pp.successful = (PaymentStatus.SUCCESS.equals(dbPaymentInLine.paymentIn.status) || (PaymentStatus.IN_TRANSACTION.equals(dbPaymentInLine.paymentIn.status)))
            pp.amount = dbPaymentInLine.amount.toBigDecimal()
            pp
        }
    }

    static InvoicePaymentPlanDTO toRestPaymentPlan(PaymentOutLine dbPaymentOutLine) {
        new InvoicePaymentPlanDTO().with { pp ->
            pp.id = dbPaymentOutLine.paymentOut.id
            pp.entityName = PaymentOut.simpleName
            pp.date = dbPaymentOutLine.paymentOut.createdOn?.toInstant()?.atZone(ZoneOffset.systemDefault())?.toLocalDate()
            pp.type = "$dbPaymentOutLine.paymentOut.typeOfPayment ($dbPaymentOutLine.paymentOut.paymentMethod.name)"
            pp.successful = (PaymentStatus.SUCCESS.equals(dbPaymentOutLine.paymentOut.status) || (PaymentStatus.IN_TRANSACTION.equals(dbPaymentOutLine.paymentOut.status)))
            pp.amount = dbPaymentOutLine.amount.toBigDecimal()
            pp
        }
    }

    static InvoicePaymentPlanDTO toRestPaymentPlan(InvoiceDueDate dbInvoiceDueDate) {
        new InvoicePaymentPlanDTO().with { pp ->
            pp.id = dbInvoiceDueDate.id
            pp.entityName = InvoiceDueDate.simpleName
            pp.date = dbInvoiceDueDate.dueDate
            pp.type = 'Payment due'
            pp.successful = true
            pp.amount = dbInvoiceDueDate.amount.toBigDecimal()
            pp
        }
    }
}
