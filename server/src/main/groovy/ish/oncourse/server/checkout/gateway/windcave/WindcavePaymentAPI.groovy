/*
 * Copyright ish group pty ltd 2023.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.checkout.gateway.windcave

import com.google.inject.Inject
import groovy.json.JsonOutput
import groovy.transform.CompileStatic
import groovy.transform.TypeCheckingMode
import groovyx.net.http.ContentType
import groovyx.net.http.HTTPBuilder
import groovyx.net.http.HttpResponseDecorator
import groovyx.net.http.HttpResponseException
import ish.common.checkout.gateway.SessionAttributes
import ish.common.checkout.gateway.windcave.WindcaveResponseCode
import ish.util.DateFormatter
import org.apache.commons.lang3.time.DateUtils

import java.text.SimpleDateFormat

import static ish.common.types.CreditCardType.*
import ish.math.Money
import ish.oncourse.server.PreferenceController
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

import java.time.LocalDate

@CompileStatic
class WindcavePaymentAPI {

//    SXNoR3JvdXBSRVNUX0RldjphOTljYWUxYmRlZGJjNGU5ODQ3OTNmZjNhNjkwMDM5ZTdlZWUyOTgyMmQ0ZDQzZDg2M2JkZDE4NGNlOTk4NmRj

    private static final String WINDCAVE_BASE = 'https://sec.windcave.com'
    public static final String  AUTH_TYPE = "auth"
    public static final String  PURCHASE_TYPE = "purchase"

    public static final int ACCEPTED_STATUS = 202
    public static final int STATUS_CHECK_LIMIT = 3

    private static final Logger logger = LogManager.getLogger(WindcavePaymentAPI)

    @Inject
    PreferenceController preferenceController

    Closure failHandler =  { Object response, Object body, SessionAttributes attributes  ->
        logger.error('Fail to create session {} {}', body, response)
        try {
            if (body['errors'] && !(body['errors'] as List).empty) {
                attributes.errorMessage = (body['errors'] as List).collect {it['message']}.join('\n')
            }
        } catch (Exception e) {
            logger.catching(e)
            attributes.errorMessage =  "Something unexpected has happened, please contact wish support or try again later"
        }
    }

    @CompileStatic(TypeCheckingMode.SKIP)
    SessionAttributes createSession(String origin, Money amount, String merchantReference, Boolean storeCard) {
        SessionAttributes attributes = new SessionAttributes()

        try {

            HTTPBuilder builder =  new HTTPBuilder()

            builder.handler['failure'] = { response, body -> failHandler(response, body, attributes)}

            builder.handler['success'] = { response, body ->
                attributes.sessionId = body["id"]
                attributes.ccFormUrl = body["links"].find {it['method'] == 'REDIRECT' && it['rel'] == 'hpp'}['href']
            }

            Map<String, Object> body = [type               : transactionType,
                                        amount             : amount.toPlainString(),
                                        amountSurcharge    : "0.00",
                                        currency           : amount.currencyContext.currencyCode,
                                        merchantReference  : merchantReference,
                                        language           : "en",
                                        methods            : ["card"],
                                        "expires": new SimpleDateFormat(DateFormatter.FORMAT_DATE_RFC3339)
                                                .format(DateUtils.addMinutes(new Date(), 5)),
                                        callbackUrls       : [
                                                approved :  origin + '/checkout?paymentStatus=success',
                                                declined :  origin + '/checkout?paymentStatus=fail',
                                                cancelled:  origin + '/checkout?paymentStatus=cancel'
                                            ]
                                        ]
            if (storeCard) {
                body['storeCard'] = true
                body['storedCardIndicator'] = 'recurringinitial'
            } else {
                body['storeCard'] = false
            }

            builder.post(
                    [uri: WINDCAVE_BASE,
                     path: '/api/v1/sessions',
                     contentType: ContentType.JSON,
                     headers: [Authorization: "Basic $preferenceController.paymentGatewayPass"],
                     requestContentType: ContentType.JSON,
                     body: body
                    ])
        } catch (HttpResponseException e) {
            logger.error("Fail to create session")
            logger.catching(e)
        }

        return attributes
    }

    SessionAttributes completeTransaction(String transactionId, Money amount, String merchantReference) {
        SessionAttributes result = new SessionAttributes()
        Integer status = null
        try {
            new HTTPBuilder().post(
                    [uri               : WINDCAVE_BASE,
                     path              : '/api/v1/transactions',
                     contentType       : ContentType.JSON,
                     headers           : [Authorization: "Basic $preferenceController.paymentGatewayPass"],
                     requestContentType: ContentType.JSON,
                     body              : [type             : "complete",
                                          amount           : amount.toPlainString(),
                                          amountSurcharge  : "0.00",
                                          currency         : amount.currencyContext.currencyCode,
                                          merchantReference: merchantReference,
                                          transactionId    : transactionId]
                    ]) { response, body ->
                            logger.info("Complite transaction success: ${body.toString()}")
                            buildSessionAttributes(result, body as Map<String, Object>)
                            status = (response as HttpResponseDecorator).getStatus()
                         }
        } catch(Exception e) {
            logger.error("Fail to complete payment transaction")
            logger.catching(e)
        }

        // In very rare cases, the server may return a 202 response and only the transaction ID. This happens because the transaction did not have time to be processed by the services in Windcave.
        // Therefore, we will try to get a response through a GET request 3 times in 10 seconds. https://www.windcave.com/developer-e-commerce-api-rest#Server_Side_Post_Walkthrough
        if (status == ACCEPTED_STATUS) {
            result = checkTransactionIfStatusAccept(status, result)
        }

        return result
    }

    private SessionAttributes checkTransactionIfStatusAccept(Integer status, SessionAttributes sessionAttributes) {
        Integer statusCheckLimit = 0
        while (status == ACCEPTED_STATUS && statusCheckLimit < STATUS_CHECK_LIMIT) {
            Thread.sleep(10000)
            status = checkTransactionStatus(sessionAttributes)
            statusCheckLimit++
        }
        return sessionAttributes
    }

    Integer checkTransactionStatus(SessionAttributes sessionAttributes) {
        Integer status = null
        try {
            new HTTPBuilder().get(
                    [uri               : WINDCAVE_BASE,
                     path              : "/api/v1/transactions/${sessionAttributes.transactionId}",
                     contentType       : ContentType.JSON,
                     headers           : [Authorization: "Basic $preferenceController.paymentGatewayPass"],
                    ]) { response, body ->
                logger.info("Get transaction success: ${body.toString()}")
                status = (response as HttpResponseDecorator).getStatus()
                buildSessionAttributes(sessionAttributes, body as Map<String, Object>)
            }
        } catch(Exception e) {
            logger.error("Fail to get payment transaction")
            logger.catching(e)
        }
        return status
    }

    @CompileStatic(TypeCheckingMode.SKIP)
    SessionAttributes checkStatus(String sessionId) {
        SessionAttributes attributes = new SessionAttributes()
        try {
            new HTTPBuilder().get(
                    [uri: WINDCAVE_BASE,
                     path: "/api/v1/sessions/$sessionId",
                     contentType: ContentType.JSON,
                     headers: [Authorization: "Basic $preferenceController.paymentGatewayPass"],
                    ]) { response, body ->
                        /* json response example
                            {
                                "id": "00001101079254190bf45028e73ca86d",
                                "state": "complete",
                                "type": "auth",
                                "amount": "100.22",
                                ......
                                "transactions": [
                                    {
                                        "id": "0000000b6adf206b",
                                        "username": "IshGroupREST_Dev",
                                        "authorised": false,
                                        "allowRetry": false,
                                        "reCo": "12",
                                        "responseText": "INVALID TRANSACTION",
                                        .......
                                    }
                                ]
                            }
                         */

                        attributes.complete = body['state'] == 'complete'
                        buildSessionAttributes(attributes, body['transactions'][0] as Map<String, Object> )
                    }
        } catch (HttpResponseException e) {
            logger.error("Fail to get session status")
            logger.catching(e)
        }

        return attributes
    }

    private void buildSessionAttributes(SessionAttributes attributes, Map<String,Object> transactionMap) {
        if (transactionMap != null) {
            attributes.authorised = transactionMap['authorised']
            attributes.statusText = WindcaveResponseCode.getExplanationByCode(transactionMap['reCo'] as String)
            attributes.transactionId = transactionMap['id']
            attributes.type = transactionMap['type']
            attributes.reCo  = transactionMap['reCo']

            if (attributes.authorised) {
                Map<String, String> card = transactionMap['card'] as Map<String, String>
                if (transactionMap['settlementDate']) {
                    attributes.paymentDate = LocalDate.parse(transactionMap['settlementDate'] as String)
                }
                attributes.creditCardType = values().find { it.name().equalsIgnoreCase(card['type']) }
                attributes.creditCardExpiry = card['dateExpiryMonth'] + '/' + card['dateExpiryYear']
                attributes.creditCardName = card['cardHolderName']
                attributes.creditCardNumber = card['cardNumber']
                attributes.billingId = card['id']
            }
            attributes.responceJson = JsonOutput.prettyPrint(JsonOutput.toJson(transactionMap))
        }
    }

    @CompileStatic(TypeCheckingMode.SKIP)
    SessionAttributes makeRefund(Money amount, String merchantReference, String transactionId) {
        SessionAttributes attributes = new SessionAttributes()

        try {
            HTTPBuilder builder  = new HTTPBuilder()
            builder.handler['failure'] = { response, body -> failHandler(response, body, attributes)}

            builder.handler['success'] = { response, body ->
                buildSessionAttributes(attributes, body as Map<String, Object>)
            }
            builder.post(
                    [uri               : WINDCAVE_BASE,
                     path              : '/api/v1/transactions',
                     contentType       : ContentType.JSON,
                     headers           : [Authorization: "Basic $preferenceController.paymentGatewayPass"],
                     requestContentType: ContentType.JSON,
                     body              : [type             : "refund",
                                          amount           : amount.toPlainString(),
                                          currency         : amount.currencyContext.currencyCode,
                                          merchantReference: merchantReference,
                                          transactionId    : transactionId]
                    ])
        } catch(Exception e) {
            attributes.errorMessage = "Something unexpected has happened, please contact wish support or try again later"
            logger.error("Fail to make refund payment")
            logger.catching(e)
        }
        return attributes
    }

    @CompileStatic(TypeCheckingMode.SKIP)
    SessionAttributes makeTransaction(Money amount, String merchantReference, String cardId) {
        SessionAttributes attributes = new SessionAttributes()
        Integer status = null
        try {
            HTTPBuilder builder  = new HTTPBuilder()
            builder.handler['failure'] = { response, body -> failHandler(response, body, attributes)}

            builder.handler['success'] = { response, body ->
                logger.info("Make transaction success: ${body.toString()}")
                buildSessionAttributes(attributes, body as Map<String, Object>)
                status = (response as HttpResponseDecorator).getStatus()
            }
            builder.post(
                    [uri               : WINDCAVE_BASE,
                     path              : '/api/v1/transactions',
                     contentType       : ContentType.JSON,
                     headers           : [Authorization: "Basic $preferenceController.paymentGatewayPass"],
                     requestContentType: ContentType.JSON,
                     body              : [type             : transactionType,
                                          amount           : amount.toPlainString(),
                                          currency         : amount.currencyContext.currencyCode,
                                          merchantReference: merchantReference,
                                          cardId    : cardId]
                    ])
        } catch(Exception e) {
            attributes.errorMessage = "Something unexpected has happened, please contact ish support or try again later"
            logger.error("Fail to create transaction")
            logger.catching(e)
        }

        // In very rare cases, the server may return a 202 response and only the transaction ID. This happens because the transaction did not have time to be processed by the services in Windcave.
        // Therefore, we will try to get a response through a GET request 3 times in 10 seconds. https://www.windcave.com/developer-e-commerce-api-rest#Server_Side_Post_Walkthrough
        if (status == ACCEPTED_STATUS) {
            attributes = checkTransactionIfStatusAccept(status, attributes)
        }

        return attributes
    }

    private String getTransactionType() {
        preferenceController.isPurchaseWithoutAuth() ? PURCHASE_TYPE : AUTH_TYPE
    }
}
