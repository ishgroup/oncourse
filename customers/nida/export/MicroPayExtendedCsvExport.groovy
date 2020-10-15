/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

def exportMap = [
        '01.24.2802.000/NZ1CB0P01' : 'CP PUBLIC COURSE',
        '01.24.2801.000/NV1CB0P01' : 'CP PUBLIC COURSE',
        '01.24.2800.000/NN1CB0P01' : 'CP PUBLIC COURSE',
        '01.24.2812.000/NZ1CB0E01' : 'CP STAFF ENGAGEMENT',
        '01.24.2811.000/NV1CB0E01' : 'CP STAFF ENGAGEMENT',
        '01.24.2810.000/NN1CB0E01' : 'CP STAFF ENGAGEMENT',
        '01.24.2809.000/NZ1CB0K01' : 'CP 1:1',
        '01.24.2806.000/NV1CB0K01' : 'CP 1:1',
        '01.24.2805.000/NN1CB0K01' : 'CP 1:1',
        '01.24.2808.000/NZ1CB0C01' : 'CUSTOMISED COURSE',
        '01.24.2807.000/NO1CB0C01' : 'CUSTOMISED COURSE',
        '01.24.2804.000/NV1CB0C01' : 'CUSTOMISED COURSE',
        '01.24.2803.000/NN1CB0C01' : 'CUSTOMISED COURSE',
        '01.25.2984.000/NZ1EB0P01' : '18+ GENERALIST COURSES',
        '01.25.2984.000/NV1EB0P01' : '18+ GENERALIST COURSES',
        '01.25.2984.000/NQ1EB0P01' : '18+ GENERALIST COURSES',
        '01.25.2984.000/NN1EB0P01' : '18+ GENERALIST COURSES',
        '01.25.2988.000/NZ1IB0P01' : 'EDUCATOR COURSES',
        '01.25.2988.000/NV1IB0P01' : 'EDUCATOR COURSES',
        '01.25.2988.000/NQ1IB0P01' : 'EDUCATOR COURSES',
        '01.25.2988.000/NN1IB0P01' : 'EDUCATOR COURSES',
        '01.25.2983.000/NZ1JB0P01' : 'GRADE 11-12 COURSES',
        '01.25.2983.000/NV1JB0P01' : 'GRADE 11-12 COURSES',
        '01.25.2983.000/NQ1JB0P01' : 'GRADE 11-12 COURSES',
        '01.25.2983.000/NN1JB0P01' : 'GRADE 11-12 COURSES',
        '01.25.2982.000/NZ1DB0P01' : 'GRADE 7-10 COURSES',
        '01.25.2982.000/NV1DB0P01' : 'GRADE 7-10 COURSES',
        '01.25.2982.000/NQ1DB0P01' : 'GRADE 7-10 COURSES',
        '01.25.2982.000/NN1DB0P01' : 'GRADE 7-10 COURSES',
        '01.25.2981.000/NZ1BB0P01' : 'GRADE K-6 COURSES',
        '01.25.2981.000/NV1BB0P01' : 'GRADE K-6 COURSES',
        '01.25.2981.000/NQ1BB0P01' : 'GRADE K-6 COURSES',
        '01.25.2981.000/NN1BB0P01' : 'GRADE K-6 COURSES',
        '01.25.2980.000/NZ1AB0P01' : 'PRESCHOOL COURSES',
        '01.25.2980.000/NV1AB0P01' : 'PRESCHOOL COURSES',
        '01.25.2980.000/NQ1AB0P01' : 'PRESCHOOL COURSES',
        '01.25.2980.000/NN1AB0P01' : 'PRESCHOOL COURSES',
        '01.25.2986.000/NZ1GB0P01' : 'PRIMARY STUDENT',
        '01.25.2986.000/NV1GB0P01' : 'PRIMARY STUDENT',
        '01.25.2986.000/NQ1GB0P01' : 'PRIMARY STUDENT',
        '01.25.2986.000/NN1GB0P01' : 'PRIMARY STUDENT',
        '01.25.2987.000/NZ1HB0P01' : 'SECONDARY STUDENT',
        '01.25.2987.000/NV1HB0P01' : 'SECONDARY STUDENT',
        '01.25.2987.000/NQ1HB0P01' : 'SECONDARY STUDENT',
        '01.25.2987.000/NN1HB0P01' : 'SECONDARY STUDENT',
        '01.25.2985.000/NN1FB0P01' : '15+ PROF STUDIOS & RESIDENCES',
        '01.25.2985.000/NQ1FB0P01' : '15+ PROF STUDIOS & RESIDENCES',
        '01.25.2985.000/NV1FB0P01' : '15+ PROF STUDIOS & RESIDENCES',
        '01.25.2985.000/NZ1FB0P01' : '15+ PROF STUDIOS & RESIDENCES',
        '01.26.2610' : 'VET',
        '01.26.2620' : 'VET',
        '01.26.2630' : 'VET',
        '01.26.2640' : 'VET',
        '01.26.2650' : 'VET'
]

records.collectMany { Payslip p -> p.paylines }.each { PayLine pl ->
    csv << [
            "Employee No"  : pl.payslip.contact.tutor.payrollRef,
            "Cost Account" : getCostAccount(pl),
            "Total"        : pl.value.multiply(pl.quantity).toPlainString(),
            "MicrOpay Code": "ISH01",
            "Portfolio"    : exportMap[pl.classCost?.courseClass?.incomeAccount?.accountCode]?.take(30),
            "CourseClass"  : pl.classCost?.courseClass?.uniqueCode ?: "",
            "TutorName"    : pl.payslip.contact.name,
            "SessionDate"  : pl.dateFor.format("MM/dd/yyyy")
    ]
}

def getCostAccount(payLine) {
    def code = payLine.classCost?.courseClass?.course?.code
    def payrollRef = payLine.payslip.contact.tutor.payrollRef
    def state = payLine.classCost?.courseClass?.room?.site?.state

    code?.startsWith("O") && payrollRef?.matches("^[a-zA-Z0-9].*") ?
            "C-083-CASAL - ${state}" :
            code?.startsWith("C") && payrollRef?.matches("^[a-zA-Z0-9].*") ?
                    "C-084-CASAL - ${state}" :
                    code?.startsWith("V") && payrollRef?.matches("^[a-zA-Z].*") ?
                            "C-045-AUDIT - ${state}" :
                            code?.startsWith("V") && payrollRef?.matches("^[0-9].*") ?
                                    "C-045-CERT3 - ${state}" :
                                    "INVALID"
}
