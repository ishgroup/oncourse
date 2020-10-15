def run(args) {
    def invoice = args.entity

    if (invoice.confirmationStatus == ConfirmationStatus.NOT_SENT) {
        if (!Money.ZERO.equals(invoice.totalIncTax)) {


            def toEmail = (invoice.corporatePassUsed ? invoice.corporatePassUsed.email : invoice.contact.email)

            def emailContent = "o-------------------o\n" +
                    "Tax Invoice\n" +
                    "o-------------------o\n" +
                    "Invoice created on: ${invoice.invoiceDate.format("d/M/yy")}\n" +
                    "Invoice due on: ${invoice.dateDue.format("d/M/yy")}\n" +
                    "Invoice number: ${invoice.invoiceNumber}\n" +
                    "o-------------------o\n" +
                    "Total (inc Tax) ${invoice.totalIncTax}\n" +
                    "Total Tax ${invoice.totalTax}\n" +
                    "Total Paid ${invoice.totalIncTax.subtract(invoice.amountOwing)}\n" +
                    "Total ${invoice.amountOwing >= 0 ? 'Owing' : 'Credit'} ${invoice.amountOwing}\n" +
                    "o-------------------o\n"

            email {
                subject "Invoice #${invoice.invoiceNumber}"
                content emailContent
                to toEmail
                from preference.email.from
                attachment "Invoice ${invoice.invoiceNumber}.pdf", "application/pdf", report {
                                                                                            keycode "ish.onCourse.invoiceReport"
                                                                                            records invoice
                                                                                            background "Invoices.pdf"
                                                                                        }
            }
         }

        invoice.setConfirmationStatus(ConfirmationStatus.SENT)
        args.context.commitChanges()
    }
}