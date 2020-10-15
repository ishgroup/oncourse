/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

records.collectMany { cc -> cc.enrolments }.collectMany { e -> e.outcomes }.each { o ->
	csv << [
			"studentNumber"         : o.enrolment.student.studentNumber,
			"USI"                   : o.enrolment.student.usi,
			"lastName"              : o.enrolment.student.contact.lastName,
			"firstName"             : o.enrolment.student.contact.firstName,
			"address"				: o.enrolment.student.contact.address,
			"postcode"				: o.enrolment.student.contact.postcode,
			"email"					: o.enrolment.student.contact.email,
			"QualNationalCode"      : o.enrolment.courseClass.course.qualification?.nationalCode,
			"UnitNationalCode"      : o.module?.nationalCode,
			"UnitTitle"             : o.module?.title,
			"OutcomeStatus"         : o.status?.displayName,
			"deliveryLocation"		: o.enrolment.courseClass?.displayableLocation,
			"aborigionality"		: o.enrolment.student.indigenousStatus?.displayName,
			"employmentStaus"		: o.enrolment.student.labourForceStatus?.displayName,
			"disabilityStatus"		: o.enrolment.student.disabilityType?.displayName,
			"VETFlag"               : o.enrolment.courseClass.course.isVET,
			"Cost"					: o.enrolment.originalInvoiceLine?.finalPriceToPayIncTax,
			"NationalFundingSource" : o.fundingSource,
			"CommitmentId"          : o.vetPurchasingContractID,
			"StateFundingSource"    : o.vetFundingSourceStateID,
			"StudyReason"			: o.enrolment.studyReason?.displayName,
			"ConcessionType"		: o.enrolment.vetFeeExemptionType
	]
}
