def assessmentsReleasedToday = query {
    entity "AssessmentClass"
    query "assessment.active is true and (releaseDate is today or releaseDate is tomorrow)"
}

def assesmentsGrouped = [:]

assessmentsReleasedToday.each { ac ->
    ac.courseClass.successAndQueuedEnrolments*.student.flatten().unique().each { s ->
        if (!assesmentsGrouped[s]) {
            assesmentsGrouped[s] = []
        }
        assesmentsGrouped[s] << ac
    }
}

assesmentsGrouped.each { student, assessmentClassList ->
    message {
        template templateKeycode
        record student
        assessments assessmentClassList
    }

}
