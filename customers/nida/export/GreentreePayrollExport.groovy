/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

records.collectMany { Payslip p -> p.paylines }.each { PayLine pl ->
    csv << [
            "Line Type"             : "GL",
            "Employee Code"         : pl.payslip.contact.tutor.payrollRef,
            "Transaction Type Code" : "ORD",
            "Line Date (yyyy-mm-dd)": pl.dateFor.format("yyyy-MM-dd"),
            "Hours/Qty"             : pl.quantity,
            "Rate"                  : pl.value.toPlainString() ,
            "Narration"             : pl.classCost?.courseClass?.uniqueCode ?: "",
            "Classification"        : "",
            "GL Account"            : getGLAccount(pl) ?:"",
            "GL Subcode"            : "",
            "Tree/Course Code"      : getCourseCode(pl),
            "Date To(yyyy-mm-dd)"   : ""
    ]
}

def getGLAccount(payLine) {
    def code = payLine.classCost?.courseClass?.course?.code
    def payrollRef = payLine.payslip.contact.tutor.payrollRef

    code?.startsWith("O") && payrollRef?.matches("^[a-zA-Z].*") ?
            "01.90.6000.000" :
            code?.startsWith("O") && payrollRef?.matches("^[0-9].*") ?
            "01.90.7000.000" :
            code?.startsWith("C") && payrollRef?.matches("^[a-zA-Z].*") ?
            "01.93.6100.000" :
            code?.startsWith("C") && payrollRef?.matches("^[0-9].*") ?
            "01.93.7300.000" :
            code?.startsWith("V") && payrollRef?.matches("^[a-zA-Z].*") ?
            "01.76.6010.000" :
            code?.startsWith("V") && payrollRef?.matches("^[0-9].*") ?
            "01.76.7000.000" :
            "01.90.6000.000"
}

def getCourseCode(payLine) {
    def accountCode = payLine.classCost?.courseClass?.incomeAccount?.accountCode
    if (accountCode == null) {
         return ""
    }
    return accountCode.tokenize('/')[1] ?: ""
}
