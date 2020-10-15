/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

records.collectMany { Invoice i -> i.invoiceLines }.each { il ->
	csv << [
		"Invoice Number" : il.invoice.invoiceNumber,
		"Invoice Date" : il.invoice.invoiceDate?.format("yyyy-MM-dd"),
		"Full Name" : il.invoice.contact.fullName,
		"Account Code" : il.account.accountCode,
		"Amount" : il.priceTotalIncTax?.toPlainString(),
		"Tax Type" : il.tax.taxCode,
		"Invoice Due Date" : il.invoice.dateDue?.format("yyyy-MM-dd"),
		"CourseName" : il?.enrolment?.courseClass?.course?.name,
		"Start" : il?.enrolment?.courseClass?.startDateTime?.format("yyyy-MM-dd"),
		"End" : il?.enrolment?.courseClass?.endDateTime?.format("yyyy-MM-dd"),
		"Last payment dateBanked" : (!il.invoice.paymentInLines.isEmpty()) ?  il.invoice.paymentInLines.sort{it.createdOn}.last().paymentIn.dateBanked?.format("yyyy-MM-dd") : "No payments banked",
		"Last payment type" : (!il.invoice.paymentInLines.isEmpty()) ? "" + il.invoice.paymentInLines.sort{it.createdOn}.last().paymentIn.paymentMethod?.name : "No payments banked"
	]
}
