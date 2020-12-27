if (record.confirmationStatus == ConfirmationStatus.NOT_SENT) {

    if (record.status == ApplicationStatus.OFFERED) {
        message {
            template applicationAcceptedTemplate
            record record
        }
    } else if (record.status == ApplicationStatus.REJECTED) {
        message {
            template applicationRejectedTemplate
            record record
        }
    } else {
        return
    }

    record.setConfirmationStatus(ConfirmationStatus.SENT)
    context.commitChanges()
}
