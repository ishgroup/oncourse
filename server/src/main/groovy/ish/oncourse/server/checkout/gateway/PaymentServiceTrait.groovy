/*
 * Copyright ish group pty ltd 2023.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.checkout.gateway

import ish.common.types.PaymentStatus
import ish.oncourse.server.api.checkout.Checkout
import ish.oncourse.server.api.v1.model.CheckoutArticleDTO
import ish.oncourse.server.api.v1.model.CheckoutCCResponseDTO
import ish.oncourse.server.api.v1.model.CheckoutEnrolmentDTO
import ish.oncourse.server.api.v1.model.CheckoutMembershipDTO
import ish.oncourse.server.api.v1.model.CheckoutResponseDTO
import ish.oncourse.server.api.v1.model.CheckoutValidationErrorDTO
import ish.oncourse.server.api.v1.model.CheckoutVoucherDTO
import ish.oncourse.server.api.v1.model.InvoiceDTO
import ish.oncourse.server.api.v1.model.AbstractInvoiceLineDTO
import ish.oncourse.server.cayenne.Article
import ish.oncourse.server.cayenne.Membership
import ish.oncourse.server.cayenne.ProductItem
import ish.oncourse.server.cayenne.Voucher
import ish.common.checkout.gateway.PaymentGatewayError
import ish.util.LocalDateUtils
import org.apache.cayenne.validation.ValidationException
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

import javax.ws.rs.ClientErrorException
import javax.ws.rs.core.Response

import static ish.common.types.ConfirmationStatus.DO_NOT_SEND
import static ish.common.types.ConfirmationStatus.NOT_SENT

trait PaymentServiceTrait {

    private static final Logger logger = LogManager.getLogger(PaymentServiceTrait)

    CheckoutCCResponseDTO succeedPayment(Checkout checkout, Boolean sendInvoice) {
        checkout.paymentIn.status = PaymentStatus.SUCCESS
        checkout.paymentIn.privateNotes += ' Payment successful.'
        checkout.paymentIn.confirmationStatus = sendInvoice ? NOT_SENT : DO_NOT_SEND
        checkout.paymentIn.paymentInLines.each {  line  ->
            line.invoice.updateAmountOwing()
            line.invoice.updateDateDue()
            line.invoice.updateOverdue()
        }
        saveCheckout(checkout)

        return new CheckoutCCResponseDTO().with {
            it.paymentId = checkout.paymentIn.id
            it.invoiceId = checkout.invoice?.id
            it
        }
    }

    void saveCheckout(Checkout checkout) {
        try {
            checkout.context.commitChanges()
        } catch (ValidationException e) {
            List<CheckoutValidationErrorDTO> errors = e.validationResult.failures.collect { new CheckoutValidationErrorDTO( error: it.description) }
            String validationMessages = errors*.error.join('\n')
            logger.error(validationMessages)
            logger.catching(e)
            handleError(PaymentGatewayError.VALIDATION_ERROR.errorNumber, errors)
        } catch (Exception e) {
            logger.error('Unexpected error')
            logger.catching(e)
            handleError(PaymentGatewayError.VALIDATION_ERROR.errorNumber, [new CheckoutValidationErrorDTO(error: "Sorry, something unexpected happened. Please contact ish support team")])
        }
    }

    void fillResponse(CheckoutResponseDTO dtoResponse, Checkout checkout) {
        dtoResponse.paymentId = checkout.paymentIn?.id

        if (checkout.invoice) {
            dtoResponse.invoice = new InvoiceDTO().with { dtoInvoice ->
                dtoInvoice.id = checkout.invoice.id
                dtoInvoice.invoiceNumber = checkout.invoice.invoiceNumber
                dtoInvoice.amountOwing = checkout.invoice.amountOwing.toBigDecimal()
                checkout.invoice.invoiceLines.each { invoiceLine ->
                    dtoInvoice.invoiceLines << new AbstractInvoiceLineDTO().with { dtoInvoiceLine ->
                        dtoInvoiceLine.priceEachExTax  = invoiceLine.priceEachExTax?.toBigDecimal()
                        dtoInvoiceLine.taxEach  = invoiceLine.taxEach?.toBigDecimal()
                        dtoInvoiceLine.discountEachExTax = invoiceLine.discountEachExTax?.toBigDecimal()
                        dtoInvoiceLine.finalPriceToPayIncTax = invoiceLine.finalPriceToPayIncTax?.toBigDecimal()
                        dtoInvoiceLine.quantity = invoiceLine.quantity
                        if (invoiceLine.enrolment) {
                            dtoInvoiceLine.contactId = invoiceLine.enrolment.student.contact.id
                            dtoInvoiceLine.enrolment = new CheckoutEnrolmentDTO(
                                    id: invoiceLine.enrolment.id,
                                    classId: invoiceLine.enrolment.courseClass.id,
                                    appliedDiscountId: invoiceLine.invoiceLineDiscounts.empty ? null : invoiceLine.invoiceLineDiscounts[0].discount.id)
                        } else if (!invoiceLine.productItems.empty) {
                            ProductItem item = invoiceLine.productItems[0]
                            switch (item.class) {
                                case Article:
                                    dtoInvoiceLine.contactId = item.contact.id
                                    dtoInvoiceLine.article = new CheckoutArticleDTO(ids: invoiceLine.productItems*.id,
                                            productId: item.product.id,
                                            quantity:invoiceLine.productItems.size().toBigDecimal())
                                    break
                                case Voucher:
                                    dtoInvoiceLine.contactId = checkout.invoice.contact.id
                                    dtoInvoiceLine.voucher = new CheckoutVoucherDTO(id: item.id,
                                            productId: item.product.id,
                                            code: (item as Voucher).code,
                                            validTo: LocalDateUtils.dateToValue((item as Voucher).expiryDate),
                                            value: (item as Voucher).redemptionValue.toBigDecimal(),
                                            restrictToPayer: (item as Voucher).redeemableBy != null)
                                    break
                                case Membership:
                                    dtoInvoiceLine.contactId = item.contact.id
                                    dtoInvoiceLine.membership = new CheckoutMembershipDTO(id: item.id,
                                            productId: item.product.id,
                                            validTo: LocalDateUtils.dateToValue((item as Membership).expiryDate) )
                                    break
                                default:
                                    throw new IllegalArgumentException(invoiceLine.productItems[0].class.simpleName)
                            }
                        }

                        dtoInvoiceLine
                    }
                }
                dtoInvoice
            }
        }
    }

    void handleError(int status, Object entity = null) {
        Response response = Response
                .status(status)
                .entity(entity)
                .build()

        throw new ClientErrorException(response)
    }

}