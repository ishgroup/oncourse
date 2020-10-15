/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

records.each { cc ->

    Integer sum = cc.sessions.sum { session -> DurationFormatter.parseDurationInMinutes(session.startDatetime, session.endDatetime) }

    cc.sessions.sort { s -> s.startDatetime }.each { s ->

        Integer minutes = DurationFormatter.parseDurationInMinutes(s.startDatetime, s.endDatetime)

        csv << [
                "Class name"            : s.courseClass.course.name,
                "Code"                  : s.courseClass.uniqueCode,
                "Start Date"            : s.startDatetime?.format('d/M/y'),
                "Start Time"            : s.startDatetime?.format('hh:mm a'),
                "End Date"              : s.endDatetime?.format('d/M/y'),
                "End Time"              : s.endDatetime?.format('hh:mm a'),
                "Duration"              : "${(int) (minutes / 60) }h ${minutes % 60}min",
                "Enrol maximum"         : s.courseClass.maximumPlaces,
                "Enrol budget"          : s.courseClass.budgetedPlaces,
                "Enrol actual"          : s.courseClass.validEnrolmentCount,
                "Class fee"             : s.courseClass.feeIncGst?.toPlainString(),
                "Income maximum"        : s.courseClass.maximumTotalIncome?.toPlainString(),
                "Income budget"         : s.courseClass.budgetedTotalIncome?.toPlainString(),
                "Income actual"         : s.courseClass.actualTotalIncome?.toPlainString(),
                "Session Income actual" : s.courseClass.actualTotalIncome?.multiply(minutes / sum)?.toPlainString(),
                "Expenses maximum"      : s.courseClass.maximumTotalCost?.toPlainString(),
                "Expenses budget"       : s.courseClass.budgetedTotalCost?.toPlainString(),
                "Expenses actual"       : s.courseClass.actualTotalCost?.toPlainString(),
                "Session Exepnses actual" : s.courseClass.actualTotalCost?.multiply(minutes / sum)?.toPlainString(),
                "Profit maximum"        : s.courseClass.maximumTotalProfit?.toPlainString(),
                "Profit budget"         : s.courseClass.budgetedTotalProfit?.toPlainString(),
                "Profit actual"         : s.courseClass.actualTotalProfit?.toPlainString()
        ]
    }
}
