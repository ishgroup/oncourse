def classes = query {
    entity "CourseClass"
    query "isCancelled is false and endDateTime is today or endDateTime is yesterday"
}

classes.each { courseClass ->
    if (!courseClass.hasTag("no survey")) {
        message {
            template courseSurveyTemplate
            records courseClass.successAndQueuedEnrolments
        }
    }
}
