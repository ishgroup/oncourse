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
import static ish.common.types.PaymentStatus.*
import ish.common.types.PaymentType
import ish.math.Money
import ish.oncourse.entity.services.SetPaymentMethod
import ish.oncourse.server.api.dao.ContactDao
import ish.oncourse.server.api.dao.InvoiceDao
import ish.oncourse.server.api.dao.PaymentInDao
import ish.oncourse.server.api.dao.PaymentOutDao
import static ish.oncourse.server.api.function.CayenneFunctions.getRecordById
import ish.oncourse.server.api.v1.model.PaymentInvoiceDTO
import ish.oncourse.server.api.v1.model.PaymentOutDTO
import ish.oncourse.server.api.v1.model.PaymentStatusDTO
import ish.oncourse.server.api.v1.model.PaymentTypeDTO
import ish.oncourse.server.cayenne.Contact
import ish.oncourse.server.cayenne.Invoice
import ish.oncourse.server.cayenne.PaymentIn
import ish.oncourse.server.cayenne.PaymentMethod
import ish.oncourse.server.cayenne.PaymentOut
import ish.oncourse.server.cayenne.PaymentOutLine
import ish.oncourse.server.cayenne.Site
import ish.oncourse.server.services.TransactionLockedService
import ish.oncourse.server.users.SystemUserService
import ish.oncourse.server.windcave.PaymentService
import ish.oncourse.server.windcave.SessionAttributes
import static ish.util.LocalDateUtils.dateToTimeValue
import ish.util.PaymentMethodUtil
import org.apache.cayenne.ObjectContext

import static ish.util.EnumUtil.toDTOEnum
import org.apache.cayenne.query.SelectById
import org.apache.commons.lang3.StringUtils

import java.time.LocalDate

@CompileStatic
class PaymentOutApiService extends EntityApiService<PaymentOutDTO, PaymentOut, PaymentOutDao> {

    @Inject
    private TransactionLockedService transactionLockedService

    @Inject
    BankingService bankingService

    @Inject
    ContactDao contactDao

    @Inject
    InvoiceDao invoiceDao

    @Inject
    PaymentInDao paymentInDao

    @Inject
    private SystemUserService systemUserService

    @Inject
    private PaymentService paymentService

    @Override
    Class<PaymentOut> getPersistentClass() {
        return PaymentOut
    }

    @Override
    @CompileStatic(TypeCheckingMode.SKIP)
    PaymentOutDTO toRestModel(PaymentOut cayenneModel) {
        return new PaymentOutDTO().with { it ->
            it.id = cayenneModel.id
            it.status = cayenneModel.status ? toDTOEnum(cayenneModel.status, PaymentStatusDTO) : null
            it.type = toDTOEnum(cayenneModel.paymentMethod.type, PaymentTypeDTO)
            it.paymentMethodId = cayenneModel.paymentMethod.id
            it.payeeId = cayenneModel.payee.id
            it.payeeName = cayenneModel.payee.fullName
            it.amount = cayenneModel.amount.toBigDecimal()
            it.accountOut = cayenneModel.accountOut?.id
            if (PaymentType.CHEQUE == cayenneModel.paymentMethod.type) {
                it.chequeSummary = ["Cheque branch": cayenneModel.chequeBranch, "Cheque drawer" : cayenneModel.chequeDrawer, "Cheque bank" : cayenneModel.chequeBank]
            }
            it.datePayed = cayenneModel.paymentDate
            it.dateBanked = cayenneModel.banking?.settlementDate
            it.invoices = cayenneModel.paymentOutLines.collect {pl -> new PaymentInvoiceDTO().with { i ->
                i.id = pl.invoice.id
                i.amountOwing = pl.invoice.amountOwing.toBigDecimal()
                i.dateDue = pl.invoice.dateDue
                i.invoiceNumber = pl.invoice.invoiceNumber
                i.amount = pl.amount.toBigDecimal()
                i
            }}.sort {it.id}
            it.privateNotes = cayenneModel.privateNotes
            it.createdOn =  dateToTimeValue(cayenneModel.createdOn)
            it.modifiedOn = dateToTimeValue(cayenneModel.modifiedOn)
            it.createdBy = cayenneModel.createdBy?.email
            it.administrationCenterId = cayenneModel.administrationCentre?.id
            it.administrationCenterName = cayenneModel.administrationCentre?.name
            it
        }
    }

