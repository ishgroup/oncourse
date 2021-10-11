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
import ish.common.types.PaymentStatus
import ish.oncourse.server.ICayenneService
import ish.oncourse.server.api.v1.model.FinalisePeriodInfoDTO
import ish.oncourse.server.api.v1.model.ValidationErrorDTO
import ish.oncourse.server.api.v1.service.FinalisePeriodApi
import ish.oncourse.server.cayenne.Banking
import ish.oncourse.server.cayenne.PaymentIn
import ish.oncourse.server.cayenne.PaymentMethod
import ish.oncourse.server.cayenne.PaymentOut
import ish.oncourse.server.services.ISystemUserService
import ish.oncourse.server.services.TransactionLockedService
import ish.persistence.Preferences
import ish.util.LocalDateUtils
import ish.util.PaymentMethodUtil
import ish.validation.TransactionLockedErrorCode
import static ish.validation.TransactionLockedErrorCode.*
import ish.validation.TransactionLockedValidator
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.ObjectSelect
import org.apache.commons.lang3.StringUtils

import javax.ws.rs.ClientErrorException
import javax.ws.rs.core.Response
import java.time.LocalDate

class FinalisePeriodApiImpl implements FinalisePeriodApi {

    @Inject
    private ICayenneService cayenneService
    @Inject
    private ISystemUserService userService

    @Inject
    private TransactionLockedService lockedService


    @Override
    FinalisePeriodInfoDTO getInfo(String lockDateString) {

        FinalisePeriodInfoDTO responce = new FinalisePeriodInfoDTO()
        LocalDate previousValue = lockedService.transactionLocked
        responce.lastDate = previousValue.plusDays(1)

        LocalDate lockDate

        if (!StringUtils.trimToNull(lockDateString)) {
            lockDate  = responce.lastDate.withDayOfMonth(responce.lastDate.lengthOfMonth())
            if (lockDate.isAfter(LocalDate.now())) {
                lockDate = responce.lastDate.plusDays(7)
                if (lockDate.isAfter(LocalDate.now())) {
                    lockDate = responce.lastDate
                }
            }
        } else {
            lockDate = LocalDateUtils.stringToValue(lockDateString)
        }

        responce.targetDate = lockDate

        ValidationErrorDTO error = validate(previousValue, lockDate)
        if (error) {
            throw new ClientErrorException(Response.status(Response.Status.BAD_REQUEST).entity(error).build())
        }

        List<PaymentIn> unreconciledPaymentIns = getUnreconciledPaymentIns(previousValue, lockDate)
        List<PaymentOut> unreconciledPaymentOuts = getUnreconciledPaymentOuts(previousValue, lockDate)

        if (!(unreconciledPaymentIns.empty && unreconciledPaymentOuts.empty)) {
            int count = unreconciledPaymentIns.size() + unreconciledPaymentOuts.size();
            responce.unreconciledPaymentsCount = count
            responce.unreconciledPaymentsBankingIds = getUnreconciledBankings(unreconciledPaymentIns, unreconciledPaymentOuts)*.id
        }

        List<PaymentIn> unbankedPaymentIns = getUnbankedPaymentIns(previousValue, lockDate)
        if (!unbankedPaymentIns.empty) {
            responce.unbankedPaymentInIds = unbankedPaymentIns*.id
            responce.unbankedPaymentInCount = unbankedPaymentIns.size()
        }

        List<PaymentOut> unbankedPaymentOuts = getUnbankedPaymentOuts(previousValue, lockDate)
        if (!unbankedPaymentOuts.empty) {
            responce.unbankedPaymentOutIds = unbankedPaymentOuts*.id
            responce.unbankedPaymentOutCount = unbankedPaymentOuts.size()
        }

        List<Banking> bankings =  getAffectedBankings(previousValue, lockDate)
        if (!bankings.empty) {
            responce.depositBankingCount = bankings.size()
            responce.depositBankingIds = bankings*.id
        }
        return responce
    }


    private List<Banking> getAffectedBankings(LocalDate previousValue, LocalDate newValue) {
        return ObjectSelect.query(Banking.class)
                .where(Banking.SETTLEMENT_DATE.between(previousValue, newValue))
                .select(cayenneService.newContext)
    }

    private List<Banking> getUnreconciledBankings( List<PaymentIn> unreconciledPaymentIns, List<PaymentOut> unreconciledPaymentOuts) {
        ObjectSelect.query(Banking)
                .where(Banking.PAYMENTS_IN.outer().dot(PaymentIn.ID).in(unreconciledPaymentIns*.id)
                        .orExp(Banking.PAYMENTS_OUT.outer().dot(PaymentOut.ID).in(unreconciledPaymentOuts*.id)))
                .select(cayenneService.newContext)
    }

