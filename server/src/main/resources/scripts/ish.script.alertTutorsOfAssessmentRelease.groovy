def assessmentsReleasedToday = query {
    entity "AssessmentClass"
    query "assessment.active is true and (releaseDate is today or releaseDate is tomorrow)"
}

assessmentsReleasedToday.each { a ->
    message {
        template templateKeycode
        records a.courseClass*.tutorRoles*.tutor.flatten().unique()
        assessment a
    }
}
