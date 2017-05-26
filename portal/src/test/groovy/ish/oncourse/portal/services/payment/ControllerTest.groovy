package ish.oncourse.portal.services.payment

import groovy.time.TimeCategory
import ish.common.types.PaymentStatus
import ish.oncourse.model.Contact
import ish.oncourse.model.Invoice
import ish.oncourse.model.PaymentIn
import ish.oncourse.portal.services.APortalTest
import ish.oncourse.services.paymentexpress.NewPaymentExpressGatewayService
import ish.oncourse.services.paymentexpress.PaymentExpressGatewayService
import org.dbunit.dataset.IDataSet
import org.dbunit.dataset.ReplacementDataSet
import org.junit.Test

import static org.apache.cayenne.query.SelectById.query
import static org.apache.commons.lang3.time.DateUtils.addYears
import static org.junit.Assert.assertEquals

/**
 * User: akoiro
 * Date: 2/07/2016
 */
class ControllerTest extends APortalTest {
    def IController controller
    def Date tomorrow
    def Date fiveDaysLater

    @Override
    void setup() throws Exception {
        super.setup()
    }

    @Override
    protected IDataSet adjustDataSet(IDataSet dataSet) {
        ReplacementDataSet rDataSet = new ReplacementDataSet(dataSet)
        use(TimeCategory.class) {
            tomorrow = 1.day.from.now
            fiveDaysLater = 5.days.from.now
            rDataSet.addReplacementObject("[tomorrow]", tomorrow)
            rDataSet.addReplacementObject("[fiveDaysLater]", fiveDaysLater)
        }
        return super.adjustDataSet(rDataSet)
    }

    @Test
    public void test() {
        Contact contact = query(Contact, 1L).selectOne(objectContext)
        Invoice invoice = query(Invoice, 1L).selectOne(objectContext)

        controller = new Controller(contact, objectContext, paymentGatewayService)

        Request request = new Request()
        request.action = Action.init

        Response response = controller.process(request)
        assertEquals(20.0, response.amount, 0)
        assertEquals(tomorrow, response.dateDue)
        assertEquals(1L, response.invoiceId)

        request = new Request()
        request.action = Action.make
        request.invoiceId = 1
        request.card.with {
            it.name = "john smith"
            it.number = "5431111111111111"
            it.cvv = "123"
            it.amount = 20.0
            it.date = addYears(new Date(), 3).format("MM/YYYY")
        }

        NewPaymentExpressGatewayService paymentExpressGatewayService = new NewPaymentExpressGatewayService(cayenneService.newNonReplicatingContext())
        controller = new Controller(contact, objectContext, paymentExpressGatewayService)

        response = controller.process(request)

        PaymentIn paymentIn = query(PaymentIn, 1L).selectOne(objectContext)

        assertEquals(20.0, paymentIn.amount.doubleValue(), 0)
        assertEquals(20.0, response.amount, 0)
        assertEquals(tomorrow, response.dateDue)
        assertEquals(1L, response.invoiceId)
        assertEquals(1L, response.paymentId)
        assertEquals(Status.result, response.status)
        assertEquals(PaymentStatus.FAILED_CARD_DECLINED, paymentIn.status)
    }
}
