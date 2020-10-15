def run(args) {
    def enrolment = args.entity

    if(!enrolment.courseClass.course.hasTag("Moodle")) {
        if (enrolment.status == EnrolmentStatus.SUCCESS && enrolment.confirmationStatus == ConfirmationStatus.NOT_SENT) {

            email {
                template "Enrolment Confirmation"
                bindings enrolment: enrolment
                to enrolment.student.contact
                from ("acwa@acwa.asn.au", "ACWA Events")
            }
            enrolment.setConfirmationStatus(ConfirmationStatus.SENT)
            args.context.commitChanges()
        }
    }
}



