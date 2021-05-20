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

import com.google.inject.Inject
import groovy.transform.CompileStatic
import groovy.transform.TypeCheckingMode
import ish.common.types.ConfirmationStatus
import ish.common.types.PaymentStatus
import ish.common.types.PaymentType
import static ish.common.types.PaymentType.*
import ish.oncourse.function.GetContactFullName
import ish.oncourse.server.ICayenneService
import ish.oncourse.server.api.dao.PaymentInDao
import static ish.oncourse.server.api.function.CayenneFunctions.getRecordById
import ish.oncourse.server.api.v1.model.PaymentInDTO
import ish.oncourse.server.api.v1.model.PaymentInvoiceDTO
import ish.oncourse.server.cayenne.Account
import ish.oncourse.server.cayenne.PaymentIn
import ish.oncourse.server.cayenne.PaymentInLine
import ish.oncourse.server.cayenne.Site
import ish.oncourse.server.cayenne.SystemUser
import ish.oncourse.server.services.TransactionLockedService
import ish.oncourse.server.users.SystemUserService
import ish.util.AccountUtil
import ish.util.PaymentMethodUtil
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.access.DataContext
import org.apache.cayenne.query.SelectById

import java.time.LocalDate
import java.time.ZoneId
import java.util.stream.Collectors

@CompileStatic
class PaymentInApiService extends EntityApiService<PaymentInDTO, PaymentIn, PaymentInDao> {

    @Inject
    BankingService bankingService

    @Inject
    TransactionLockedService transactionLockedService

    @Inject
    private SystemUserService systemUserService

    @Inject ICayenneService cayenneService

    @Inject PaymentInDao paymentInDao

    @Inject
    private PaymentOutApiService paymentOutService

    @Override
    Class<PaymentIn> getPersistentClass() {
        return PaymentIn
    }

    @Override
    @CompileStatic(TypeCheckingMode.SKIP)
    PaymentInDTO toRestModel(PaymentIn paymentIn) {
        new PaymentInDTO().with { paymentInDTO ->
            paymentInDTO.id = paymentIn.id
            paymentInDTO.payerId = paymentIn.payer?.id
            paymentInDTO.payerName = paymentIn.payer != null ? GetContactFullName.valueOf(paymentIn.payer, true).get() : null
            paymentInDTO.status = paymentIn.statusString
            paymentInDTO.paymentInType = paymentIn.paymentMethod?.name?.toString()
            paymentInDTO.amount = paymentIn.amount.toBigDecimal()
            paymentInDTO.accountInName = paymentIn.accountIn?.description + " " + paymentIn.accountIn?.accountCode
            paymentInDTO.source = paymentIn.source?.toString()
            paymentInDTO.datePayed = paymentIn.paymentDate
            paymentInDTO.dateBanked = paymentIn.banking?.settlementDate
            paymentInDTO.ccSummary = getCCSummary(paymentIn).toList()
            paymentInDTO.chequeSummary = getChequeSummary(paymentIn)
            paymentInDTO.createdBy = paymentIn.createdBy?.email
            paymentInDTO.ccTransaction = paymentIn.getCreditCardClientReference()
            paymentInDTO.emailConfirmation = paymentIn.confirmationStatus == ConfirmationStatus.NOT_SENT || paymentIn.confirmationStatus == ConfirmationStatus.SENT
            paymentInDTO.invoices = paymentIn.paymentInLines.stream().map { PaymentInLine l ->
                new PaymentInvoiceDTO().with { PaymentInvoiceDTO i ->
                    i.id = l.invoice.id
                    i.amount = l.amount.toBigDecimal()
                    i.amountOwing = l.invoice.amountOwing.toBigDecimal()
                    i.invoiceNumber = l.invoice.invoiceNumber
                    i.dateDue = l.invoice.dateDue
                    i
                }
            }.collect(Collectors.toList())
            paymentInDTO.createdOn = paymentIn.createdOn?.toInstant()?.atZone(ZoneId.systemDefault())?.toLocalDateTime()
            paymentInDTO.modifiedOn = paymentIn.modifiedOn?.toInstant()?.atZone(ZoneId.systemDefault())?.toLocalDateTime()
            paymentInDTO.administrationCenterId = paymentIn.administrationCentre?.id
            paymentInDTO.administrationCenterName = paymentIn.administrationCentre?.name

            paymentInDTO
        }
    }

