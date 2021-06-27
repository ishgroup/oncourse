records = record.tutorRoles*.tutor.unique()
message {
    template classCancellationTemplate
    record records
    courseClass record
}