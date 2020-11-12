/*
 * Copyright ish group pty ltd 2020.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.accounting.builder

import ish.math.Money
import ish.oncourse.server.accounting.AccountTransactionDetail
import ish.oncourse.server.accounting.TransactionSettings
import ish.oncourse.server.cayenne.InvoiceLine

import java.time.LocalDate

class DelayedIncomeTransactionsBuilder implements TransactionsBuilder {

    private InvoiceLine invoiceLine
    private Money amountToPost
    private LocalDate transactionDate

    private DelayedIncomeTransactionsBuilder() {

    }

    static DelayedIncomeTransactionsBuilder valueOf(InvoiceLine invoiceLine, Money amountToPost, LocalDate transactionDate) {
        DelayedIncomeTransactionsBuilder builder = new DelayedIncomeTransactionsBuilder()
        builder.invoiceLine = invoiceLine
        builder.amountToPost = amountToPost
        builder.transactionDate = transactionDate
        builder
    }

    @Override
    TransactionSettings build() {
        TransactionSettings.valueOf(AccountTransactionDetail.valueOf(invoiceLine, amountToPost, invoiceLine.account, invoiceLine.prepaidFeesAccount, transactionDate))
    }
}
