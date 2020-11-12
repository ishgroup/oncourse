def paymentOut = record
if (paymentOut.status == PaymentStatus.SUCCESS && paymentOut.confirmationStatus == ConfirmationStatus.NOT_SENT) {
    message {
        template refundAdviceTemplate
        record paymentOut
    }

    paymentOut.setConfirmationStatus(ConfirmationStatus.SENT)
    context.commitChanges()
}
