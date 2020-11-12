message {
    template classCancellationTemplate
    records record.tutorRoles*.tutor.unique()
    courseClass record
}
