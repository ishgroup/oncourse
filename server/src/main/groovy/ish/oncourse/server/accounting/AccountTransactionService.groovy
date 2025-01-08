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

package ish.oncourse.server.accounting

import javax.inject.Inject
import ish.common.types.AccountTransactionType
import ish.oncourse.server.ICayenneService
import ish.oncourse.server.accounting.builder.JournalTransactionsBuilder
import ish.oncourse.server.accounting.builder.TransactionsBuilder
import ish.oncourse.server.cayenne.Account
import ish.oncourse.server.cayenne.AccountTransaction
import ish.request.AccountTransactionRequest
import org.apache.cayenne.query.ObjectSelect

import java.util.concurrent.locks.ReentrantLock

class AccountTransactionService {

    private ICayenneService cayenneService
    private ReentrantLock lock = new ReentrantLock()

    @Inject
    AccountTransactionService(ICayenneService cayenneService) {
        this.cayenneService = cayenneService
    }

    void createManualTransactions(AccountTransactionRequest request) {
        createTransactions(JournalTransactionsBuilder.valueOf(getAccount(request.primaryAccountId), getAccount(request.secondaryAccountId), request.amount, request.transactionDate))
    }

    void createTransactions(TransactionsBuilder transactionsBuilder) {
        TransactionSettings settings = transactionsBuilder.build()
        if (settings.isInitialTransaction) {
            lock.lock()
            try {
                if (hasNoInitialTransactions(settings.details[0].tableName, settings.details[0].foreignRecordId)) {
                    settings.details.each { CreateAccountTransactions.valueOf(cayenneService.newContext, it).create() }
                }
            } finally {
                lock.unlock()
            }
        } else {
            settings.details.each { CreateAccountTransactions.valueOf(cayenneService.newContext, it).create() }
        }
    }

    private Account getAccount(Long id) {
        ObjectSelect.query(Account)
                .where(Account.ID.eq(id))
                .selectOne(cayenneService.newContext)
    }

    private boolean hasNoInitialTransactions(AccountTransactionType tableName, Long foreignRecordId) {
        ObjectSelect.query(AccountTransaction)
                .where(AccountTransaction.TABLE_NAME.eq(tableName))
                .and(AccountTransaction.FOREIGN_RECORD_ID.eq(foreignRecordId))
                .select(cayenneService.newContext).empty
    }
}
