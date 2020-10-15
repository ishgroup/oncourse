
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
        
        if (enrolment.courseClass.customField("ZoomMeeting")) {
      email {
          template "Zoom Link"
          bindings enrolment: enrolment
          to enrolment.student.contact
        }
      }
    }

