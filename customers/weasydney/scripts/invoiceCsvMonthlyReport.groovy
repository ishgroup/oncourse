/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import org.apache.commons.lang3.time.DateUtils
def run(args) {
	def currentDate = new Date()
	def prevMonth = DateUtils.addMonths(currentDate, -1)
	def lastMonthFirst = DateUtils.truncate(prevMonth, Calendar.MONTH)
	def currentMonthFirst =  DateUtils.truncate(currentDate, Calendar.MONTH)
    def lastMonthLast = DateUtils.addDays(currentMonthFirst, -1)

    invoices = ObjectSelect.query(Invoice.class)
            .where(Invoice.DATE_DUE.between(lastMonthFirst, lastMonthLast))
            .orderBy(Invoice.DATE_DUE.asc())
            .select(args.context)

    fileName = currentMonthFirst.format("yyyyMMdd'.csv'")

    smtp {
        from preference.email.from
        subject fileName
        to "soma.gupta@weasydney.nsw.edu.au"
        cc "bruce@omniacorp.com.au"
        content "Finance csv export from ${lastMonthFirst} to ${currentMonthFirst}"
        attachment fileName,  "text/csv", export {
            template "wea.onCourse.invoiceLine.csv"
            records invoices
        }
    }
}
