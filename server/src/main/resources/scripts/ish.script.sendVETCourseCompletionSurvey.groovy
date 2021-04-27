records = query {
    entity "Enrolment"
    query "courseClass.isCancelled is false and courseClass.course.isVET is true and courseClass.endDateTime before today + 1 days and courseClass.endDateTime after today - 14 days and courseClass.course.courseModules.module.id not is null and status in ( NEW , QUEUED ,  IN_TRANSACTION , SUCCESS )"
}
message {
    template vetCourseSurveyTemplate
    record records
}