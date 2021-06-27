def classesStartingTomorrow = query {
    entity "CourseClass"
    query "isCancelled is false and startDateTime is tomorrow + 7 days"
}
records = classesStartingTomorrow*.successAndQueuedEnrolments.flatten()
message {
    template classCommencementTemplate
    record records
}