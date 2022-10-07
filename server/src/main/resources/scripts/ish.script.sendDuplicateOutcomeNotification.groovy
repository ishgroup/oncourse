import ish.common.types.EnrolmentStatus
import ish.oncourse.server.cayenne.Enrolment
import ish.oncourse.server.cayenne.Outcome


def enrolment = record as Enrolment
def enrolmentModules = enrolment.outcomes*.module.findAll{it}
def studentOutcomes = enrolment.student.enrolments.findAll { it.id != enrolment.id && it.status != EnrolmentStatus.CANCELLED}.outcomes.flatten() as List<Outcome>
def alreadyEnrolledOutcomes = studentOutcomes.findAll {enrolmentModules.contains(it.module)}.findAll{it}
if(!alreadyEnrolledOutcomes.isEmpty()){
    message {
        template "ish.email.duplicateOutcomesNotification"
        to preference.email.admin
        record enrolment
        outcomes alreadyEnrolledOutcomes
    }
}