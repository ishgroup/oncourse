import java.time.LocalDate

def enrolments = query {
    entity "Enrolment"
    query "courseClass.course.isVET is true and outcomes.modifiedOn is yesterday and outcomes.status not is STATUS_NOT_SET "
}

enrolments.each { enrolment ->

    def unlinkedOutcomes = enrolment.outcomes.findAll { o -> o.certificateOutcomes.empty }
    int successfulOutcomesCount = unlinkedOutcomes.findAll { o -> OutcomeStatus.STATUSES_VALID_FOR_CERTIFICATE.contains(o.status) }.size()

    if (successfulOutcomesCount > 0) {
        boolean fullQualification = enrolment.courseClass.course.isSufficientForQualification
        boolean validToCertificate = successfulOutcomesCount == enrolment.outcomes.size()

        enrolment.context.newObject(Certificate).with { certificate ->
            certificate.student = enrolment.student
            certificate.qualification = enrolment.courseClass.course.qualification
            certificate.awardedOn = LocalDate.now()
            unlinkedOutcomes.each { o ->
                certificate.addToOutcomes(o)
            }

            if (fullQualification && validToCertificate) {
                certificate.isQualification = true
            } else {
                certificate.isQualification = false
            }
        }
        enrolment.context.commitChanges()
    }
}
