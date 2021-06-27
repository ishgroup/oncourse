if (record.status == PaymentStatus.SUCCESS && record.confirmationStatus == ConfirmationStatus.NOT_SENT) {
    message {
        template refundAdviceTemplate
        record records
    }

    record.setConfirmationStatus(ConfirmationStatus.SENT)
    context.commitChanges()
}