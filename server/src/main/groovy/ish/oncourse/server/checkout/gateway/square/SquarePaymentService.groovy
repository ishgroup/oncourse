/*
 * Copyright ish group pty ltd 2024.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.checkout.gateway.square

import com.google.inject.Inject
import com.squareup.square.Environment
import com.squareup.square.SquareClient
import com.squareup.square.authentication.BearerAuthModel
import com.squareup.square.models.CreatePaymentRequest
import com.squareup.square.models.CreatePaymentResponse
import com.squareup.square.models.Customer
import com.squareup.square.models.CustomerFilter
import com.squareup.square.models.CustomerQuery
import com.squareup.square.models.CustomerTextFilter
import com.squareup.square.models.Payment
import com.squareup.square.models.SearchCustomersRequest
import com.stripe.Stripe
import com.stripe.model.Charge
import com.stripe.model.PaymentIntent
import com.stripe.model.Refund
import com.stripe.param.PaymentIntentConfirmParams
import com.stripe.param.PaymentIntentCreateParams
import com.stripe.param.RefundCreateParams
import groovy.transform.CompileDynamic
import ish.common.checkout.gateway.PaymentGatewayError
import ish.common.checkout.gateway.SessionAttributes
import ish.common.checkout.gateway.stripe.CardTypeAdapter
import ish.common.checkout.gateway.stripe.payment.PaymentIntentStatus
import ish.math.Money
import ish.oncourse.server.PreferenceController
import ish.oncourse.server.api.checkout.Checkout
import ish.oncourse.server.api.v1.model.CheckoutCCResponseDTO
import ish.oncourse.server.api.v1.model.CheckoutSubmitRequestDTO
import ish.oncourse.server.api.v1.model.CheckoutValidationErrorDTO
import ish.oncourse.server.checkout.gateway.TransactionPaymentServiceInterface
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

@CompileDynamic
class SquarePaymentService implements TransactionPaymentServiceInterface {
    private static final Logger logger = LogManager.getLogger(SquarePaymentService)

    private static final String CURRENCY_CODE_AUD = "AUD"

    @Inject
    private PreferenceController preferenceController


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

    SessionAttributes confirmExistedPayment(Money money, CheckoutSubmitRequestDTO requestDTO) {
       /* Stripe.apiKey = apiKey
        PaymentIntent resource = PaymentIntent.retrieve(requestDTO.transactionId)
        PaymentIntentConfirmParams params = PaymentIntentConfirmParams.builder()
                .setReturnUrl(requestDTO.origin + "/checkout?onCourseSessionId="+requestDTO.onCoursePaymentSessionId)
                .build()

        try {
            PaymentIntent paymentIntent = resource.confirm(params)
            def sessionAttributes = new SessionAttributes()
            buildSessionAttributesFromPayment(sessionAttributes, paymentIntent)
            return sessionAttributes
        } catch (Exception e) {
            logger.catching(e)
            handleError(PaymentGatewayError.GATEWAY_ERROR.errorNumber, [new CheckoutValidationErrorDTO(error: e.message)])
            return null //unreachable
        }*/
        return null
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

    SessionAttributes sendTwoStepPayment(Money amount, CheckoutSubmitRequestDTO requestDTO) {
        com.squareup.square.models.Money amountMoney = new com.squareup.square.models.Money.Builder()
                .amount(amount.multiply(100).toLong())
                .currency(CURRENCY_CODE_AUD)
                .build()

        def squareClient = new SquareClient.Builder()
                .environment(environment)
                .bearerAuthCredentials(new BearerAuthModel.Builder(apiKey).build())
                .build()

        CreatePaymentRequest body = new CreatePaymentRequest.Builder(requestDTO.transactionId, requestDTO.merchantReference)
                .amountMoney(amountMoney)
                .autocomplete(false)
                .referenceId("123456")
                .verificationToken(requestDTO.secureCode)
                .build()
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
        Stripe.apiKey = apiKey
        //for stripe we check transaction status
        def paymentIntent = PaymentIntent.retrieve(sessionIdOrAccessCode)
        def sessionAttributes = new SessionAttributes()

        try {
            def status = PaymentIntentStatus.from(paymentIntent.status)
            sessionAttributes = new SessionAttributes().with {
                it.sessionId = paymentIntent.id
                // payment expire time = 10 min - ? check it on server side ???
                it.complete = status == PaymentIntentStatus.Succeeded
                it.type = paymentIntent.paymentMethodTypes.first()
                it.responceJson = paymentIntent.toJson()
                it.statusText = paymentIntent.status
                it.billingId = paymentIntent.customer // ? - billingId == customerID
                it
            }
            //buildSessionAttributesFromPayment(sessionAttributes, paymentIntent)
        } catch (Exception e) {
            logger.catching(e)
            sessionAttributes.errorMessage = e.message
        }

        return sessionAttributes
    }

    @Override
    SessionAttributes makeRefund(Money amount, String merchantReference, String transactionId) {
        Stripe.apiKey = apiKey
        SessionAttributes sessionAttributes = new SessionAttributes()

        try {
            def refund = Refund.create(RefundCreateParams.builder().setAmount(amount.longValue())
                    .setCurrency(CURRENCY_CODE_AUD)
                    .setPaymentIntent(transactionId)
                    .build())

            if (refund.failureReason) {
                sessionAttributes.errorMessage = refund.failureReason
                return sessionAttributes
            }

            buildSessionAttributesFromRefund(sessionAttributes, refund)
        } catch (Exception e) {
            logger.catching(e)
            sessionAttributes.errorMessage = e.message
        }

        return sessionAttributes
    }

    @Override
    SessionAttributes makeTransaction(Money amount, String merchantReference, String cardId) {
        Stripe.apiKey = apiKey
        PaymentIntentCreateParams params =
                PaymentIntentCreateParams.builder()
                        .setAmount(amount.multiply(100).toLong())
                        .setCurrency(CURRENCY_CODE_AUD)
                        .setCustomer(cardId)
                        .build()

        def sessionAttributes = new SessionAttributes()
        try {
            def paymentIntent = PaymentIntent.create(params)
            sessionAttributes.responceJson = paymentIntent.toJson()
            //buildSessionAttributesFromPayment(sessionAttributes, paymentIntent)
        } catch (Exception e) {
            logger.catching(e)
            sessionAttributes.errorMessage = e.message
        }

        return sessionAttributes
    }

    @Override
    String getClientKey() {
        return preferenceController.paymentGatewayClientPassStripe
    }

    private static void buildSessionAttributesFromPaymentResponse(SessionAttributes sessionAttributes, CreatePaymentResponse payment) {
        /*sessionAttributes.transactionId = payment.id // to make refund could be used
        sessionAttributes.sessionId = payment.id*/
        /*def status = payment.status PaymentIntentStatus.from(paymentIntent.status)
        sessionAttributes.secure3dRequired = status == PaymentIntentStatus.RequiresAction

        if (paymentIntent.lastPaymentError) {
            sessionAttributes.errorMessage = paymentIntent.lastPaymentError
        }

        if (paymentIntent.latestCharge) {
            def charge = Charge.retrieve(paymentIntent.latestCharge)
            if (charge) {
                sessionAttributes.authorised = charge.outcome.type == "authorized"

                // set up info about credit card
                def creditCard = charge.paymentMethodDetails?.card
                if (creditCard) {
                    sessionAttributes.creditCardExpiry = "${creditCard.expMonth}/${creditCard.expYear}"
                    sessionAttributes.creditCardName = charge.billingDetails?.name
                    sessionAttributes.creditCardNumber = 'XXXXXXXXXXXX' + creditCard.last4
                    // https://docs.stripe.com/testing?testing-method=payment-methods
                    sessionAttributes.creditCardType = CardTypeAdapter.convertFromStripeBrand(creditCard.brand)
                }

                // set up info about payment data
                Long paymentDateUnix = paymentIntent.getCreated()
                sessionAttributes.paymentDate = new Date(paymentDateUnix * 1000).toLocalDate()
            }

            if (charge?.outcome?.sellerMessage) {
                sessionAttributes.errorMessage += '\n' + charge.outcome.sellerMessage
            }
        }*/
    }


    private static void buildSessionAttributesFromRefund(SessionAttributes sessionAttributes, Refund refund) {
        sessionAttributes.transactionId = refund.paymentIntent
        sessionAttributes.responceJson = refund.toJson()
        sessionAttributes.statusText = refund.status
        Long paymentDateUnix = refund.getCreated()
        sessionAttributes.paymentDate = new Date(paymentDateUnix * 1000).toLocalDate()
        sessionAttributes
    }

}
