package ish.oncourse.willow.checkout.windcave

import groovy.transform.CompileStatic
import groovy.transform.TypeCheckingMode
import ish.common.types.CreditCardType
import ish.math.Money


class TestPaymentService implements IPaymentService {

    Boolean isSkipAuth() {
        return false
    }

    static final String VALID_TEST_ID = 'testSessionId'
    static final String INVALID_TEST_ID = 'invalidTestSessionId'

    @CompileStatic(TypeCheckingMode.SKIP)
    SessionAttributes createSession(String origin, Money amount, String merchantReference, Boolean storeCard) {
        SessionAttributes result = new SessionAttributes()
        String delimiter = new URL(origin).query ? '&' : '?'
        origin += delimiter
        result.sessionId = VALID_TEST_ID
        result.ccFormUrl = "${origin}paymentStatus=success&sessionId=$VALID_TEST_ID"
        return result
    }

    SessionAttributes completeTransaction(String transactionId, Money amount, String merchantReference) {
        SessionAttributes result = new SessionAttributes()
        if (VALID_TEST_ID) {
            result.authorised = true
            result.statusText = 'Transaction Approved'
            result.responceJson = "{\n" +
                    "    \"id\": \"0000002548t403g16\",\n" +
                    "    \"username\": \"IshGroupREST_Dev\",\n" +
                    "    \"authorised\": true,\n" +
                    "    \"allowRetry\": false,\n" +
                    "    \"reCo\": \"00\",\n" +
                    "    \"responseText\": \"APPROVED\",\n" +
                    "    \"authCode\": \"184550\",\n" +
                    "    \"type\": \"complete\",\n" +
                    "    \"method\": \"card\",\n" +
                    "    \"localTimeZone\": \"NZT\",\n" +
                    "    \"dateTimeUtc\": \"2020-08-04T06:55:50Z\",\n" +
                    "    \"dateTimeLocal\": \"2020-08-04T18:55:50+12:00\",\n" +
                    "    \"settlementDate\": \"2020-08-04\",\n" +
                    "    \"amount\": \"100.00\",\n" +
                    "    \"balanceAmount\": \"0.00\",\n" +
                    "    \"currency\": \"GBP\",\n" +
                    "    \"currencyNumeric\": 826,\n" +
                    "    \"clientType\": \"internet\",\n" +
                    "    \"merchantReference\": \"27534378-b817-4d46-8321-a0aa728b8768\",\n" +
                    "    \"card\": {\n" +
                    "        \"cardHolderName\": \"JOHN\",\n" +
                    "        \"cardNumber\": \"543111......1111\",\n" +
                    "        \"dateExpiryMonth\": \"08\",\n" +
                    "        \"dateExpiryYear\": \"20\",\n" +
                    "        \"type\": \"mastercard\"\n" +
                    "    },\n" +
                    "    \"cvc2ResultCode\": \"P\",\n" +
                    "    \"storedCardIndicator\": \"single\",\n" +
                    "    \"sessionId\": \"000033000293686721c3e3cd551fc139\",\n" +
                    "    \"browser\": {\n" +
                    "        \"ipAddress\": \"37.45.236.112\",\n" +
                    "        \"userAgent\": \"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/83.0.4103.116 Safari/537.36\"\n" +
                    "    },\n" +
                    "    \"isSurcharge\": false,\n" +
                    "    \"liabilityIndicator\": \"standard\",\n" +
                    "    \"links\": [\n" +
                    "        {\n" +
                    "            \"href\": \"https://sec.windcave.com/api/v1/transactions/0000002148b102b1\",\n" +
                    "            \"rel\": \"self\",\n" +
                    "            \"method\": \"GET\"\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"href\": \"https://sec.windcave.com/api/v1/sessions/000033000293686721c3e3cd551fc139\",\n" +
                    "            \"rel\": \"session\",\n" +
                    "            \"method\": \"GET\"\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"href\": \"https://sec.windcave.com/api/v1/transactions\",\n" +
                    "            \"rel\": \"complete\",\n" +
                    "            \"method\": \"POST\"\n" +
                    "        }\n" +
                    "    ]\n" +
                    "}"
        }
        return result
    }

