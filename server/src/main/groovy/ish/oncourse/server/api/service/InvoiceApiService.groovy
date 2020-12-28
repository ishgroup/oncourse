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
import ish.common.types.ConfirmationStatus
import static ish.common.types.PaymentSource.SOURCE_ONCOURSE
import ish.common.types.PaymentStatus
import ish.math.Money
import ish.oncourse.DefaultAccount
import ish.oncourse.aql.AqlService
import ish.oncourse.cayenne.PaymentLineInterface
import ish.oncourse.function.GetContactFullName
import ish.oncourse.server.PreferenceController
import ish.oncourse.server.api.dao.AccountDao
import ish.oncourse.server.api.dao.ContactDao
import ish.oncourse.server.api.dao.CourseClassDao
import ish.oncourse.server.api.dao.EnrolmentDao
import ish.oncourse.server.api.dao.InvoiceDao
import ish.oncourse.server.api.dao.InvoiceDueDateDao
import ish.oncourse.server.api.dao.InvoiceLineDao
import ish.oncourse.server.api.dao.PaymentInDao
import ish.oncourse.server.api.dao.TaxDao
import static ish.oncourse.server.api.function.EntityFunctions.addAqlExp
import static ish.oncourse.server.api.function.MoneyFunctions.toMoneyValue
import static ish.oncourse.server.api.v1.function.InvoiceFunctions.toRestInvoiceLineModel
import static ish.oncourse.server.api.v1.function.InvoiceFunctions.toRestPaymentPlan
import ish.oncourse.server.api.v1.model.InvoiceDTO
import ish.oncourse.server.api.v1.model.InvoiceInvoiceLineDTO
import ish.oncourse.server.api.v1.model.InvoicePaymentPlanDTO
import ish.oncourse.server.cayenne.Invoice
import ish.oncourse.server.cayenne.InvoiceDueDate
import ish.oncourse.server.cayenne.InvoiceLine
import ish.oncourse.server.cayenne.PaymentIn
import ish.oncourse.server.cayenne.PaymentMethod
import ish.oncourse.server.cayenne.SystemUser
import ish.oncourse.server.duplicate.DuplicateInvoiceService
import ish.oncourse.server.services.IAutoIncrementService
import ish.oncourse.server.services.TransactionLockedService
import ish.oncourse.server.users.SystemUserService
import ish.util.InvoiceUtil
import static ish.util.InvoiceUtil.calculateTaxEachForInvoiceLine
import static ish.util.LocalDateUtils.dateToTimeValue
import ish.util.MoneyUtil
import ish.util.PaymentMethodUtil
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.ObjectSelect
import org.apache.commons.lang3.StringUtils
import static org.apache.commons.lang3.StringUtils.isBlank
import static org.apache.commons.lang3.StringUtils.isNotBlank
import static org.apache.commons.lang3.StringUtils.trimToNull

import java.time.LocalDate

class InvoiceApiService extends EntityApiService<InvoiceDTO, Invoice, InvoiceDao> {

    @Inject
    private SystemUserService systemUserService

    @Inject
    private IAutoIncrementService autoIncrementService

    @Inject
    private PreferenceController preferenceController

    @Inject
    private TransactionLockedService transactionLockedService

    @Inject
    private AccountDao accountDao

    @Inject
    private ContactDao contactDao

    @Inject
    private InvoiceDueDateDao invoiceDueDateDao

    @Inject
    private InvoiceLineDao invoiceLineDao

    @Inject
    private CourseClassDao courseClassDao

    @Inject
    private EnrolmentDao enrolmentDao

    @Inject
    private TaxDao taxDao

    @Inject
    private PaymentInDao paymentInDao

    @Inject
    private DuplicateInvoiceService duplicateInvoiceService

    @Inject
    private AqlService aql

    @Override
    Class<Invoice> getPersistentClass() {
        return Invoice
    }

