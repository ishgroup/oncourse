if (record.confirmationStatus == ConfirmationStatus.NOT_SENT) {
    if (!Money.ZERO.equals(record.totalIncTax)) {
        message {
            template invoiceTemplate
            record records
        }
    }

    record.setConfirmationStatus(ConfirmationStatus.SENT)
    context.commitChanges()
}