def classes = query {
    entity "CourseClass"
    query "isCancelled is false and endDateTime is yesterday and (taggingRelations is null or taggingRelations.tag.name not is \"no survey\")"
}
records = classes*.successAndQueuedEnrolments
message {
    template courseSurveyTemplate
    record records
}
