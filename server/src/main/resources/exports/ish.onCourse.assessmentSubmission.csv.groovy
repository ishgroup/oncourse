records.each { AssessmentSubmission submission ->
    csv << [
            "id"                    : submission.id,
            "createdOn"             : submission.createdOn?.format("d-M-y HH:mm:ss"),
            "modifiedOn"            : submission.modifiedOn?.format("d-M-y HH:mm:ss"),
            "Student name"          : submission.studentName,
            "Class name"            : submission.courseClassName,
            "Assessment name"       : submission.assessmentName,
            "Submitted on"          : submission.submittedOn?.format("d-M-y"),
            "Marked on"             : submission.markedOn?.format("d-M-y")
    ]
}