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

package ish.oncourse.server.api.v1.function

import static ish.oncourse.server.api.function.CayenneFunctions.getRecordById
import ish.oncourse.server.api.v1.model.TransactionDTO
import ish.oncourse.server.api.v1.model.ValidationErrorDTO
import ish.oncourse.server.cayenne.Account
import ish.oncourse.server.cayenne.AccountTransaction
import org.apache.cayenne.ObjectContext

class TransactionFunctions {

    static TransactionDTO toRestTransaction(AccountTransaction accountTransaction) {
        new TransactionDTO().with { transaction ->
            transaction.id = accountTransaction.id
            transaction.fromAccount = accountTransaction.account.id
            transaction.amount = accountTransaction.amount.toBigDecimal()
            transaction.transactionDate = accountTransaction.transactionDate
            transaction
        }
    }

    static ValidationErrorDTO validate(TransactionDTO transaction, ObjectContext context) {
        if (!transaction.fromAccount) {
            return new ValidationErrorDTO(null, 'fromAccount', 'From account is required.')
        } else if (!getRecordById(context, Account, transaction.fromAccount)) {
            return new ValidationErrorDTO(null, 'fromAccount', "Cannot account with id = ${transaction.fromAccount}.")
        }

        if (!transaction.toAccount) {
            return new ValidationErrorDTO(null, 'fromAccount', 'To account is required.')
        } else if (!getRecordById(context, Account, transaction.toAccount)) {
            return new ValidationErrorDTO(null, 'fromAccount', "Cannot account with id = ${transaction.toAccount}.")
        }

        if (!transaction.amount) {
            return new ValidationErrorDTO(null, 'amount', 'Amount is required.')
        }

        if (!transaction.transactionDate) {
            return new ValidationErrorDTO(null, 'amount', 'Transaction date is required.')
        }

        null
    }
}
