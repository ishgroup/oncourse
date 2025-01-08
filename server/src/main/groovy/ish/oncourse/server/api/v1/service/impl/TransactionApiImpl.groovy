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

package ish.oncourse.server.api.v1.service.impl

import javax.inject.Inject
import ish.math.Money
import ish.oncourse.server.ICayenneService
import ish.oncourse.server.accounting.AccountTransactionService
import static ish.oncourse.server.api.function.CayenneFunctions.getRecordById
import static ish.oncourse.server.api.function.EntityFunctions.checkForBadRequest
import static ish.oncourse.server.api.v1.function.TransactionFunctions.toRestTransaction
import static ish.oncourse.server.api.v1.function.TransactionFunctions.validate
import ish.oncourse.server.api.v1.model.TransactionDTO
import ish.oncourse.server.api.v1.service.TransactionApi
import ish.oncourse.server.cayenne.AccountTransaction
import ish.request.AccountTransactionRequest
import org.apache.cayenne.ObjectContext

class TransactionApiImpl implements TransactionApi {

    @Inject
    private ICayenneService cayenneService

    @Inject
    AccountTransactionService accountTransactionService

    @Override
    TransactionDTO get(Long id) {
        return toRestTransaction(getRecordById(cayenneService.newContext, AccountTransaction, id))
    }

    @Override
    void create(TransactionDTO transaction) {
        ObjectContext context = cayenneService.newContext

        checkForBadRequest(validate(transaction, context))

        AccountTransactionRequest request = AccountTransactionRequest.valueOf(
                new Money(transaction.amount),
                transaction.toAccount,
                transaction.fromAccount,
                transaction.transactionDate)

        accountTransactionService.createManualTransactions(request)
    }
}
