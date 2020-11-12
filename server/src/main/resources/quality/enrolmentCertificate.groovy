import ish.common.types.OutcomeStatus
import ish.oncourse.types.Severity
import ish.oncourse.server.cayenne.CertificateOutcome
import ish.oncourse.server.cayenne.CourseClass
import ish.oncourse.server.cayenne.Enrolment
import ish.oncourse.server.cayenne.Outcome
import org.apache.cayenne.query.ObjectSelect

import java.time.LocalDate

def today = new Date()
LocalDate now = LocalDate.now()
def enrolments = ObjectSelect.query(Enrolment).where(Enrolment.OUTCOMES.dot(Outcome.MODULE).isNotNull())
		.and(Enrolment.OUTCOMES.dot(Outcome.STATUS).in(OutcomeStatus.STATUSES_VALID_FOR_CERTIFICATE))
		.and(Enrolment.OUTCOMES.outer().dot(Outcome.CERTIFICATE_OUTCOMES).dot(CertificateOutcome.ID).isNotNull())
		.orderBy(Enrolment.COURSE_CLASS.dot(CourseClass.END_DATE_TIME).asc()).select(context)

def results = []
def filtered = enrolments.findAll { it.outcomes*.endDate.find { (it < now.minusDays(28)) } }
if (filtered.size())
	results.addAll result {
		message "VET enrolments ended more than a month ago with no certificate created"
		severity Severity.ERROR
		records filtered
	}

filtered = enrolments.findAll { it.courseClass.endDateTime < (today - 7) && it.courseClass.endDateTime >= (today - 28)}
if (filtered.size())
	results.addAll result {
		message "VET enrolments ended in the last a month with no certificate created"
		severity Severity.WARNING
		records filtered
	}

filtered = enrolments.findAll { it.outcomes*.endDate.find { (it >= now.minusDays(7)) } }
if (filtered.size())
	results.addAll result {
		message "VET enrolments ended in the last week with no certificate created"
		severity Severity.ADVICE
		records filtered
	}


return results





e = enrolments.with { outcome.endDate < now.minusDays(28) }
result {
	message "VET enrolments ended more than a month ago with no certificate created"
	severity high
	records e
}

e = enrolments.with { outcome.endDate < now.minusDays(7) }
result {
	message "VET enrolments ended in the last a month with no certificate created"
	severity medium
	records e
}


e = enrolments.with { all the rest }
result {
	message "VET enrolments ended in the last week with no certificate created"
	severity low
	records e
}


