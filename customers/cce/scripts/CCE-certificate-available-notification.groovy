/**
    Check if students attended their class and then create a certificate and send them an email to tell them about it.
*/

def run(args) {

    // Look this many days into the past. If the attendance is not marked by then, the student will never get a certificate
    def checkDays = 30

    // Only send a certificate if the student attended this percentage of sessions
    def minimumAttendance = 70

    def endDate = Calendar.getInstance().getTime()
    endDate.set(hourOfDay: 0, minute: 0, second: 0)

    def allClasses = ObjectSelect.query(CourseClass)
            .where(CourseClass.END_DATE_TIME.between(endDate - checkDays, endDate))
            .and(CourseClass.IS_CANCELLED.eq(false))
            .select(args.context)


    allClasses.each { cc ->

        def notYetSent = cc.successAndQueuedEnrolments.findAll { e -> !hasCert(e) }

        def attended = notYetSent.findAll { e -> e.attendancePercent >= minimumAttendance }

        attended.each { e ->

            def printData = report {
                keycode "cce.oncourse.statementOfCompletion"
                // keycode "ish.oncourse.nonVetCertificate"
                records Arrays.asList(e)
                background "Certificate of Attendance Background.pdf"
            }

            def d = document {
                action "create"
                content printData
                name getAttachmentName(e)
                mimeType "application/pdf"
                permission AttachmentInfoVisibility.STUDENTS
                attach e
            }

            email {
                template "CCE Certificate available"
                key "attendance-certificate", e
                keyCollision "drop"
                bindings enrolment: e, document: d
                to e.student.contact
            }
        }
    }
}


// Get the name of the attachment to add to the enrolment
// Never change this name or else students will get duplicates
def getAttachmentName(Enrolment e) {
    return "${e.courseClass.uniqueCode}_${e.student.contact.lastName}_${e.student.contact.firstName}_Statement_of_Completion.pdf"
}

// Check whether the enrolment already has a certificate
def hasCert(Enrolment e) {
    def result = false
    e.documents.each { d ->
        if(d.name.equals(getAttachmentName(e))) {
            result = true
        }
        result
    }
}