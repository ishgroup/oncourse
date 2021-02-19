if (send_before_each_session) {

    // Query closure start
    records = query {
        entity "TutorAttendance"
        query "session.courseClass.startDateTime not is null and session.courseClass.isCancelled is false and session.startDatetime after today + 1 days and session.startDatetime before today + 3 days"
    }
    // Query closure end

    records.each { attandance ->
        message {
            template sessionCommencementTemplate
            to attandance.courseClassTutor.tutor.contact
            tutorAttendance attandance
        }
    }

} else {
    // Query closure start
    records = query {
        entity "CourseClass"
        query "isCancelled is false and startDateTime not is null and startDateTime after today + 1 days and startDateTime before today + 3 days"
    }
    // Query closure end

    message {
        template classCommencementTemplate
        record records
    }
}
