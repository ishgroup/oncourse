/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

records.collectMany { Invoice i -> i.invoiceLines }.each { il ->

	def courseType = "Other"

	switch( il.enrolment?.courseClass?.course?.code) {
		case ~/^HR.*/:
		  courseType = "HR"
		  break
		case ~/.*^WB.*/:
		  courseType = "PTS Admin"
		  break
		case ~/.*^AD.*/:
		  courseType = "PTS Admin"
		  break
		case ~/.*^PTS.*/:
		  courseType = "PTS Billable"
		  break
		case ~/.*^EL.*/:
		  courseType = "PTS Billable"
		  break
	}

	def internalFlag = "unknown"

	if (il.enrolment?.student?.contact?.email != null) {
		internalFlag = il.enrolment.student.contact.email.endsWith("westernpower.com.au") ? "Internal" : "External"
	}
	def localStartDate = il.enrolment?.courseClass?.startDateTime ? new java.sql.Date(il.enrolment?.courseClass?.startDateTime.getTime()).toLocalDate() : null

	csv << [
		"Invoice Date": il.invoice.invoiceDate?.format("dd/MM/yy"),
		"FT Type": "", "Contractor": il.invoice.contact.fullName,
		"Customer Code": il.invoice.contact.customField("Customer Code") ?: "",
		"Student Name": il.enrolment?.student?.contact?.fullName,
		"OnCourse Invoice Number": il.invoice.invoiceNumber,
		"Class Code": il.enrolment?.courseClass?.course?.code+"-"+il.enrolment?.courseClass?.code,
		"Class Start Date": il.enrolment?.courseClass?.startDateTime?.format("dd/MM/yy"),
		"Purchase Order": il.invoice.customerReference ?: "", "No Charge": "",
		"Fee ex GST": il.priceTotalExTax,
		"Payroll Number": il.enrolment?.student?.contact?.customField("Employee ID") ?: "",
		"Course Type": courseType,
		"Class end date": il.enrolment?.courseClass?.endDateTime?.format("dd/MM/yy"),
		"Course Code": il.enrolment?.courseClass?.course?.code,
		"Course Name": il.enrolment?.courseClass?.course?.name,
		"Duration": il.enrolment?.courseClass?.sessions?.collect { it.durationInHours }?.sum() ?: "",
		"Tutors": il.enrolment?.courseClass?.tutorRoles?.collect { tr -> tr.tutor.fullName }?.join(", "),
		"Location": il.enrolment?.courseClass?.room?.site?.name,
		"Tax Type": il.tax.taxCode,
		"Internal": internalFlag,
		"Reporting Month": [ il.invoice.invoiceDate, localStartDate ].max().format("MMM yy"),
		"Description": il.description,
		"Title": il.title,
		"Description required for billing": il.invoice.invoiceNumber+" "+il?.enrolment?.student?.contact?.fullName+" "+il.description,
		"Tax": il.taxEach,
		"Attendance": "",
		"Ellipse Invoice/Journal": "",
		"Date": "",
	]
}
