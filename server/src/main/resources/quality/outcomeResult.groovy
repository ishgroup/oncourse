import java.time.LocalDate

def today = LocalDate.now()
def enrolments = ObjectSelect.query(Enrolment).where(Enrolment.OUTCOMES.dot(Outcome.MODULE).isNotNull())
        .and(
            Enrolment.OUTCOMES.dot(Outcome.STATUS).isNull()
                    .orExp(Enrolment.OUTCOMES.dot(Outcome.STATUS).in(
                        OutcomeStatus.STATUS_NOT_SET,
                        OutcomeStatus.STATUS_NO_RESULT_QLD,
                        OutcomeStatus.STATUS_ASSESSABLE_CONTINUING_ENROLMENT))
        )
        .and(Enrolment.OUTCOMES.dot(Outcome.END_DATE).lt(today)).orderBy(Enrolment.OUTCOMES.dot(Outcome.END_DATE).asc()).select(context)

def results = []
def filtered = enrolments.findAll { it.outcomes*.endDate.find { it < today.minusDays(28) } }
if (filtered.size())
    results.addAll result {
        message "outcomes ended more than a month ago with no results"
        severity Severity.ERROR
        records filtered
    }

filtered = enrolments.findAll { it.outcomes*.endDate.find { it < today.minusDays(7) && it >= today.minusDays(28)} }
if (filtered.size())
    results.addAll result {
        message "outcomes ended in the last month with no results"
        severity Severity.WARNING
        records filtered
    }

filtered = enrolments.findAll { it.outcomes*.endDate.find { it >= today.minusDays(7) } }
if (filtered.size())
    results.addAll result {
        message "outcomes ended in the last week with no results"
        severity Severity.ADVICE
        records filtered
    }


return results
