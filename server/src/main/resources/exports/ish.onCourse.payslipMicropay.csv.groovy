import ish.common.types.ClassCostRepetitionType

/**
 * Micropay's MIF format is used to send data from onCourse paylines into
 * Micropay for payment. We first send a header row, then all the paylines
 * grouped by employee.
 *
 * Paylines can be either type U (unit) for a fixed amount, or type R (rate)
 * for an hourly rate.
 */

csv.delimiter = '\\'
def now = new Date()

// output nothing to skip the header generation
csv.writeHeader = false

// file header
csv << [
    "version" : "MIFV02.00",
    "software" : "ONCOURSE",
    "source date" : now.format("ddMMyyyy"),
    "end date" : null,
    "create date" : now.format("ddMMyyyy"),
    "create time" : now.format("HHmmss"),
    "hours format" : "M"
]

records.collectMany { it.paylines }.groupBy { it.payslip?.contact?.tutor?.payrollRef }.each {
    payrollRef, lines ->

    // employee record
    csv << [
            "employee code"  : payrollRef,
            "location"       : null,
            "pay point"      : null,
            "autopay flag"   : null,
            "pay periods"    : null,
            "RDO flag"       : null,
            "recommence date": null
    ]

    lines.each { pl ->

        if (pl.classCost?.repetitionType == ClassCostRepetitionType.PER_STUDENT_CONTACT_HOUR ||
                pl.classCost?.repetitionType == ClassCostRepetitionType.PER_TIMETABLED_HOUR) {

            csv << [
                ""      : null,
                "id"    : "TN",
                "type"  : "R",
                "code"  : "ONCOURSE",
                "rate"  : pl.value?.toPlainString(),
                "unit rate" : null,
                "percent"   : null,
                "unit"  : null,
                "minutes" : pl.quantity * 60,
                "blank" : null,
                "value" : null,
                "account" : "WAGES",
                "effective date": pl.dateFor?.format("ddMMyyyy"),
                "job code": pl.classCost?.courseClass?.incomeAccount?.accountCode,
                "end date": null
            ]

        } else {

            csv << [
                ""      : null,
                "id"    : "TN",
                "type"  : "U",
                "code"  : "ONCOURSE2",
                "rate"  : null,
                "unit rate" : pl.value?.toPlainString(),
                "percent"   : null,
                "unit"  : pl.quantity,
                "minutes" : null,
                "blank" : null,
                "value" : null,
                "account" : "WAGES",
                "effective date": pl.dateFor?.format("ddMMyyyy"),
                "job code": pl.classCost?.courseClass?.incomeAccount?.accountCode,
                "end date": null
            ]
        }
    }
}

