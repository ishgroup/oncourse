/*
 * Copyright ish group pty ltd 2023.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.checkout.gateway.eway

import com.google.inject.Inject
import groovy.json.JsonOutput
import groovy.transform.CompileStatic
import groovy.transform.TypeCheckingMode
import groovyx.net.http.ContentType
import groovyx.net.http.HTTPBuilder
import groovyx.net.http.HttpResponseException
import ish.common.checkout.gateway.eway.EWayErrorCode
import ish.common.checkout.gateway.eway.EWayResponseMessage
import ish.common.types.CreditCardType
import ish.math.Money
import ish.oncourse.server.PreferenceController
import ish.oncourse.server.cayenne.Contact
import ish.common.checkout.gateway.SessionAttributes
import org.apache.commons.lang.StringUtils
import org.apache.http.HttpResponse
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

import java.time.LocalDate
import java.time.format.DateTimeFormatter

@CompileStatic
class EWayPaymentAPI {

    private static final String EWAY_BASE_PROD = 'https://api.ewaypayments.com'

    private static final String  TRX_PURCHASE_TYPE = "Purchase"
    private static final String  TRX_RECURRING_TYPE = "Recurring"
    private static final String  METHOD_PROCESS_PAYMENT = "ProcessPayment"
    private static final String  METHOD_TOKEN_PAYMENT = "TokenPayment"

    private static final Logger logger = LogManager.getLogger(EWayPaymentAPI)

    protected String eWayBaseUrl

    @Inject
    PreferenceController preferenceController

    EWayPaymentAPI() {
        this.eWayBaseUrl = EWAY_BASE_PROD
    }

    Closure failHandler =  { Object response, Object body, SessionAttributes attributes  ->
        logger.error('Fail to create eWay Access Code (SharedPaymentUrl) {} {}', body, response)
        try {
            if (body && StringUtils.isNotBlank(body["Errors"] as String)) {
                attributes.errorMessage = buildErrorMessages(body["Errors"] as String)
            } else {
                if (response && response instanceof HttpResponse) {
                    attributes.errorMessage = "Fail to create eWay Access Code, eWay responseMessage: $response.statusLine"
                }
            }
        } catch (Exception e) {
            logger.catching(e)
            attributes.errorMessage =  "Something unexpected has happened, please contact wish support or try again later"
        }
    }

    @CompileStatic(TypeCheckingMode.SKIP)
    SessionAttributes createSession(String origin, Money amount, String merchantReference, Boolean storeCard, Contact contact){
        SessionAttributes attributes = new SessionAttributes()
        try {

            HTTPBuilder builder =  new HTTPBuilder()

            builder.handler['failure'] = { response, body -> failHandler(response, body, attributes)}

            builder.handler['success'] = { response, body ->
                logger.info("Create eWay Access Code request finished, response body: ${body?.toString()}")
                attributes.sessionId = body["AccessCode"]
                attributes.ccFormUrl = body["SharedPaymentUrl"]
                if (StringUtils.isNotBlank(body["Errors"] as String)) {
                    attributes.errorMessage = buildErrorMessages(body["Errors"] as String)
                    logger.error("eWay Request failed with errors: $attributes.errorMessage")
                } else {
                    logger.info("eWay Request completed successfully")
                }
            }

            Map<String, Object> body = [transactionType    : TRX_PURCHASE_TYPE,
                                        payment            : [
                                                                // totalAmount*100 (Docs: [1] For AUD, NZD, USD etc. These currencies have a decimal part: a $27.00 AUD transaction would have a TotalAmount = '2700')
                                                                totalAmount: amount.multiply(100).toInteger(),
                                                                currencyCode: amount.currencyContext.currencyCode,
                                                                invoiceReference: merchantReference
                                        ],
                                        language           : "en",
                                        redirectUrl        : origin + '/checkout?paymentStatus=success',
                                        cancelUrl          : origin + '/checkout?paymentStatus=cancel',
                                    // 'firstName', 'lastName', 'country' are required fields when method is 'TokenPayment'. 'email', 'phone' fields are shown when users enters card details in iframe payment field
                                        Customer           : [
                                                                firstName: contact.firstName,
                                                                lastName: contact.lastName,
                                                                country: amount.getCurrencyContext().locale.country,
                                                                email: contact.email,
                                                                phone: contact.mobilePhone
                                        ],
                                       customerReadOnly    : true
            ]

            if (storeCard) {
                body['method'] = METHOD_TOKEN_PAYMENT
                // 'firstName' is required fields when method is 'TokenPayment'.
                if (StringUtils.isBlank(contact.firstName)) {
                    body['Customer']['firstName'] = contact.lastName
                }
            } else {
                body['method'] = METHOD_PROCESS_PAYMENT
            }

            builder.post(
                    [uri: eWayBaseUrl,
                     path: '/AccessCodesShared',
                     contentType: ContentType.JSON,
                     headers: [
                                Authorization: "Basic $preferenceController.paymentGatewayPassEway",
                                "X-EWAY-APIVERSION": 47
                     ],
                     requestContentType: ContentType.JSON,
                     body: body
                    ])

        } catch (HttpResponseException e) {
            logger.error("Fail to create eWay Access Code (SharedPaymentUrl)")
            logger.catching(e)
        }

        return attributes
    }

    SessionAttributes getTransaction(String accessCodeOrTransactionId) {
        SessionAttributes attributes = new SessionAttributes()
        try {
            new HTTPBuilder().get(
                    [uri: eWayBaseUrl,
                     path: "/transaction/$accessCodeOrTransactionId",
                     contentType: ContentType.JSON,
                     headers: [
                                Authorization: "Basic $preferenceController.paymentGatewayPassEway",
                                "X-EWAY-APIVERSION": 47
                              ],
                    ]) { response, body ->
                /* json response example
                    {
                        "Transactions": [
                            {
                                "TransactionDateTime": "2023-03-14T07:24:35+11:00",
                                "FraudAction": "",
                                "TransactionCaptured": true,
                                "TransactionType": 1,
                                "CurrencyCode": "036",
                                "Source": 0,
                                "MaxRefund": 100,
                                "OriginalTransactionId": null,
                                "Customer": {
                                    "CardDetails": {
                                        "CardType": "UNKNOWN",
                                        "Number": "992266XXXXXX2650",
                                        "Name": "John Smith",
                                        "ExpiryMonth": "12",
                                        "ExpiryYear": "23",
                                        "StartMonth": null,
                                        "StartYear": null,
                                        "IssueNumber": null
                                    },
                                    "TokenCustomerID": null,
                                    "Reference": "",
                                    "Title": "Mr.",
                                    "FirstName": "John",
                                    "LastName": "Smith",
                                    "CompanyName": "",
                                    "JobDescription": "",
                                    "Street1": null,
                                    "Street2": "",
                                    "City": "",
                                    "State": "",
                                    "PostalCode": "",
                                    "Country": "AU",
                                    "Email": "test@mail.ru",
                                    "Phone": "123242424",
                                    "Mobile": "",
                                    "Comments": "",
                                    "Fax": null,
                                    "Url": ""
                                },
                                "AuthorisationCode": "445905",
                                "ResponseCode": "00",
                                "ResponseMessage": "A2000",
                                "InvoiceNumber": "",
                                "InvoiceReference": "testInvoiceReference",
                                "InvoiceDescription": "",
                                "TotalAmount": 100,
                                "TransactionID": 35819868,
                                "TransactionStatus": true,
                                "TokenCustomerID": 914170567371,
                                "BeagleScore": null,
                                "Options": [],
                                "Verification": {
                                    "CVN": 0,
                                    "Address": 0,
                                    "Email": 0,
                                    "Mobile": 0,
                                    "Phone": 0
                                },
                                "BeagleVerification": {
                                    "Email": 0,
                                    "Phone": 0
                                },
                                "CustomerNote": null,
                                "ShippingAddress": {
                                    "ShippingMethod": "Unknown",
                                    "FirstName": "",
                                    "LastName": "",
                                    "Street1": "",
                                    "Street2": "",
                                    "City": "",
                                    "State": "",
                                    "Country": "",
                                    "PostalCode": "",
                                    "Email": "",
                                    "Phone": "",
                                    "Fax": null
                                },
                                "Items": [],
                                "PaymentInstrument": {
                                    "ThreeDSecureAuth": {
                                        "Cryptogram": null,
                                        "ECI": null,
                                        "XID": null,
                                        "AuthStatus": null,
                                        "Version": null,
                                        "dsTransactionId": null
                                    },
                                    "PaymentType": "CreditCard"
                                }
                            }
                        ],
                        "TotalRows": 1,
                        "Errors": ""
                    }
                 */

                logger.info("Get eWay transaction request finished, response body: ${body?.toString()}")
                buildSessionAttributesFromArrayOfOneTransaction(attributes, body as Map<String, Object>)
                logStatusOrError(attributes)
            }
        } catch (HttpResponseException e) {
            logger.error("Fail to get eWay session status")
            logger.catching(e)
        }


        return attributes
    }

    SessionAttributes sendTwoStepPayment(Money amount, String merchantReference, String cardDataId) {
        SessionAttributes attributes = new SessionAttributes()
        try {
            HTTPBuilder builder  = new HTTPBuilder()
            builder.handler['failure'] = {
                response, body -> failHandler(response, body, attributes)
            }

            builder.handler['success'] = { response, body ->
                logger.info("Make eWay capture request finished, response body: ${body.toString()}")
                buildSessionAttributesFromTransaction(attributes, body as Map<String, Object>)
                attributes.clientSecret = body["AuthorisationCode"]
                logStatusOrError(attributes)
            }

            builder.post(
                    [uri               : eWayBaseUrl,
                     path              : '/transaction',
                     contentType       : ContentType.JSON,
                     headers           : [
                             Authorization: "Basic $preferenceController.paymentGatewayPassEway",
                             "X-EWAY-APIVERSION": 47
                     ],
                     requestContentType: ContentType.JSON,
                     body              : [
                             transactionType    : TRX_PURCHASE_TYPE,
                             Method             : METHOD_PROCESS_PAYMENT,
                             SecuredCardData    : cardDataId,
                             payment            : [
                                     // totalAmount*100 (Docs: [1] For AUD, NZD, USD etc. These currencies have a decimal part: a $27.00 AUD transaction would have a TotalAmount = '2700')
                                     totalAmount: amount.multiply(100).toInteger(),
                                     // 'currencyCode' must be taken from 'preferenceController.country.currencySymbol()', but all colleges are from Australia that is why currency code will be 'AUD'
                                     currencyCode: amount.currencyContext.currencyCode,
                                     invoiceReference: merchantReference
                             ]
                     ]
                    ])
        } catch(Exception e) {
            attributes.errorMessage = "Something unexpected has happened, please contact ish support or try again later"
            logger.error("Fail to create eWay transaction")
            logger.catching(e)
        }
        return attributes
    }

    SessionAttributes verify3dSecure(String secureCode) {
        SessionAttributes attributes = new SessionAttributes()
        try {
            HTTPBuilder builder  = new HTTPBuilder()
            builder.handler['failure'] = { response, body -> failHandler(response, body, attributes)}

            builder.handler['success'] = { response, body ->
                logger.info("Make eWay capture request finished, response body: ${body.toString()}")
                buildSessionAttributesFrom3dSecureResponse(attributes, body as Map<String, Object>)
                logStatusOrError(attributes)
            }
            builder.post(
                    [uri               : eWayBaseUrl,
                     path              : '/3dsverify',
                     contentType       : ContentType.JSON,
                     headers           : [
                             Authorization: "Basic $preferenceController.paymentGatewayPassEway",
                             "X-EWAY-APIVERSION": 47
                     ],
                     requestContentType: ContentType.JSON,
                     body              : [
                             AccessCode    : secureCode
                     ]
                    ])
        } catch(Exception e) {
            attributes.errorMessage = "Something unexpected has happened, please contact ish support or try again later"
            logger.error("Fail to create eWay transaction")
            logger.catching(e)
        }
        return attributes
    }

    @CompileStatic(TypeCheckingMode.SKIP)
    SessionAttributes makeTransaction(Money amount, String merchantReference, String cardId) {
        SessionAttributes attributes = new SessionAttributes()
        try {
            HTTPBuilder builder  = new HTTPBuilder()
            builder.handler['failure'] = { response, body -> failHandler(response, body, attributes)}

            builder.handler['success'] = { response, body ->
                logger.info("Make eWay transaction request finished, response body: ${body.toString()}")
                buildSessionAttributes(attributes, body as Map<String, Object>)
                logStatusOrError(attributes)
            }
            builder.post(
                    [uri               : eWayBaseUrl,
                     path              : '/transaction',
                     contentType       : ContentType.JSON,
                     headers           : [
                                            Authorization: "Basic $preferenceController.paymentGatewayPassEway",
                                            "X-EWAY-APIVERSION": 47
                                         ],
                     requestContentType: ContentType.JSON,
                     body              : [
                                            transactionType    : TRX_RECURRING_TYPE,
                                            payment            : [
                                                                    // totalAmount*100 (Docs: [1] For AUD, NZD, USD etc. These currencies have a decimal part: a $27.00 AUD transaction would have a TotalAmount = '2700')
                                                                    totalAmount: amount.multiply(100).toInteger(),
                                                                    currencyCode: amount.currencyContext.currencyCode,
                                                                    invoiceReference: merchantReference
                                            ],
                                            language           : "en",
                                            method             : METHOD_PROCESS_PAYMENT,
                                            customer           : [
                                                                    TokenCustomerID: cardId
                                            ]
                                         ]
                    ])
        } catch(Exception e) {
            attributes.errorMessage = "Something unexpected has happened, please contact ish support or try again later"
            logger.error("Fail to create eWay transaction")
            logger.catching(e)
        }
        return attributes
    }

    @CompileStatic(TypeCheckingMode.SKIP)
    SessionAttributes makeRefund(Money amount, String merchantReference, String transactionId) {
        SessionAttributes attributes = new SessionAttributes()
        try {
            HTTPBuilder builder  = new HTTPBuilder()
            builder.handler['failure'] = { response, body -> failHandler(response, body, attributes)}

            builder.handler['success'] = { response, body ->
                logger.info("Refund eWay transaction request finished, response body: ${body?.toString()}")
                buildSessionAttributes(attributes, body as Map<String, Object>)
                logStatusOrError(attributes)
            }
            builder.post(
                    [uri               : eWayBaseUrl,
                     path              : "/transaction/$transactionId/refund",
                     contentType       : ContentType.JSON,
                     headers           : [
                                            Authorization: "Basic $preferenceController.paymentGatewayPassEway",
                                            "X-EWAY-APIVERSION": 47
                                        ],
                     requestContentType: ContentType.JSON,
                     body              : [
                                        refund     : [
                                                        // totalAmount*100 (Docs: [1] For AUD, NZD, USD etc. These currencies have a decimal part: a $27.00 AUD transaction would have a TotalAmount = '2700')
                                                        amount              : amount.multiply(100).toInteger(),
                                                        invoiceReference    : merchantReference,
                                                        currencyCode        : amount.currencyContext.currencyCode,
                                                     ],
                                        ]
                    ])
        } catch(Exception e) {
            attributes.errorMessage = "Something unexpected has happened, please contact wish support or try again later"
            logger.error("Fail to make eWay refund payment")
            logger.catching(e)
        }
        return attributes
    }

    @CompileStatic(TypeCheckingMode.SKIP)
    static void buildSessionAttributes(SessionAttributes attributes, Map<String, Object> body) {
        if (body) {
            buildSessionAttributesFromTransaction(attributes, body)
            if (StringUtils.isNotBlank(body["Errors"] as String)) {
                attributes.errorMessage = buildErrorMessages(body["Errors"] as String)
            }
            attributes.responceJson = JsonOutput.prettyPrint(JsonOutput.toJson(body))
        }
    }

    @CompileStatic(TypeCheckingMode.SKIP)
    static void buildSessionAttributesFromArrayOfOneTransaction(SessionAttributes attributes, Map<String, Object> body) {
        if (body) {
            if (body['Transactions'] && body['TotalRows'] == 1 && ['Transactions'][0]) {
                Map<String, Object> transaction = body['Transactions'][0] as Map<String, Object>
                buildSessionAttributesFromTransaction(attributes, transaction)
            }
            if (StringUtils.isNotBlank(body["Errors"] as String)) {
                attributes.errorMessage = buildErrorMessages(body["Errors"] as String)
            }
            attributes.responceJson = JsonOutput.prettyPrint(JsonOutput.toJson(body))
        }
    }

    @CompileStatic(TypeCheckingMode.SKIP)
    static void buildSessionAttributesFrom3dSecureResponse(SessionAttributes attributes, Map<String, Object> secureResponse) {
        attributes.complete = secureResponse['Enrolled']
        if(secureResponse['ThreeDSecureAuth']) {
            attributes.transactionId = secureResponse['ThreeDSecureAuth']['34898df8-aeaf-4cc1-9200-b18c88b52522']
        }
        attributes.errorMessage = secureResponse['Errors']
        attributes.responceJson = JsonOutput.prettyPrint(JsonOutput.toJson(secureResponse))
    }

    @CompileStatic(TypeCheckingMode.SKIP)
    static void buildSessionAttributesFromTransaction(SessionAttributes attributes, Map<String, Object> transaction) {
        attributes.complete = transaction['TransactionStatus']
        attributes.authorised = StringUtils.isNotBlank(transaction['AuthorisationCode'] as String)
        attributes.transactionId = transaction['TransactionID']
        attributes.statusText = EWayResponseMessage.getExplanationByCode(transaction['ResponseMessage'] as String)
        attributes.reCo  = transaction['ResponseMessage']
        if (transaction['TransactionDateTime']) {
            attributes.paymentDate = LocalDate.parse(transaction['TransactionDateTime'] as String, DateTimeFormatter.ISO_DATE_TIME)
        }
        attributes.billingId = transaction['TokenCustomerID']

        if (transaction['Customer']) {
            if (transaction['Customer']['CardDetails']) {
                Map<String, String> card = transaction['Customer']['CardDetails'] as Map<String, String>
                attributes.creditCardExpiry = card['ExpiryMonth'] + '/' + card['ExpiryYear']
                attributes.creditCardName = card['Name']
                attributes.creditCardNumber = card['Number']
                attributes.creditCardType = mapCreditCardType(card['CardType'] as String)
            }
            if (!attributes.billingId) {
                attributes.billingId = transaction['Customer']['TokenCustomerID']
            }
        }
    }

//  eWay can return credit types such as "VI", "MC", "AX", "UNKNOWN", 0.
    static CreditCardType mapCreditCardType(String creditCardTypeEWay) {
        switch (creditCardTypeEWay) {
            case "VI":
                return CreditCardType.VISA
            case "MC":
                return CreditCardType.MASTERCARD
            case "AX":
                return CreditCardType.AMEX
            default:
                logger.error("Can't map eWay credit card type ($creditCardTypeEWay) to CreditCardType.")
        }
    }

    static String buildErrorMessages(String errorCodes) {
        List<String> list = Arrays.asList(errorCodes.split("\\s*,\\s*"))
        return list.collect { EWayErrorCode.getErrorMessageByCode(it.trim()) ?: it}?.toString()
    }

    static void logStatusOrError(SessionAttributes attributes) {
        if (attributes.errorMessage) {
            logger.error("eWay Request failed with errors: $attributes.errorMessage")
        } else {
            logger.info("eWay Request completed successfully")
        }
    }
}
