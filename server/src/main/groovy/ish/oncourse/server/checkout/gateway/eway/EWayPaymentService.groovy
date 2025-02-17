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
import ish.math.Money
import ish.oncourse.server.PreferenceController
import ish.oncourse.server.api.checkout.Checkout
import ish.oncourse.server.api.v1.model.CheckoutCCResponseDTO
import ish.oncourse.server.api.v1.model.CheckoutSubmitRequestDTO
import ish.oncourse.server.api.v1.model.CheckoutValidationErrorDTO
import ish.oncourse.server.checkout.gateway.TransactionPaymentServiceInterface
import ish.common.checkout.gateway.SessionAttributes
import org.eclipse.jetty.http.HttpStatus

class EWayPaymentService implements TransactionPaymentServiceInterface {

    @Inject
    private PreferenceController preferenceController

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

    @Override
    String getClientKey() {
        return preferenceController.paymentGatewayClientPassEway
    }

    @Override
    SessionAttributes sendTwoStepPayment(Money amount, CheckoutSubmitRequestDTO requestDTO) {
        if(requestDTO.cardDataToken == null)
            handleError(HttpStatus.BAD_REQUEST_400, [new CheckoutValidationErrorDTO(propertyName: 'transactionId', error: "Card data is required to make payment")])

        if(requestDTO.merchantReference == null)
            handleError(HttpStatus.BAD_REQUEST_400, [new CheckoutValidationErrorDTO(propertyName: 'merchantReference', error: "Merchant reference is required to make payment")])

        return eWayPaymentAPI.sendTwoStepPayment(amount, requestDTO.merchantReference, requestDTO.cardDataToken)
    }

    @Override
    SessionAttributes confirmExistedPayment(Money amount, CheckoutSubmitRequestDTO requestDTO) {
        if(requestDTO.secureCode == null)
            handleError(HttpStatus.BAD_REQUEST_400, [new CheckoutValidationErrorDTO(propertyName: 'secureCode', error: "Secure code is required for 3dsecure verify")])

        return eWayPaymentAPI.verify3dSecure(requestDTO.secureCode)
    }
}
