/*
 * Copyright ish group pty ltd 2023.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.eway

import com.google.inject.Inject
import groovy.json.JsonOutput
import groovy.transform.CompileStatic
import groovy.transform.TypeCheckingMode
import groovyx.net.http.ContentType
import groovyx.net.http.HTTPBuilder
import groovyx.net.http.HttpResponseException
import ish.math.Money
import ish.oncourse.server.PreferenceController
import ish.oncourse.server.windcave.SessionAttributes
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

@CompileStatic
class EWayPaymentService {

    // QTEwMDFDWis4cnlMd21VTEs5UU84U1d0UllrYXZVdVNBVW5ybUh5d1Q0WC92V0xBNXVHdzRBRDRxSXF2VXdoaTdQOThsVjphNUpOcUFMUw==

    private static final String EWAY_BASE = 'https://api.sandbox.ewaypayments.com'
    // MOTO, Recurring - no auth
    public static final String  AUTH_TYPE = "auth"
    public static final String  PURCHASE_TYPE = "Purchase"

    private static final Logger logger = LogManager.getLogger(EWayPaymentService)

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
    SessionAttributes createAccessCodeShared(String origin, Money amount, String merchantReference, Boolean storeCard){
        SessionAttributes attributes = new SessionAttributes()

        try {

            HTTPBuilder builder =  new HTTPBuilder()

            builder.handler['failure'] = {
                response, body -> failHandler(response, body, attributes)
            }

            builder.handler['success'] = { response, body ->
                attributes.sessionId = body["AccessCode"]
                attributes.ccFormUrl = body["SharedPaymentUrl"] + "&View=Modal"
            }

            Map<String, Object> body = [transactionType    : "Purchase",
//                                        transactionType    : transactionType,
                                        payment            : [
//                                                          ???totalAmount*100 (Docs: [1] For AUD, NZD, USD etc. These currencies have a decimal part: a $27.00 AUD transaction would have a TotalAmount = '2700')
                                                            totalAmount: amount.toFloat(),
//                                                currencyCode: preferenceController.country.currencySymbol(),
                                                            currencyCode: "AUD",
                                                            invoiceReference: merchantReference
                                        ],
//                                        amountSurcharge    : "0.00",
                                        language           : "en",
                                        method            : "ProcessPayment",
                                        redirectUrl        : origin + '/checkout?paymentStatus=success',
                                        cancelUrl          : origin + '/checkout?paymentStatus=cancel'
//                                        callbackUrls       : [
//                                                approved :  origin + '/checkout?paymentStatus=success',
//                                                declined :  origin + '/checkout?paymentStatus=fail',
//                                                cancelled:  origin + '/checkout?paymentStatus=cancel'
//                                        ]
            ]

//            if (storeCard) {
//                body['storeCard'] = true
//                body['storedCardIndicator'] = 'recurringinitial'
//            } else {
//                body['storeCard'] = false
//            }

            builder.post(
                    [uri: EWAY_BASE,
                     path: '/AccessCodesShared',
                     contentType: ContentType.JSON,
                     //"Basic $preferenceController.paymentGatewayPass"],
                     headers: [Authorization: "Basic QTEwMDFDWis4cnlMd21VTEs5UU84U1d0UllrYXZVdVNBVW5ybUh5d1Q0WC92V0xBNXVHdzRBRDRxSXF2VXdoaTdQOThsVjphNUpOcUFMUw=="],
                     requestContentType: ContentType.JSON,
                     body: body
                    ])

        } catch (HttpResponseException e) {
            logger.error("Fail Access Code Shared")
            logger.catching(e)
        }

        return attributes
    }

    @CompileStatic(TypeCheckingMode.SKIP)
    SessionAttributes checkStatus(String accessCode) {
        SessionAttributes attributes = new SessionAttributes()
        try {
            new HTTPBuilder().get(
                    [uri: EWAY_BASE,
                     path: "/AccessCode/$accessCode",
                     contentType: ContentType.JSON,
                     headers: [Authorization: "Basic QTEwMDFDWis4cnlMd21VTEs5UU84U1d0UllrYXZVdVNBVW5ybUh5d1Q0WC92V0xBNXVHdzRBRDRxSXF2VXdoaTdQOThsVjphNUpOcUFMUw=="],
//                     headers: [Authorization: "Basic $preferenceController.paymentGatewayPass"],
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

                attributes.complete = body['TransactionStatus']
                attributes.authorised = body['AuthorisationCode'] != null
                attributes.transactionId = body['TransactionID']
                attributes.type = body['AuthorisationCode'] != null ? "auth" : null
                //Do correct
                attributes.responceJson = JsonOutput.prettyPrint(JsonOutput.toJson(body))
//                println (response + "===" + body)
//                buildSessionAttributes(attributes, body['transactions'][0] as Map<String, Object> )
            }
        } catch (HttpResponseException e) {
            logger.error("Fail to get session status")
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
                //Write correct mapping
                attributes.complete = body['TransactionStatus']
                attributes.authorised = body['AuthorisationCode'] != null
                attributes.transactionId = body['TransactionID']
                attributes.type = body['AuthorisationCode'] != null ? "auth" : null
            }
            builder.post(
                    [uri               : EWAY_BASE,
                     path              : "/transaction/$transactionId/refund",
                     contentType       : ContentType.JSON,
//                     headers           : [Authorization: "Basic $preferenceController.paymentGatewayPass"],
                     headers           : [Authorization: "Basic QTEwMDFDWis4cnlMd21VTEs5UU84U1d0UllrYXZVdVNBVW5ybUh5d1Q0WC92V0xBNXVHdzRBRDRxSXF2VXdoaTdQOThsVjphNUpOcUFMUw=="],
                     requestContentType: ContentType.JSON,
                     body              : [refund           : [
//                             ???totalAmount*100 (Docs: [1] For AUD, NZD, USD etc. These currencies have a decimal part: a $27.00 AUD transaction would have a TotalAmount = '2700')
                                                            amount              : amount.toFloat(),
                                                            invoiceReference    : merchantReference,
//                                                            currencyCode        : preferenceController.country.currencySymbol(),
                                                            currencyCode        : "AUD",
                                                            ],
                                          ]
                    ])
        } catch(Exception e) {
            attributes.errorMessage = "Something unexpected has happened, please contact wish support or try again later"
            logger.error("Fail to make refund payment")
            logger.catching(e)
        }
        return attributes
    }

    private String getTransactionType() {
        preferenceController.isPurchaseWithoutAuth() ? PURCHASE_TYPE : AUTH_TYPE
    }
}
