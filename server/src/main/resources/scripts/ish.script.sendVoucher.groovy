if (record.status == ProductStatus.ACTIVE && record.confirmationStatus == ConfirmationStatus.NOT_SENT) {
    message {
        template voucherEmailTemplate
        record record
    }

    record.setConfirmationStatus(ConfirmationStatus.SENT)
    context.commitChanges()
}
