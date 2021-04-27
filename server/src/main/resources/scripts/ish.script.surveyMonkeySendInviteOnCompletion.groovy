def enrolmentsJustEnded = query {
    entity "Enrolment"
    query "courseClass.endDateTime is yesterday"
}

enrolmentsJustEnded.each { enrolment ->
    if (tagName == null || tagName == "" || enrolment.courseClass.course.hasTag(tagName)) {
        surveyMonkey {
            contact enrolment.student.contact
        }
    }
}