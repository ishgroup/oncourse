def grandTotalUnit = 0
def grandTotalValue = Money.ZERO

records.collectMany { Payslip p -> p.paylines }
        .groupBy { PayLine pl -> pl.payslip.contact }
        .sort { a, b -> a.key.lastName <=> b.key.lastName ?: a.key.firstName <=> b.key.firstName }
        .each { Contact c, List<PayLine> payLines ->

    def totalUnit = 0
    def tolalValue = Money.ZERO

    payLines.sort { a, b ->
        a1 = a?.classCost?.courseClass?.incomeAccount?.accountCode
        b1 = b?.classCost?.courseClass?.incomeAccount?.accountCode

        (a1 ? a1.substring(Math.max(0, a1.length() - 3)) : "") <=> (b1 ? b1.substring(Math.max(0, b1.length() - 3)) : "") ?: a.dateFor <=> b.dateFor 
    }.each { PayLine pl ->

        def code = pl?.classCost?.courseClass?.incomeAccount?.accountCode

        totalUnit+= pl.quantity
        tolalValue= tolalValue.add(pl.value?.multiply(pl.quantity))

        csv << [
                "Teacher"           : c?.name,
                "Emp.Card ID"       : c?.tutor?.payrollRef,
                "course-class code" : pl?.classCost?.courseClass?.uniqueCode,
                "Cost Centre"       : code ? code.substring(Math.max(0, code.length() - 3)) : "",
                "Date"              : pl.dateFor?.format("dd/MM/yy"),
                "Activity ID"       : "teaching",
                "Units/Hrs"         : pl.quantity,
                "description"       : pl.description,
                "taxValue"          : pl.taxValue?.toString(),
                "value \$"          : pl.value?.toString(),
                "Total"             : pl.value?.multiply(pl.quantity)?.toString()

        ]
    }

    grandTotalUnit+=totalUnit
    grandTotalValue= grandTotalValue.add(tolalValue)

    csv << [
            "Teacher"           : "${c?.getName(true)} total",
            "Emp.Card ID"       : "",
            "course-class code" : "",
            "Cost Centre"       : "",
            "Date"              : "",
            "Activity ID"       : "",
            "Units/Hrs"         : totalUnit,
            "description"       : "",
            "taxValue"          : "",
            "value \$"          : "",
            "Total"             : tolalValue.toString()
    ]
}

csv << [
        "Teacher"           : "Grand total",
        "Emp.Card ID"       : "",
        "course-class code" : "",
        "Cost Centre"       : "",
        "Date"              : "",
        "Activity ID"       : "",
        "Units/Hrs"         : grandTotalUnit,
        "description"       : "",
        "taxValue"          : "",
        "value \$"          : "",
        "Total"             : grandTotalValue.toString()
]
