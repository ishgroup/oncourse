if (tagName == null || tagName == "" || record.courseClass.course.hasTag(tagName)) {
    surveyGizmo {
        template surveyInvitationTemplate
        reply preference.email.from
        contact record.student.contact
    }
}