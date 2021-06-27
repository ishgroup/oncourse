def classesStartingTomorrow = query {
    entity "CourseClass"
    query "isCancelled is false and startDateTime is tomorrow"
}
records = classesStartingTomorrow.findAll { cc -> cc.successAndQueuedEnrolments.size() >= cc.minimumPlaces }*.successAndQueuedEnrolments.flatten()
message {
    template classCommencementTemplate
    record records
}