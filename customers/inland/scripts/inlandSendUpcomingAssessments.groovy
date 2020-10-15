def run(args) {
	
	recipients = [ "admin@communitycollegeni.nsw.edu.au", 
					"eo@communitycollegeni.nsw.edu.au", 
					"barraba@communitycollegeni.nsw.edu.au", 
					"brooke@communitycollegeni.nsw.edu.au", 
					"penny@communitycollegeni.nsw.edu.au" ]

	def today = new Date()
	today.set(hourOfDay: 0, minute: 0, second: 0)

	def context = args.context

	def assessmentsReleasedToday = ObjectSelect.query(AssessmentClass)
							.where(AssessmentClass.RELEASE_DATE.between(today, today+14))
							.and(AssessmentClass.ASSESSMENT.dot(Assessment.ACTIVE).eq(true))
							.and(AssessmentClass.COURSE_CLASS.dot(CourseClass.IS_CANCELLED).eq(false))
							.and(AssessmentClass.COURSE_CLASS.dot(CourseClass.IS_ACTIVE).eq(true))
							.select(context)


	if(assessmentsReleasedToday.size() > 0) {
		def assessment_export = export {
			template "inland.upcomingAssessments.csv"
			records assessmentsReleasedToday
		}

		recipients.each { r ->
			email {
				to r
				from "theinfo@communitycollegeni.nsw.edu.au"
				subject "Assessments to be released in the next 14 days"
				content "Attached is an export of assessments released in the next 14 days."
				attachment "Norther_inlands_assessments_to_be_released.csv", "text/csv", assessment_export

			}
		}
	}
}