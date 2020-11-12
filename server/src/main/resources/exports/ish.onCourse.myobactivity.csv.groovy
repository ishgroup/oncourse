import ish.oncourse.server.cayenne.Payslip

records.collectMany { Payslip p -> p.paylines }.each { pl ->
	csv << [
			"Emp. Co./Last Name": pl.payslip?.contact?.lastName,
			"Emp. First Name"   : pl.payslip?.contact?.firstName,
			"Date"              : pl.dateFor?.format("yyyy-MM-dd"),
			"Activity ID"       : "teaching",
			"Units"             : pl.quantity,

			"description"       : pl.description,
			"course-class code" : "${pl?.classCost?.courseClass?.course?.code ?: ""}${pl.classCost ? "-" : ""}${pl?.classCost?.courseClass?.code ?: ""}",

			"taxValue"          : pl.taxValue?.toPlainString(),
			"value"             : pl.value?.toPlainString(),

			"Emp.Card ID"       : pl.payslip?.contact?.tutor?.payrollRef,

	]
}

