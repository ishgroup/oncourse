def run(args) {
    def currentDate = new Date()
    def allCertificates = ObjectSelect.query(Certificate)
            .where(Certificate.PRINTED_ON.isNull())
            .select(args.context)


    def usiNonVerifiedCerts = allCertificates.findAll { c ->
        c?.student?.usiStatus != UsiStatus.VERIFIED
    }


    def usiVerifiedCerts = allCertificates.findAll { c ->
        c?.student?.usiStatus == UsiStatus.VERIFIED
    }

    usiNonVerifiedCerts.each { c ->
        email {
            template "coffee certificate not printed invalid usi"
            to c.student.contact
            bindings certificate: c
        }

    }


    usiVerifiedCerts.each { c ->
        def eLsit = c.student.enrolments.findAll { e ->
             e.outcomes.contains(c.certificateOutcomes[0].outcome) &&
                    EnrolmentStatus.STATUSES_LEGIT.contains(e.status) &&
                    e.documents.find { d -> d.name == "${e.courseClass.uniqueCode}_${c.student.contact.lastName}_${c.student.contact.firstName}_Certificate.pdf" } == null
        }

        if (eLsit.size() > 0) {

            def bg = 'oncourse_SOA_Template_B.pdf'
            if (c.qualification.type == QualificationType.SKILLSET_TYPE) {
                bg = ''
            }

			def printData = report {
                keycode "coffee.certificate"
                records Arrays.asList(c)
				background bg
            }

            eLsit.each { e ->
                document {
                    action "create"
                    content printData
                    name "${e.courseClass.uniqueCode}_${c.student.contact.lastName}_${c.student.contact.firstName}_Certificate.pdf"
                    mimeType "application/pdf"
                    permission AttachmentInfoVisibility.STUDENTS
                    attach e
                }

                email {
                    template "coffee certificate available"
                    bindings enrolment: e
                    to e.student.contact
                }

                c.setPrintedOn(currentDate)
                if (c.issuedOn == null) {
                    c.setIssuedOn(currentDate)
                }
                args.context.commitChanges()

            }
        }
    }
}