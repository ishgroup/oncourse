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
import ish.common.types.InvoiceType
import ish.common.types.PaymentStatus
import ish.math.Money
import ish.oncourse.DefaultAccount
import ish.oncourse.aql.AqlService
import ish.oncourse.cayenne.PaymentLineInterface
import ish.oncourse.server.PreferenceController
import ish.oncourse.server.api.dao.*
import ish.oncourse.server.api.v1.model.InvoiceDTO
import ish.oncourse.server.api.v1.model.InvoiceInvoiceLineDTO
import ish.oncourse.server.api.v1.model.InvoicePaymentPlanDTO
import ish.oncourse.server.api.v1.model.InvoiceTypeDTO
import ish.oncourse.server.api.v1.model.LeadInvoiceDTO
import ish.oncourse.server.cayenne.*
import ish.oncourse.server.duplicate.DuplicateInvoiceService
import ish.oncourse.server.services.IAutoIncrementService
import ish.oncourse.server.services.TransactionLockedService
import ish.oncourse.server.users.SystemUserService
import ish.util.InvoiceUtil
import ish.util.MoneyUtil
import ish.util.PaymentMethodUtil
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.ObjectSelect

import java.time.LocalDate

import static ish.common.types.PaymentSource.SOURCE_ONCOURSE
import static ish.oncourse.server.api.function.EntityFunctions.addAqlExp
import static ish.oncourse.server.api.function.MoneyFunctions.toMoneyValue
import static ish.oncourse.server.api.v1.function.InvoiceFunctions.toRestInvoiceLineModel
import static ish.oncourse.server.api.v1.function.InvoiceFunctions.toRestPaymentPlan
import static ish.util.InvoiceUtil.calculateTaxEachForInvoiceLine
import static ish.util.LocalDateUtils.dateToTimeValue
import static org.apache.commons.lang3.StringUtils.EMPTY
import static org.apache.commons.lang3.StringUtils.trimToNull
import static org.apache.commons.lang3.StringUtils.isNotBlank
import static org.apache.commons.lang3.StringUtils.isBlank

class InvoiceApiService extends EntityApiService<InvoiceDTO, AbstractInvoice, InvoiceDao> {

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
    private DiscountDao discountDao

    @Inject
    private InvoiceDueDateDao invoiceDueDateDao

    @Inject
    private InvoiceLineDao invoiceLineDao

    @Inject
    private CourseClassDao courseClassDao

    @Inject
    private EnrolmentDao enrolmentDao

    @Inject
    private LeadDao leadDao

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
    InvoiceDTO toRestModel(AbstractInvoice abstractInvoice) {
        InvoiceDTO dto = new InvoiceDTO().with {invoiceDTO ->
            invoiceDTO.id = abstractInvoice.id
            invoiceDTO.type = InvoiceTypeDTO.values()[0].fromDbType(abstractInvoice.type)
            invoiceDTO.contactId = abstractInvoice.contact.id
            invoiceDTO.leadId = abstractInvoice.lead?.id
            invoiceDTO.contactName = abstractInvoice.contact.with {it.getFullName() }
            invoiceDTO.customerReference = abstractInvoice.customerReference
            invoiceDTO.billToAddress = abstractInvoice.billToAddress
            invoiceDTO.shippingAddress = abstractInvoice.shippingAddress
            invoiceDTO.invoiceDate = abstractInvoice.invoiceDate
            invoiceDTO.dateDue = abstractInvoice.dateDue
            invoiceDTO.overdue = abstractInvoice.overdue?.toBigDecimal()
            invoiceDTO.total = abstractInvoice.totalIncTax?.toBigDecimal()
            invoiceDTO.amountOwing = abstractInvoice.amountOwing?.toBigDecimal()
            invoiceDTO.publicNotes = abstractInvoice.publicNotes
            invoiceDTO.source = abstractInvoice.source.displayName
            invoiceDTO.createdByUser = abstractInvoice.createdByUser?.email
            invoiceDTO.sendEmail = abstractInvoice.confirmationStatus == ConfirmationStatus.NOT_SENT
            invoiceDTO.createdOn = dateToTimeValue(abstractInvoice.createdOn)
            invoiceDTO.modifiedOn = dateToTimeValue(abstractInvoice.modifiedOn)
            invoiceDTO.paymentPlans.addAll([toRestPaymentPlan(abstractInvoice)])
            invoiceDTO
        }
        if (abstractInvoice instanceof Invoice) {
            return toRestInvoiceModel(dto, (Invoice) abstractInvoice)
        } else if (abstractInvoice instanceof Quote) {
            return toRestQuoteModel(dto, (Quote) abstractInvoice)
        }
        return dto
    }
    