    static Map<String, String> getChequeSummary(PaymentIn paymentIn) {
        Map<String, String> result = [:]
        if (PaymentType.CHEQUE == paymentIn.paymentMethod.type) {
            result.put("Cheque branch", paymentIn.chequeBranch)
            result.put("Cheque drawer", paymentIn.chequeDrawer)
            result.put("Cheque bank", paymentIn.chequeBank)
            return result
        }
        return result
    }

    static String[] getCCSummary(PaymentIn paymentIn) {
        if (PaymentType.CREDIT_CARD == paymentIn.paymentMethod.type &&
                !PaymentStatus.STATUSES_FAILED.contains(paymentIn.status) &&
                paymentIn.creditCardType != null) {
            return [paymentIn.creditCardName,
             String.format("%s, %s", paymentIn.creditCardType?.displayName, paymentIn.creditCardNumber),
             String.format("expiry: %s", paymentIn.creditCardExpiry)].toArray(new String[0])
        }
        return new String[0]
    }

    @Override
    PaymentIn toCayenneModel(PaymentInDTO paymentInDTO, PaymentIn paymentIn) {
        if (paymentInDTO.administrationCenterId != null) {
            paymentIn.administrationCentre = getRecordById(paymentIn.context, Site, paymentInDTO.administrationCenterId)
        } else {
            paymentIn.administrationCentre = null
        }
        bankingService.changeBanking(paymentIn, paymentInDTO.dateBanked, paymentIn.administrationCentre)

        paymentIn
    }

    @Override
    void validateModelBeforeRemove(PaymentIn paymentIn) {
        // Is not applicable for this entity
    }

    void throwDateBankedException(String message) {
        validator.throwClientErrorException("dateBanked", message)
    }

    @Override
    void validateModelBeforeSave(PaymentInDTO paymentInDTO, ObjectContext context, Long id) {
        PaymentIn paymentIn = SelectById.query(PaymentIn, id).selectOne(context) as PaymentIn
        if (paymentIn.status != PaymentStatus.SUCCESS) {
            throwDateBankedException("Can be modified only payments with status equals SUCCESS")
        }

        if (PaymentMethodUtil.SYSTEM_TYPES.contains(paymentIn.paymentMethod.type)) {
            throwDateBankedException("Can not be modified system payments")
        }

        LocalDate locked = transactionLockedService.transactionLocked
        if (paymentIn.dateBanked != null && !paymentIn.dateBanked.isAfter(locked)) {
            throwDateBankedException("Date banked value must be after ${locked.toString()}")
        }



        if (paymentInDTO.administrationCenterId != null ) {
            if (paymentIn.banking && paymentInDTO.administrationCenterId !=  paymentIn.administrationCentre?.id) {
                validator.throwClientErrorException("administrationCentreId", "Administration centre can not be changed for payment assigned to banking")
            }

            Site site = getRecordById(cayenneService.newContext, Site, paymentInDTO.administrationCenterId)
            if (!site) {
                validator.throwClientErrorException("administrationCentreId", "Administration centre is wrong")
            }
        }




        if (PaymentType.CREDIT_CARD !=  paymentIn.paymentMethod.type && paymentInDTO.administrationCenterId == null) {
            validator.throwClientErrorException("administrationCentreId", "Administration centre is required")
        }

    }

