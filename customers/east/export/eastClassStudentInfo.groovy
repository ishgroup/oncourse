/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

records.each { cc ->
	cc.successAndQueuedEnrolments.each { e ->
		csv << [
			"Student Number"		: e.student.studentNumber,
			"Student Name"			: e.student.contact.fullName,
			"Course-class Code"		: e.courseClass. uniqueCode,
			"Course Name"			: e.courseClass.course.name,
			"Funding Contact"		: e?.relatedFundingSource?.name ?: "Fee for service (non-funded)",
			"Units of Competency"	: e.courseModules.size(),
			"Fee Paid"				: e.originalInvoiceLine.finalPriceToPayIncTax.toPlainString()
		]
	}
}