    @Override
    InvoiceDTO toRestModel(Invoice invoice) {
        new InvoiceDTO().with { invoiceDTO ->
            invoiceDTO.id = invoice.id
            invoiceDTO.contactId = invoice.contact.id
            invoiceDTO.contactName = invoice.contact.with { GetContactFullName.valueOf(it, true).get() }
            invoiceDTO.customerReference = invoice.customerReference
            invoiceDTO.invoiceNumber = invoice.invoiceNumber
            invoiceDTO.billToAddress = invoice.billToAddress
            invoiceDTO.shippingAddress = invoice.shippingAddress
            invoiceDTO.invoiceDate = invoice.invoiceDate
            invoiceDTO.dateDue = invoice.dateDue
            invoiceDTO.overdue = invoice.overdue?.toBigDecimal()
            invoiceDTO.invoiceLines = invoice.invoiceLines.collect { toRestInvoiceLineModel(it) }
            invoiceDTO.total = invoice.totalIncTax?.toBigDecimal()
            invoiceDTO.amountOwing = invoice.amountOwing?.toBigDecimal()
            invoiceDTO.publicNotes = invoice.publicNotes
            invoiceDTO.paymentPlans.addAll([toRestPaymentPlan(invoice)])
            invoiceDTO.paymentPlans.addAll(invoice.invoiceDueDates.collect { toRestPaymentPlan(it) }.sort { it.date })
            invoiceDTO.paymentPlans.addAll(invoice.paymentInLines.collect { toRestPaymentPlan(it) }.sort { it.date })
            invoiceDTO.paymentPlans.addAll(invoice.paymentOutLines.collect { toRestPaymentPlan(it) }.sort { it.date })

            invoiceDTO.source = invoice.source.displayName
            invoiceDTO.createdByUser = invoice.createdByUser?.login
            invoiceDTO.sendEmail = invoice.confirmationStatus == ConfirmationStatus.NOT_SENT
            invoiceDTO.createdOn = dateToTimeValue(invoice.createdOn)
            invoiceDTO.modifiedOn = dateToTimeValue(invoice.modifiedOn)
            invoiceDTO
        }
    }

    @Override
    Invoice toCayenneModel(InvoiceDTO invoiceDTO, Invoice invoice) {
        if (invoice.newRecord) {
            invoice.contact = contactDao.getById(invoice.context, invoiceDTO.contactId)
            invoice.invoiceNumber = autoIncrementService.nextInvoiceNumber
            invoice.invoiceDate = invoiceDTO.invoiceDate
            invoice.source = SOURCE_ONCOURSE
            invoice.createdByUser = invoice.context.localObject(systemUserService.currentUser)
            updateInvoiceLines(invoice, invoiceDTO.invoiceLines)
        }

        invoice.customerReference = trimToNull(invoiceDTO.customerReference)
        invoice.billToAddress = trimToNull(invoiceDTO.billToAddress)
        invoice.shippingAddress = trimToNull(invoiceDTO.shippingAddress)
        invoice.publicNotes = trimToNull(invoiceDTO.publicNotes)
        invoice.dateDue = invoiceDTO.dateDue
        invoice.updateAmountOwing()
        updateInvoiceDueDates(invoice, invoiceDTO.paymentPlans)
        invoice.confirmationStatus = invoiceDTO.sendEmail ? ConfirmationStatus.NOT_SENT : ConfirmationStatus.DO_NOT_SEND
        invoice
    }

