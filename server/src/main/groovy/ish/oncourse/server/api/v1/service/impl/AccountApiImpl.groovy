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

import com.google.inject.Inject
import ish.common.types.PaymentStatus
import ish.math.Money
import ish.oncourse.server.ICayenneService
import static ish.oncourse.server.api.function.CayenneFunctions.deleteRecord
import static ish.oncourse.server.api.function.CayenneFunctions.getRecordById
import static ish.oncourse.server.api.function.EntityFunctions.checkForBadRequest
import static ish.oncourse.server.api.function.EntityFunctions.validateEntityExistence
import static ish.oncourse.server.api.function.EntityFunctions.validateIdParam
import static ish.oncourse.server.api.v1.function.AccountFunctions.toDbAccount
import static ish.oncourse.server.api.v1.function.AccountFunctions.toRestAccount
import static ish.oncourse.server.api.v1.function.AccountFunctions.validateForDelete
import static ish.oncourse.server.api.v1.function.AccountFunctions.validateForSave
import ish.oncourse.server.api.v1.model.AccountDTO
import ish.oncourse.server.api.v1.service.AccountApi
import ish.oncourse.server.cayenne.Account
import ish.oncourse.server.cayenne.PaymentIn
import ish.oncourse.server.cayenne.PaymentMethod
import ish.oncourse.server.cayenne.PaymentOut
import ish.oncourse.server.cayenne.Site
import ish.oncourse.server.users.SystemUserService
import ish.util.PaymentMethodUtil
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.ObjectSelect

import java.util.stream.Collectors

class AccountApiImpl implements AccountApi {

    @Inject
    private ICayenneService cayenneService

    @Inject
    private SystemUserService systemUserService

    @Override
    void create(AccountDTO account) {
        ObjectContext context = cayenneService.newContext

        checkForBadRequest(validateForSave(account, context))

        Account newAccount = context.newObject(Account)
        toDbAccount(account, newAccount, context)

        context.commitChanges()
    }

    @Override
    AccountDTO get(Long id) {
        toRestAccount(getRecordById(cayenneService.newContext, Account, id))
    }

    @Override
    List<AccountDTO> getDepositAccounts() {
        Site site = systemUserService.currentUser.defaultAdministrationCentre

        return (ObjectSelect.query(Account.class)
                    .where(Account.PAYMENTS.dot(PaymentIn.BANKING).isNull())
                    .and(Account.PAYMENTS.dot(PaymentIn.STATUS).eq(PaymentStatus.SUCCESS))
                    .and(Account.PAYMENTS.dot(PaymentIn.AMOUNT).gt(Money.ZERO))
                    .and(Account.PAYMENTS.dot(PaymentIn.PAYMENT_METHOD.dot(PaymentMethod.TYPE)).nin(PaymentMethodUtil.SYSTEM_TYPES))
                    .select(cayenneService.newContext) +
                ObjectSelect.query(Account.class)
                    .where(Account.PAYMENTS_OUT.dot(PaymentOut.BANKING).isNull())
                    .and(Account.PAYMENTS_OUT.dot(PaymentOut.STATUS).eq(PaymentStatus.SUCCESS))
                    .and(Account.PAYMENTS_OUT.dot(PaymentOut.AMOUNT).gt(Money.ZERO))
                    .select(cayenneService.newContext))
                .unique { it.id }
                .stream()
                .map{ toRestAccount(it) }
                .collect(Collectors.toList())
    }

    @Override
    void remove(Long id) {
        checkForBadRequest(validateIdParam(id))

        ObjectContext context = cayenneService.newContext
        Account entity = getRecordById(context, Account, id)

        checkForBadRequest(validateEntityExistence(id, entity))
        checkForBadRequest(validateForDelete(entity))

        deleteRecord(context, entity)
    }

    @Override
    void update(Long id, AccountDTO account) {
        checkForBadRequest(validateIdParam(id))

        ObjectContext context = cayenneService.newContext

        Account dbAccount = getRecordById(context, Account, id)
        checkForBadRequest(validateEntityExistence(id, dbAccount))
        checkForBadRequest(validateForSave(account, context, dbAccount))

        toDbAccount(account, dbAccount, context)
        context.commitChanges()
    }
}
