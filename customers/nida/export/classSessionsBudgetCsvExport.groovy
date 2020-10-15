import groovy.time.TimeCategory




records.collectMany{ cc -> cc.sessions }.each { s ->
	csv << [
			"Class name"      : s.course.name,
			"Code"            : "${s.course.code}-${s.courseClass.code}",
			"Start"           : s.courseClass.startDateTime?.format("yyyy-MM-dd"),
			"End"             : s.courseClass.endDateTime?.format("yyyy-MM-dd"),
			"Enrol maximum"   : s.courseClass.maximumPlaces,
			"Enrol budget"    : s.courseClass.budgetedPlaces,
			"Enrol actual"    : s.courseClass.validEnrolmentCount,
			"Class fee"       : s.courseClass.feeIncGst?.toPlainString(),
			"Income maximum"  : s.courseClass.maximumTotalIncome?.toPlainString(),
			"Income budget"   : s.courseClass.budgetedTotalIncome?.toPlainString(),
			"Income actual"   : s.courseClass.actualTotalIncome?.toPlainString(),
			"Expenses maximum": s.courseClass.maximumTotalCost?.toPlainString(),
			"Expenses budget" : s.courseClass.budgetedTotalCost?.toPlainString(),
			"Expenses actual" : s.courseClass.actualTotalCost?.toPlainString(),
			"Profit maximum"  : s.courseClass.maximumTotalProfit?.toPlainString(),
			"Profit budget"   : s.courseClass.budgetedTotalProfit?.toPlainString(),
			"Profit actual"   : s.courseClass.actualTotalProfit?.toPlainString()
	]
}
