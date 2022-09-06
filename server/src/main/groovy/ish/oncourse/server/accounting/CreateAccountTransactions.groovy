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

import ish.common.types.AccountTransactionType
import ish.oncourse.server.cayenne.Account
import ish.oncourse.server.cayenne.AccountTransaction
import org.apache.cayenne.ObjectContext
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger


class CreateAccountTransactions {

    private static final Logger logger = LogManager.getLogger(CreateAccountTransactions)

    private ObjectContext context
    private AccountTransactionDetail details

    private CreateAccountTransactions() {

    }

    static CreateAccountTransactions valueOf(ObjectContext context, AccountTransactionDetail details) {
        CreateAccountTransactions function = new CreateAccountTransactions()
        function.context = context
        function.details = details
        function
    }

    List<Long> create() {
        if (!details.primaryAccount || !details.secondaryAccount) {
            throw new IllegalArgumentException("The accounts have not been specified")
        }
        // do not create GL transactions for $0
        if (!details.amount.isZero()) {
            logger.info("creating transactions for {}, {}, {}, {}, {}",
                    details.tableName, details.foreignRecordId, details.amount, details.primaryAccount.accountCode, details.secondaryAccount.accountCode)

            Date now = new Date()

            Account primaryAccount = context.localObject(details.primaryAccount)
            AccountTransaction at1 = context.newObject(AccountTransaction)
            at1.createdOn = now
            at1.modifiedOn = now
            at1.transactionDate = details.transactionDate
            at1.tableName = details.tableName
            at1.foreignRecordId = details.foreignRecordId
            if (details.tableName == AccountTransactionType.INVOICE_LINE && primaryAccount.expense) {
                at1.amount = details.amount.negate()
            } else {
                at1.amount = details.amount
            }
            at1.account = primaryAccount
            at1.description = details.description

            Account secondaryAccount = context.localObject(details.secondaryAccount)
            AccountTransaction at2 = context.newObject(AccountTransaction)
            at2.createdOn = now
            at2.modifiedOn = now
            at2.transactionDate = details.transactionDate
            at2.tableName = details.tableName
            at2.foreignRecordId = details.foreignRecordId
            if (details.tableName == AccountTransactionType.INVOICE_LINE && primaryAccount.expense && secondaryAccount.asset) {
                at2.amount = details.amount
            } else {
                at2.amount = (isLeftSideGlAccount(primaryAccount) == isLeftSideGlAccount(secondaryAccount)) ? details.amount.negate() : details.amount
            }
            at2.account = secondaryAccount
            at2.description = details.description

            context.commitChanges()
            return [at1.id, at2.id]
        }
        return []
    }

    private static boolean isLeftSideGlAccount(Account account) {
        return account.asset || account.expense || account.COS
    }

}