    static InvoiceDTO toRestInvoiceModel(InvoiceDTO dto, Invoice invoice) {
        dto.with {invoiceDTO ->
            invoiceDTO.invoiceNumber = invoice.invoiceNumber
            invoiceDTO.paymentPlans.addAll(invoice.invoiceDueDates.collect { toRestPaymentPlan(it) }.sort { it.date })
            invoiceDTO.paymentPlans.addAll(invoice.paymentInLines.collect { toRestPaymentPlan(it) }.sort { it.date })
            invoiceDTO.paymentPlans.addAll(invoice.paymentOutLines.collect { toRestPaymentPlan(it) }.sort { it.date })
            invoiceDTO.invoiceLines = invoice.invoiceLines.collect { toRestInvoiceLineModel(it as InvoiceLine) }
            invoiceDTO        
        }
    }

    static InvoiceDTO toRestQuoteModel(InvoiceDTO dto, Quote quote) {
        dto.with {invoiceDTO ->
            invoiceDTO.title = quote.title
            invoiceDTO.description = quote.description
            invoiceDTO.invoiceLines = quote.quoteLines.collect { toRestInvoiceLineModel(it as QuoteLine) }
            invoiceDTO
        }
    }

    LeadInvoiceDTO toRestLeadInvoice(AbstractInvoice abstractInvoice) {
        new LeadInvoiceDTO().with {leadInvoice ->
            leadInvoice.id = abstractInvoice.id
            leadInvoice.invoiceType = InvoiceTypeDTO.values()[0].fromDbType(abstractInvoice.type)
            leadInvoice.title = abstractInvoice.title
            leadInvoice.invoiceNumber = abstractInvoice.invoiceNumber
            leadInvoice.total = abstractInvoice.total.toBigDecimal()
            leadInvoice
        }
    }

    @Override
    AbstractInvoice toCayenneModel(InvoiceDTO invoiceDTO, AbstractInvoice abstractInvoice) {
        if (abstractInvoice == null) {
            abstractInvoice = entityDao.newObject(cayenneService.newContext, invoiceDTO.type.getDbType())
        }
        if (InvoiceTypeDTO.INVOICE == invoiceDTO.type && InvoiceType.QUOTE == abstractInvoice.type) {
            abstractInvoice = transformQuoteToInvoice(invoiceDTO, abstractInvoice)
        }
        if (abstractInvoice.newRecord || abstractInvoice instanceof Quote) {
            abstractInvoice.type = invoiceDTO.type.getDbType()
            abstractInvoice.lead = leadDao.getById(abstractInvoice.context, invoiceDTO.leadId)
            abstractInvoice.contact = contactDao.getById(abstractInvoice.context, invoiceDTO.contactId)
            abstractInvoice.invoiceDate = invoiceDTO.invoiceDate
            abstractInvoice.source = SOURCE_ONCOURSE
            abstractInvoice.createdByUser = abstractInvoice.context.localObject(systemUserService.currentUser)
            updateInvoiceLines(abstractInvoice, invoiceDTO.invoiceLines)
        }

        abstractInvoice.confirmationStatus = invoiceDTO.sendEmail ? ConfirmationStatus.NOT_SENT : ConfirmationStatus.DO_NOT_SEND
        abstractInvoice.customerReference = trimToNull(invoiceDTO.customerReference)
        abstractInvoice.billToAddress = trimToNull(invoiceDTO.billToAddress)
        abstractInvoice.shippingAddress = trimToNull(invoiceDTO.shippingAddress)
        abstractInvoice.publicNotes = trimToNull(invoiceDTO.publicNotes)
        abstractInvoice.title = trimToNull(invoiceDTO.title)
        abstractInvoice.description = trimToNull(invoiceDTO.description)
        abstractInvoice.dateDue = invoiceDTO.dateDue
        abstractInvoice.updateAmountOwing()
        if (abstractInvoice instanceof Invoice) {
            updateInvoiceDueDates(abstractInvoice as Invoice, invoiceDTO.paymentPlans)
        }
        abstractInvoice
    }

