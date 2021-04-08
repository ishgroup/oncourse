def allCertificates = query {
    entity "Certificate"
    query "student.usiStatus is VERIFIED and printedOn is null and revokedOn is null"
}
def currentDate = LocalDate.now()
allCertificates.each { certificate ->
    def dataContext = certificate.context
    records = certificate.student.enrolments.findAll { enrolment ->
        enrolment.outcomes.contains(certificate.certificateOutcomes[0].outcome) &&
            EnrolmentStatus.STATUSES_LEGIT.contains(enrolment.status) &&
            enrolment.documents.find { d ->
                d.name == "${enrolment.courseClass.uniqueCode}_${certificate.student.contact.lastName}_${certificate.student.contact.firstName}_Certificate.pdf" } == null
    }

    if (records.size() > 0) {
        def backGround = certificate.isQualification ?
                (QualificationType.SKILLSET_TYPE == certificate.qualification.type ?
                        vetSkillsetBackground : vetQualificationBackground) : vetSoaBackground

        if (certificate.issuedOn == null) {
            certificate.setIssuedOn(currentDate)
        }
        certificate.setPrintedOn(currentDate)
        dataContext.commitChanges()

        def printData = report {
            keycode certificateReportTemplate
            records Arrays.asList(certificate)
            param "print_qr_code": true
            background backGround
        }

        records.each { enrolment ->
            document {
                action "create"
                content printData
                name "${enrolment.courseClass.uniqueCode}_${certificate.student.contact.lastName}_${certificate.student.contact.firstName}_Certificate.pdf"
                mimeType "application/pdf"
                permission AttachmentInfoVisibility.STUDENTS
                attach enrolment
            }
        }
    }
    message {
        template certificateMessageTemplate
        record records
    }
}