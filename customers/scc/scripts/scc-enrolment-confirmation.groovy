
def run(args) {
    def enrolment = args.entity

    if (enrolment.status == EnrolmentStatus.SUCCESS && enrolment.confirmationStatus == ConfirmationStatus.NOT_SENT) {

        def documents = enrolment.courseClass.course.documents.findAll { d ->
            d.webVisibility == AttachmentInfoVisibility.STUDENTS
        }


        email {
            template "SCC Enrolment Confirmation"
            bindings enrolment: enrolment, documents: documents
            to enrolment.student.contact
        }

        enrolment.setConfirmationStatus(ConfirmationStatus.SENT)
        args.context.commitChanges()
    }
}