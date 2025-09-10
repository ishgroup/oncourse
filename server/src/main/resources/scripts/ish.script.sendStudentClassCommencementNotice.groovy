def classesQuery = "isCancelled is false and startDateTime not is null and startDateTime is today"
number_of_days_int = number_of_days.toInteger()
if (number_of_days_int > 0)
    classesQuery = classesQuery + " + ${number_of_days_int} days"
def classesStartingTomorrow = query {
    entity "CourseClass"
    query classesQuery
}
records = classesStartingTomorrow.findAll { cc -> cc.successAndQueuedEnrolments.size() >= cc.minimumPlaces }*.successAndQueuedEnrolments.flatten()
message {
    template classCommencementTemplate
    record records
}
