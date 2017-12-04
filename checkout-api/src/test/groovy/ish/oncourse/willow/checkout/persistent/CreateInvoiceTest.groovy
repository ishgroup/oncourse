package ish.oncourse.willow.checkout.persistent

import ish.common.types.ConfirmationStatus
import ish.common.types.PaymentSource
import ish.math.Money
import ish.oncourse.model.College
import ish.oncourse.model.Contact
import ish.oncourse.model.CorporatePass
import ish.oncourse.model.Invoice
import ish.oncourse.model.PaymentIn
import ish.oncourse.model.PaymentInLine
import ish.oncourse.model.WebSite
import ish.oncourse.willow.service.NoDbApiTest
import org.apache.cayenne.ObjectContext
import org.apache.commons.lang3.time.DateUtils
import org.junit.Test

import static org.mockito.Mockito.mock
import static org.mockito.Mockito.spy
import static org.mockito.Mockito.when
import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertNull
import static org.junit.Assert.assertTrue
import static org.mockito.Mockito.withSettings

class CreateInvoiceTest extends NoDbApiTest{

    private ObjectContext context
    private College college
    private WebSite webSite
    private Contact contact
    private CorporatePass pass
    private String reference
    private PaymentIn payment
    private String sessionId

    private void prepareData() {
        context = cayenneService.newContext()

        college = context.newObject(College)
        webSite = context.newObject(WebSite)

        contact = spy(context.newObject(Contact))
        when(contact.address).thenReturn('221b Baker St, Marylebone, London NW1 6XE, UK')

        pass = context.newObject(CorporatePass)
        reference = 'testRef'

        payment = context.newObject(PaymentIn)
        sessionId = 'customSession'

    }

    @Test
    void testInvoiceForPaymentModel() {
        prepareData()

        Invoice invoice = new CreateInvoice(context, college, webSite, contact)
                .forPaymentModel(payment, sessionId)

        assertTrue(DateUtils.isSameDay(new Date(), invoice.invoiceDate))
        assertTrue(DateUtils.isSameDay(new Date(), invoice.dateDue))
        assertEquals(Money.ZERO, invoice.amountOwing)
        assertEquals(PaymentSource.SOURCE_WEB, invoice.source)
        assertEquals(college, invoice.college)
        assertEquals(webSite, invoice.webSite)
        assertEquals(contact, invoice.contact)
        assertEquals(contact.address, invoice.billToAddress)

        assertEquals(ConfirmationStatus.DO_NOT_SEND, invoice.confirmationStatus)
        assertEquals(sessionId, invoice.sessionId)
        assertEquals(1, invoice.paymentInLines.size())
        PaymentInLine line = invoice.paymentInLines[0]
        assertEquals(payment, line.paymentIn)
        assertEquals(college, line.college)
        assertEquals(Money.ZERO, line.amount)

        assertNull(invoice.corporatePassUsed)
        assertNull(invoice.customerReference)

    }

    @Test
    void testInvoiceForCorporatePassModel() {
        prepareData()

        Invoice invoice = new CreateInvoice(context, college, webSite, contact)
                .forCorpPassModel(pass, reference)

        assertTrue(DateUtils.isSameDay(new Date(), invoice.invoiceDate))
        assertTrue(DateUtils.isSameDay(new Date(), invoice.dateDue))
        assertEquals(Money.ZERO, invoice.amountOwing)
        assertEquals(PaymentSource.SOURCE_WEB, invoice.source)
        assertEquals(college, invoice.college)
        assertEquals(webSite, invoice.webSite)
        assertEquals(contact, invoice.contact)
        assertEquals(contact.address, invoice.billToAddress)


        assertEquals(ConfirmationStatus.NOT_SENT, invoice.confirmationStatus)
        assertEquals(pass, invoice.corporatePassUsed)
        assertEquals(reference, invoice.customerReference)

        assertNull(invoice.sessionId)
        assertEquals(0, invoice.paymentInLines.size())
    }
}
