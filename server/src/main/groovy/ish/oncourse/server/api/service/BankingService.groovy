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

package ish.oncourse.server.api.service

import javax.inject.Inject
import groovy.transform.CompileStatic
import ish.common.types.CreditCardType
import ish.common.types.PaymentStatus
import ish.oncourse.cayenne.PaymentInterface
import ish.oncourse.common.BankingType
import static ish.oncourse.common.BankingType.AUTO_AMEX
import static ish.oncourse.common.BankingType.AUTO_MCVISA
import static ish.oncourse.common.BankingType.AUTO_OTHER
import static ish.oncourse.common.BankingType.MANUAL
import ish.oncourse.server.ICayenneService
import ish.oncourse.server.cayenne.Account
import ish.oncourse.server.cayenne.Banking
import ish.oncourse.server.cayenne.PaymentIn
import ish.oncourse.server.cayenne.PaymentMethod
import ish.oncourse.server.cayenne.PaymentOut
import ish.oncourse.server.cayenne.Site
import ish.oncourse.server.cayenne.SystemUser
import ish.oncourse.server.users.SystemUserService
import ish.util.PaymentMethodUtil
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.ObjectSelect
import static org.apache.cayenne.query.ObjectSelect.query

import java.time.LocalDate

@CompileStatic
class BankingService {

    @Inject
    private ICayenneService cayenneService

    @Inject
    private SystemUserService systemUserService

    void changeBanking(PaymentInterface payment, LocalDate settlementDate, Site adminCenter) {
        Banking banking = payment.banking as Banking
        LocalDate prevValue = banking?.settlementDate
        if (( prevValue == null && settlementDate != null)
                || (prevValue != null && settlementDate == null)
                || (prevValue !=  settlementDate)) {

            if (settlementDate == null) {
                payment.banking = null
            } else {
                assignBanking(payment, settlementDate, adminCenter)
            }
            if ((payment.paymentMethod as PaymentMethod).reconcilable) {
                payment.reconciled = false
            }
            safeDeleteBanking(banking, payment.context)
        }
    }

    void assignBanking(PaymentInterface payment, LocalDate settlementDate, Site adminCenter) {
        if (PaymentMethodUtil.SYSTEM_TYPES.contains(payment.paymentMethod.type)) {
            return
        }
        if (PaymentStatus.SUCCESS != payment.status) {
            return
        }
        Account account = payment instanceof PaymentIn ? payment.accountIn : ((PaymentOut)payment).accountOut
        BankingType type = getBankingType(payment)

        Banking banking = findBanking(type, account, settlementDate, adminCenter, payment.context)
        if (banking == null) {
            banking = createAndCommitBanking(type, account, settlementDate, adminCenter, payment.context)
        }

        payment.banking = banking
    }

    private Banking createAndCommitBanking(BankingType type, Account account, LocalDate settlementDate, Site adminCenter, ObjectContext context) {
        ObjectContext newContext = cayenneService.newContext
        Banking newBanking = createBanking(type, account, settlementDate, adminCenter, newContext)
        newContext.commitChanges()
        return context.localObject(newBanking)
    }

    Banking createBanking(BankingType type, Account account, LocalDate settlementDate,  Site adminCenter, ObjectContext context) {
        Banking newBanking = context.newObject(Banking)
        newBanking.type = type
        newBanking.settlementDate = settlementDate
        newBanking.assetAccount = context.localObject(account)
        if (MANUAL == type) {
            SystemUser user = systemUserService.currentUser
            SystemUser localUser = context.localObject(user)
            newBanking.createdBy = localUser
            newBanking.adminSite = context.localObject(adminCenter)
        }
        return newBanking
    }

    static Banking findBanking(BankingType type, Account account, LocalDate settlementDate, Site adminCenter, ObjectContext context) {

        ObjectSelect<Banking> bankingQuery = query(Banking.class)
                .where(Banking.TYPE.eq(type))
                .and(Banking.SETTLEMENT_DATE.eq(settlementDate))
                .and(Banking.ASSET_ACCOUNT.eq(account))

        if (MANUAL == type) {
            bankingQuery = bankingQuery & Banking.ADMIN_SITE.eq(adminCenter)
        }

        List<Banking>  bankings = bankingQuery.select(context)

        if (bankings.size() > 0) {
            return bankings.get(0)
        }
        return null
    }

    private static BankingType getBankingType(PaymentInterface payment) {
        if (!payment.paymentMethod.bankedAutomatically) {
            return MANUAL
        }
        if (CreditCardType.MASTERCARD == payment.creditCardType || CreditCardType.VISA == payment.creditCardType) {
            return AUTO_MCVISA
        }
        if (CreditCardType.AMEX == payment.creditCardType) {
            return AUTO_AMEX
        }
        return AUTO_OTHER
    }

    private static boolean safeDeleteBanking(Banking banking, ObjectContext context) {
        if (banking != null) {
            if (banking.getPaymentsIn().isEmpty() && banking.getPaymentsOut().isEmpty()) {
                context.deleteObject(banking)
                return true
            }
            return false
        }
        return false
    }
}
