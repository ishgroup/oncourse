package ish.oncourse.portal.services.payment

import groovy.time.TimeCategory
import ish.math.Money
import ish.oncourse.model.Contact
import ish.oncourse.model.Invoice
import ish.oncourse.model.PaymentIn
import ish.oncourse.portal.services.APortalTest
import org.junit.Test

import static org.apache.cayenne.query.SelectById.query
import static org.testng.Assert.*

/**
 * User: akoiro
 * Date: 2/07/2016
 */
class ContextTest extends APortalTest {
    @Override
    protected String getDataSetName() {
        return ControllerTest.class.name.replace('.', '/') + ".xml"
    }

    @Override
    protected Map<String, Object> replacements() {
        Map<String, Object> replacements = new HashMap<>()
        use(TimeCategory.class) {
            replacements.put("[tomorrow]", 1.day.from.now)
            replacements.put("[fiveDaysLater]", 5.days.from.now)

        }
        return replacements
    }


    @Test
    void testInit() {
        Contact contact = query(Contact, 1L).selectOne(objectContext)
        Invoice invoice = query(Invoice, 1L).selectOne(objectContext)

        Request request = new Request(action: Action.init)

        Context context = Context.valueOf(contact, request)
        assertEquals(invoice, context.invoice)
        assertTrue(invoice.amountOwing.isGreaterThan(new Money(0, 1)))
        assertNull(context.notFinalPaymentIn)
        assertNull(context.paymentIn)
        assertNull(context.paymentTransaction)
    }

    @Test
    public void testMake() {
        Contact contact = query(Contact, 1L).selectOne(objectContext)
        Invoice invoice = query(Invoice, 1L).selectOne(objectContext)

        Request request = new Request()
        request.action = Action.make
        request.invoiceId = 1L


        Context context = Context.valueOf(contact, request)

        assertEquals(invoice, context.invoice)
        assertTrue(context.invoice.amountOwing.isGreaterThan(new Money(0, 1)))
        assertNull(context.notFinalPaymentIn)
        assertNull(context.paymentIn)
        assertNull(context.paymentTransaction)
    }

    @Test
    public void testUpdate() {
        Contact contact = query(Contact, 2L).selectOne(objectContext)
        Invoice invoice = query(Invoice, 2L).selectOne(objectContext)
        PaymentIn paymentIn = query(PaymentIn, 2L).selectOne(objectContext)

        Request request = new Request()
        request.action = Action.update
        request.invoiceId = 2L
        request.paymentInId = 2L


        Context context = Context.valueOf(contact, request)

        assertEquals(invoice, context.invoice)
        assertEquals(paymentIn, context.paymentIn)
        assertFalse(context.paymentTransaction.isFinalised)

        assertTrue(invoice.amountOwing.isGreaterThan(new Money(0, 1)))
    }
}
