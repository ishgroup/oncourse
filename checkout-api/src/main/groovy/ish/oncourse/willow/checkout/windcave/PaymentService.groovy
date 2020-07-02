package ish.oncourse.willow.checkout.windcave

import groovy.json.JsonOutput
import groovy.transform.CompileStatic
import groovy.transform.TypeCheckingMode
import groovyx.net.http.ContentType
import groovyx.net.http.HTTPBuilder
import ish.common.types.CreditCardType
import ish.math.Country
import ish.math.Money
import ish.oncourse.model.College
import ish.oncourse.model.PaymentGatewayType
import ish.oncourse.services.preference.GetPreference
import org.apache.cayenne.ObjectContext
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

import java.time.LocalDate
import java.time.format.DateTimeFormatter

import static ish.oncourse.services.preference.Preferences.PAYMENT_GATEWAY_TYPE
import static ish.persistence.Preferences.ACCOUNT_CURRENCY
import static ish.persistence.Preferences.PAYMENT_GATEWAY_PASS
import static ish.persistence.Preferences.PAYMENT_GATEWAY_PURCHASE_WITHOUT_AUTH

@CompileStatic
class PaymentService {

    private static final String WINDCAVE_BASE = 'https://sec.windcave.com'
    public static final String  AUTH_TYPE = "auth"
    public static final String  PURCHASE_TYPE = "purchase"
    public static final String DEFAULT_ERROR_MESSAGE =  "Something unexpected has happened, please contact ish support or try again later"

    private static final Logger logger = LogManager.getLogger(PaymentService)
    private String gatewayPass
    private boolean skipAuth
    private Country country

    PaymentService(College college, ObjectContext context) {

        PaymentGatewayType gatewayType = PaymentGatewayType.valueOf(new GetPreference(college, PAYMENT_GATEWAY_TYPE, context).getValue())

        switch (gatewayType) {
            case PaymentGatewayType.TEST:
                this.gatewayPass = "SXNoR3JvdXBSRVNUX0RldjphOTljYWUxYmRlZGJjNGU5ODQ3OTNmZjNhNjkwMDM5ZTdlZWUyOTgyMmQ0ZDQzZDg2M2JkZDE4NGNlOTk4NmRj"
                this.skipAuth = false
                break
            case PaymentGatewayType.PAYMENT_EXPRESS:
                this.gatewayPass = new GetPreference(college, PAYMENT_GATEWAY_PASS, context).getValue()
                this.skipAuth = new GetPreference(college, PAYMENT_GATEWAY_PURCHASE_WITHOUT_AUTH, context).getBooleanValue()
                break
            case PaymentGatewayType.DISABLED:
            default:
                throw new IllegalArgumentException()
        }

        this.country =  new GetPreference(college, ACCOUNT_CURRENCY, context).getCountry()
    }

    Boolean isSkipAuth() {
        return this.skipAuth
    }

    Closure failHandler =  { Object response, Object body, SessionAttributes attributes  ->
        logger.error('Fail to create session {} {}', body, response)
        try {
            if (body['errors'] && !(body['errors'] as List).empty) {
                attributes.errorMessage = (body['errors'] as List).collect {it['message']}.join('\n')
            }
        } catch (Exception e) {
            logger.catching(e)
            attributes.errorMessage = DEFAULT_ERROR_MESSAGE
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
                                        currency           : country.currencySymbol(),
                                        merchantReference  : merchantReference,
                                        language           : "en",
                                        methods            : ["card"],
                                        callbackUrls       : [
                                                approved :  origin + '?paymentStatus=success',
                                                declined :  origin + '?paymentStatus=fail',
                                                cancelled:  origin + '?paymentStatus=cancel'
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
                     headers: [Authorization: "Basic $gatewayPass"],
                     requestContentType: ContentType.JSON,
                     body: body
                    ])
        } catch (Exception e) {
            logger.error("Fail to create session")
            logger.catching(e)
        }

        return attributes
    }

    SessionAttributes completeTransaction(String transactionId, Money amount, String merchantReference) {
        SessionAttributes result = new SessionAttributes()
        try {
            new HTTPBuilder().post(
                    [uri               : WINDCAVE_BASE,
                     path              : '/api/v1/transactions',
                     contentType       : ContentType.JSON,
                     headers           : [Authorization: "Basic $gatewayPass"],
                     requestContentType: ContentType.JSON,
                     body              : [type             : "complete",
                                          amount           : amount.toPlainString(),
                                          amountSurcharge  : "0.00",
                                          currency         : country.currencySymbol(),
                                          merchantReference: merchantReference,
                                          transactionId    : transactionId]
                    ]) { response, body ->
                logger.info("Complite transaction success: ${body.toString()}")
                buildSessionAttributes(result, body as Map<String, Object>)
            }
        } catch(Exception e) {
            logger.error("Fail to complete payment transaction")
            logger.catching(e)
        }

        return result
    }

    @CompileStatic(TypeCheckingMode.SKIP)
    SessionAttributes checkStatus(String sessionId) {
        SessionAttributes attributes = new SessionAttributes()
        try {
            new HTTPBuilder().get(
                    [uri: WINDCAVE_BASE,
                     path: "/api/v1/sessions/$sessionId",
                     contentType: ContentType.JSON,
                     headers: [Authorization: "Basic $gatewayPass"],
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
        } catch (Exception e) {
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

            if (attributes.authorised) {
                Map<String, String> card = transactionMap['card'] as Map<String, String>
                if (transactionMap['settlementDate']) {
                    attributes.paymentDate = Date.parse('yyyy-MM-dd', (transactionMap['settlementDate'] as String))
                }
                attributes.creditCardType = CreditCardType.values().find { it.name().equalsIgnoreCase(card['type']) }
                attributes.creditCardExpiry = card['dateExpiryMonth'] + '/' + card['dateExpiryYear']
                attributes.creditCardName = card['cardHolderName']
                attributes.creditCardNumber = card['cardNumber']
                attributes.billingId = card['id']
            }
            attributes.responceJson = JsonOutput.prettyPrint(JsonOutput.toJson(transactionMap))
        }
    }

    private String getTransactionType() {
        skipAuth ? PURCHASE_TYPE : AUTH_TYPE
    }
}
