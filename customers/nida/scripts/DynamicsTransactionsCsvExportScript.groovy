/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

def run(args) {

    EMAIL_ADDRESS = "howaida.azer@nida.edu.au"

    endDate = Calendar.getInstance().getTime()
    endDate.set(hourOfDay: 0, minute: 0, second: 0)
    startDate = endDate - 7

    fileName = startDate.format("yyyy-MM-dd'.csv'")

    accountTransactions = ObjectSelect.query(AccountTransaction.class)
            .where(AccountTransaction.TRANSACTION_DATE.between(startDate, endDate))
            .addOrderBy(AccountTransaction.TRANSACTION_DATE.asc())
            .select(args.context)

    smtp {
        from preference.email.from
        subject fileName
        to EMAIL_ADDRESS
        content "Transactions csv export from ${startDate} to ${endDate}"
        attachment fileName, "text/csv", export {
            template "dynamicsTransactions.csv"
            records accountTransactions
        }
    }
}
