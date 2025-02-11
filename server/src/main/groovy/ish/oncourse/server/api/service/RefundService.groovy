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
import ish.common.types.PaymentSource
import ish.common.types.PaymentStatus
import ish.math.Money
import ish.oncourse.entity.services.SetPaymentMethod
import ish.oncourse.server.cayenne.Account
import ish.oncourse.server.cayenne.Invoice
import ish.oncourse.server.cayenne.InvoiceLine
import ish.oncourse.server.cayenne.InvoiceNoteRelation
import ish.oncourse.server.cayenne.PaymentIn
import ish.oncourse.server.cayenne.PaymentMethod
import ish.oncourse.server.cayenne.Tax
import ish.oncourse.server.users.SystemUserService
import ish.util.AccountUtil
import ish.util.InvoiceUtil
import ish.util.NoteUtil
import ish.util.PaymentMethodUtil
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.validation.ValidationResult
import org.apache.commons.lang3.StringUtils

import java.time.LocalDate

class RefundService {

    @Inject
    SystemUserService userService

    ValidationResult refundInvoiceLine(InvoiceLine invoiceLineToRefund, Money cancellationFee, Tax tax, Account account, String entityName) {

        ObjectContext context = invoiceLineToRefund.objectContext

        Invoice invoiceToRefund = invoiceLineToRefund.invoice

        Invoice refundInvoice = context.newObject(Invoice)

        refundInvoice.billToAddress  = invoiceToRefund.billToAddress
        refundInvoice.description = "Refund for $entityName : $invoiceLineToRefund.title"
        NoteUtil.copyNotes(invoiceToRefund, refundInvoice, InvoiceNoteRelation)
        refundInvoice.publicNotes = invoiceToRefund.publicNotes
        refundInvoice.shippingAddress = invoiceToRefund.shippingAddress
        refundInvoice.invoiceDate = LocalDate.now()
        refundInvoice.dateDue = LocalDate.now()
        refundInvoice.debtorsAccount = invoiceToRefund.debtorsAccount
        refundInvoice.contact = invoiceToRefund.contact

        ValidationResult result = refund(invoiceLineToRefund, cancellationFee, tax, account, refundInvoice)

        InvoiceUtil.updateAmountOwing(invoiceToRefund)
        InvoiceUtil.updateAmountOwing(refundInvoice)

        PaymentIn contraPayment = context.newObject(PaymentIn)
        contraPayment.payer = invoiceToRefund.contact

        SetPaymentMethod.valueOf(PaymentMethodUtil.getCONTRAPaymentMethods(context, PaymentMethod), contraPayment).set()
        contraPayment.source = PaymentSource.SOURCE_ONCOURSE
        contraPayment.amount = Money.ZERO()
        contraPayment.status = PaymentStatus.SUCCESS
        contraPayment.paymentDate = LocalDate.now()
        contraPayment.createdBy = contraPayment.context.localObject(userService.currentUser)
        contraPayment.administrationCentre = contraPayment.context.localObject(userService.currentUser.defaultAdministrationCentre)
        contraPayment.accountIn = AccountUtil.getDefaultBankAccount(contraPayment.context, Account)
        contraPayment.privateNotes = ''
        contraPayment.confirmationStatus = ConfirmationStatus.DO_NOT_SEND
        contraPayment.source = PaymentSource.SOURCE_ONCOURSE

        // add all owing invoices for payer to allocation list to contra them with
        // credit amount just created from refund
        List<Invoice> invoices = []
        invoices += invoiceToRefund.contact.owingInvoices

        // make sure the refunded and refund invoice are in the list and this invoice is first
        // as this required for #18615 to balance the refunded invoice in first order:
        invoices.remove(invoiceToRefund)
        invoices.add(0, invoiceToRefund)

        if (!(refundInvoice in invoices)) {
            invoices << refundInvoice
        }

        // reverse GL records
        InvoiceUtil.allocateMoneyToInvoices(Money.ZERO(), invoices, contraPayment, [])

        return result
    }

    ValidationResult refund(InvoiceLine invoiceLineToRefund, Money cancellationFee, Tax tax, Account account, Invoice refundInvoice) {
        ObjectContext context = invoiceLineToRefund.context

        InvoiceLine refundInvoiceLine = context.newObject(InvoiceLine)

        refundInvoiceLine.account = invoiceLineToRefund.account
        refundInvoiceLine.prepaidFeesAccount = invoiceLineToRefund.prepaidFeesAccount
        refundInvoiceLine.description  =  "Cancellation of ${invoiceLineToRefund.enrolment  ? 'enrolment' : 'product'}: $invoiceLineToRefund.description"
        refundInvoiceLine.invoice = refundInvoice
        refundInvoiceLine.sortOrder = invoiceLineToRefund.sortOrder
        refundInvoiceLine.title = invoiceLineToRefund.title
        refundInvoiceLine.unit = invoiceLineToRefund.unit
        refundInvoiceLine.quantity = new BigDecimal("1.00")
        refundInvoiceLine.tax = invoiceLineToRefund.tax
        refundInvoiceLine.courseClass = invoiceLineToRefund.courseClass

        refundInvoiceLine.priceEachExTax = invoiceLineToRefund.priceEachExTax.negate()
        refundInvoiceLine.discountEachExTax = invoiceLineToRefund.discountEachExTax.negate()
        // when we create refund we should not recalculate tax each calculated on payment creation,
        // because of any applied on fly tax adjustment this value can be not the same
        refundInvoiceLine.taxEach  = invoiceLineToRefund.taxEach.negate()

        if (cancellationFee != null && Money.ZERO().isLessThan(cancellationFee)) {
            InvoiceLine cancelaltionFeeLine = context.newObject(InvoiceLine)

            cancelaltionFeeLine.account = account // specified refund account
            cancelaltionFeeLine.prepaidFeesAccount = invoiceLineToRefund.prepaidFeesAccount

            // this gives the description for class (including course name etc)
            cancelaltionFeeLine.description = "Cancellation of ${invoiceLineToRefund.enrolment  ? 'enrolment' : 'product'}: $invoiceLineToRefund.description"
            cancelaltionFeeLine.invoice = refundInvoice
            cancelaltionFeeLine.sortOrder = invoiceLineToRefund.sortOrder
            cancelaltionFeeLine.title = 'Cancellation fee'
            cancelaltionFeeLine.unit = StringUtils.EMPTY
            cancelaltionFeeLine.quantity = new BigDecimal("1.00")
            if (tax != null) {
                cancelaltionFeeLine.tax = context.localObject(tax)
            } else {
                throw new RuntimeException("no tax set")
            }

            // calculate the refund amount based on this enrolment paid price, so include the discount:
            cancelaltionFeeLine.priceEachExTax = cancellationFee
            cancelaltionFeeLine.discountEachExTax = Money.ZERO()

            cancelaltionFeeLine.taxEach = InvoiceUtil.calculateTaxEachForInvoiceLine(cancellationFee, Money.ZERO(), tax.rate,  Money.ZERO())
        }

        ValidationResult result = new ValidationResult()
        refundInvoice.validateForSave(result)
        refundInvoiceLine.validateForSave(result)

        return result
    }
}
