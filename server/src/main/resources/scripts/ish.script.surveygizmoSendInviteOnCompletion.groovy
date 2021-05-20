def enrolmentsJustEnded = query {
    entity "Enrolment"
    query "courseClass.endDateTime is yesterday"
}

enrolmentsJustEnded.each { enrolment ->
    if (tagName == null || tagName == "" || enrolment.courseClass.course.hasTag(tagName)) {
        surveyGizmo {
            template surveyInvitationTemplate
            reply preference.email.from
            contact enrolment.student.contact
        }
    }
}