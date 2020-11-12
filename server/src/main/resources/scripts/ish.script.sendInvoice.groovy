def invoice = record

if (invoice.confirmationStatus == ConfirmationStatus.NOT_SENT) {
    if (!Money.ZERO.equals(invoice.totalIncTax)) {
        message {
            template invoiceTemplate
            record invoice
        }
    }

    invoice.setConfirmationStatus(ConfirmationStatus.SENT)
    context.commitChanges()
}
