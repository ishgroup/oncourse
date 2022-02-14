// find all recrods before the threshold date exclusive
// atDate - export param
import ish.math.Money
import ish.oncourse.cayenne.PaymentLineInterface
import ish.oncourse.server.cayenne.*
import org.apache.cayenne.query.ObjectSelect

import static java.time.temporal.ChronoUnit.DAYS
import java.time.LocalDate

LocalDate atDate = LocalDate.parse('2019-06-30')
def detail = false

List<ExportInvoice> rows = []

ObjectSelect.query(Invoice)
    .where(Invoice.INVOICE_DATE.lte(atDate))
    .prefetch(Invoice.CONTACT.joint())
    .prefetch(Invoice.INVOICE_DUE_DATES.joint())
    .prefetch(Invoice.INVOICE_LINES.joint())
    .prefetch(Invoice.INVOICE_LINES.dot(InvoiceLine.TAX).joint())
    .prefetch(Invoice.PAYMENT_IN_LINES.joint())
    .prefetch(Invoice.PAYMENT_IN_LINES.dot(PaymentInLine.PAYMENT_IN).joint())
    .prefetch(Invoice.PAYMENT_OUT_LINES.joint())
    .prefetch(Invoice.PAYMENT_OUT_LINES.dot(PaymentOutLine.PAYMENT_OUT).joint())
    .select(context)
    .each { i ->

        def name = i.contact.firstName ? "${i.contact.lastName}, ${ i.contact.firstName}" : i.contact.lastName
        ExportInvoice row = new ExportInvoice(name: name, invoiceId: i.id, contactId: i.contact.id)

        List<PaymentLineInterface> paymentLines = i.paymentLines.findAll { pl -> pl.payment.paymentDate <= atDate && pl.payment.status == PaymentStatus.SUCCESS }
        if (i.invoiceDueDates.size() > 0) {
            Money invoiceTotal = i.invoiceDueDates.sum { it.amount } as Money
            Money nonOverdued =  i.invoiceDueDates.findAll { it.dueDate > atDate }.sum { it.amount } as Money ?: Money.ZERO
            Money paidAmount = paymentLines.sum { it instanceof PaymentOutLine ? it.amount.negate() : it.amount } as Money ?: Money.ZERO
            if (invoiceTotal.isGreaterThan(paidAmount)) {
                row.b_0 += invoiceTotal.subtract(paidAmount).min(nonOverdued)

                Money overpay = Money.ZERO

                LocalDate startOfperiod = i.invoiceDueDates.sort { it.dueDate }[0].dueDate
                boolean fromInvoicedate = true

                for (int days = 1; days < 91;) {

                    if (startOfperiod <= atDate) {
                        LocalDate nextPeriod = DAYS.between(startOfperiod, atDate) > 30 ? startOfperiod.plusDays(30) : atDate

                        Money periodOwing
                        Money paidForPeriod
                        if (fromInvoicedate) {
                            fromInvoicedate = false
                            periodOwing = i.invoiceDueDates.findAll { it.dueDate <= nextPeriod }.sum { it.amount } as Money ?: Money.ZERO
                            paidForPeriod = paymentLines.findAll { it.payment.paymentDate <= nextPeriod }.sum {
                                it instanceof PaymentOutLine ? it.amount.negate() : it.amount
                            } as Money ?: Money.ZERO
                        } else {
                            fromInvoicedate = false
                            periodOwing = i.invoiceDueDates.findAll { it.dueDate > startOfperiod && it.dueDate <= nextPeriod }.sum { it.amount } as Money ?: Money.ZERO
                            paidForPeriod = paymentLines.findAll { it.payment.paymentDate > startOfperiod && it.payment.paymentDate <= nextPeriod }.sum {
                                it instanceof PaymentOutLine ? it.amount.negate() : it.amount
                            } as Money ?: Money.ZERO
                        }

                        paidForPeriod += overpay
                        overpay = Money.ZERO

                        if (periodOwing.isGreaterThan(paidForPeriod)) {
                            Money owing = periodOwing.subtract(paidForPeriod)
                            switch (DAYS.between(i.dateDue, atDate).intValue()) {
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
                            }
                        } else {
                            overpay = paidForPeriod.subtract(periodOwing)
                        }

                        startOfperiod = nextPeriod
                    }

                    days += 30

                }
            }

        }
        else {

            def dueDate = i.dateDue

            Money owing = i.totalIncTax.subtract(paymentLines.sum { pl -> pl instanceof PaymentOutLine ? pl.amount.negate() : pl.amount } as Money ?: Money.ZERO)

            switch (DAYS.between(dueDate, atDate).intValue()) {
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

        if (row.notPaid) {
            rows << row
        }
    }


if (detail) {
    rows.sort { it.name }.each { invoices ->
        invoices.each { i ->

            csv << [
                    "Debtor"               : i.name,
                    "Not due"              : i.b_0,
                    "Overdue up to 30 days": i.b_1_30,
                    "Overdue 31-60 days"   : i.b_31_60,
                    "Overdue 61-90 days"   : i.b_61_90,
                    "Overdue over 90 days" : i.b_90
            ]
        }
    }
} else {

    rows.groupBy { it.name + it.contactId.toString() }
            .sort()
            .each { contactId, invoices ->

        csv << [
                "Debtor"               : invoices.first().name,
                "Not due"              : invoices.b_0.sum(),
                "Overdue up to 30 days": invoices.b_1_30.sum(),
                "Overdue 31-60 days"   : invoices.b_31_60.sum(),
                "Overdue 61-90 days"   : invoices.b_61_90.sum(),
                "Overdue over 90 days" : invoices.b_90.sum()
                ]
    }
}


// A row in the export. Might represent either contacts or invoices
class ExportInvoice {
    String name
    int contactId
    int invoiceId

    Money b_0 = Money.ZERO
    Money b_1_30 = Money.ZERO
    Money b_31_60 = Money.ZERO
    Money b_61_90 = Money.ZERO
    Money b_90 = Money.ZERO

    boolean isNotPaid() {
        return b_0 != Money.ZERO || b_1_30 != Money.ZERO || b_31_60 != Money.ZERO || b_61_90 != Money.ZERO || b_90 != Money.ZERO
    }
}

