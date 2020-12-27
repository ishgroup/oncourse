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
        entity "CourseClassTutor"
        query "courseClass.startDateTime not is null and courseClass.isCancelled is false and courseClass.startDateTime after today + 1 days and courseClass.startDateTime before today + 3 days"
    }
    // Query closure end

    records.each { cctutor ->
        message {
            template classCommencementTemplate
            to cctutor.tutor.contact
            courseClassTutor cctutor
        }
    }
}
