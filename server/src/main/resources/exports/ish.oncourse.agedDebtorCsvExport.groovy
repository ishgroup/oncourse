List<ExportInvoice> rows = []

// Our starting target of invoices are:
// 1. Created before the atDate, and either
// a. Those which currently have something owing, or
// b. Where there is a payment after the atDate

// Because the atDate is usually in the recent past, this is a shorter list to look for than starting at the beginning of time
def invoices = ObjectSelect.query(Invoice)
        .where(Invoice.INVOICE_DATE.lte(atDate)
                .andExp(
                    Invoice.AMOUNT_OWING.ne(Money.ZERO)
                    .orExp(Invoice.PAYMENT_IN_LINES.outer().dot(PaymentInLine.PAYMENT_IN.outer().dot(PaymentIn.PAYMENT_DATE)).gt(atDate))
                    .orExp(Invoice.PAYMENT_OUT_LINES.outer().dot(PaymentOutLine.PAYMENT_OUT.outer().dot(PaymentOut.PAYMENT_DATE)).gt(atDate))
                )
        )
        .select(context)

invoices.each { i ->

    // get the total of all successful payments for this invoice before the atDate
    def paymentOut = ObjectSelect.query(PaymentOutLine)
            .where(PaymentOutLine.INVOICE.eq(i))
            .and(PaymentOutLine.PAYMENT_OUT.dot(PaymentOut.PAYMENT_DATE).lte(atDate))
            .and(PaymentOutLine.PAYMENT_OUT.dot(PaymentOut.STATUS).eq(PaymentStatus.SUCCESS))
            .sum(PaymentOutLine.AMOUNT)
            .selectOne(context) ?: Money.ZERO

    def paymentIn = ObjectSelect.query(PaymentInLine)
            .where(PaymentInLine.INVOICE.eq(i))
            .and(PaymentInLine.PAYMENT_IN.dot(PaymentIn.PAYMENT_DATE).lte(atDate))
            .and(PaymentInLine.PAYMENT_IN.dot(PaymentIn.STATUS).eq(PaymentStatus.SUCCESS))
            .sum(PaymentInLine.AMOUNT)
            .selectOne(context) ?: Money.ZERO

    def owing = i.totalIncTax - paymentIn + paymentOut

    if (owing != Money.ZERO) {
        def row = new ExportInvoice(i)

        // For each payment plan due date, starting from the latest, allocate some of the amount owing
        i.invoiceDueDates.sort { it.dueDate }.reverse().findAll { invoiceDueDate ->
            def thisAmount = owing.min(invoiceDueDate.amount)
            owing = owing - thisAmount
            row.addOwing(thisAmount, invoiceDueDate.dueDate, atDate)

            return owing != Money.ZERO  // breaks the loop when we run out of owing
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
                        "Contact identifier"        : row.invoice.contact.id,
                        "Invoice number"            : row.invoice.invoiceNumber,
                        "Invoice date"              : row.invoice.invoiceDate,
                        "Date due"                  : row.invoice.dateDue,
                        "Not due"                   : row.b_0.toPlainString(),
                        "Overdue up to 30 days"     : row.b_1_30.toPlainString(),
                        "Overdue 31-60 days"        : row.b_31_60.toPlainString(),
                        "Overdue 61-90 days"        : row.b_61_90.toPlainString(),
                        "Overdue over 90 days"      : row.b_90.toPlainString()
                ]
            }
} else {
    rows.groupBy { it.key }
            .sort()
            .each { key, contactGroup ->
                csv << [
                        (title)                     : contactGroup.first().name,
                        "Contact identifier"        : contactGroup.first().invoice.contact.id,
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
    String key // a contact key for grouping and sorting (unique id, but starting with name for alphabetical sorting)
    Invoice invoice

    Money b_0 = Money.ZERO
    Money b_1_30 = Money.ZERO
    Money b_31_60 = Money.ZERO
    Money b_61_90 = Money.ZERO
    Money b_90 = Money.ZERO

    ExportInvoice(Invoice invoice) {
        this.invoice = invoice
        this.key = invoice.contact.lastName + invoice.contact.id.toString()
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

