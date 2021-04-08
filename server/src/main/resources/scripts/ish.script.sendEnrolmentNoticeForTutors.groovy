if (tagName == "" || record.courseClass.hasTag(tagName)) {
    records = record.courseClass.tutorRoles*.tutor.flatten()
    message {
        template enrolmentNotificationTemplate
        record records
        enrolment record
    }
}