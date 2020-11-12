records.collectMany { Payslip p -> p.paylines }.each { pl ->
	csv << [
			"created on"       : pl.payslip.createdOn?.format("yyyy-MM-dd'T'HH:mm:ssXXX"),
			"modified on"      : pl.payslip.modifiedOn?.format("yyyy-MM-dd'T'HH:mm:ssXXX"),
			"status"           : pl.payslip.status.displayName,
			"budgeted quantity": pl.budgetedQuantity,
			"budgeted value"   : pl.budgetedValue?.toPlainString(),
			"date for"         : pl.dateFor?.format("yyyy-MM-dd"),
			"description"      : pl.description,
			"course-class code": "${pl?.classCost?.courseClass?.course?.code ?: ""}${pl.classCost ? "-" : ""}${pl?.classCost?.courseClass?.code ?: ""}",
			"quantity"         : pl.quantity,
			"taxValue"         : pl.taxValue?.toPlainString(),
			"value"            : pl.value?.toPlainString(),
			"payroll ref"      : pl.payslip?.contact?.tutor?.payrollRef,
			"tutor first name" : pl.payslip?.contact?.firstName,
			"tutor last name"  : pl.payslip?.contact?.lastName,
			"tutor email"      : pl.payslip?.contact?.email,
			"tutor street"     : pl.payslip?.contact?.street,
			"tutor state"      : pl.payslip?.contact?.state,
			"tutor postcode"   : pl.payslip?.contact?.postcode,
			"tutor suburb"     : pl.payslip?.contact?.suburb
	]
}

