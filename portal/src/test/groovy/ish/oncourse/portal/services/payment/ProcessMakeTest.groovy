package ish.oncourse.portal.services.payment

import groovy.mock.interceptor.MockFor
import ish.common.types.PaymentStatus
import ish.math.Money
import ish.oncourse.model.PaymentIn
import org.junit.Test

import static org.apache.commons.lang3.time.DateUtils.addYears
import static org.junit.Assert.*

/**
 * User: akoiro
 * Date: 3/07/2016
 */
class ProcessMakeTest {
    def ContextFactory contextFactory = new ContextFactory()

    @Test
    public void test() {
        Request request = new Request()
        request.invoiceId = 1
        request.card.number = "5555555555554444"
        request.card.cvv = "1234"
        request.card.name = "User1 User1"
        request.card.date = addYears(new Date(), 3).format("MM/YYYY")
		request.card.amount = new Money("20.00")

        Context context = contextFactory.get(Action.make)
        ProcessMake process = new ProcessMake(request: request, context: context, createPaymentInClosure: {
            return new PaymentIn()
        }, processPaymentInClosure: {
            def paymentIn = new MockFor(PaymentIn).with {
                it.ignore.getStatus { return PaymentStatus.SUCCESS }
                it.ignore.getId { return 1L }
                return it
            }
            return paymentIn.proxyInstance()
        })

        process.validate()
        assertNotNull(process.response)
        assertNotNull(process.response.validationResult)
        assertTrue(process.response.validationResult.valid())

        process.process()
        assertEquals(PaymentStatus.SUCCESS, process.response.paymentStatus)
        assertEquals(Status.result, process.response.status)
        assertNotNull(process.response.amount)
        assertNotNull(process.response.dateDue)
        assertNotNull(process.response.invoiceId)

    }
}
