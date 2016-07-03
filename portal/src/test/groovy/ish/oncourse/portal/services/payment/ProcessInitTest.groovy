package ish.oncourse.portal.services.payment

import org.junit.Test

import static org.junit.Assert.*

/**
 * User: akoiro
 * Date: 30/06/2016
 */
class ProcessInitTest {
    def ContextFactory contextFactory = new ContextFactory()

    @Test
    public void test() {

        Request request = new Request()
        Context context = contextFactory.get(Action.init)


        ProcessInit processInit = new ProcessInit(request: request, context: context)

        processInit.validate()
        assertNotNull(processInit.response)
        assertNotNull(processInit.response.validationResult)
        assertTrue(processInit.response.validationResult.valid())

        processInit.process()
        assertEquals(Status.card, processInit.response.status)
        assertNotNull(processInit.response.amount)
        assertNotNull(processInit.response.dateDue)
        assertNotNull(processInit.response.invoiceId)
    }
}
