def classesQuery = "isCancelled is false and startDateTime not is null and startDateTime is today"
if(!number_of_days.equals("0"))
    classesQuery = classesQuery + " + ${number_of_days} days"
def classesStartingTomorrow = query {
    entity "CourseClass"
    query classesQuery
}
records = classesStartingTomorrow.findAll { cc -> cc.successAndQueuedEnrolments.size() >= cc.minimumPlaces }*.successAndQueuedEnrolments.flatten()
message {
    template classCommencementTemplate
    record records
}