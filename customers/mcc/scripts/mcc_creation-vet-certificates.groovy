def lastWeekStart = new Date() - 8
lastWeekStart.set(hourOfDay: 0, minute: 0, second: 0)

def yesterdayEnd = new Date()
yesterdayEnd.set(hourOfDay: 0, minute: 0, second: 0)

def badOutcomes = OutcomeStatus.STATUSES_VALID_FOR_CERTIFICATE + [
		OutcomeStatus.STATUS_ASSESSABLE_DET_DID_NOT_START,
		OutcomeStatus.STATUS_NON_ASSESSABLE_COMPLETED,
		OutcomeStatus.STATUS_NON_ASSESSABLE_NOT_COMPLETED,
		OutcomeStatus.STATUS_NOT_SET
]

def enrolments = ObjectSelect.query(Enrolment)
		.where(Enrolment.COURSE_CLASS.dot(CourseClass.COURSE).dot(Course.IS_VET).eq(true))
		.and(Enrolment.STATUS.eq(EnrolmentStatus.SUCCESS))
		.and(Enrolment.OUTCOMES.dot(Outcome.MODIFIED_ON).between(lastWeekStart, yesterdayEnd))
		.select(context);

// no outcomes have empty or bad status
enrolments = enrolments.findAll { e ->  e.outcomes.findAll { o -> o.status in badOutcomes }.empty }

// at least one outcome not linked to a certificate
enrolments = enrolments.findAll { e ->  e.outcomes.any { it.certificateOutcomes.empty } }

enrolments.each { e ->

	def goodOutcomes =  e.outcomes.findAll { o -> !(o.status in badOutcomes)}

	int successfulOutcomesCount = goodOutcomes.findAll { it.status in OutcomeStatus.STATUSES_VALID_FOR_CERTIFICATE }.size()

	if (successfulOutcomesCount > 0) {
		context.newObject(Certificate).with { certificate ->
			certificate.setStudent(e.student)
			certificate.setQualification(e.courseClass.course.qualification)
			certificate.setAwardedOn(new Date())
			goodOutcomes.each { o ->
				certificate.addToOutcomes(o)
			}
			certificate.setIsQualification(e.courseClass.course.isSufficientForQualification && (successfulOutcomesCount == e.outcomes.size()))
		}
		context.commitChanges()
	}
}