    @Override
    void validateModelBeforeSave(InvoiceDTO invoiceDTO, ObjectContext context, Long id) {
        Invoice invoice = id == null ? null : entityDao.getById(context, id)
        if (!invoiceDTO.contactId) {
            validator.throwClientErrorException(id, 'contact', 'Contact id is required.')
        } else if (!contactDao.getById(context, invoiceDTO.contactId)) {
            validator.throwClientErrorException(id, 'contact', "Contact with id=$invoiceDTO.contactId not found.")
        }

        if (isNotBlank(invoiceDTO.customerReference) && trimToNull(invoiceDTO.customerReference).size() > 5000) {
            validator.throwClientErrorException(id, 'customerReference', 'Customer reference cannot be more than 5000 chars.')
        }
        if (isNotBlank(invoiceDTO.billToAddress) && trimToNull(invoiceDTO.billToAddress).size() > 32000) {
            validator.throwClientErrorException(id, 'billToAddress', 'Billing address cannot be more than 32000 chars.')
        }
        if (isNotBlank(invoiceDTO.shippingAddress) && trimToNull(invoiceDTO.shippingAddress).size() > 32000) {
            validator.throwClientErrorException(id, 'shippingAddress', 'Shipping address cannot be more than 32000 chars.')
        }
        if (!invoiceDTO.invoiceDate) {
            validator.throwClientErrorException(id, 'invoiceDate', 'Invoice date is required.')
        } else if (invoiceDTO.invoiceDate <= transactionLockedService.transactionLocked && (id == null ||
                (id != null && invoice.invoiceDate != invoiceDTO.invoiceDate))) {
            validator.throwClientErrorException(id, 'invoiceDate', "Date is locked. Invoice date must be after $transactionLockedService.transactionLocked")
        }
        if (!invoiceDTO.dateDue) {
            validator.throwClientErrorException(id, 'dateDue', 'Date due is required.')
        } else if (invoiceDTO.dateDue < invoiceDTO.invoiceDate) {
            validator.throwClientErrorException(id, 'dateDue', 'Date due must be after invoice date or have the same date.')
        }

        if (isNotBlank(invoiceDTO.publicNotes) && trimToNull(invoiceDTO.publicNotes).size() > 32000) {
            validator.throwClientErrorException(id, 'publicNotes', 'Public notes cannot be more than 32000 chars.')
        }

        if (id == null) {
            if (invoiceDTO.invoiceLines.empty) {
                validator.throwClientErrorException(id, 'invoiceLines', 'At least 1 invoice line required.')
            } else {
                invoiceDTO.invoiceLines.eachWithIndex { InvoiceInvoiceLineDTO iil, int idx ->
                    if (isBlank(iil.title)) {
                        validator.throwClientErrorException(id, "invoiceLines[$idx].title", 'Invoice line title is required.')
                    } else if (trimToNull(iil.title).length() > 200) {
                        validator.throwClientErrorException(id, "invoiceLines[$idx].title", 'Invoice line title cannot be more than 200 chars.')
                    }

                    if (iil.quantity == null) {
                        validator.throwClientErrorException(id, "invoiceLines[$idx].quantity", 'Invoice line quantity is required.')
                    }


                    if (iil.priceEachExTax == null) {
                        validator.throwClientErrorException(id, "invoiceLines[$idx].priceEachExTax", 'Invoice line price each ex Tax is required.')
                    }

                    if (iil.discountEachExTax == null) {
                        validator.throwClientErrorException(id, "invoiceLines[$idx].discountEachExTax", 'Invoice line discount each ex Tax is required.')
                    } else {
                        Money discountTotalExTax = toMoneyValue(iil.discountEachExTax).multiply(iil.quantity)
                        Money priceTotalExTax = toMoneyValue(iil.priceEachExTax).multiply(iil.quantity)

                        if (!discountTotalExTax.zero) {
                            if (priceTotalExTax.negative ? discountTotalExTax < priceTotalExTax : discountTotalExTax > priceTotalExTax) {
                                validator.throwClientErrorException(id, "invoiceLines[$idx].discountEachExTax", 'Discount is too large.')
                            }
                        }
                    }

                    if (!iil.incomeAccountId) {
                        validator.throwClientErrorException(id, "invoiceLines[$idx].incomeAccountId", 'Income account id is required.')
                    } else if (!accountDao.getById(context, iil.incomeAccountId)) {
                        validator.throwClientErrorException(id, "invoiceLines[$idx].incomeAccountId", "Income account with id=$iil.incomeAccountId not found.")
                    }

                    if (!iil.taxId) {
                        validator.throwClientErrorException(id, "invoiceLines[$idx].taxId", 'Tax id is required.')
                    } else if (!taxDao.getById(context, iil.taxId)) {
                        validator.throwClientErrorException(id, "invoiceLines[$idx].taxId", "Tax with id=$iil.taxId not found.")
                    }

                    if (iil.cosAccountId && !accountDao.getById(context, iil.cosAccountId)) {
                        validator.throwClientErrorException(id, "invoiceLines[$idx].cosAccountId", "COS account with id=$iil.cosAccountId not found.")
                    }

                    if (iil.courseClassId && !courseClassDao.getById(context, iil.courseClassId)) {
                        validator.throwClientErrorException(id, "invoiceLines[$idx].courseClassId", "COS account with id=$iil.courseClassId not found.")
                    }

                    if (iil.enrolmentId && !enrolmentDao.getById(context, iil.enrolmentId)) {
                        validator.throwClientErrorException(id, "invoiceLines[$idx].enrolmentId", "COS account with id=$iil.enrolmentId not found.")
                    }
                }
            }
        }

        if (!invoiceDTO.paymentPlans.empty) {
            invoiceDTO.paymentPlans.stream()
                    .filter({ paymentPlan -> paymentPlan.type.equals("Payment due") })
                    .eachWithIndex { InvoicePaymentPlanDTO ipp, int idx ->
                        if (ipp.entityName == InvoiceDueDate.simpleName && ipp.id != null && !invoiceDueDateDao.getById(context, ipp.id)) {
                            validator.throwClientErrorException(id, 'contact', "Payment plan with id=$ipp.id not found.")
                        }

                        if (!ipp.amount) {
                            validator.throwClientErrorException(id, "paymentPlans[$idx].amount", 'Payment plan amount is required.')
                        } else if (ipp.amount <= 0) {
                            validator.throwClientErrorException(id, "paymentPlans[$idx].amount", 'Payment plan amount must be greater than zero.')
                        }

                        if (!ipp.date) {
                            validator.throwClientErrorException(id, "paymentPlans[$idx].date", 'Payment plan date is required.')
                        }
            }

            // If payment plan has any paymentDue, check - does total equal paymentDue sum?
            if (invoiceDTO.paymentPlans.stream().anyMatch({ value -> value.entityName.equals(InvoiceDueDate.simpleName) })) {
                BigDecimal totalOverdue = invoiceDTO.paymentPlans.stream()
                        .filter({ value -> (value.entityName == InvoiceDueDate.simpleName) })
                        .map({ value -> value.amount })
                        .reduce(new BigDecimal(0), { BigDecimal accumulator, BigDecimal value -> accumulator.add(value) })

                BigDecimal totalIncTax = id == null
                        ? invoiceDTO.invoiceLines
                        .collect {il -> (il.priceEachExTax - il.discountEachExTax + il.taxEach) * il.quantity }
                        .inject { a,d -> a.add(d)}
                        : invoice.totalIncTax.toBigDecimal()

                if (!(totalOverdue.round(2).compareTo(totalIncTax.round(2)) == 0)) {
                    validator.throwClientErrorException(id, 'paymentPlans', "The payment plan adds up to ${toMoneyValue(totalOverdue)} but the invoice total is ${toMoneyValue(totalIncTax)}. These must match before you can save this invoice.")
                }
            }
        }
    }

