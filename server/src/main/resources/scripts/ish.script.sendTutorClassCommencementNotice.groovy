if (send_before_each_session) {

    records = query {
        entity "TutorAttendance"
        query "session.courseClass.startDateTime not is null and session.courseClass.isCancelled is false and session.startDatetime is tomorrow + ${number_of_days} days"
    }

    records.each { attendance ->
        message {
            template sessionCommencementTemplate
            to attendance.courseClassTutor.tutor.contact
            tutorAttendance attendance
        }
    }

} else {
    records = query {
        entity "CourseClass"
        query "isCancelled is false and startDateTime not is null and startDateTime after today + 1 days and startDateTime before today + ${number_of_days} days"
    }

    message {
        template classCommencementTemplate
        record records
    }
}