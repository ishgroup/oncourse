/*
 * Copyright ish group pty ltd 2023.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.checkout


import com.google.inject.Inject
import com.google.inject.Injector
import ish.common.checkout.gateway.PaymentGatewayError
import ish.common.checkout.gateway.SessionAttributes
import ish.common.types.PaymentGatewayType
import ish.common.types.SystemEventType
import ish.math.Money
import ish.oncourse.common.SystemEvent
import ish.oncourse.server.CayenneService
import ish.oncourse.server.PreferenceController
import ish.oncourse.server.api.checkout.Checkout
import ish.oncourse.server.api.checkout.CheckoutController
import ish.oncourse.server.api.dao.FundingSourceDao
import ish.oncourse.server.api.dao.ModuleDao
import ish.oncourse.server.api.dao.PaymentInDao
import ish.oncourse.server.api.service.*
import ish.oncourse.server.api.v1.model.CheckoutModelDTO
import ish.oncourse.server.api.v1.model.CheckoutResponseDTO
import ish.oncourse.server.api.v1.model.CheckoutValidationErrorDTO
import ish.oncourse.server.api.v1.model.SessionStatusDTO
import ish.oncourse.server.cayenne.PaymentIn
import ish.oncourse.server.checkout.gateway.EmbeddedFormPaymentServiceInterface
import ish.oncourse.server.checkout.gateway.PaymentServiceInterface
import ish.oncourse.server.checkout.gateway.eway.EWayPaymentService
import ish.oncourse.server.checkout.gateway.eway.test.EWayTestPaymentService
import ish.oncourse.server.checkout.gateway.offline.OfflinePaymentService
import ish.oncourse.server.checkout.gateway.stripe.StripePaymentService
import ish.oncourse.server.checkout.gateway.stripe.StripePaymentTestService
import ish.oncourse.server.checkout.gateway.windcave.WindcavePaymentService
import ish.oncourse.server.integration.EventService
import ish.oncourse.server.users.SystemUserService
import org.apache.cayenne.query.ObjectSelect

import javax.ws.rs.ClientErrorException
import javax.ws.rs.core.Response

class CheckoutApiService {

    @Inject
    PreferenceController preferenceController

    @Inject
    EventService eventService

    @Inject
    PaymentInDao paymentInDao

    @Inject
    InvoiceApiService invoiceApiService

    @Inject
    SystemUserService systemUserService

    @Inject
    VoucherProductApiService voucherApiService

    @Inject
    ArticleProductApiService articleApiService

    @Inject
    ModuleDao moduleDao

    @Inject
    FundingSourceDao fundingSourceDao

    @Inject
    CayenneService cayenneService

    @Inject
    ContactApiService contactApiService

    @Inject
    MembershipProductApiService membershipApiService

    @Inject
    CourseClassApiService courseClassApiService

    @Inject
    Injector injector

    @Inject
    CheckoutSessionService checkoutSessionService

    PaymentServiceInterface paymentService

    SessionAttributes makeRefund(Money amount, String merchantReference, String transactionId) {
        paymentService = getPaymentServiceByGatewayType()
        return paymentService.makeRefund(amount, merchantReference, transactionId)
    }

    SessionStatusDTO status(String sessionIdOrAccessCode) {
        SessionStatusDTO dto = new SessionStatusDTO()
        paymentService = getPaymentServiceByGatewayType()
        SessionAttributes attributes = paymentService.checkStatus(sessionIdOrAccessCode)
        dto.authorised = attributes.authorised
        dto.complete = attributes.complete
        dto.responseText = attributes.statusText
        return dto
    }

    CheckoutResponseDTO createSession(CheckoutModelDTO checkoutModel, String xOrigin) {
        CheckoutResponseDTO dtoResponse = new CheckoutResponseDTO()
        Checkout checkout = checkoutController.createCheckout(checkoutModel)
        paymentService = getPaymentServiceByGatewayType()

        if (!checkout.errors.empty) {
            paymentService.handleError(PaymentGatewayError.VALIDATION_ERROR.errorNumber, checkout.errors)
        } else  {
            eventService.postEvent(SystemEvent.valueOf(SystemEventType.VALIDATE_CHECKOUT, checkoutModel))
        }

        paymentService.saveCheckout(checkout)

        if (checkout.isCreditCard()) {
            if (checkoutModel.payWithSavedCard) {
                return dtoResponse
            }

            String merchantReference = UUID.randomUUID().toString()
            SessionAttributes attributes = paymentService.createSession(xOrigin, new Money(checkoutModel.payNow), merchantReference, checkoutModel.allowAutoPay, checkout.paymentIn.payer)
            if (attributes.sessionId) {
                dtoResponse.sessionId = attributes.sessionId
                dtoResponse.ccFormUrl = attributes.ccFormUrl
                dtoResponse.clientSecret = attributes.clientSecret
                dtoResponse.merchantReference = merchantReference

                checkoutSessionService.saveCheckoutSession(checkoutModel, dtoResponse.sessionId)
            } else if (attributes.errorMessage) {
                paymentService.handleError(PaymentGatewayError.GATEWAY_ERROR.errorNumber, [new CheckoutValidationErrorDTO(error: attributes.errorMessage)])
            } else {
                paymentService.handleError(PaymentGatewayError.GATEWAY_ERROR.errorNumber)
            }
        } else {
            paymentService.fillResponse(dtoResponse, checkout)
        }

        return dtoResponse
    }

    CheckoutResponseDTO submitPayment(String xPaymentSessionId) {
        CheckoutResponseDTO dtoResponse = new CheckoutResponseDTO()

        paymentService = getPaymentServiceByGatewayType()
        def checkoutModel = checkoutSessionService.getCheckoutSession(xPaymentSessionId, paymentService)
        Checkout checkout = checkoutController.createCheckout(checkoutModel)

        if (!checkout.errors.empty) {
            paymentService.handleError(PaymentGatewayError.VALIDATION_ERROR.errorNumber, checkout.errors)
        }

        String cardId = null
        if (checkoutModel.payWithSavedCard) {
            cardId =  paymentInDao.getCreditCardId(checkout.paymentIn.payer)
            if (cardId == null) {
                paymentService.handleError(PaymentGatewayError.VALIDATION_ERROR.errorNumber, [new CheckoutValidationErrorDTO(propertyName: 'payWithSavedCard', error: 'Payer has no credit card history')])
            }
        }

        Money amount  = checkout.paymentIn.amount
        SessionAttributes sessionAttributes
        String merchantReference = null

        if (checkoutModel.payWithSavedCard) {
            merchantReference = UUID.randomUUID().toString()
            sessionAttributes = paymentService.makeTransaction(amount, merchantReference, cardId)
        } else {
            if (!checkoutModel.merchantReference) {
                paymentService.handleError(PaymentGatewayError.VALIDATION_ERROR.errorNumber, [new CheckoutValidationErrorDTO(propertyName: 'merchantReference', error: "Merchant reference is required")])
            } else {
                merchantReference = checkoutModel.merchantReference
            }

            sessionAttributes = paymentService.checkStatus(xPaymentSessionId)

            if (!sessionAttributes.complete) {
                paymentService.handleError(PaymentGatewayError.VALIDATION_ERROR.errorNumber, [new CheckoutValidationErrorDTO(error: "Credit card authorisation is not complite, $sessionAttributes.statusText ${sessionAttributes.errorMessage ? (", " + sessionAttributes.errorMessage) : ""}")])
            }
        }

        if (!sessionAttributes.authorised) {
            paymentService.handleError(PaymentGatewayError.VALIDATION_ERROR.errorNumber, [new CheckoutValidationErrorDTO(error: "Credit card declined: $sessionAttributes.statusText ${sessionAttributes.errorMessage ? (", " +  sessionAttributes.errorMessage) : ""}")])
        }

        if (ObjectSelect.query(PaymentIn).where(PaymentIn.GATEWAY_REFERENCE.eq(sessionAttributes.transactionId)).selectFirst(cayenneService.newContext) != null) {
            paymentService.handleError(PaymentGatewayError.VALIDATION_ERROR.errorNumber, [new CheckoutValidationErrorDTO(error: "Credit card payment already complete")])
        }

        PaymentIn paymentIn = checkout.paymentIn
        paymentIn.creditCardExpiry = sessionAttributes.creditCardExpiry
        paymentIn.creditCardName = sessionAttributes.creditCardName
        paymentIn.creditCardNumber = sessionAttributes.creditCardNumber
        paymentIn.creditCardType = sessionAttributes.creditCardType
        paymentIn.gatewayResponse = sessionAttributes.statusText
        paymentIn.gatewayReference = sessionAttributes.transactionId
        paymentIn.paymentDate = sessionAttributes.paymentDate
        paymentIn.billingId = sessionAttributes.billingId
        paymentIn.sessionId = merchantReference
        paymentIn.privateNotes = sessionAttributes.responceJson

        paymentService.succeedPaymentAndCompleteTransaction(dtoResponse, checkout, checkoutModel.sendInvoice, sessionAttributes, amount, merchantReference)

        postEnrolmentSuccessfulEvents(checkout)
        checkoutSessionService.removeCheckoutSession(xPaymentSessionId)
        return dtoResponse
    }

    CheckoutResponseDTO submit(CheckoutModelDTO checkoutModel, Boolean xValidateOnly, String xPaymentSessionId, String xOrigin ) {
        CheckoutResponseDTO dtoResponse = new CheckoutResponseDTO()
        Checkout checkout = checkoutController.createCheckout(checkoutModel)
        String cardId = null

        paymentService = getPaymentServiceByGatewayType()

        if (!checkout.errors.empty) {
            paymentService.handleError(PaymentGatewayError.VALIDATION_ERROR.errorNumber, checkout.errors)
        }  else if (xValidateOnly) {
            eventService.postEvent(SystemEvent.valueOf(SystemEventType.VALIDATE_CHECKOUT, checkoutModel))
        }

        if (checkoutModel.payWithSavedCard) {
            cardId =  paymentInDao.getCreditCardId(checkout.paymentIn.payer)
            if (cardId == null) {
                paymentService.handleError(PaymentGatewayError.VALIDATION_ERROR.errorNumber, [new CheckoutValidationErrorDTO(propertyName: 'payWithSavedCard', error: 'Payer has no credit card history')])
            }
        }

        if (checkout.isCreditCard()) {

            if (xValidateOnly) {
                paymentService.saveCheckout(checkout)

                if (checkoutModel.payWithSavedCard) {
                    return dtoResponse
                }

                String merchantReference = UUID.randomUUID().toString()
                SessionAttributes attributes = paymentService.createSession(xOrigin, new Money(checkoutModel.payNow), merchantReference, checkoutModel.allowAutoPay, checkout.paymentIn.payer)
                if (attributes.sessionId) {
                    dtoResponse.sessionId = attributes.sessionId
                    dtoResponse.ccFormUrl = attributes.ccFormUrl
                    dtoResponse.clientSecret = attributes.clientSecret
                    dtoResponse.merchantReference = merchantReference
                } else if (attributes.errorMessage) {
                    paymentService.handleError(PaymentGatewayError.GATEWAY_ERROR.errorNumber, [new CheckoutValidationErrorDTO(error: attributes.errorMessage)])
                } else {
                    paymentService.handleError(PaymentGatewayError.GATEWAY_ERROR.errorNumber)
                }

            } else {
                Money amount  = checkout.paymentIn.amount
                SessionAttributes sessionAttributes
                String merchantReference = null

                if (checkoutModel.payWithSavedCard) {
                    merchantReference = UUID.randomUUID().toString()
                    sessionAttributes = paymentService.makeTransaction(amount, merchantReference, cardId)
                } else {
                    if (!checkoutModel.merchantReference) {
                        paymentService.handleError(PaymentGatewayError.VALIDATION_ERROR.errorNumber, [new CheckoutValidationErrorDTO(propertyName: 'merchantReference', error: "Merchant reference is required")])
                    } else {
                        merchantReference = checkoutModel.merchantReference
                    }

                    sessionAttributes = paymentService.checkStatus(xPaymentSessionId)

                    if (!sessionAttributes.complete) {
                        paymentService.handleError(PaymentGatewayError.VALIDATION_ERROR.errorNumber, [new CheckoutValidationErrorDTO(error: "Credit card authorisation is not complite, $sessionAttributes.statusText ${sessionAttributes.errorMessage ? (", " + sessionAttributes.errorMessage) : ""}")])
                    }
                }

                if (!sessionAttributes.authorised) {
                    paymentService.handleError(PaymentGatewayError.VALIDATION_ERROR.errorNumber, [new CheckoutValidationErrorDTO(error: "Credit card declined: $sessionAttributes.statusText ${sessionAttributes.errorMessage ? (", " +  sessionAttributes.errorMessage) : ""}")])
                }

                if (ObjectSelect.query(PaymentIn).where(PaymentIn.GATEWAY_REFERENCE.eq(sessionAttributes.transactionId)).selectFirst(cayenneService.newContext) != null) {
                    paymentService.handleError(PaymentGatewayError.VALIDATION_ERROR.errorNumber, [new CheckoutValidationErrorDTO(error: "Credit card payment already complete")])
                }

                PaymentIn paymentIn = checkout.paymentIn
                paymentIn.creditCardExpiry = sessionAttributes.creditCardExpiry
                paymentIn.creditCardName = sessionAttributes.creditCardName
                paymentIn.creditCardNumber = sessionAttributes.creditCardNumber
                paymentIn.creditCardType = sessionAttributes.creditCardType
                paymentIn.gatewayResponse = sessionAttributes.statusText
                paymentIn.gatewayReference = sessionAttributes.transactionId
                paymentIn.paymentDate = sessionAttributes.paymentDate
                paymentIn.billingId = sessionAttributes.billingId
                paymentIn.sessionId = merchantReference
                paymentIn.privateNotes = sessionAttributes.responceJson

                paymentService.succeedPaymentAndCompleteTransaction(dtoResponse, checkout, checkoutModel.sendInvoice, sessionAttributes, amount, merchantReference)
            }
        } else {
            paymentService.saveCheckout(checkout)
            paymentService.fillResponse(dtoResponse, checkout)
        }

        postEnrolmentSuccessfulEvents(checkout, xValidateOnly)
        return dtoResponse
    }

    private void postEnrolmentSuccessfulEvents(Checkout checkout) {
        if (checkout.invoice) {
            checkout.invoice.invoiceLines.findAll { it.enrolment }*.enrolment.each { enrol ->
                eventService.postEvent(SystemEvent.valueOf(SystemEventType.ENROLMENT_SUCCESSFUL, enrol))
            }
        }
    }

    private CheckoutController getCheckoutController() {
        new CheckoutController(cayenneService, systemUserService, contactApiService, invoiceApiService, courseClassApiService,  membershipApiService,  voucherApiService,  articleApiService, fundingSourceDao, moduleDao)
    }

    private PaymentServiceInterface getPaymentServiceByGatewayType() {
        String gatewayType = preferenceController.getPaymentGatewayType()
        switch (gatewayType) {
            case PaymentGatewayType.WINDCAVE.value:
                return injector.getInstance(WindcavePaymentService.class)
            case PaymentGatewayType.EWAY.value:
                return injector.getInstance(EWayPaymentService.class)
            case PaymentGatewayType.EWAY_TEST.value:
                return injector.getInstance(EWayTestPaymentService.class)
            case PaymentGatewayType.STRIPE.value:
                return injector.getInstance(StripePaymentService.class)
            case PaymentGatewayType.STRIPE_TEST.value:
                return injector.getInstance(StripePaymentTestService.class)
            case PaymentGatewayType.OFFLINE.value:
                return injector.getInstance(OfflinePaymentService.class)
            default:
                handleError(PaymentGatewayError.PAYMENT_ERROR.errorNumber, [new CheckoutValidationErrorDTO(error: "Sorry, you cannot make a purchase. The selected payment method is prohibited for the ${gatewayType} payment system. Please contact the administrator.")])
        }
    }

    private static void handleError(int status, Object entity = null) {
        Response response = Response
                .status(status)
                .entity(entity)
                .build()

        throw new ClientErrorException(response)
    }

    String getClientKey(){
        try {
            def service = getPaymentServiceByGatewayType()
            if (!(service instanceof EmbeddedFormPaymentServiceInterface))
                throw new IllegalAccessException("Client key not supported for selected system")

            return (service as EmbeddedFormPaymentServiceInterface).getClientKey()
        } catch(Exception e) {
            handleError(PaymentGatewayError.GATEWAY_ERROR.errorNumber, [new CheckoutValidationErrorDTO(error: e.message)])
            return null
        }
    }
}
