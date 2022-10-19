records.each { CourseClass cc ->
	csv << [
			"Class name"      : cc.course.name,
			"Code"            : "${cc.course.code}-${cc.code}",
			"Start"           : cc.startDateTime?.format("yyyy-MM-dd"),
			"End"             : cc.endDateTime?.format("yyyy-MM-dd"),
			"Enrol maximum"   : cc.maximumPlaces,
			"Enrol budget"    : cc.budgetedPlaces,
			"Enrol actual"    : cc.validEnrolmentCount,
			"Enrol cancelled" : cc.cancelledEnrolmentCount,
			"Class fee"       : cc.feeIncGst?.toPlainString(),
			"Income maximum"  : cc.maximumTotalIncome?.toPlainString(),
			"Income budget"   : cc.budgetedTotalIncome?.toPlainString(),
			"Income actual"   : cc.actualTotalIncome?.toPlainString(),
			"Expenses maximum": cc.maximumTotalCost?.toPlainString(),
			"Expenses budget" : cc.budgetedTotalCost?.toPlainString(),
			"Expenses actual" : cc.actualTotalCost?.toPlainString(),
			"Profit maximum"  : cc.maximumTotalProfit?.toPlainString(),
			"Profit budget"   : cc.budgetedTotalProfit?.toPlainString(),
			"Profit actual"   : cc.actualTotalProfit?.toPlainString(),
			"Invoiced total"  : cc.totalInvoiced?.toPlainString(),
			"Credits total"   : cc.totalCredits?.toPlainString()
	]
}
