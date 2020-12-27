if (record.status == PaymentStatus.SUCCESS && record.confirmationStatus == ConfirmationStatus.NOT_SENT && !Money.ZERO.equals(record.amount)) {
    message {
        template paymentReceiptTemplate
        record record
    }

    record.setConfirmationStatus(ConfirmationStatus.SENT)
    context.commitChanges()
}
