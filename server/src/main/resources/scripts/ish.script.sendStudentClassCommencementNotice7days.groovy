def classesStartingTomorrow = query {
    entity "CourseClass"
    query "isCancelled is false and startDateTime is tomorrow + 7 days"
}

message {
    template classCommencementTemplate
    records classesStartingTomorrow*.successAndQueuedEnrolments.flatten()
}
