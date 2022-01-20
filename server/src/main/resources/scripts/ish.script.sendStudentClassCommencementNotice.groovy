def classesStartingTomorrow = query {
    entity "CourseClass"
    query "isCancelled is false and startDateTime not is null and startDateTime is today + ${number_of_days} days"
}
records = classesStartingTomorrow.findAll { cc -> cc.successAndQueuedEnrolments.size() >= cc.minimumPlaces }*.successAndQueuedEnrolments.flatten()
message {
    template classCommencementTemplate
    record records
}