package ish.oncourse.willow.checkout.v2

import ish.common.types.CreditCardType
import ish.math.Money
import ish.oncourse.model.PaymentIn
import ish.oncourse.services.paymentexpress.TestPaymentGatewayService
import ish.oncourse.willow.checkout.CheckoutApiImpl
import ish.oncourse.willow.checkout.windcave.TestPaymentService
import ish.oncourse.willow.model.checkout.payment.PaymentStatus
import ish.oncourse.willow.model.common.CommonError
import ish.oncourse.willow.model.v2.checkout.payment.PaymentRequest
import ish.oncourse.willow.model.v2.checkout.payment.PaymentResponse
import ish.oncourse.willow.service.ApiTest
import org.apache.cayenne.query.ObjectSelect
import org.junit.Test

import javax.ws.rs.BadRequestException

import static ish.oncourse.willow.filters.RequestFilter.ThreadLocalPayerId
import static ish.oncourse.willow.filters.XValidateFilter.ThreadLocalValidateOnly
import static org.junit.Assert.*


class PaymentTest extends ApiTest {
    @Override
    protected String getDataSetResource() {
        return 'ish/oncourse/willow/checkout/v2/PaymentTest.xml'
    }

    @Test
    void testCCPayment() {
        ThreadLocalPayerId.set(1005l)
        ThreadLocalValidateOnly.set(true)

        CheckoutApiImpl api = new CheckoutApiImpl(cayenneService, collegeService, financialService, entityRelationService)
        PaymentRequest request = new PaymentRequest()
        request.setCcAmount(50.0)

        PaymentResponse responce =  api.makePayment(request, true, '1005', "https://localhost")
        PaymentIn payment = ObjectSelect.query(PaymentIn).selectOne(cayenneService.newContext())
        assertNull(payment)
        assertNull(responce.status)
        assertNotNull(responce.sessionId)
        assertNotNull(responce.paymentFormUrl)
        assertNotNull(responce.merchantReference)
        request.sessionId = responce.sessionId
        request.merchantReference = responce.merchantReference

        ThreadLocalValidateOnly.set(false)

        responce =  api.makePayment(request, false, '1005', "https://localhost")

        assertEquals(responce.status, PaymentStatus.SUCCESSFUL)
        assertEquals(responce.reference, request.merchantReference)

        payment = ObjectSelect.query(PaymentIn).where(PaymentIn.SESSION_ID.eq(request.merchantReference)).selectOne(cayenneService.newContext())

        assertEquals(payment.status, ish.common.types.PaymentStatus.SUCCESS)
        assertEquals(payment.statusNotes, "{\n" +
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
                "}Payment successful.")
        assertEquals(payment.creditCardType, CreditCardType.MASTERCARD)
        assertEquals(payment.creditCardNumber, "543111XXXXXXX111")
        assertEquals(payment.creditCardName, "JOHN")
        assertEquals(payment.creditCardExpiry, "08/20")
        assertNull(payment.creditCardCVV)
        assertEquals(payment.gatewayResponse, "Transaction Approved")
        assertEquals(payment.gatewayReference,'0000002148c403m1')
        assertEquals(payment.amount, new Money("50.00"))

        //do not store cc id
        assertNull(payment.billingId)

    }


    @Test
    void testCardDeclined() {
        ThreadLocalPayerId.set(1005l)
        ThreadLocalValidateOnly.set(false)

        CheckoutApiImpl api = new CheckoutApiImpl(cayenneService, collegeService, financialService, entityRelationService)
        PaymentRequest request = new PaymentRequest()
        request.setCcAmount(50.0)
        request.sessionId = TestPaymentService.INVALID_TEST_ID
        request.merchantReference = UUID.randomUUID().toString()
        try {
            api.makePayment(request, false, '1005', "https://localhost")
        } catch(BadRequestException e) {
            assertEquals((e.response.entity as CommonError).getMessage(), "Credit card declined: Transaction Declined")
            return
        }
        assertTrue('Bad request here',false)
    }

}