    void reverseWithCommit(Long id) {
        DataContext context = cayenneService.newContext

        PaymentIn paymentIn = getEntityAndValidateExistence(context, id)

        if (PaymentType.CREDIT_CARD == paymentIn.paymentMethod.type) {
            validateRefund(paymentIn)
            paymentOutService.createCCpayment(paymentIn.amount, paymentIn, null)
        } else {
            validateForReverse(paymentIn, id)
            reverse(paymentIn)
        }

        try {
            context.commitChanges()
        } catch (Throwable ignored) {
            context.rollbackChanges()
            throw ignored
        }
    }

    private void validateForReverse(PaymentIn paymentIn, long id) {
        if (!paymentIn.isSuccess()
            || paymentIn.paymentMethod.type in [INTERNAL, REVERSE, VOUCHER]
            || paymentIn.reversalOf != null
            || paymentIn.reversedBy != null) {
                validator.throwClientErrorException(id, "banking", "Payment can not be reversed")
        }
        if (paymentIn.reconciled) {
            validator.throwClientErrorException(id, "reconciled", "Reconciled payment can not be reversed. Please unreconcile this payment first.")
        }

        if (paymentIn.banking != null) {
            validator.throwClientErrorException(id, "banking", "A banked payment can not be reversed. Please unbank this payment first.")
        }
    }

    /**
     * Copied from ReverseHelperPayment
     * @param paymentIn
     */
    private void reverse(PaymentIn paymentIn) {
        ObjectContext context = paymentIn.getObjectContext()

        PaymentIn reverse = context.newObject(PaymentIn.class)
        reverse.amount = paymentIn.amount.negate()
        reverse.account = paymentIn.accountIn
        reverse.administrationCentre = paymentIn.administrationCentre
        reverse.status = PaymentStatus.SUCCESS
        reverse.createdOn = new Date()
        reverse.payer = paymentIn.payer
        reverse.privateNotes = String.format("Reverse payment for payment %s%s", paymentIn.source.databaseValue, paymentIn.id)
        reverse.source = paymentIn.source
        reverse.paymentMethod = paymentIn.paymentMethod

        reverse.account = paymentIn.paymentMethod.account
        reverse.undepositedFundsAccount = paymentIn.paymentMethod.undepositedFundsAccount


        if (PaymentType.CHEQUE == paymentIn.paymentMethod.type) {
            reverse.chequeBank = paymentIn.chequeBank
            reverse.chequeBranch = paymentIn.chequeBranch
            reverse.chequeDrawer = paymentIn.chequeDrawer
        }

        reverse.reversalOf = paymentIn

        for (PaymentInLine line : paymentIn.getPaymentInLines()) {
            PaymentInLine reverseLine = context.newObject(PaymentInLine.class)
            reverseLine.payment = reverse
            reverseLine.invoice = line.invoice
            reverseLine.account = line.account
            reverseLine.amount = line.amount.negate()
            reverseLine.createdOn = new Date()
        }

        Account account = AccountUtil.getDefaultBankAccount(context, Account.class)
        if(account.isAsset() || account.isLiability()) {
            reverse.setAccountIn(account)
        }

        SystemUser currentUser = context.localObject(systemUserService.currentUser)
        reverse.createdBy = currentUser
        reverse.administrationCentre = currentUser.defaultAdministrationCentre

        if (reverse.confirmationStatus == null) {
            reverse.confirmationStatus = ConfirmationStatus.DO_NOT_SEND
        }
        if (reverse.paymentDate == null) {
            reverse.paymentDate = LocalDate.now()
        }
    }


    private void validateRefund(PaymentIn paymentIn) {

        if (paymentIn.gatewayReference == null) {
            validator.throwClientErrorException(paymentIn.id, "gatewayReference", "You can only reverse credit card payments which were done against ish web.")
        }

        if (PaymentStatus.SUCCESS != paymentIn.status) {
            validator.throwClientErrorException(paymentIn.id, "gatewayReference", "You can only reverse Success credit card payments.")
        }
        if (!paymentOutService.entityDao.getReversedFor(paymentIn).empty) {
            validator.throwClientErrorException(paymentIn.id, "status", "Credit card payment already refunded.")
        }

    }
}
