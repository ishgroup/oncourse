/*
 * Copyright ish group pty ltd 2024.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.checkout.gateway.square

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.inject.Inject
import com.squareup.square.Environment
import com.squareup.square.SquareClient
import com.squareup.square.authentication.BearerAuthModel
import com.squareup.square.models.*
import groovy.transform.CompileDynamic
import ish.common.checkout.gateway.PaymentGatewayError
import ish.common.checkout.gateway.SessionAttributes
import ish.common.checkout.gateway.stripe.CardTypeAdapter
import ish.math.Money
import ish.oncourse.server.PreferenceController
import ish.oncourse.server.api.checkout.Checkout
import ish.oncourse.server.api.v1.model.CheckoutCCResponseDTO
import ish.oncourse.server.api.v1.model.CheckoutSubmitRequestDTO
import ish.oncourse.server.api.v1.model.CheckoutValidationErrorDTO
import ish.oncourse.server.checkout.gateway.TransactionPaymentServiceInterface
import ish.util.LocalDateUtils
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

@CompileDynamic
class SquarePaymentService implements TransactionPaymentServiceInterface {
    private static final Logger logger = LogManager.getLogger(SquarePaymentService)

    private static final String CURRENCY_CODE_AUD = "AUD"

    @Inject
    private PreferenceController preferenceController


    private SquareClient getSquareClient() {
       return new SquareClient.Builder()
                .environment(environment)
                .bearerAuthCredentials(new BearerAuthModel.Builder(apiKey).build())
                .build()
    }

    private static com.squareup.square.models.Money convertMoney(Money amount) {
        return new com.squareup.square.models.Money.Builder()
                .amount(amount.multiply(100).toLong())
                .currency(CURRENCY_CODE_AUD)
                .build()
    }


    protected String getApiKey() {
        try {
            return preferenceController.paymentGatewayPassSquare
        } catch (Exception e) {
            handleError(PaymentGatewayError.GATEWAY_ERROR.errorNumber, [new CheckoutValidationErrorDTO(error: e.message)])
            return null
        }
    }

    protected Environment getEnvironment() {
        return Environment.PRODUCTION
    }


    private Optional<Customer> findCustomerWithEmail(String email, SquareClient squareClient) {
        CustomerTextFilter emailAddress = new CustomerTextFilter.Builder()
                .fuzzy(email)
                .build()

        CustomerFilter filter = new CustomerFilter.Builder()
                .emailAddress(emailAddress)
                .build()

        CustomerQuery query = new CustomerQuery.Builder()
                .filter(filter)
                .build()

        SearchCustomersRequest body = new SearchCustomersRequest.Builder()
                .limit(1L)
                .query(query)
                .build()

        def response = squareClient.customersApi.searchCustomers(body)
        return Optional.of(response.customers.empty ? null : response.customers.first())
    }



    SessionAttributes sendPayment(Money amount, CheckoutSubmitRequestDTO requestDTO) {
        com.squareup.square.models.Money amountMoney = convertMoney(amount)

        CreatePaymentRequest body = new CreatePaymentRequest.Builder(requestDTO.cardDataToken, requestDTO.merchantReference)
                .amountMoney(amountMoney)
                .autocomplete(true)
                .verificationToken(requestDTO.secureCode)
                .build()

        sendPaymentRequest(body)
    }

    private SessionAttributes sendPaymentRequest(CreatePaymentRequest body) {
        try {
            def paymentResponse = squareClient.paymentsApi.createPayment(body)
            def sessionAttributes = new SessionAttributes()
            buildSessionAttributesFromPaymentResponse(sessionAttributes, paymentResponse)
            return sessionAttributes
        } catch (Exception e) {
            logger.catching(e)
            handleError(PaymentGatewayError.GATEWAY_ERROR.errorNumber, [new CheckoutValidationErrorDTO(error: e.message)])
            return null //unreachable
        }
    }

    @Override
    CheckoutCCResponseDTO succeedPaymentAndCompleteTransaction(Checkout checkout, Boolean sendInvoice, SessionAttributes sessionAttributes, String merchantReference) {
        succeedPayment(checkout, sendInvoice)
    }

    @Override
    SessionAttributes checkStatus(String sessionIdOrAccessCode) {
        def sessionAttributes = new SessionAttributes()
        try {
            def paymentResponse = squareClient.paymentsApi.getPayment(sessionIdOrAccessCode)
            if(paymentResponse.errors && !paymentResponse.errors.empty) {
                sessionAttributes.errorMessage = paymentResponse.errors.join("; ")
                logger.error(sessionAttributes.errorMessage)
                return sessionAttributes
            }

            buildSessionAttributesFromPayment(sessionAttributes, paymentResponse.payment)
        } catch (Exception e) {
            logger.catching(e)
            sessionAttributes.errorMessage = e.message
        }

        return sessionAttributes
    }

    @Override
    SessionAttributes makeRefund(Money amount, String merchantReference, String transactionId) {
        def squareMoney = convertMoney(amount)

        RefundPaymentRequest body = new RefundPaymentRequest.Builder(merchantReference, squareMoney)
                .paymentId(transactionId)
                .build()

        SessionAttributes sessionAttributes = new SessionAttributes()
        try {
            def refundPaymentResponse = squareClient.refundsApi.refundPayment(body)

            if(refundPaymentResponse.errors && !refundPaymentResponse.errors.empty) {
                sessionAttributes.errorMessage = refundPaymentResponse.errors.join("; ")
                logger.error(sessionAttributes.errorMessage)
                return sessionAttributes
            }

            buildSessionAttributesFromRefund(sessionAttributes, refundPaymentResponse.refund)
        } catch (Exception e) {
            logger.catching(e)
            sessionAttributes.errorMessage = e.message
        }

        return sessionAttributes
    }

    @Override
    SessionAttributes makeTransaction(Money amount, String merchantReference, String cardId) {
        com.squareup.square.models.Money amountMoney = convertMoney(amount)
        CreatePaymentRequest body = new CreatePaymentRequest.Builder(cardId, merchantReference)
                .amountMoney(amountMoney)
                .autocomplete(true)
                .build()

        sendPaymentRequest(body)
    }

    @Override
    String getClientKey() {
        return preferenceController.paymentGatewayClientPassSquare
    }

    String getLocationId() {
        return preferenceController.paymentGatewayLocationIdSquare
    }

    private static void buildSessionAttributesFromPaymentResponse(SessionAttributes sessionAttributes, CreatePaymentResponse paymentResponse) {
        if(paymentResponse.errors && !paymentResponse.errors.empty) {
            sessionAttributes.errorMessage = paymentResponse.errors.join("; ")
            logger.error(sessionAttributes.errorMessage)
            return
        }

        def payment = paymentResponse.payment
        buildSessionAttributesFromPayment(sessionAttributes, payment)
    }

    private static void buildSessionAttributesFromPayment(SessionAttributes sessionAttributes, Payment payment) {
        sessionAttributes.transactionId = payment.id // to make refund could be used
        sessionAttributes.sessionId = payment.id
        sessionAttributes.complete = payment.status.equals("COMPLETED")
        sessionAttributes.paymentDate = LocalDateUtils.stringToTimeValue(payment.updatedAt).toLocalDate()
        sessionAttributes.statusText = payment.status

        if(payment.cardDetails?.card) {
            sessionAttributes.authorised = true
            def creditCard = payment.cardDetails?.card
            sessionAttributes.creditCardExpiry = "${creditCard.expMonth}/${creditCard.expYear}"
            sessionAttributes.creditCardName = creditCard.cardholderName
            sessionAttributes.creditCardNumber = 'XXXXXXXXXXXX' + creditCard.last4
            // https://docs.stripe.com/testing?testing-method=payment-methods
            sessionAttributes.creditCardType = CardTypeAdapter.convertFromStripeBrand(creditCard.cardBrand.toLowerCase())
        }
        sessionAttributes.responceJson = new ObjectMapper().writeValueAsString(payment)

    }

    private static void buildSessionAttributesFromRefund(SessionAttributes sessionAttributes, PaymentRefund refund) {
        sessionAttributes.transactionId = refund.paymentId
        sessionAttributes.responceJson = new ObjectMapper().writeValueAsString(refund)
        sessionAttributes.statusText = refund.status
        sessionAttributes.paymentDate = LocalDateUtils.stringToTimeValue(refund.updatedAt).toLocalDate()
        sessionAttributes
    }

}
