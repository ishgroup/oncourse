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
import ish.common.checkout.gateway.SessionAttributes
import ish.common.checkout.gateway.stripe.CardTypeAdapter
import ish.common.checkout.gateway.stripe.session.StripeSessionStatus
import ish.math.Money
import ish.oncourse.server.PreferenceController
import ish.oncourse.server.api.checkout.Checkout
import ish.oncourse.server.api.v1.model.CheckoutResponseDTO
import ish.oncourse.server.cayenne.Contact
import ish.oncourse.server.checkout.gateway.PaymentServiceInterface
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

import static com.stripe.param.checkout.SessionCreateParams.PaymentIntentData.SetupFutureUsage.OFF_SESSION
import static com.stripe.param.checkout.SessionCreateParams.PaymentIntentData.SetupFutureUsage.ON_SESSION

class StripePaymentService implements PaymentServiceInterface {
    private static final Logger logger = LogManager.getLogger(StripePaymentService)

    private static final String CURRENCY_CODE_AUD = "AUD"

    @Inject
    private PreferenceController preferenceController;


    @Override
    SessionAttributes createSession(String origin, Money amount, String merchantReference, Boolean storeCard, Contact contact) {
        Stripe.apiKey = preferenceController.paymentGatewayPassStripe
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
                        .setMode(SessionCreateParams.Mode.PAYMENT)
                        .setCurrency(CURRENCY_CODE_AUD)
                        .setClientReferenceId(merchantReference)
                        .setSuccessUrl(origin + '/checkout?paymentStatus=success')
                        .setCancelUrl(origin + '/checkout?paymentStatus=cancel')
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
        def session = Session.retrieve(sessionIdOrAccessCode)

        def sessionAttributes = new SessionAttributes().with {
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

        return sessionAttributes
    }

    @Override
    SessionAttributes makeRefund(Money amount, String merchantReference, String transactionId) {
        def refund = Refund.create(RefundCreateParams.builder().setAmount(amount.longValue())
                .setCurrency(CURRENCY_CODE_AUD)
                .setPaymentIntent(transactionId)
                .build())

        def card = refund.destinationDetails.card

        return new SessionAttributes().with {
            it.transactionId = refund.balanceTransaction
            it
        }
    }

    @Override
    SessionAttributes makeTransaction(Money amount, String merchantReference, String cardId) {
        PaymentIntentCreateParams params =
                PaymentIntentCreateParams.builder()
                        .setAmount(amount.longValue())
                        .setCurrency(CURRENCY_CODE_AUD)
                        .setCustomer(cardId)
                        .build()

        def sessionAttributes = new SessionAttributes()
        try {
            def paymentIntent = PaymentIntent.create(params)
            sessionAttributes.transactionId = paymentIntent.id
        } catch (Exception e) {
            sessionAttributes.errorMessage = e.message
        }

        return sessionAttributes
    }

}
