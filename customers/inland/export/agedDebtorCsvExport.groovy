/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

// find all recrods before the threshold date exclusive
// debtorThreshold - export param
import ish.math.Money
import ish.oncourse.server.cayenne.PaymentOutLine
import static java.time.temporal.ChronoUnit.DAYS

import java.time.LocalDate

//LocalDate debtorThreshold = LocalDate.parse('2019-06-30')

def context = records.first().getContext()

List<ContactRow> rows = []
ObjectSelect.query(Contact)
        .where(Contact.INVOICES.dot(Invoice.INVOICE_DATE).lte(debtorThreshold))
        .columns(Contact.ID, Contact.LAST_NAME, Contact.FIRST_NAME)
        .select(context).parallelStream().forEach({ c ->
    ContactRow row = new ContactRow()
    row.lastName = c[1]
    row.firstName = c[2]

    ObjectSelect.query(Invoice).where(Invoice.CONTACT.dot(Contact.ID).eq(c[0] as Long)).and(Invoice.INVOICE_DATE.lte(debtorThreshold))
            .prefetch(Invoice.INVOICE_DUE_DATES.joint())
            .prefetch(Invoice.INVOICE_LINES.joint())
            .prefetch(Invoice.INVOICE_LINES.dot(InvoiceLine.TAX).joint())
            .prefetch(Invoice.PAYMENT_IN_LINES.joint())
            .prefetch(Invoice.PAYMENT_IN_LINES.dot(PaymentInLine.PAYMENT_IN).joint())
            .prefetch(Invoice.PAYMENT_OUT_LINES.joint())
            .prefetch(Invoice.PAYMENT_OUT_LINES.dot(PaymentOutLine.PAYMENT_OUT).joint())
            .select(context).stream()
            .forEach( { i ->
                List<ish.oncourse.cayenne.PaymentLineInterface> paymentLines = i.paymentLines.findAll { pl -> pl.payment.paymentDate <= debtorThreshold && pl.payment.status == PaymentStatus.SUCCESS }
                if (i.invoiceDueDates.size() > 0) {
                    Money invoiceTotal = i.invoiceDueDates.sum { it.amount } as Money
                    Money payedAmount = paymentLines.sum { it instanceof PaymentOutLine ? it.amount.negate() : it.amount } as Money?: Money.ZERO
                    if (invoiceTotal.isGreaterThan(payedAmount)) {
                        i.invoiceDueDates.sort { it.dueDate }.each { it ->
                            if (payedAmount.subtract(it.amount) < 0) {
                                Money owing = it.amount.subtract(payedAmount).min(it.amount)
                                switch (DAYS.between(it.dueDate, debtorThreshold).intValue()) {
                                    case 1..30:
                                        row.b_1_30 += owing
                                        break
                                    case 31..60:
                                        row.b_31_60 += owing
                                        break
                                    case 61..90:
                                        row.b_61_90 += owing
                                        break
                                    case { it > 90 }:
                                        row.b_90 += owing
                                        break
                                    default:
                                        row.b_0 += owing
                                }
                            }
                            payedAmount = payedAmount.subtract(it.amount)
                        }
                    }
                }
                else {

                    def dueDate = i.dateDue

                    Money owing = i.totalIncTax.subtract(paymentLines.sum { pl -> pl instanceof PaymentOutLine ? pl.amount.negate() : pl.amount } as Money ?: Money.ZERO)

                    switch (DAYS.between(dueDate, debtorThreshold).intValue()) {
                        case 1..30:
                            row.b_1_30 += owing
                            break
                        case 31..60:
                            row.b_31_60 += owing
                            break
                        case 61..90:
                            row.b_61_90 += owing
                            break
                        case { it > 90 }:
                            row.b_90 += owing
                            break
                        default:
                            row.b_0 += owing
                    }
                }
            })
    if (row.notPaid) {
        rows << row
    }
})

rows.sort{it.lastName}.each { row ->
    csv << [
            "Debtor" : row.fullName,
            "Not due" : row.b_0.toBigDecimal(),
            "Overdue up to 30 days"  : row.b_1_30.toBigDecimal(),
            "Overdue 31-60 days" : row.b_31_60.toBigDecimal(),
            "Overdue 61-90 days" : row.b_61_90.toBigDecimal(),
            "Overdue over 90 days" : row.b_90.toBigDecimal(),
    ]
}



class ContactRow {
    String firstName
    String lastName

    Money b_0 = Money.ZERO
    Money b_1_30 = Money.ZERO
    Money b_31_60 = Money.ZERO
    Money b_61_90 = Money.ZERO
    Money b_90 = Money.ZERO

    boolean isNotPaid() {
        return b_0 != Money.ZERO || b_1_30 != Money.ZERO || b_31_60 != Money.ZERO || b_61_90 != Money.ZERO || b_90 != Money.ZERO
    }

    String getFullName() {
        return firstName ? "$lastName $firstName" : lastName
    }
}

