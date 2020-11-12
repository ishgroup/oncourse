def voucher = record
if (voucher.status == ProductStatus.ACTIVE && voucher.confirmationStatus == ConfirmationStatus.NOT_SENT) {
    message {
        template voucherEmailTemplate
        record voucher
    }

    voucher.setConfirmationStatus(ConfirmationStatus.SENT)
    context.commitChanges()
}
