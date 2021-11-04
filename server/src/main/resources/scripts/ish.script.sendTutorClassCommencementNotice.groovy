if (send_before_each_session) {

    records = query {
        entity "TutorAttendance"
        query "session.courseClass.startDateTime not is null and session.courseClass.isCancelled is false and startDatetime after today + 1 days and startDatetime before today + 3 days"
    }

    records.each { attendance ->
        message {
            template sessionCommencementTemplate
            to attendance.courseClassTutor.tutor.contact
            record attendance
        }
    }

} else {
    records = query {
        entity "CourseClass"
        query "isCancelled is false and startDateTime not is null and startDateTime after today + 1 days and startDateTime before today + 3 days"
    }

    message {
        template classCommencementTemplate
        record records
    }
}