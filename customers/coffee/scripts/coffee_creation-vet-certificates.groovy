/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

def run(args) {

	// last midnight
	def periodEnd = new Date()
	periodEnd.set(hourOfDay: 0, minute: 0, second: 0)

	def context = args.context

	// find all enrolments for outcomes which were modified yesterday
	def outcome_enrolments = ObjectSelect.query(Enrolment)
			.where(Enrolment.COURSE_CLASS.dot(CourseClass.COURSE).dot(Course.IS_VET).eq(true))
			.and(Enrolment.OUTCOMES.dot(Outcome.MODIFIED_ON).between(periodEnd - 1, periodEnd))
			.select(context)

	// find all attendance which was modified yesterday
	def attendance = ObjectSelect.query(Attendance)
			.where(Attendance.MODIFIED_ON.between(periodEnd - 1, periodEnd))
			.select(context)

	// the following lines of code find all enrolments connected to the attendance which was touched
	def students = attendance*.student.flatten().unique()
	def attendance_enrolments = attendance*.session*.courseClass*.enrolments.flatten().unique().findAll { e -> students.contains(e.student)}

	def enrolments = (outcome_enrolments + attendance_enrolments).unique()

	// check all outcomes attached to the enrolment have been marked
	def legitimateEnrolments = enrolments.findAll { e -> EnrolmentStatus.STATUSES_LEGIT.contains(e.getStatus()) }
	def markedEnrolments = legitimateEnrolments.findAll { e -> e.outcomes.findAll { o -> OutcomeStatus.STATUS_NOT_SET.equals(o.status) }.empty }


	// check that attendance was marked and student attended 80% or more of the class
	def attendedEnrolments = markedEnrolments.findAll { e ->  e.attendancePercent >= 80 || e.courseClass.isDistantLearningCourse}

	attendedEnrolments.each { e ->

		// discard outcomes which are already linked to a certificate
		def unlinkedOutcomes = e.outcomes.findAll { o -> o.certificateOutcomes.empty }

		// count successful outcomes
		int successfulOutcomesCount = unlinkedOutcomes.findAll { o -> OutcomeStatus.STATUSES_VALID_FOR_CERTIFICATE.contains(o.status) }.size()

		if (successfulOutcomesCount > 0) {
			boolean fullQualification = e.courseClass.course.isSufficientForQualification && (successfulOutcomesCount == e.outcomes.size())

			context.newObject(Certificate).with { certificate ->
				certificate.setStudent(e.student)
				certificate.setQualification(e.courseClass.course.qualification)
				certificate.setAwardedOn(java.time.LocalDate.now())
				unlinkedOutcomes.each { o ->
					certificate.addToOutcomes(o)
				}

				if (fullQualification) {
					certificate.setIsQualification(true)
				} else {
					certificate.setIsQualification(false)
				}
			}
			context.commitChanges()

		}
	}
}
