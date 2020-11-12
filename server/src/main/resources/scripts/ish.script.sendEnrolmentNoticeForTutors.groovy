def newEnrolment = record
if (tagName == "" || newEnrolment.courseClass.hasTag(tagName)) {
    message {
        template enrolmentNotificationTemplate
        records newEnrolment.courseClass.tutorRoles*.tutor.flatten()
        enrolment newEnrolment
    }
}
