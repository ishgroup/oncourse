def byName = { u -> u.discountNames }
records.each { CourseClass cc ->

	def totalInvoiceLinesFee = cc.discountedInvoiceLinesForEnrolments.collect{it.finalPriceToPayExTax}.sum() ?: BigDecimal.ZERO
	def totalInvoiceLinesDiscount = cc.discountedInvoiceLinesForEnrolments.collect{it.discountEachExTax}.sum() ?: BigDecimal.ZERO
	def fullFeeSum = cc.fullFeeEnrolmentsFeesSum ?: BigDecimal.ZERO
	def totalFee = totalInvoiceLinesFee.add(fullFeeSum.add(cc.manuallyDiscountedEnrolmentsFeesSum.toBigDecimal() ?: BigDecimal.ZERO).toBigDecimal())
	def totalDiscount = totalInvoiceLinesDiscount.add(cc.manuallyDiscountedEnrolmentsDiscountSum.toBigDecimal() ?: BigDecimal.ZERO)

	csv << [
			"Class"                    : cc.uniqueCode+" " +cc.course.name,
			"Starts"                   : cc.startDateTime?.format("d MMM yyyy h:mma", cc.timeZone),
			"Site"                     : cc.room?.site?.name,
			"Full fee (ex GST)"        : cc.feeExGst,
			"Discount name"            : "Full Fee",
			"Enrolments count"         : cc.fullFeeEnrolments.size(),
			"Fees collected"           : cc.fullFeeEnrolmentsFeesSum,
			"Discount value"           : Money.of("0.0"),
			"Total fees collected"     : totalFee,
			"Total discount value"     : totalDiscount
	]

	csv << [
			"Class"                    : cc.uniqueCode+" " +cc.course.name,
			"Starts"                   : cc.startDateTime?.format("d MMM yyyy h:mma", cc.timeZone),
			"Site"                     : cc.room?.site?.name,
			"Full fee (ex GST)"        : cc.feeExGst,
			"Discount name"            : "Manual discount",
			"Enrolments count"         : cc.manuallyDiscountedEnrolments.size(),
			"Fees collected"           : cc.manuallyDiscountedEnrolmentsFeesSum,
			"Discount value"           : cc.manuallyDiscountedEnrolmentsDiscountSum,
			"Total fees collected"     : totalFee,
			"Total discount value"     : totalDiscount
	]

	cc.discountedInvoiceLinesForEnrolments.groupBy(byName).each{discountNames, invoiceLines ->
		csv << [
				"Class"               : cc.uniqueCode + " " + cc.course.name,
				"Starts"              : cc.startDateTime?.format("d MMM yyyy h:mma", cc.timeZone),
				"Site"                : cc.room?.site?.name,
				"Full fee (ex GST)"   : cc.feeExGst,
				"Discount name"       : discountNames == null ? "Manual discount" : discountNames,
				"Enrolments count"    : invoiceLines.size(),
				"Fees collected"      : invoiceLines.collect { it.finalPriceToPayExTax }.sum(),
				"Discount value"      : invoiceLines.collect { it.discountEachExTax }.sum(),
				"Total fees collected": totalFee,
				"Total discount value": totalDiscount
		]
	}
}