    @Override
    void validateModelBeforeSave(InvoiceDTO invoiceDTO, ObjectContext context, Long id) {
        AbstractInvoice invoice = id == null ? null : entityDao.getById(context, id)

        if (invoice instanceof Invoice && InvoiceTypeDTO.QUOTE == invoiceDTO.type) {
            validator.throwClientErrorException(id, 'type', 'Impossible to transform an invoice to a quote.')
        }

        if (!invoiceDTO.contactId) {
            validator.throwClientErrorException(id, 'contact', 'Contact id is required.')
        } else if (!contactDao.getById(context, invoiceDTO.contactId)) {
            validator.throwClientErrorException(id, 'contact', "Contact with id=$invoiceDTO.contactId not found.")
        }

        if (invoiceDTO.leadId && !leadDao.getById(context, invoiceDTO.leadId)) {
            validator.throwClientErrorException(id, 'lead', "Lead with id=$invoiceDTO.leadId not found.")
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

                    if (iil.discountId && !discountDao.getById(context, iil.discountId)) {
                        validator.throwClientErrorException(id, "invoiceLines[$idx].discountId", "Discount with name=$iil.discountName not found.")
                    }

                    if (iil.courseClassId && !courseClassDao.getById(context, iil.courseClassId)) {
                        validator.throwClientErrorException(id, "invoiceLines[$idx].courseClassId", "Class with id=$iil.courseClassId not found.")
                    }

                    if (iil.enrolmentId && !enrolmentDao.getById(context, iil.enrolmentId)) {
                        validator.throwClientErrorException(id, "invoiceLines[$idx].enrolmentId", "Enrolment with id=$iil.enrolmentId not found.")
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
    void validateModelBeforeRemove(AbstractInvoice cayenneModel) {
        if (cayenneModel instanceof Invoice) {
            validator.throwClientErrorException(cayenneModel.id, 'id', 'Invoice cannot be deleted.')
        }
    }

    private AbstractInvoice transformQuoteToInvoice(InvoiceDTO invoiceDTO, AbstractInvoice abstractInvoice) {
        abstractInvoice.type = InvoiceType.INVOICE //set a new type
        abstractInvoice.context.deleteObjects(abstractInvoice.lines) // need to create new lines, otherwise account transactions won't be created
        abstractInvoice.context.commitChanges() //commit

        abstractInvoice = getEntityAndValidateExistence(abstractInvoice.context, abstractInvoice.id) // take updated
        abstractInvoice.invoiceNumber = autoIncrementService.nextInvoiceNumber
        updateInvoiceLines(abstractInvoice, invoiceDTO.invoiceLines)
        abstractInvoice
    }

    private void updateInvoiceLines(AbstractInvoice cayenneModel, List<InvoiceInvoiceLineDTO> invoiceLines) {
        invoiceLines.each { il ->
            AbstractInvoiceLine iLine = invoiceLineDao.getById(cayenneModel.context, il.id, cayenneModel.linePersistentClass)
            if (!iLine) {
                iLine = cayenneModel.context.newObject(cayenneModel.linePersistentClass)
            }
            iLine.title = trimToNull(il.title)
            iLine.quantity = il.quantity
            iLine.unit = trimToNull(il.unit)
            iLine.account = accountDao.getById(cayenneModel.context, il.incomeAccountId)
            if (il.discountId) {
                Discount discount = discountDao.getById(cayenneModel.context, il.discountId)
                iLine.cosAccount = discount.cosAccount

                cayenneModel.context.deleteObjects(iLine.invoiceLineDiscounts.findAll {it.discount.id != discount.id })
                if (iLine.invoiceLineDiscounts.find {it.discount.id == discount.id } == null) {
                    cayenneModel.context.newObject(InvoiceLineDiscount).with { ild ->
                        ild.discount = discount
                        ild.invoiceLine = iLine
                        ild
                    }
                }
            }
            iLine.tax = taxDao.getById(cayenneModel.context, il.taxId)
            iLine.priceEachExTax = toMoneyValue(il.priceEachExTax)
            iLine.discountEachExTax = toMoneyValue(il.discountEachExTax)
            iLine.description = trimToNull(il.description)
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

        Invoice currentInvoice = getEntityAndValidateExistence(context, id) as Invoice
        List<Invoice> listOfInvoices = invoicesToPay.collect { getEntityAndValidateExistence(context, it) as Invoice }

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

            pIn.privateNotes = EMPTY
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
            dto.overdue = invoice.overdue.toBigDecimal()
            dto.amountOwing = invoice.amountOwing.toBigDecimal()
            dto.paymentPlans.addAll([toRestPaymentPlan(invoice)])
            dto.paymentPlans.addAll(invoice.invoiceDueDates.collect { toRestPaymentPlan(it) }.sort { it.date })
            dto.paymentPlans.addAll(invoice.paymentInLines.collect { toRestPaymentPlan(it) }.sort { it.date })
            dto.paymentPlans.addAll(invoice.paymentOutLines.collect { toRestPaymentPlan(it) }.sort { it.date })
            dto
        }

    }
}
