List<ExportInvoice> rows = []

ObjectSelect.query(Invoice)
    .where(Invoice.INVOICE_DATE.lte(atDate))
    .prefetch(Invoice.CONTACT.joint())
    .prefetch(Invoice.PAYMENT_IN_LINES.joint())
    .prefetch(Invoice.PAYMENT_OUT_LINES.joint())
    .select(context)
    .each { i ->

        def row = new ExportInvoice(i)

        def paymentLines = i.paymentLines.findAll { pl -> pl.payment.paymentDate <= atDate && pl.payment.status == PaymentStatus.SUCCESS }
        def owing = i.totalIncTax.subtract(paymentLines.sum { pl -> pl instanceof PaymentOutLine ? pl.amount.negate() : pl.amount } ?: Money.ZERO)

        if (owing != Money.ZERO) {
            // For each due date, starting from the latest, allocate some of the amount owing
            i.invoiceDueDates.sort { it.dueDate }.reverse().findAll { invoiceDueDate ->
                def thisAmount = owing.min(invoiceDueDate.amount)
                owing = owing - thisAmount
                row.addOwing(thisAmount, invoiceDueDate.dueDate, atDate)

                return owing > 0  // breaks the loop when we run out of owing
            }

            // anything remaining just attch to the invoice due date
            row.addOwing(owing, i.dateDue, atDate)

            if (row.nonZero()) {
                rows << row
            }
        }
    }

def title = "Debtor as at end ${atDate}".toString()
if (detail) {
    rows.sort { a,b -> a.key <=> b.key ?: a.invoice.dateDue <=> b.invoice.dateDue }
         .each { row ->
            csv << [
                (title)                     : row.name,
                "Not due"                   : row.b_0.toPlainString(),
                "Overdue up to 30 days"     : row.b_1_30.toPlainString(),
                "Overdue 31-60 days"        : row.b_31_60.toPlainString(),
                "Overdue 61-90 days"        : row.b_61_90.toPlainString(),
                "Overdue over 90 days"      : row.b_90.toPlainString(),
                "Date due"                  : row.invoice.dateDue,
                "Invoice id"                : row.invoice.id
            ]
         }
} else {
    rows.groupBy { it.key }
        .sort()
        .each { key, contactGroup ->
            csv << [
                (title)                     : contactGroup.first().name,
                "Not due"                   : contactGroup.b_0.sum().toPlainString(),
                "Overdue up to 30 days"     : contactGroup.b_1_30.sum().toPlainString(),
                "Overdue 31-60 days"        : contactGroup.b_31_60.sum().toPlainString(),
                "Overdue 61-90 days"        : contactGroup.b_61_90.sum().toPlainString(),
                "Overdue over 90 days"      : contactGroup.b_90.sum().toPlainString()
            ]
    }
}

// A row in the export.
class ExportInvoice {
    String key // a key for grouping and sorting (unique id, but starting with name for alphabetical sorting)
    Invoice invoice

    Money b_0 = Money.ZERO
    Money b_1_30 = Money.ZERO
    Money b_31_60 = Money.ZERO
    Money b_61_90 = Money.ZERO
    Money b_90 = Money.ZERO

    ExportInvoice(Invoice i) {
        this.invoice = invoice
        this.key = i.contact.lastName + i.contact.id.toString()
    }

    boolean nonZero() {
        return b_0 != Money.ZERO || b_1_30 != Money.ZERO || b_31_60 != Money.ZERO || b_61_90 != Money.ZERO || b_90 != Money.ZERO
    }

    String getName() {
        return invoice.contact.firstName ? "${invoice.contact.lastName}, ${invoice.contact.firstName}" : invoice.contact.lastName
    }

    void addOwing(Money owing, dateDue, atDate) {
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

