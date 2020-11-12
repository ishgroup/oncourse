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

import ish.common.types.AccountTransactionType
import ish.math.Money
import ish.oncourse.server.accounting.AccountTransactionDetail
import ish.oncourse.server.accounting.TransactionSettings
import ish.oncourse.server.cayenne.Account

import java.time.LocalDate

class JournalTransactionsBuilder implements TransactionsBuilder {

    private Account primaryAccount
    private Account secondaryAccount
    private Money amount
    private LocalDate transactionDate

    private JournalTransactionsBuilder() {

    }

    static JournalTransactionsBuilder valueOf(Account primaryAccount, Account secondaryAccount, Money amount, LocalDate transactionDate) {
        JournalTransactionsBuilder builder = new JournalTransactionsBuilder()
        builder.primaryAccount = primaryAccount
        builder.secondaryAccount = secondaryAccount
        builder.amount = amount
        builder.transactionDate = transactionDate
        builder
    }

    @Override
    TransactionSettings build() {
        TransactionSettings.valueOf(AccountTransactionDetail.valueOf(primaryAccount, secondaryAccount, amount, AccountTransactionType.JOURNAL, 0L, transactionDate))
    }
}
