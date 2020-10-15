
def run(args) {

    def today =  new Date()
    today.set(hourOfDay: 0, minute: 0, second: 0)

    def courseClasses = ObjectSelect.query(CourseClass)
            .where(CourseClass.IS_CANCELLED.eq(false))
            .and(CourseClass.START_DATE_TIME.between(today+2, today+3))
            .select(args.context)

    courseClasses.each { cc ->
        cc.successAndQueuedEnrolments.each { enrolment ->


            def documents = enrolment.courseClass.course.documents.findAll { d ->
                d.webVisibility == AttachmentInfoVisibility.STUDENTS
            }

            if (documents.size() > 0) {

                def doc_links = ""

                documents.each { d ->
                    doc_links += enrolment.student.contact.getPortalLink(d) + " \n"
                }

                email {
                    to enrolment.student.contact.email
                    from Preferences.get("email.from")
                    subject "Enrolment reminder"
                    content "You have enrolled in a course that has notes associated. It is important that you download your notes prior to your class. " +
                            "The notes are in PDF format. You may choose to print your notes and bring the hard copy with you or save your notes to your own electronic device.\n\n" +
                            "Please follow this link to access your notes.\n" +
                            "${doc_links}\n" +
                            "Alternatively, you may request a hard copy of the notes to be available for you in class. " +
                            "Should that be the case please email notes@sydneycommunitycollege.edu.au with the name of your course."
                }
            }
        }
    }
}