    @Override
    PaymentOut toCayenneModel(PaymentOutDTO dto, PaymentOut cayenneModel) {
        ObjectContext context = cayenneModel.context
        PaymentMethod method = SelectById.query(PaymentMethod, dto.paymentMethodId).selectOne(context)

        if (cayenneModel.newRecord ) {
            if (PaymentType.CREDIT_CARD == method.type) {
                context.deleteObject(cayenneModel)
                cayenneModel = createCCpayment(Money.valueOf(dto.amount), paymentInDao.getById(context, dto.refundableId), dto.invoices)
                cayenneModel.privateNotes += "/n $dto.privateNotes"
            } else {
                SetPaymentMethod.valueOf(method, cayenneModel).set()
                cayenneModel.payee = contactDao.getById(context, dto.payeeId)
                cayenneModel.reconciled = false
                cayenneModel.amount =  Money.valueOf(dto.amount)
                cayenneModel.createdBy = cayenneModel.context.localObject(systemUserService.currentUser)
                dto.invoices.each { invoiceDto ->
                    PaymentOutLine line = context.newObject(PaymentOutLine)
                    Invoice invoice = invoiceDao.getById(context, invoiceDto.id) as Invoice
                    line.accountIn = invoice.debtorsAccount
                    line.invoice = invoice
                    line.paymentOut = cayenneModel
                    line.amount =  Money.valueOf(invoiceDto.amount)
                }
                cayenneModel.status = SUCCESS
                cayenneModel.confirmationStatus = ConfirmationStatus.NOT_SENT
                cayenneModel.privateNotes = dto.privateNotes
                cayenneModel.paymentDate = dto.datePayed
            }
        } else {
            cayenneModel.paymentDate = dto.datePayed
            cayenneModel.privateNotes = dto.privateNotes
            bankingService.changeBanking(cayenneModel, dto.dateBanked, cayenneModel.administrationCentre)
        }

        if (dto.administrationCenterId != null) {
            cayenneModel.administrationCentre = getRecordById(context, Site, dto.administrationCenterId)
        } else {
            cayenneModel.administrationCentre = null
        }
        return cayenneModel
    }

    @Override
    void validateModelBeforeSave(PaymentOutDTO dto, ObjectContext context, Long id) {

        if (id) {
            validateModelBeforeUpdate(dto, context, id)
            return
        }

        if (dto.amount <= BigDecimal.ZERO) {
            validator.throwClientErrorException("amount", "Amount should be greater then zero")
        }

        if (dto.invoices.empty) {
            validator.throwClientErrorException("invoices", "Select credit note to refund")
        }

        if (dto.invoices.collect{it.amount}.inject { a,d -> a.add(d)} != dto.amount) {
            validator.throwClientErrorException("amount", "Payment amount does not match to allocated invoices amount")
        }

        if (!dto.payeeId ) {
            validator.throwClientErrorException("payeeId", "Payee is required")
        }

        Contact contact = contactDao.getById(context, dto.payeeId)

        if (!contact) {
            validator.throwClientErrorException("payeeId", "Payee is wrong")
        }

        dto.invoices.each { it ->
            if (!it.id) {
                validator.throwClientErrorException("invoices", "Invoice is required")
            }

            Invoice invoice = invoiceDao.getById(context, it.id) as Invoice
            if (!invoice || !invoice.contact.equalsIgnoreContext(contact)) {
                validator.throwClientErrorException("invoices", "Invoice is wrong")
            }

            if (invoice.amountOwing.abs().isLessThan(Money.valueOf(it.amount))) {
                validator.throwClientErrorException("invoices", "Allocated invoice amount is wrong: ${Money.valueOf(it.amount)}")
            }
        }

        if (dto.paymentMethodId == null) {
            validator.throwClientErrorException("paymentMethodId", "Payment method is required")
        }

        PaymentMethod method = SelectById.query(PaymentMethod, dto.paymentMethodId).selectOne(context)

        if (!method) {
            validator.throwClientErrorException("paymentMethodId", "Payment method is wrong")
        }

        List<PaymentMethod> methods = PaymentMethodUtil.getChoosablePaymentMethods(context, PaymentMethod).values().toList()

        if (!(method in methods)) {
            validator.throwClientErrorException("paymentMethodId", "Payment method $method.name is not avalible")
        }

        if (dto.administrationCenterId != null ) {

            Site site = getRecordById(cayenneService.newContext, Site, dto.administrationCenterId)
            if (!site) {
                validator.throwClientErrorException("administrationCentreId", "Administration centre is wrong")
            }
        }

        if (PaymentType.CREDIT_CARD == method.type) {

            if (!dto.refundableId) {
                validator.throwClientErrorException("refundableId", "Refundable payment in is recuired for credit card type")
            }

            List<Long> paymentIn = paymentInDao.getRefundablePaymentIds(context, contact, Money.valueOf(dto.amount))

            if (!(dto.refundableId in paymentIn)) {
                validator.throwClientErrorException("refundableId", "Refundable payment in is wrong")
            }

        } else {
            if (dto.administrationCenterId == null) {
                validator.throwClientErrorException("administrationCentreId", "Administration centre is required")
            }
            if (!dto.datePayed) {
                validator.throwClientErrorException("datePayed", "Date paid is mandatory")
            }

            LocalDate locked = transactionLockedService.transactionLocked

            if (dto.datePayed.isBefore(locked)) {
                validator.throwClientErrorException("datePayed", "Date paid  must be after lock date: ${locked.toString()}")
            }
        }


    }


