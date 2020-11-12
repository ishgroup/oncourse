def application = record

if (application.confirmationStatus == ConfirmationStatus.NOT_SENT) {

    if (application.status == ApplicationStatus.OFFERED) {
        message {
            template applicationAcceptedTemplate
            record application
        }
    } else if (application.status == ApplicationStatus.REJECTED) {
        message {
            template applicationRejectedTemplate
            record application
        }
    } else {
        return
    }

    application.setConfirmationStatus(ConfirmationStatus.SENT)
    context.commitChanges()
}
