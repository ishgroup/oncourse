package ish.oncourse.portal.services.payment

import ish.common.types.PaymentStatus
import org.junit.Test

import static org.junit.Assert.*

/**
 * User: akoiro
 * Date: 3/07/2016
 */
class ProcessUpdateTest {
    def ContextFactory contextFactory = new ContextFactory()

    @Test
    public void test() {
        Request request = new Request().with {
            it.action = Action.update
            it.invoiceId = 1L
            it.paymentInId = 1L
            return it
        }
        ProcessUpdate process = new ProcessUpdate(request: request, context: contextFactory.get(Action.update))

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
