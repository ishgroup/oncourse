/*
 * Copyright ish group pty ltd 2023.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.checkout.gateway.windcave

import com.google.inject.Inject
import ish.common.checkout.gateway.PaymentGatewayError
import ish.common.checkout.gateway.SessionAttributes
import ish.math.Money
import ish.oncourse.server.PreferenceController
import ish.oncourse.server.api.checkout.Checkout
import ish.oncourse.server.api.v1.model.CheckoutCCResponseDTO
import ish.oncourse.server.api.v1.model.CheckoutValidationErrorDTO
import ish.oncourse.server.api.v1.model.SessionStatusDTO
import ish.oncourse.server.cayenne.Contact
import ish.oncourse.server.checkout.gateway.SessionPaymentServiceInterface

import static WindcavePaymentAPI.AUTH_TYPE

class WindcavePaymentService implements SessionPaymentServiceInterface {

    @Inject
    WindcavePaymentAPI windcavePaymentAPI

    @Inject
    PreferenceController preferenceController

    @Override
    SessionAttributes createSession(String origin, Money amount, String merchantReference, Boolean storeCard, Contact contact) {
        return windcavePaymentAPI.createSession(origin, amount, merchantReference, storeCard)
    }

    @Override
    CheckoutCCResponseDTO succeedPaymentAndCompleteTransaction(Checkout checkout, Boolean sendInvoice, SessionAttributes sessionAttributes, String merchantReference) {
        if (preferenceController.isPurchaseWithoutAuth()) {
            return succeedPayment(checkout, sendInvoice)
        } else {
            if (AUTH_TYPE != sessionAttributes.type) {
                handleError(PaymentGatewayError.VALIDATION_ERROR.errorNumber, [new CheckoutValidationErrorDTO(error: "Credit card transaction has wrong type")])
            }

           sessionAttributes = windcavePaymentAPI.completeTransaction(sessionAttributes.transactionId, checkout.paymentIn.amount, merchantReference)

            if (sessionAttributes.authorised) {
                return succeedPayment(checkout, sendInvoice)
            } else {
                checkout.paymentIn.gatewayResponse = sessionAttributes.statusText
                checkout.paymentIn.privateNotes = sessionAttributes.responceJson
                checkout.context.commitChanges()
                handleError(PaymentGatewayError.PAYMENT_ERROR.errorNumber,  new SessionStatusDTO(complete: sessionAttributes.complete, authorised: sessionAttributes.authorised, responseText: sessionAttributes.statusText))
            }
            return new CheckoutCCResponseDTO()
        }
    }

    @Override
    SessionAttributes checkStatus(String sessionIdOrAccessCode) {
        return windcavePaymentAPI.checkStatus(sessionIdOrAccessCode)
    }

    @Override
    SessionAttributes makeRefund(Money amount, String merchantReference, String transactionId) {
        return windcavePaymentAPI.makeRefund(amount, merchantReference, transactionId)
    }

    @Override
    SessionAttributes makeTransaction(Money amount, String merchantReference, String cardId) {
        return windcavePaymentAPI.makeTransaction(amount, merchantReference, cardId)
    }

}
