/*
 * Copyright ish group pty ltd 2023.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.checkout.gateway.eway

import com.google.inject.Inject
import com.google.inject.Injector
import com.stripe.Stripe
import com.stripe.model.PaymentIntent
import com.stripe.param.PaymentIntentConfirmParams
import ish.common.checkout.gateway.PaymentGatewayError
import ish.math.Money
import ish.oncourse.server.api.checkout.Checkout
import ish.oncourse.server.api.v1.model.CheckoutCCResponseDTO
import ish.oncourse.server.api.v1.model.CheckoutResponseDTO
import ish.oncourse.server.api.v1.model.CheckoutSubmitRequestDTO
import ish.oncourse.server.api.v1.model.CheckoutValidationErrorDTO
import ish.oncourse.server.cayenne.Contact
import ish.oncourse.server.checkout.gateway.PaymentServiceInterface
import ish.common.checkout.gateway.SessionAttributes

class EWayPaymentService implements PaymentServiceInterface {

    protected EWayPaymentAPI eWayPaymentAPI

    @Inject
    EWayPaymentService(Injector injector) {
        this.eWayPaymentAPI = injector.getInstance(EWayPaymentAPI.class)
    }

    @Override
    CheckoutCCResponseDTO succeedPaymentAndCompleteTransaction(Checkout checkout, Boolean sendInvoice, SessionAttributes sessionAttributes, String merchantReference) {
        succeedPayment(checkout, sendInvoice)
    }

    @Override
    SessionAttributes checkStatus(String accessCode) {
        return eWayPaymentAPI.getTransaction(accessCode)
    }

    @Override
    SessionAttributes makeRefund(Money amount, String merchantReference, String transactionId) {
        SessionAttributes attributes = eWayPaymentAPI.makeRefund(amount, merchantReference, transactionId)
        SessionAttributes refundTransactionAttributes = eWayPaymentAPI.getTransaction(attributes.transactionId)
        attributes.paymentDate = refundTransactionAttributes.paymentDate
        return attributes
    }

    @Override
    SessionAttributes makeTransaction(Money amount, String merchantReference, String cardId) {
        SessionAttributes attributes = eWayPaymentAPI.makeTransaction(amount, merchantReference, cardId)
        SessionAttributes transactionAttributes = eWayPaymentAPI.getTransaction(attributes.transactionId)
        attributes.paymentDate = transactionAttributes.paymentDate
        return attributes
    }
}
