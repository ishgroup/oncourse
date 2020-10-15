/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

def run(args) {
    def invoices = ObjectSelect.query(Invoice).where(Invoice.AMOUNT_OWING.ne(Money.ZERO)).select(args.context)
    if (invoices.size() > 0) {
        def invoice_export = export {
            template "cce.debtorreport.csv"
            records invoices
        }
        email {
            from "info@sydney.edu.au"
            to "finance.directdeposit@sydney.edu.au"
            cc "natalia.borisova@sydney.edu.au", "debra.ashworth@sydney.edu.au", "vicki.holmes-newsome@sydney.edu.au", "corpfinance.cce@sydney.edu.au"
            subject "CCE Debtor Invoices"
            content "Please check the attached file for Debtor Report."
            attachment "Debtors Report - Invoices.csv", "text/csv", invoice_export
        }
    }
}
