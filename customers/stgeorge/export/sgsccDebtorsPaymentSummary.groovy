/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.math.BigDecimal
import ish.math.Money

LocalDate today = LocalDate.now()

def moneyPercentage(BigDecimal base, BigDecimal pct) {
	return pct.divide(base,2, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100))
}
def dateDiff(LocalDate start, LocalDate end) {
	return ChronoUnit.DAYS.between(start, end)
}

records.each { Invoice i ->
	csv << [
			"Student Name"				: i.contact.fullName,
			"Invoice Number"			: i.invoiceNumber,
			"Total"						: "\$" + i.total.toPlainString(),
			"Owing"						: "\$" + i.amountOwing.toPlainString(),
			"Last Payment In Date"		: (!i.paymentInLines.isEmpty()) ? i.paymentInLines.sort{it.createdOn}.last().createdOn.format("dd/MM/YYYY") : "No payments",
			"Last Payment In Amount"	: (!i.paymentInLines.isEmpty()) ? "\$" + i.paymentInLines.sort{it.createdOn}.last().amount.toPlainString() : "No payments",
			"Payment Due Date"			: i.dateDue,
			"Days Overdue"				: i.overdue > Money.ZERO ? dateDiff(i.dateDue, today) : "0" ,
			"Overdue Amount"			: "\$" + i.overdue.toPlainString(),
			"Percent Overdue of Owing"	: i.overdue > Money.ZERO ? moneyPercentage(i.amountOwing.toBigDecimal(), i.overdue.toBigDecimal()) + "%" : "0%"
	]
}
