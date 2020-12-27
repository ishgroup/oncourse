if (record.status == PaymentStatus.SUCCESS && record.confirmationStatus == ConfirmationStatus.NOT_SENT) {
    message {
        template refundAdviceTemplate
        record record
    }

    record.setConfirmationStatus(ConfirmationStatus.SENT)
    context.commitChanges()
}
