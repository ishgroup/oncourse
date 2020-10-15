def run(args) {
    Calendar calendar = Calendar.getInstance()
    calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)

    endDate = calendar.getTime()
    endDate.set(hourOfDay: 0, minute: 0, second: 0)

    startDate = endDate - 7

    invoices = ObjectSelect.query(Invoice.class)
            .where(Invoice.DATE_DUE.between(startDate, endDate))
            .addOrderBy(Invoice.DATE_DUE.asc())
            .select(args.context)

    fileName = endDate.format("yyyy-MM-dd'.csv'")

    smtp {
        from preference.email.from
        subject fileName
        to "janice.jenkins@westernpower.com.au"
        content "Finance csv export from ${startDate} to ${endDate}"
        attachment fileName,  "text/csv", export {
            template "ish.onCourse.invoice.finance.csv"
            records invoices
        }
    }
}
