// find all recrods before the threshold date exclusive
// debtorThreshold - export param
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
                    Money nonOverdued =  i.invoiceDueDates.findAll { it.dueDate > debtorThreshold }.sum { it.amount } as Money?: Money.ZERO
                    Money payedAmount = paymentLines.sum { it instanceof PaymentOutLine ? it.amount.negate() : it.amount } as Money?: Money.ZERO
                    if (invoiceTotal.isGreaterThan(payedAmount)) {
                        row.b_0 += invoiceTotal.subtract(payedAmount).min(nonOverdued)

                        Money overpay = Money.ZERO

                        LocalDate startOfperiod = i.invoiceDueDates.sort { it.dueDate }[0].dueDate
                        boolean fromInvoicedate = true

                        for (int days = 1; days < 91;) {

                            if (startOfperiod <= debtorThreshold) {
                                LocalDate nextPeriod = DAYS.between(startOfperiod, debtorThreshold) > 30 ? startOfperiod.plusDays(30) : debtorThreshold

                                Money periodOwing
                                Money payedForPeriod
                                if (fromInvoicedate) {
                                    fromInvoicedate = false
                                    periodOwing = i.invoiceDueDates.findAll { it.dueDate <= nextPeriod }.sum { it.amount } as Money ?: Money.ZERO
                                    payedForPeriod = paymentLines.findAll { it.payment.paymentDate <= nextPeriod }.sum {
                                        it instanceof PaymentOutLine ? it.amount.negate() : it.amount
                                    } as Money ?: Money.ZERO
                                } else {
                                    fromInvoicedate = false
                                    periodOwing = i.invoiceDueDates.findAll { it.dueDate > startOfperiod && it.dueDate <= nextPeriod }.sum { it.amount } as Money ?: Money.ZERO
                                    payedForPeriod = paymentLines.findAll { it.payment.paymentDate > startOfperiod && it.payment.paymentDate <= nextPeriod }.sum {
                                        it instanceof PaymentOutLine ? it.amount.negate() : it.amount
                                    } as Money ?: Money.ZERO
                                }

                                payedForPeriod += overpay
                                overpay = Money.ZERO

                                if (periodOwing.isGreaterThan(payedForPeriod)) {
                                    Money owing = periodOwing.subtract(payedForPeriod)
                                    switch (days) {
                                        case 1..30:
                                            row.b_1_30 = owing
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
                                    overpay = payedForPeriod.subtract(periodOwing)
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

    if (row.overdue) {
        rows << row
    }

})

rows.sort{it.lastName}.each { row ->
    csv << [
            "Debtor": row.fullName,
            "0"     : row.b_0.toBigDecimal(),
            "1-30"  : row.b_1_30.toBigDecimal(),
            "31-60" : row.b_31_60.toBigDecimal(),
            "61-90" : row.b_61_90.toBigDecimal(),
            "91+"   : row.b_90.toBigDecimal(),
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

    boolean isOverdue() {
       return b_0 != Money.ZERO || b_1_30 != Money.ZERO || b_31_60 != Money.ZERO || b_61_90 != Money.ZERO || b_90 != Money.ZERO
    }
    String getFullName() {
        return firstName ? "$lastName $firstName" : lastName
    }
}

