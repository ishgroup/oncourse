package ish.oncourse.willow.checkout.v2

import ish.common.types.PaymentStatus
import ish.oncourse.model.PaymentIn
import ish.oncourse.willow.checkout.CheckoutApiImpl
import ish.oncourse.willow.model.common.CommonError
import ish.oncourse.willow.model.v2.checkout.payment.PaymentRequest
import ish.oncourse.willow.model.v2.checkout.payment.PaymentResponse
import ish.oncourse.willow.service.ApiTest
import org.apache.cayenne.query.ObjectSelect
import org.junit.Test

import javax.ws.rs.BadRequestException

import static ish.oncourse.willow.filters.RequestFilter.ThreadLocalPayerId
import static ish.oncourse.willow.filters.XValidateFilter.ThreadLocalValidateOnly
import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertNotNull
import static org.junit.Assert.assertNotNull
import static org.junit.Assert.assertNotNull
import static org.junit.Assert.assertNull
import static org.junit.Assert.assertNull
import static org.junit.Assert.assertTrue

class RealGatewayTest  extends ApiTest {
    @Override
    protected String getDataSetResource() {
        return 'ish/oncourse/willow/checkout/v2/RealGatewayTest.xml'
    }

    @Test
    void testXValidate() {
        ThreadLocalPayerId.set(1005l)
        ThreadLocalValidateOnly.set(true)

        CheckoutApiImpl api = new CheckoutApiImpl(cayenneService, collegeService, financialService, entityRelationService)
        PaymentRequest request = new PaymentRequest()
        request.setCcAmount(50.0)

        PaymentResponse response =  api.makePayment(request, true, '1005', "https://localhost")

        PaymentIn payment = ObjectSelect.query(PaymentIn).selectOne(cayenneService.newContext())

        assertNull(payment)
        assertNull(response.status)
        assertNotNull(response.sessionId)
        assertNotNull(response.paymentFormUrl)
        assertNotNull(response.merchantReference)

        //open windcave page
        String text = new URL(response.paymentFormUrl).getText()
        assertTrue(text.contains("<form"))

        //try to complete transaction
        request.sessionId = response.sessionId
        request.merchantReference = response.merchantReference
        ThreadLocalValidateOnly.set(false)
        try {
            api.makePayment(request, false, '1005', "https://localhost")
        } catch (BadRequestException e) {
            CommonError error = e.response.entity as CommonError
            assertEquals(error.getMessage(), "Credit card authorisation is not complete")
            return
        }
        assertTrue('Bad request here', false)


    }

}
