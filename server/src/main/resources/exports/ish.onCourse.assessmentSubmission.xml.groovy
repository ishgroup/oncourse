xml.mkp.xmlDeclaration(version: "1.0", encoding: "utf-8")

xml.data() {
    records.each { AssessmentSubmission submission ->
        assessmentSubmission(id: submission.id) {
            createdOn(submission.createdOn?.format("d-M-y HH:mm:ss"))
            modifiedOn(submission.modifiedOn?.format("d-M-y HH:mm:ss"))
            studentName(submission.studentName)
            courseClassName(submission.courseClassName)
            assessmentName(submission.assessmentName)
            submittedOn(submission.submittedOn?.format("d-M-y"))
            markedOn(submission.markedOn?.format("d-M-y"))
        }
    }
}