records = query {
    entity "Enrolment"
    query "createdOn after today - 30 days and status is SUCCESS and courseClass.course.courseModules.id not is null and student.usi is null and (courseClass.endDateTime is null or courseClass.endDateTime after ${usiRequiredDate})"
}
message {
    template usiReminderTemplate
    record records
}
message {
    from preference.email.admin
    subject 'USI reminder email notification'
    to preference.email.admin
    content "A USI reminder was sent to ${records.size()} students enrolled in VET classes who have not supplied their USI."
}