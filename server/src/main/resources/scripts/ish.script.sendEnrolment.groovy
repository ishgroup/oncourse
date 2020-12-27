if (record.confirmationStatus == ConfirmationStatus.NOT_SENT) {

    message {
        template enrolmentConfirmationTemplate
        record record
    }

    record.setConfirmationStatus(ConfirmationStatus.SENT)
    context.commitChanges()
}
