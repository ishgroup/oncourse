if (send_before_each_session) {

    // Query closure start
    def result = query {
        entity "TutorAttendance"
        query "session.courseClass.startDateTime not is null and session.courseClass.isCancelled is false and session.startDatetime after today + 1 days and session.startDatetime before today + 3 days"
    }
    // Query closure end

    result.each { tutorAttendance ->
        email {
            template sessionCommencementTemplate
            to tutorAttendance.courseClassTutor.tutor.contact
            bindings record: tutorAttendance
        }
    }

} else {
    // Query closure start
    def result = query {
        entity "CourseClassTutor"
        query "courseClass.startDateTime not is null and courseClass.isCancelled is false and courseClass.startDateTime after today + 1 days and courseClass.startDateTime before today + 3 days"
    }
    // Query closure end

    result.each { courseClassTutor ->
        email {
            template classCommencementTemplate
            to courseClassTutor.tutor.contact
            bindings record: courseClassTutor
        }
    }
}
