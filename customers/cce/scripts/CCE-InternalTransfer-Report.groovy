/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import groovy.time.TimeCategory
def run(args) {

    use (TimeCategory) {
        paymentMethod = "Journal transfer"

        end = new Date().copyWith(dayOfMonth: 1) - 1.day
        end.set(hourOfDay: 23, minute: 59, second: 59)
        start = new Date().copyWith(dayOfMonth: 1) - 1.month
        start.set(hourOfDay: 0, minute: 0, second: 0)
        payments = ObjectSelect.query(PaymentIn)
                .where(PaymentIn.CREATED_ON.between(start, end))
                .and(PaymentIn.PAYMENT_METHOD.dot(PaymentMethod.NAME).eq(paymentMethod))
                .select(args.context)

        if (payments) {
            journal_export = export {
                template "cce.paymentIn.journals.csv"
                records payments
            }

            email {
                from "operations@cce.sydney.edu.au"
                to "corpfinance.cce@sydney.edu.au"
                cc "systems@cce.sydney.edu.au"
                subject "CCE Journal transfers: please action"
                content
"Please find attached the journal transfers required from University \
departments into the CCE account. You should make the following journals \
in PeopleSoft for each row in the attachment. Then reply to the email \
to let CCE know that this has been done."
                attachment "CCE Journal Transfer.csv", "text/csv", journal_export
            }
        }
    }



}
