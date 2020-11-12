

def classesStartingTomorrow = query {
    entity "CourseClass"
    query "isCancelled is false and startDateTime is tomorrow"
}

message {
    template classCommencementTemplate
    records classesStartingTomorrow.findAll { cc -> cc.successAndQueuedEnrolments.size() >= cc.minimumPlaces }*.successAndQueuedEnrolments.flatten()
}