    @CompileStatic(TypeCheckingMode.SKIP)
    SessionAttributes checkStatus(String sessionId) {
        SessionAttributes result = new SessionAttributes()
        result.sessionId = VALID_TEST_ID
        result.complete = true
        result.type = 'auth'
        result.responceJson = "{\n" +
                "    \"id\": \"0000002148c403m1\",\n" +
                "    \"username\": \"IshGroupREST_Dev\",\n" +
                "    \"authorised\": true,\n" +
                "    \"allowRetry\": false,\n" +
                "    \"reCo\": \"00\",\n" +
                "    \"responseText\": \"APPROVED\",\n" +
                "    \"authCode\": \"185550\",\n" +
                "    \"type\": \"auth\",\n" +
                "    \"method\": \"card\",\n" +
                "    \"localTimeZone\": \"NZT\",\n" +
                "    \"dateTimeUtc\": \"2020-08-04T06:55:50Z\",\n" +
                "    \"dateTimeLocal\": \"2020-08-04T18:55:50+12:00\",\n" +
                "    \"settlementDate\": \"2020-08-04\",\n" +
                "    \"amount\": \"100.00\",\n" +
                "    \"balanceAmount\": \"0.00\",\n" +
                "    \"currency\": \"GBP\",\n" +
                "    \"currencyNumeric\": 826,\n" +
                "    \"clientType\": \"internet\",\n" +
                "    \"merchantReference\": \"27534378-b817-4d46-8321-a0aa728b8768\",\n" +
                "    \"card\": {\n" +
                "        \"cardHolderName\": \"JOHN\",\n" +
                "        \"cardNumber\": \"543111......1111\",\n" +
                "        \"dateExpiryMonth\": \"08\",\n" +
                "        \"dateExpiryYear\": \"20\",\n" +
                "        \"type\": \"mastercard\"\n" +
                "    },\n" +
                "    \"cvc2ResultCode\": \"P\",\n" +
                "    \"storedCardIndicator\": \"single\",\n" +
                "    \"sessionId\": \"000033000293686721c3e3cd551fc139\",\n" +
                "    \"browser\": {\n" +
                "        \"ipAddress\": \"37.45.236.112\",\n" +
                "        \"userAgent\": \"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/83.0.4103.116 Safari/537.36\"\n" +
                "    },\n" +
                "    \"isSurcharge\": false,\n" +
                "    \"liabilityIndicator\": \"standard\",\n" +
                "    \"links\": [\n" +
                "        {\n" +
                "            \"href\": \"https://sec.windcave.com/api/v1/transactions/0000002148b102b1\",\n" +
                "            \"rel\": \"self\",\n" +
                "            \"method\": \"GET\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"href\": \"https://sec.windcave.com/api/v1/sessions/000033000293686721c3e3cd551fc139\",\n" +
                "            \"rel\": \"session\",\n" +
                "            \"method\": \"GET\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"href\": \"https://sec.windcave.com/api/v1/transactions\",\n" +
                "            \"rel\": \"complete\",\n" +
                "            \"method\": \"POST\"\n" +
                "        }\n" +
                "    ]\n" +
                "}"
        result.transactionId = '0000002148c403m1'
        result.billingId = null


        if (VALID_TEST_ID == sessionId) {
            result.authorised = true
            result.statusText = 'Transaction Approved'
            result.creditCardExpiry ='08/20'
            result.creditCardName = 'JOHN'
            result.creditCardNumber = '543111XXXXXXX111'
            result.creditCardType = CreditCardType.MASTERCARD
            result.paymentDate = new Date()
        } else if (INVALID_TEST_ID == sessionId) {
            result.authorised = false
            result.statusText = 'Transaction Declined'
        }
        return result
    }

}
