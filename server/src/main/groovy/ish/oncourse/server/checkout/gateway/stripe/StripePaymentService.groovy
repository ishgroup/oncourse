/*
 * Copyright ish group pty ltd 2024.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.checkout.gateway.stripe

import com.google.inject.Inject
import com.stripe.Stripe
import com.stripe.model.Charge
import com.stripe.model.PaymentIntent
import com.stripe.model.Refund
import com.stripe.model.checkout.Session
import com.stripe.param.PaymentIntentCreateParams
import com.stripe.param.RefundCreateParams
import com.stripe.param.checkout.SessionCreateParams
import groovy.transform.CompileDynamic
import ish.common.checkout.gateway.PaymentGatewayError
import ish.common.checkout.gateway.SessionAttributes
import ish.common.checkout.gateway.stripe.CardTypeAdapter
import ish.common.checkout.gateway.stripe.session.StripeSessionStatus
import ish.math.Money
import ish.oncourse.server.PreferenceController
import ish.oncourse.server.api.checkout.Checkout
import ish.oncourse.server.api.v1.model.CheckoutResponseDTO
import ish.oncourse.server.api.v1.model.CheckoutValidationErrorDTO
import ish.oncourse.server.cayenne.Contact
import ish.oncourse.server.checkout.gateway.EmbeddedFormPaymentServiceInterface
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

import static com.stripe.param.checkout.SessionCreateParams.PaymentIntentData.SetupFutureUsage.OFF_SESSION
import static com.stripe.param.checkout.SessionCreateParams.PaymentIntentData.SetupFutureUsage.ON_SESSION

@CompileDynamic
class StripePaymentService implements EmbeddedFormPaymentServiceInterface {
    private static final Logger logger = LogManager.getLogger(StripePaymentService)

    private static final String CURRENCY_CODE_AUD = "AUD"

    @Inject
    private PreferenceController preferenceController


    protected String getApiKey() {
        try {
            return preferenceController.paymentGatewayPassStripe
        } catch(Exception e) {
            handleError(PaymentGatewayError.GATEWAY_ERROR.errorNumber, [new CheckoutValidationErrorDTO(error: e.message)])
            return null
        }
    }


    @Override
    SessionAttributes createSession(String origin, Money amount, String merchantReference, Boolean storeCard, Contact contact) {
        Stripe.apiKey = apiKey
        def product = SessionCreateParams.LineItem.PriceData.ProductData.builder()
                .setName("onCourse payment operation")
                .build()

        def price = SessionCreateParams.LineItem.PriceData.builder()
                .setCurrency(CURRENCY_CODE_AUD)
                .setUnitAmount(amount.multiply(100).toInteger())
                .setProductData(product)
                .build()

        def lineItem = SessionCreateParams.LineItem.builder()
                .setPriceData(price)
                .setQuantity(1L)
                .build()

        SessionCreateParams.Builder paramsBuilder =
                SessionCreateParams.builder()
                        .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                        .setCurrency(CURRENCY_CODE_AUD)
                        .setClientReferenceId(merchantReference)
                        .addLineItem(lineItem)
                        .setCustomerEmail(contact.email)
                        .setUiMode(SessionCreateParams.UiMode.EMBEDDED)
                        .setReturnUrl(origin + "/checkout?sessionId={CHECKOUT_SESSION_ID}")
                        .setMode(SessionCreateParams.Mode.PAYMENT)

        def futureUsage = storeCard ? OFF_SESSION : ON_SESSION
        paramsBuilder.setPaymentIntentData(
                SessionCreateParams.PaymentIntentData.builder()
                        .setSetupFutureUsage(futureUsage) // OFF_SESSION is auto payments
                        .build()
        )
        def sessionAttributes = new SessionAttributes()
        try {
            def session = Session.create(paramsBuilder.build())
            def status = StripeSessionStatus.from(session.status)
            if(status == StripeSessionStatus.Open) {
                sessionAttributes.sessionId = session.id
                sessionAttributes.ccFormUrl = session.url
                sessionAttributes.clientSecret = session.clientSecret
                sessionAttributes.billingId = session.customer
            } else {
                sessionAttributes.errorMessage = "Unable to establish a connection with the Stripe application. Please contact ish support or try again later"
            }
        } catch (Exception e) {
            logger.catching(e)
            sessionAttributes.errorMessage = e.message
        }

        return sessionAttributes
    }

    @Override
    void succeedPaymentAndCompleteTransaction(CheckoutResponseDTO dtoResponse, Checkout checkout, Boolean sendInvoice, SessionAttributes sessionAttributes, Money amount, String merchantReference) {
        succeedPayment(dtoResponse, checkout, sendInvoice)
    }

    @Override
    SessionAttributes checkStatus(String sessionIdOrAccessCode) {
        Stripe.apiKey = apiKey
        def session = Session.retrieve(sessionIdOrAccessCode)
        def sessionAttributes = new SessionAttributes()

        try {
            sessionAttributes = new SessionAttributes().with {
                it.sessionId = session.id
                // payment expire time = 10 min - ? check it on server side ???
                it.complete = StripeSessionStatus.from(session.status) == StripeSessionStatus.Complete
                it.type = session.mode
                it.responceJson = session.toJson()
                it.statusText = session.paymentStatus // paid / unpaid / no_payment_required + message for client
                it.billingId = session.customer // ? - billingId == customerID
                it
            }

            if (session.paymentIntent) {
                PaymentIntent paymentIntent = PaymentIntent.retrieve(session.paymentIntent)
                buildSessionAttributesFromPaymentIntent(sessionAttributes, paymentIntent)
            }
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
            def refund = Refund.create(RefundCreateParams.builder().setAmount(amount.toLong())
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
            buildSessionAttributesFromPaymentIntent(sessionAttributes, paymentIntent)
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

    private static void buildSessionAttributesFromPaymentIntent(SessionAttributes sessionAttributes, PaymentIntent paymentIntent) {
        sessionAttributes.transactionId = paymentIntent.id // to make refund could be used

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

        if (paymentIntent.lastPaymentError) {
            sessionAttributes.errorMessage = paymentIntent.lastPaymentError
            if (charge?.outcome?.sellerMessage) {
                sessionAttributes.errorMessage += '\n' + charge.outcome.sellerMessage
            }
        }
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
