/*
 * Copyright ish group pty ltd 2023.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.checkout.gateway.eway

import javax.inject.Inject
import io.bootique.di.Injector
import ish.math.Money
import ish.oncourse.server.api.checkout.Checkout
import ish.oncourse.server.api.v1.model.CheckoutResponseDTO
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
    SessionAttributes createSession(String origin, Money amount, String merchantReference, Boolean storeCard, Contact contact) {
        return eWayPaymentAPI.createSession(origin, amount, merchantReference, storeCard, contact)
    }

    @Override
    void succeedPaymentAndCompleteTransaction(CheckoutResponseDTO dtoResponse, Checkout checkout, Boolean sendInvoice, SessionAttributes sessionAttributes, Money amount, String merchantReference) {
        succeedPayment(dtoResponse, checkout, sendInvoice)
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
