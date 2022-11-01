def allClasses = query {
    entity "CourseClass"
    query "isCancelled is false and endDateTime is yesterday"
}

def nonVetClasses = allClasses.findAll { cc -> cc.course.courseModules.isEmpty() }

//change 'notVETClasses' below to 'allClasses' to create attendance certificates for all classes, including those with Units of Competency attached
nonVetClasses.each { cc ->

    records = cc.successAndQueuedEnrolments
    if(attendanceRequired){
        records = cc.successAndQueuedEnrolments.findAll { e -> e.attendancePercent >= attendanceThreshold }
    }

    records.each { enrolment ->
        def printData = report {
            keycode certificateReportTemplate
            records Arrays.asList(enrolment)
            background certificateReportBackground
        }

        document {
            action "create"
            content printData
            name "${cc.uniqueCode}_${enrolment.student.contact.lastName}_${enrolment.student.contact.firstName}_Certificate_Attendance.pdf"
            mimeType "application/pdf"
            permission AttachmentInfoVisibility.STUDENTS
            attach enrolment
        }
    }
    message {
        template certificateMessageTemplate
        record records
    }
}