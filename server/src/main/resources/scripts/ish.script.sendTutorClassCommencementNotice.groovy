if (send_before_each_session) {

    records = query {
        entity "TutorAttendance"
        query "session.courseClass.startDateTime not is null and session.courseClass.isCancelled is false and session.startDatetime is tomorrow + ${number_of_days} days"
    }

    message {
        template sessionCommencementTemplate
        record records
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