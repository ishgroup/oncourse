/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

records.each { c ->
	c.outcomes.each { o ->
		 csv << [
			"RTO registration number" 	: preference.avetmiss.identifier,
			"Student's full name"		: o.enrolment.student.contact.fullName,
			"Student's email address"	: o.enrolment.student.contact.email,
			"Unique student Identifier"	: o.enrolment.student?.usi,
			"Date birth"				: o.enrolment.student.contact.dateOfBirth?.format("dd MM YYYY"),
			"Student ID number" 		: o.enrolment.student?.studentNumber,
			"Activity start date"		: o?.startDate?.format("dd MM YYYY"),
			"Activity end date"			: o?.endDate?.format("dd MM YYYY"),
			"Qualification code"		: c?.qualification?.nationalCode,
			"Qualification title"		: c?.qualification?.title,
			"Qualification date completed" : c?.lastOutcome?.endDate?.format("ddMMYYYY"),
			"Units of competencies code": o.code,
			"Units of competency title"	: o.name,
			"Unit of competency outcome": o.status?.displayName,
			"Outcome identifier — national" : o?.specificProgramIdentifier,
			"VET In Schools"			: o.enrolment.vetInSchools,
			"Funding source"			: o.enrolment?.relatedFundingSource?.name ?: "Fee for service",
			"Administering state/territory" : "NSW",
			"issuance date"				: c?.printedOn?.format("dd MM YYYY"),
 		]
	}
}
