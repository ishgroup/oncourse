if (tagName == "" || record.courseClass.hasTag(tagName)) {
    message {
        template enrolmentNotificationTemplate
        records record.courseClass.tutorRoles*.tutor.flatten()
        enrolment record
    }
}
