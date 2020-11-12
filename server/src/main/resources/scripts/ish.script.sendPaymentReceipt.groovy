def paymentIn = record
if (paymentIn.status == PaymentStatus.SUCCESS && paymentIn.confirmationStatus == ConfirmationStatus.NOT_SENT && !Money.ZERO.equals(paymentIn.amount)) {
    message {
        template paymentReceiptTemplate
        record paymentIn
    }

    paymentIn.setConfirmationStatus(ConfirmationStatus.SENT)
    context.commitChanges()
}