    @Override
    void validateModelBeforeRemove(Invoice cayenneModel) {
        validator.throwClientErrorException(cayenneModel.id, 'id', 'Invoice cannot be deleted.')
    }

    private void updateInvoiceLines(Invoice cayenneModel, List<InvoiceInvoiceLineDTO> invoiceLines) {
        invoiceLines.each { il ->
            InvoiceLine iLine = invoiceLineDao.newObject(cayenneModel.context)
            iLine.title = trimToNull(il.title)
            iLine.quantity = il.quantity
            iLine.unit = trimToNull(il.unit)
            iLine.account = accountDao.getById(cayenneModel.context, il.incomeAccountId)
            if (il.cosAccountId) {
                iLine.cosAccount = accountDao.getById(cayenneModel.context, il.cosAccountId)
            }
            iLine.priceEachExTax = toMoneyValue(il.priceEachExTax)
            iLine.discountEachExTax = toMoneyValue(il.discountEachExTax)
            iLine.description = trimToNull(il.description)
            iLine.tax = taxDao.getById(cayenneModel.context, il.taxId)
            if (il.courseClassId) {
                iLine.courseClass = courseClassDao.getById(cayenneModel.context, il.courseClassId)
                if (il.enrolmentId) {
                    iLine.enrolment = enrolmentDao.getById(cayenneModel.context, il.enrolmentId)
                }
            }
            iLine.prepaidFeesRemaining = il.courseClassId || il.enrolmentId ? iLine.priceEachExTax.subtract(iLine.discountEachExTax).multiply(iLine.quantity) : Money.ZERO
            iLine.prepaidFeesAccount = accountDao.getById(cayenneModel.context, preferenceController.getDefaultAccountId(DefaultAccount.PREPAID_FEES.preferenceName))

            Money totalEachExTax = toMoneyValue(il.priceEachExTax - il.discountEachExTax)
            Money totalEachIncTax = totalEachExTax.add(toMoneyValue(il.taxEach))

            Money taxAdjustment = MoneyUtil.calculateTaxAdjustment(totalEachIncTax, totalEachExTax,  iLine.tax.rate)

            iLine.taxEach = calculateTaxEachForInvoiceLine(iLine.priceEachExTax, iLine.discountEachExTax, iLine.tax.rate, taxAdjustment)
            iLine.invoice = cayenneModel
        }
    }

    void updateInvoiceDueDates(Invoice invoice, List<InvoicePaymentPlanDTO> paymentPlanDTOs) {
        List<InvoicePaymentPlanDTO> invoiceDueDates = paymentPlanDTOs.findAll { it.entityName == InvoiceDueDate.simpleName }
        List<Long> invoiceDueDatesToSave = invoiceDueDates*.id.findAll()
        invoice.context.deleteObjects(invoice.invoiceDueDates.findAll { !invoiceDueDatesToSave.contains(it.id) })
        invoiceDueDates.each { idd ->
            InvoiceDueDate dbInvoiceDueDate = idd.id != null ? invoice.invoiceDueDates.find { it.id == idd.id } :
                    invoiceDueDateDao.newObject(invoice.context).with { i ->
                        i.invoice = invoice
                        i
                    }

            dbInvoiceDueDate.amount = toMoneyValue(idd.amount)
            dbInvoiceDueDate.dueDate = idd.date
        }
        invoice.updateDateDue()
        invoice.updateOverdue()
    }