    private List<PaymentOut> getUnbankedPaymentOuts(LocalDate previousValue, LocalDate newValue) {
        ObjectSelect.query(PaymentOut.class)
                .where(PaymentOut.BANKING.isNull()
                .andExp(PaymentOut.PAYMENT_DATE.between(previousValue, newValue))
                .andExp(PaymentOut.STATUS.eq(PaymentStatus.SUCCESS))
                .andExp(PaymentOut.PAYMENT_METHOD.dot(PaymentMethod.TYPE).nin(PaymentMethodUtil.SYSTEM_TYPES)))
                .select(cayenneService.newContext)
    }

    private List<PaymentIn> getUnbankedPaymentIns(LocalDate previousValue, LocalDate newValue) {
        ObjectSelect.query(PaymentIn.class)
                .where(PaymentIn.BANKING.isNull()
                .andExp(PaymentIn.PAYMENT_DATE.between(previousValue, newValue))
                .andExp(PaymentIn.STATUS.nin(PaymentStatus.STATUSES_FAILED))
                .andExp(PaymentIn.PAYMENT_METHOD.dot(PaymentMethod.TYPE).nin(PaymentMethodUtil.SYSTEM_TYPES)))
                .select(cayenneService.newContext)
    }

    private List<PaymentIn> getUnreconciledPaymentIns(LocalDate previousValue, LocalDate newValue, ObjectContext context = null) {
        return ObjectSelect.query(PaymentIn.class)
                .where(PaymentIn.BANKING.dot(Banking.SETTLEMENT_DATE).between(previousValue, newValue))
                .and(PaymentIn.RECONCILED.isFalse())
                .and(PaymentIn.PAYMENT_METHOD.dot(PaymentMethod.RECONCILABLE).eq(Boolean.TRUE))
                .select(context?:cayenneService.newContext)
    }

    private List<PaymentOut> getUnreconciledPaymentOuts(LocalDate previousValue, LocalDate newValue, ObjectContext context = null) {
        return ObjectSelect.query(PaymentOut.class)
                .where(PaymentOut.BANKING.dot(Banking.SETTLEMENT_DATE).between(previousValue, newValue))
                .and(PaymentOut.RECONCILED.isFalse())
                .and(PaymentOut.PAYMENT_METHOD.dot(PaymentMethod.RECONCILABLE).eq(Boolean.TRUE))
                .select(context?:cayenneService.newContext)
    }

    private static ValidationErrorDTO validate(LocalDate previousValue, LocalDate newValue) {
        Map<String, TransactionLockedErrorCode> validate = TransactionLockedValidator.valueOf(previousValue, newValue).validate()
        if (!validate.isEmpty()) {
            switch (validate.get(Preferences.FINANCE_TRANSACTION_LOCKED)) {
                case beforeCurrentValue:
                    return new ValidationErrorDTO(errorMessage: "Finalise date must be after or equal the from date.")
                case todayOrInFuture:
                    return new ValidationErrorDTO(errorMessage: "Finalise date cannot be today or in the future.")
                case allDaysFinalised:
                    return new ValidationErrorDTO(errorMessage: "You have already finalised all transactions up until yesterday.")
                default:
                    break
            }
        }
        return null
    }

    @Override
    void updateLockDate(String lockDateString) {
        ValidationErrorDTO error = null
        if (!StringUtils.trimToNull(lockDateString)) {
            error = new ValidationErrorDTO( propertyName: "lockDate", errorMessage: "You have already finalised all transactions up until yesterday.")
            throw new ClientErrorException(Response.status(Response.Status.BAD_REQUEST).entity(error).build())
        }
        LocalDate lockDate  = LocalDateUtils.stringToValue(lockDateString)
        LocalDate prevDate = lockedService.transactionLocked
        if (!error) {
            error = validate(prevDate, lockDate)
        }
        if (error) {
            throw new ClientErrorException(Response.status(Response.Status.BAD_REQUEST).entity(error).build())
        }
        ObjectContext context = cayenneService.newContext
        getUnreconciledPaymentIns(prevDate, lockDate, context).each { paymentIn -> paymentIn.reconciled = true }
        getUnreconciledPaymentOuts(prevDate, lockDate, context).each { paymentOut -> paymentOut.reconciled = true}
        context.commitChanges()

        lockedService.updateTransactionLockedDate(lockDate)
    }

}
