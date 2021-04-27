if (record.confirmationStatus == ConfirmationStatus.NOT_SENT) {

    message {
        template enrolmentConfirmationTemplate
        record records
    }

    record.setConfirmationStatus(ConfirmationStatus.SENT)
    context.commitChanges()
}