    InvoiceDTO duplicateInvoice(Long id) {
        def context = cayenneService.newContext
        Invoice invoiceToRefund = ObjectSelect.query(Invoice.class)
                .where(Invoice.ID.eq(id))
                .selectOne(context)


        def reversedInvoice = duplicateInvoiceService.duplicateAndReverseInvoice(invoiceToRefund)
        reversedInvoice.setInvoiceNumber(null)
        return toRestModel(reversedInvoice)
    }

    void contraInvoice(Long id, List<Long> invoicesToPay) {
        ObjectContext context = cayenneService.newContext

        Invoice currentInvoice = getEntityAndValidateExistence(context, id)
        List<Invoice> listOfInvoices = invoicesToPay.collect { getEntityAndValidateExistence(context, it) }

        if (!currentInvoice.amountOwing.isLessThan(Money.ZERO)) {
            validator.throwClientErrorException(id, 'id', "Invoice with id=$id is not a credit note.")
        }
        if (listOfInvoices.empty) {
            validator.throwClientErrorException(id, 'id', "There are no invoices selected.")
        } else {
            listOfInvoices.find { !it.amountOwing.isGreaterThan(Money.ZERO) }
                    ?.with { validator.throwClientErrorException(it.id, 'id', "Invoice with id=$id has no amount owing.") }
        }

        PaymentMethod method = PaymentMethodUtil.getCONTRAPaymentMethods(context, PaymentMethod)
        List<PaymentLineInterface> paymentInLines = []

        paymentInDao.newObject(context).with { PaymentIn pIn ->
            SystemUser currentUser = context.localObject(systemUserService.currentUser)

            pIn.privateNotes = StringUtils.EMPTY
            pIn.reconciled = false
            pIn.source = SOURCE_ONCOURSE
            pIn.confirmationStatus = ConfirmationStatus.DO_NOT_SEND
            pIn.administrationCentre = currentUser.defaultAdministrationCentre
            pIn.createdBy = currentUser
            pIn.paymentDate = LocalDate.now()
            pIn.status = PaymentStatus.SUCCESS
            pIn.payer = currentInvoice.contact
            pIn.paymentMethod = method
            pIn.account = method.account
            pIn.undepositedFundsAccount = method.undepositedFundsAccount
            listOfInvoices.each { invoiceToPay ->
                InvoiceUtil.invoiceAllocate(invoiceToPay, currentInvoice.amountOwing.negate().subtract(pIn.amount), pIn, paymentInLines)
                invoiceToPay.updateAmountOwing()
                pIn.amount = pIn.paymentInLines.collect { it.amount }.inject { a, b -> a.add(b) }
            }
            InvoiceUtil.invoiceAllocate(currentInvoice, pIn.amount.negate(), pIn, paymentInLines)
            pIn.amount = pIn.paymentInLines.collect { it.amount }.inject { a, b -> a.add(b) }

            if (!pIn.amount.isZero()) {
                validator.throwClientErrorException(id, 'id', "Error during contra operation: invoices are not matching.")
            }
            pIn
        }

        context.commitChanges()
    }

    List<InvoiceDTO> search(String search) {

        if (trimToNull(search) == null) {
            validator.throwClientErrorException(null, 'search', "Search is mandatory")
        }
        ObjectContext context = cayenneService.newContext
        ObjectSelect<Invoice> query = ObjectSelect.query(Invoice)
        query = addAqlExp(search, query.getEntityType(), context, query, aql)
        return query.select(context).collect { Invoice invoice ->
            InvoiceDTO dto = new InvoiceDTO()
            dto.id = invoice.id
            dto.invoiceNumber = invoice.invoiceNumber
            dto.invoiceDate = invoice.invoiceDate
            dto.dateDue = invoice.dateDue
            dto.amountOwing = invoice.amountOwing.toBigDecimal()
            dto.paymentPlans.addAll([toRestPaymentPlan(invoice)])
            dto.paymentPlans.addAll(invoice.invoiceDueDates.collect { toRestPaymentPlan(it) }.sort { it.date })
            dto.paymentPlans.addAll(invoice.paymentInLines.collect { toRestPaymentPlan(it) }.sort { it.date })
            dto.paymentPlans.addAll(invoice.paymentOutLines.collect { toRestPaymentPlan(it) }.sort { it.date })
            dto
        }

    }
}
