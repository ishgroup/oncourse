Map<Integer, ExportInvoice> rows = [:]

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

        def row = addInvoice(rows, i)

        List<PaymentLineInterface> paymentLines = i.paymentLines.findAll { pl -> pl.payment.paymentDate <= atDate && pl.payment.status == PaymentStatus.SUCCESS }
        Money owing = i.totalIncTax.subtract(paymentLines.sum { pl -> pl instanceof PaymentOutLine ? pl.amount.negate() : pl.amount } as Money ?: Money.ZERO)

        if (i.invoiceDueDates.size() == 0) {
            row.addOwing(owing, i.dateDue, atDate)

        } else {

            // For each due date, starting from the latest, allocate some of the amount owing
            i.invoiceDueDates.sort { it.dueDate }.reverse().findAll { invoiceDueDate ->
                def thisAmount = owing.min(invoiceDueDate.amount)
                owing = owing - thisAmount
                row.addOwing(thisAmount, invoiceDueDate.dueDate, atDate)

                return owing > 0  // breaks the loop when we run out of owing
            }

            // we should not hit the next condition, but just in case let's not lose the money from the export
            if (owing > 0) {
                row.addOwing(owing, i.dateDue, atDate)
            }
        }
    }


if (detail) {
    rows.findAll{it.nonZero()}
            .sort { it.key }
            .each { invoices ->
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

    rows.findAll{it.nonZero()}
            .groupBy { it.key }
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

static def addInvoice(Map<Integer, ExportInvoice> rows, i) {
    def row = rows.get(i)
    if (row) {
        return row
    }
    row = new ExportInvoice(i)
    rows << row
    return row
}

// A row in the export.
class ExportInvoice {
    String name
    String key
    Invoice invoice

    Money b_0 = Money.ZERO
    Money b_1_30 = Money.ZERO
    Money b_31_60 = Money.ZERO
    Money b_61_90 = Money.ZERO
    Money b_90 = Money.ZERO

    ExportInvoice(Invoice i) {
        this.invoice = invoice
        this.name = i.contact.firstName ? "${i.contact.lastName}, ${ i.contact.firstName}" : i.contact.lastName
        this.key = i.contact.lastName + i.contact.id.toString()
    }

    boolean nonZero() {
        return b_0 != Money.ZERO || b_1_30 != Money.ZERO || b_31_60 != Money.ZERO || b_61_90 != Money.ZERO || b_90 != Money.ZERO
    }

    addOwing(Money owing, dateDue, atDate) {
        if (owing == Money.ZERO) {
            return
        }
        switch (java.time.temporal.ChronoUnit.DAYS.between(dateDue, atDate).intValue()) {
            case 1..30:
                b_1_30 += owing
                break
            case 31..60:
                b_31_60 += owing
                break
            case 61..90:
                b_61_90 += owing
                break
            case { it > 90 }:
                b_90 += owing
                break
            default:
                b_0 += owing
        }
    }

}

