if (tagName == null || tagName == "" || record.courseClass.course.hasTag(tagName)) {
    alchemer {
        template surveyInvitationTemplate
        reply preference.email.from
        contact record.student.contact
    }
}