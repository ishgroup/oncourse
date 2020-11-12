records.collectMany { cc -> cc.enrolments }.collectMany { e -> e.outcomes }.each { o ->
	csv << [
			"studentNumber"         : o.enrolment.student.studentNumber,
			"USI"                   : o.enrolment.student.usi,
			"firstName"             : o.enrolment.student.contact.firstName,
			"lastName"              : o.enrolment.student.contact.lastName,
			"classCode"             : o.enrolment.courseClass.uniqueCode,
			"CourseName"            : o.enrolment.courseClass.course.name,
			"Subject"               : o.enrolment.courseClass.course.tags?.collect { t -> t.name }?.join(" "),
			"QualNationalCode"      : o.enrolment.courseClass.course.qualification?.nationalCode,
			"QualName"              : [o.enrolment.courseClass.course.qualification?.level, o.enrolment.courseClass.course.qualification?.title].findAll().join(" "),
			"VETFlag"               : o.enrolment.courseClass.course.isVET,
			"UnitNationalCode"      : o.module?.nationalCode,
			"UnitTitle"             : o.module?.title,
			"OutcomeStartDate"      : o.startDate?.format("d/M/Y"),
			"OutcomeEndDate"        : o.endDate?.format("d/M/Y"),
			"OutcomeStatus"         : o.status?.displayName,
			"DeliveryMode"          : o.deliveryMode ?: "Not Set",
			"NationalFundingSource" : o.fundingSource,
			"StateFundingSource"    : o.vetFundingSourceStateID,
			"CommitmentId"          : o.vetPurchasingContractID,
			"ConcessionType"		: o.enrolment.vetFeeExemptionType,
			"OutcomeMarkedOnlineBy"	: o.markedByTutor?.contact?.fullName ?: "Office",
			"OnlineMarkingDate"		: o.markedByTutorDate?.format("d/M/Y"),
			"fundingSource"         : o.enrolment.relatedFundingSource?.name ?: "Fee for service (non-funded)"
	]
}
