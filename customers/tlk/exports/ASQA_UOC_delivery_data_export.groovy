use (groovy.time.TimeCategory) {
	def now = LocalDate.now()
	def reportStart = now - 365

	records.each { Module m ->
		def recentOutcomes = ObjectSelect.query(Outcome.class)
													.where(Outcome.MODULE.eq(m))
													.and(Outcome.START_DATE.gt(reportStart))
													.select(context)

		def recentEnrolmentCount = ObjectSelect.query(Enrolment.class)
													.where(Enrolment.OUTCOMES.dot(Outcome.MODULE).eq(m))
													.and(Enrolment.COURSE_CLASS.dot(CourseClass.END_DATE_TIME).gt(now))
													.selectCount(context)

		def recentCertificateCount = ObjectSelect.query(Certificate.class)
														.where(Certificate.IS_QUALIFICATION.isFalse())
														.and(Certificate.REVOKED_ON.isNull())
														.and(Certificate.CREATED_ON.gt(reportStart))
														.and(Certificate.CERTIFICATE_OUTCOMES.dot(CertificateOutcome.OUTCOME.dot(Outcome.MODULE)).eq(m))
														.selectCount(context)

		def recentEnrolments = recentOutcomes*.enrolment.flatten()
		def recentClasses = recentEnrolments*.courseClass.flatten()

		csv << [
				"Code"                        						: m.nationalCode,
				"Title of unit of competency"             : m.title,			
				"Number of Current Enrolments"            : recentEnrolmentCount,
				"Number statements of attainment issued in last 12 months"   : recentCertificateCount,
				"Principal Funding Source"                : recentEnrolments*.fundingSource*.displayName?.flatten()?.countBy{it}?.max{ it.value }?.key ?: "No data in last 12 months",			
				"Delivery Venues"					                : recentClasses*.room*.site.flatten().findAll{it}.collect{ it.suburb + "-" + it.state }.unique().join("; ") ?: "No data in last 12 months",
				"Principal Delivery Mode"                 : recentClasses*.deliveryMode?.countBy{it}?.max{ it.value }?.key?.displayName ?: "No data in last 12 months",
				"Partnership / Subcontract Arrangements"  : "",
				"Principal Client Cohort"                 : "",
				"Comments"                                : ""
		]
	}
}