    void validateModelBeforeUpdate(PaymentOutDTO dto, ObjectContext context, Long id) {
        PaymentOut paymentOut = SelectById.query(PaymentOut, id).selectOne(context)
        if (paymentOut.status != SUCCESS) {
            validator.throwClientErrorException("status", "Can be modified only payments with status equals SUCCESS")
        }

        if (!dto.datePayed) {
            validator.throwClientErrorException("datePayed", "Date paid is mandatory")
        }

        LocalDate locked = transactionLockedService.transactionLocked

        if (paymentOut.paymentDate != dto.datePayed) {
            if (paymentOut.paymentDate.isBefore(locked)) {
                validator.throwClientErrorException("datePayed", "Date paid in locked period")
            }
            if (dto.datePayed.isBefore(locked)) {
                validator.throwClientErrorException("datePayed", "Date paid  must be after lock date: ${locked.toString()}")
            }
        }

        if (dto.dateBanked && dto.dateBanked.isBefore(dto.datePayed)) {
            validator.throwClientErrorException("dateBanked", "Date banked must be after Date paid")
        }

        if (paymentOut.dateBanked == null && dto.dateBanked && dto.dateBanked.isBefore(locked)) {
            validator.throwClientErrorException("dateBanked", "Date banked must be after locked date")
        } else if (paymentOut.dateBanked && paymentOut.dateBanked != dto.dateBanked && paymentOut.dateBanked.isBefore(locked)) {
            validator.throwClientErrorException("dateBanked", "Date banked in locked period")
        }

        if (paymentOut.banking && dto.administrationCenterId !=  paymentOut.administrationCentre?.id) {
            validator.throwClientErrorException("administrationCentreId", "Administration centre can not be changed for payment assigned to banking")
        }
    }

    @Override
    void validateModelBeforeRemove(PaymentOut cayenneModel) {

    }

    PaymentOut createCCpayment(Money amount, PaymentIn paymentIn, List<PaymentInvoiceDTO> invoices) {

        ObjectContext context = paymentIn.context
        String merchantReference = UUID.randomUUID().toString()

        SessionAttributes sessionAttributes = paymentService.makeRefund(amount, merchantReference, paymentIn.gatewayReference)

        PaymentOut paymentOut = context.newObject(PaymentOut)
        paymentOut.creditCardExpiry = paymentIn.creditCardExpiry
        paymentOut.creditCardName = paymentIn.creditCardName
        paymentOut.creditCardNumber = paymentIn.creditCardNumber
        paymentOut.creditCardType = paymentIn.creditCardType
        paymentOut.paymentInGatewayReference = paymentIn.gatewayReference

        paymentOut.confirmationStatus = ConfirmationStatus.NOT_SENT

        paymentOut.privateNotes = " cc reference:" +
                " ${paymentIn.createdOn.format('EEE dd MMM yy HH:mm')}" +
                " [$paymentIn.gatewayReference/$paymentIn.creditCardClientReference]" +
                " $amount \n" +
                "merchant reference: $merchantReference"

        SetPaymentMethod.valueOf(paymentIn.paymentMethod, paymentOut).set()
        paymentOut.payee = paymentIn.payer
        paymentOut.reconciled = false
        paymentOut.amount =  amount
        paymentOut.createdBy = context.localObject(systemUserService.currentUser)
        if (invoices) {
            invoices.each { invoiceDto ->
                PaymentOutLine line = context.newObject(PaymentOutLine)
                Invoice invoice = invoiceDao.getById(context, invoiceDto.id) as Invoice
                line.accountIn = invoice.debtorsAccount
                line.invoice = invoice
                line.paymentOut = paymentOut
                line.amount = Money.valueOf(invoiceDto.amount)
            }
        } else {
            paymentIn.paymentInLines.each { inLine ->
                PaymentOutLine line = context.newObject(PaymentOutLine)
                line.accountIn = inLine.invoice.debtorsAccount
                line.invoice = inLine.invoice
                line.paymentOut = paymentOut
                line.amount = inLine.amount
            }
        }

        if (!sessionAttributes.authorised) {
            String errorMessage = "Refund transaction is failed: ${sessionAttributes.statusText?:sessionAttributes.errorMessage}"

            paymentOut.status = FAILED
            paymentOut.paymentDate = LocalDate.now()
            paymentOut.privateNotes += StringUtils.LF + errorMessage
            context.commitChanges()

            validator.throwClientErrorException(null, errorMessage)
        } else {
            paymentOut.status = SUCCESS
            paymentOut.gatewayReference = sessionAttributes.transactionId
            paymentOut.paymentDate = sessionAttributes.paymentDate
        }
        return paymentOut
    }
}
