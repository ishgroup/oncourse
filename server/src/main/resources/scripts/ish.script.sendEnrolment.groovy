def enrolment = record
if (enrolment.confirmationStatus == ConfirmationStatus.NOT_SENT) {

    message {
        template enrolmentConfirmationTemplate
        record enrolment
    }

    enrolment.setConfirmationStatus(ConfirmationStatus.SENT)
    context.commitChanges()
}
