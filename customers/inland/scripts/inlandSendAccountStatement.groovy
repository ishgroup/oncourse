def run(args) {

    def context = args.context

    def today = new Date()
    today.set(hourOfDay: 0, minute: 0, second: 0)

    def invoices = ObjectSelect.query(Invoice)
            .where(Invoice.AMOUNT_OWING.gt(Money.ZERO))
            .and(Invoice.DATE_DUE.lte(today-1))
            .select(context)

    // from outstanding invoices -> get unique contacts
    def contacts = invoices*.contact.unique()

    contacts.findAll { contact -> contact.totalOwing > 0 }.each { c ->
        
        def contactInvoice = c.invoices.findAll { invoice -> invoice.overdue > Money.ZERO  }
        
        if(c.email) {
            email {
                from preference.email.from
                to c.email
                subject "Northern Inland Community College Account Statement"
                content "Attached is your statement for last month, please contact Andrea McGregor on 0267821662 if you have any queries."
                attachment "Statement_Report.pdf", "application/pdf", report {
                    keycode "inland.onCourse.statementReport"
                    records contactInvoice
                }
            }
        }
    }
}